package ingestion;

import config.SimulationConfig;
import dbmanager.IoTDBManager;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

/**
 * Injection implementation pour IoTDB, calquée sur votre QuestDBInjection :
 * - On crée automatiquement les time‑series grâce à schema_auto_create=true
 * - Pour chaque payload, on envoie une « ligne » avec TOUTES les colonnes
 */
public class IoTDBInjection extends Injection {
    private static final String STORAGE_GROUP = "root.smart_meter";
    private final IoTDBManager dbManager;
    private final SessionPool pool;
    private final int BATCH_SIZE;

    private final List<String> MEASUREMENTS = Arrays.asList(
            "auth_user","auth_serial_number","auth_digest","is_authenticated","is_message_broker_job",
            "archiver_connection_id","cache_file_name","master_unit_number","master_unit_owner_id",
            "master_unit_type","address","received_time","connection_cause","sequence","status","version","payload"
    );

    private final List<TSDataType> TYPES = Arrays.asList(
            TSDataType.TEXT, TSDataType.TEXT, TSDataType.TEXT, TSDataType.BOOLEAN, TSDataType.BOOLEAN,
            TSDataType.TEXT, TSDataType.TEXT, TSDataType.TEXT, TSDataType.TEXT,
            TSDataType.TEXT, TSDataType.TEXT, TSDataType.INT64, TSDataType.INT64, TSDataType.INT64,
            TSDataType.INT64, TSDataType.INT64, TSDataType.DOUBLE
    );

    // Buffers persistants (partagés entre invocations)
// → instanciés dans le constructeur
    private final List<String> deviceIds;
    private final List<Long> times;
    private final List<List<String>> measurementsList;
    private final List<List<TSDataType>> typesList;
    private final List<List<Object>> valuesList;

    // Simple protection si la classe est appelée de plusieurs threads
    private final Object lock = new Object();

    public IoTDBInjection(SimulationConfig config) {
        super(config);
        this.dbManager = new IoTDBManager(config);
        this.BATCH_SIZE = config.getMdmsBatchSize();
        this.pool = this.dbManager.getSessionPool();

        // init buffers avec capacité = BATCH_SIZE
        this.deviceIds = new ArrayList<>(BATCH_SIZE);
        this.times = new ArrayList<>(BATCH_SIZE);
        this.measurementsList = new ArrayList<>(BATCH_SIZE);
        this.typesList = new ArrayList<>(BATCH_SIZE);
        this.valuesList = new ArrayList<>(BATCH_SIZE);
    }


    // Appel depuis ton service
    public void insertData(List<DataPacket> packets) {
        if (packets == null || packets.isEmpty()) return;
        long rows = 0;

        for (DataPacket packet : packets) {
            if (packet == null) continue;
            final String deviceId = STORAGE_GROUP + ".meter_" + packet.getAuthSerialNumber();
            final List<MeterData> list = packet.getMeteringData();
            if (list == null) continue;

            for (MeterData data : list) {
                if (data == null || data.getPayload() == null) continue;

                for (Number v : data.getPayload()) {
                    if (v == null) continue;

                    final long ts = System.currentTimeMillis(); // équivalent .atNow()
                    final List<Object> values = Arrays.asList(
                            nz(packet.getAuthUser()),
                            nz(packet.getAuthSerialNumber()),
                            nz(packet.getAuthDigest()),
                            packet.isAuthenticated(),
                            packet.isMessageBrokerJob(),
                            nz(packet.getArchiverConnectionId()),
                            nz(packet.getCacheFileName()),
                            nz(packet.getMasterUnitNumber()),
                            nz(packet.getMasterUnitOwnerId()),
                            nz(packet.getMasterUnitType()),
                            nz(data.getAddress()),
                            packet.getReceivedTime(),
                            (long) packet.getConnectionCause(),
                            (long) data.getSequence(),
                            (long) data.getStatus(),
                            (long) data.getVersion(),
                            v.doubleValue()
                    );

                    // push dans le buffer global
                    enqueue(deviceId, ts, values);
                    rows++;
                }
            }
        }
        // ici on ne force pas le flush : on laisse le seuil gérer,
        // mais tu peux appeler flush() si tu veux garantir la durabilité à la fin d’un lot.
        // flush();
        if (rows > 0) {
            System.out.println("Buffered " + rows + " row(s) for IoTDB (insertRecords).");
        }
    }

    // Ajout + flush automatique si seuil dépassé
    private void enqueue(String deviceId, long ts, List<Object> values) {
        synchronized (lock) {
            deviceIds.add(deviceId);
            times.add(ts);
            // on réutilise les mêmes références pour éviter les allocs inutiles
            measurementsList.add(MEASUREMENTS);
            typesList.add(TYPES);
            valuesList.add(values);

            if (deviceIds.size() >= BATCH_SIZE) {
                doFlushUnsafe();
            }
        }
    }

    // Appel manuel possible (ex: shutdown hook, fin de traitement, timer)
    public void flush() {
        synchronized (lock) {
            if (!deviceIds.isEmpty()) {
                doFlushUnsafe();
            }
        }
    }

    // Ne pas appeler hors section synchronized
    private void doFlushUnsafe() {
        try {
            pool.insertRecords(deviceIds, times, measurementsList, typesList, valuesList);
            System.out.println("Inserted a batch");
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            System.err.println("Error during IoTDB batch insertion: " + e.getMessage());
            e.printStackTrace();
            // Optionnel: backoff/retry ou découper en sous-batches
        } finally {
            deviceIds.clear();
            times.clear();
            measurementsList.clear();
            typesList.clear();
            valuesList.clear();
        }
    }

    //Should add security for closing sessionPool
    public void close() {
        // Assure qu’on n’oublie rien en mémoire
        flush();
        // Le SessionPool est en général géré ailleurs (lifecycle du dbManager)
    }

    private static String nz(String s) { return s == null ? "" : s; }

}
