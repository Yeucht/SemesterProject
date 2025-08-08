package ingestion;

import config.SimulationConfig;
import dbmanager.IoTDBManager;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

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

    public IoTDBInjection(SimulationConfig config) {
        super(config);
        this.dbManager = new IoTDBManager(config);
    }

    @Override
    public void insertData(DataPacket packet) {
        SessionPool pool = dbManager.getSessionPool();
        String deviceId = STORAGE_GROUP + ".meter_" + packet.getAuthSerialNumber();
        long timestamp = packet.getReceivedTime();

        // Liste statique des noms de colonnes, exactement comme dans QuestDBInjection
        List<String> measurements = Arrays.asList(
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
                "connection_cause",
                "sequence",
                "status",
                "version",
                "payload"
        );

        // Types correspondants : TEXT pour les symboles, BOOLEAN/INT64/DOUBLE pour le reste
        List<TSDataType> types = Arrays.asList(
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
                TSDataType.INT64,   // connection_cause
                TSDataType.INT64,   // sequence
                TSDataType.INT64,   // status
                TSDataType.INT64,   // version
                TSDataType.DOUBLE   // payload
        );

        try {
            for (MeterData data : packet.getMeterData()) {
                for (Integer payloadValue : data.getPayload()) {
                    List<Object> values = Arrays.asList(
                            packet.getAuthUser(),
                            packet.getAuthSerialNumber(),
                            packet.getAuthDigest(),
                            packet.isAuthenticated(),
                            packet.isMessageBrokerJob(),
                            packet.getArchiverConnectionId(),
                            packet.getCacheFileName(),
                            packet.getMasterUnitNumber(),
                            packet.getMasterUnitOwnerId(),
                            packet.getMasterUnitType(),
                            data.getAddress(),
                            (long) packet.getConnectionCause(),
                            (long) data.getSequence(),
                            (long) data.getStatus(),
                            (long) data.getVersion(),
                            payloadValue.doubleValue()
                    );

                    // Envoi de la « ligne » IoTDB
                    pool.insertRecord(
                            deviceId,
                            timestamp,
                            measurements,
                            types,
                            values
                    );
                }
            }
            System.out.println("Ingested DataPacket to IoTDB in " +
                    (System.currentTimeMillis() - timestamp) + " ms");
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            System.err.println("Error during IoTDB data insertion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
