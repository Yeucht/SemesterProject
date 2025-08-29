package ingestion;

import config.SimulationConfig;
import dbmanager.QuestDBManager;
import io.questdb.client.Sender;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class QuestDBInjection extends Injection implements AutoCloseable {
    //private static final String QUESTDB_URL = "jdbc:postgresql://questdb:8812/qdb";
    private static final String TABLE_NAME   = "smart_meter";
    private final String QUESTDB_SENDER_URL;
    private final QuestDBManager questDBManager;
    private final int BATCH_SIZE;
    private final int AUTO_FLUSH_INTERVAL_MS;
    private final Sender sender;
    private final BlockingQueue<Consumer<Sender>> queue = new LinkedBlockingQueue<>(100_000);
    private final Thread worker;

    public QuestDBInjection(SimulationConfig config) {
        super(config);
        this.questDBManager = new QuestDBManager(config);
        this.AUTO_FLUSH_INTERVAL_MS = Integer.parseInt(System.getenv().getOrDefault(
                "QUESTDB_FLUSH_INTERVAL_MS",
                "6000"
        ));

        this.QUESTDB_SENDER_URL = System.getenv().getOrDefault("QUESTDB_SENDER_URL", "http::addr=questdb:9000;");

        if (config.getMdmsBatch()){
            this.BATCH_SIZE = config.getMdmsBatchSize();
            this.sender = Sender.fromConfig(
                    QUESTDB_SENDER_URL
                            + "auto_flush=on;"
                            + "auto_flush_rows=" + BATCH_SIZE + ";"
                            + "auto_flush_interval=" + AUTO_FLUSH_INTERVAL_MS + ";"
            );

        }else{
            this.BATCH_SIZE = 1;
            this.sender = Sender.fromConfig(
                    QUESTDB_SENDER_URL
                    + "auto_flush=on;"
                    + "auto_flush_rows=" + BATCH_SIZE + ";"
                    + "auto_flush_interval=" + AUTO_FLUSH_INTERVAL_MS + ";"
            );
        }


        // HTTP ILP + auto-flush par lignes & intervalle

        // Démarre le worker dédié qui exécute toutes les écritures
        this.worker = new Thread(this::runWorker, "questdb-writer");
        this.worker.setDaemon(true);
        this.worker.start();
    }

    // Worker: regroupe naturellement les jobs + flush sur inactivité (latence bornée)
    private void runWorker() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Consumer<Sender> job = queue.poll(AUTO_FLUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
                if (job != null) {
                    job.accept(sender);
                    // Draine le plus possible pour profiter du batching
                    Consumer<Sender> more;
                    while ((more = queue.poll()) != null) {
                        more.accept(sender);
                    }
                } else {
                    // Inactif : flush pour garantir une latence max
                    sender.flush();
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } finally {
            try { sender.flush(); } catch (Throwable ignore) {}
        }
    }

    // Appelée par ton endpoint (compteurs individuels)
    @Override
    public void insertData(List<DataPacket> packets) {
        if (packets == null || packets.isEmpty()) return;

        // Un job = un lot de paquets (évite d’inonder la queue avec une entrée par point)
        boolean offered = queue.offer(s -> writePackets(s, packets));
        if (!offered) {
            // En cas de saturation, fallback synchrone (meilleure robustesse)
            writePackets(sender, packets);
        }
    }

    // Écriture réelle (s’exécute sur le thread du worker)
    private void writePackets(Sender s, List<DataPacket> packets) {
        try {
            for (DataPacket packet : packets) {
                if (packet == null) continue;
                List<MeterData> list = packet.getMeteringData();
                if (list == null) continue;

                for (MeterData data : list) {
                    if (data == null || data.getPayload() == null) continue;

                    for (Number v : data.getPayload()) {
                        if (v == null) continue;

                        s.table(TABLE_NAME)
                                // Symbols obligatoires (jamais null → nz pour vide si besoin)
                                .symbol("auth_user",               nz(packet.getAuthUser()))
                                .symbol("auth_serial_number",      nz(packet.getAuthSerialNumber()))
                                .symbol("auth_digest",             nz(packet.getAuthDigest()))
                                .symbol("is_authenticated",        String.valueOf(packet.isAuthenticated()))
                                .symbol("is_message_broker_job",   String.valueOf(packet.isMessageBrokerJob()))

                        // Symbols optionnels → uniquement si non-null (évite NPE & garde NULL)
                        .symbol("archiver_connection_id", nz(packet.getArchiverConnectionId()))
                        .symbol("cache_file_name",        nz(packet.getCacheFileName()))
                        .symbol("master_unit_number",     nz(packet.getMasterUnitNumber()))
                        .symbol("master_unit_owner_id",   nz(packet.getMasterUnitOwnerId()))
                        .symbol("master_unit_type",       nz(packet.getMasterUnitType()))
                        .symbol("address",                nz(data.getAddress()))

                                .longColumn("received_time",    packet.getReceivedTime())
                                .longColumn("connection_cause", packet.getConnectionCause())
                                .longColumn("sequence",         data.getSequence())
                                .longColumn("status",           data.getStatus())
                                .longColumn("version",          data.getVersion())
                                .doubleColumn("payload",        v.doubleValue())
                                .atNow(); // timestamp ligne (équivalent .atNow() TCP)
                    }
                }
            }
        } catch (Exception e) {
            // On loggue sans interrompre le worker
            e.printStackTrace();
        }
    }

    private static void addSymbolIfNotNull(Sender s, String name, String value) {
        if (value != null) s.symbol(name, value);
    }
    private static String nz(String s) { return s == null ? "" : s; }

    // Optionnel: flush manuel si tu veux forcer la durabilité à un moment précis
    public void flush() {
        try { sender.flush(); } catch (Exception e) { e.printStackTrace(); }
    }


    // Arrêt propre : stoppe le worker, flush & ferme le sender
    @Override
    public void close() {
        worker.interrupt();
        try { worker.join(1000); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        try { sender.flush(); } catch (Throwable ignore) {}
        try { sender.close(); } catch (Throwable ignore) {}
    }
}
