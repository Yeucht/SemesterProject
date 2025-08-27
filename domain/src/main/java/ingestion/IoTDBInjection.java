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
    final int buffer;

    public IoTDBInjection(SimulationConfig config) {
        super(config);
        this.dbManager = new IoTDBManager(config);
        buffer = this.config.getMdmsBatchSize();
    }

    @Override
    public void insertData(List<DataPacket> packets) {
        if (packets == null || packets.isEmpty()) return;

        final SessionPool pool = dbManager.getSessionPool();

        // Colonnes & types (identiques à QuestDBInjection + received_time)
        final List<String> MEASUREMENTS = Arrays.asList(
                "auth_user",
                "auth_serial_number",
                "auth_digest",
                "is_authenticated",
                "is_message_broker_job",
                "archiver_connection_id",
                "cache_file_name",
                "master_unit_number",
                "master_unit_owner_id",
                "master_unit_type",
                "address",
                "received_time",
                "connection_cause",
                "sequence",
                "status",
                "version",
                "payload"
        );

        final List<TSDataType> TYPES = Arrays.asList(
                TSDataType.TEXT,    // auth_user
                TSDataType.TEXT,    // auth_serial_number
                TSDataType.TEXT,    // auth_digest
                TSDataType.BOOLEAN, // is_authenticated
                TSDataType.BOOLEAN, // is_message_broker_job
                TSDataType.TEXT,    // archiver_connection_id
                TSDataType.TEXT,    // cache_file_name
                TSDataType.TEXT,    // master_unit_number
                TSDataType.TEXT,    // master_unit_owner_id
                TSDataType.TEXT,    // master_unit_type
                TSDataType.TEXT,    // address
                TSDataType.INT64,   // received_time
                TSDataType.INT64,   // connection_cause
                TSDataType.INT64,   // sequence
                TSDataType.INT64,   // status
                TSDataType.INT64,   // version
                TSDataType.DOUBLE   // payload
        );

        // Références immuables réutilisées (IoTDB recopie côté serveur)
        final List<String> SHARED_MEASUREMENTS = Collections.unmodifiableList(MEASUREMENTS);
        final List<TSDataType> SHARED_TYPES   = Collections.unmodifiableList(TYPES);

        // Buffers de batch
        final List<String> deviceIds = new ArrayList<>(buffer);
        final List<Long>   times     = new ArrayList<>(buffer);
        final List<List<String>>  measurementsList = new ArrayList<>(buffer);
        final List<List<TSDataType>> typesList     = new ArrayList<>(buffer);
        final List<List<Object>> valuesList        = new ArrayList<>(buffer);

        long totalRows = 0L;
        final long start = System.currentTimeMillis();

        try {
            for (DataPacket packet : packets) {
                if (packet == null) continue;
                final String deviceId = STORAGE_GROUP + ".meter_" + packet.getAuthSerialNumber();
                final List<MeterData> list = packet.getMeteringData();
                if (list == null) continue;

                for (MeterData data : list) {
                    if (data == null || data.getPayload() == null) continue;

                    for (Number v : data.getPayload()) {
                        if (v == null) continue;

                        // Timestamp de la ligne (équivalent QuestDB .atNow())
                        final long ts = System.currentTimeMillis();

                        // Valeurs alignées sur MEASUREMENTS
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

                        deviceIds.add(deviceId);
                        times.add(ts);
                        measurementsList.add(SHARED_MEASUREMENTS);
                        typesList.add(SHARED_TYPES);
                        valuesList.add(values);
                        totalRows++;

                        if (deviceIds.size() >= buffer) {
                            pool.insertRecords(deviceIds, times, measurementsList, typesList, valuesList);
                            deviceIds.clear();
                            times.clear();
                            measurementsList.clear();
                            typesList.clear();
                            valuesList.clear();
                        }
                    }
                }
            }

            // Flush final
            if (!deviceIds.isEmpty()) {
                pool.insertRecords(deviceIds, times, measurementsList, typesList, valuesList);
            }

            System.out.println("Ingested " + totalRows + " row(s) to IoTDB in "
                    + (System.currentTimeMillis() - start) + " ms");
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            System.err.println("Error during IoTDB batch insertion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // petit util pour éviter les null TEXT
    private static String nz(String s) { return s == null ? "" : s; }

}
