package ingestion;

import config.SimulationConfig;
import dbmanager.QuestDBManager;
import io.questdb.client.Sender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class QuestDBInjection extends Injection {
    private static final String QUESTDB_URL = "jdbc:postgresql://questdb:8812/qdb";
    private static final String TABLE_NAME = "smart_meter";
    private QuestDBManager questDBManager;

    public QuestDBInjection(SimulationConfig config) {
        super(config);
        this.questDBManager = new QuestDBManager(config);
    }


    // "tcp::addr=questdb:9009; auto_flush_rows=5000;" ajouter avec config (flush rows = batches?)
    // New method to insert real DataPacket received from controller
    public void insertData(DataPacket packet) {
        try (Sender sender = Sender.fromConfig("tcp::addr=questdb:9009")) {
            List<MeterData> meterDataList = packet.getMeteringData();

            for (MeterData data : packet.getMeteringData()) {
                for (int payload : data.getPayload()) {
                    sender
                            .table(TABLE_NAME)
                            .symbol("auth_user",       packet.getAuthUser())
                            .symbol("auth_serial_number", packet.getAuthSerialNumber())
                            .symbol("auth_digest",     packet.getAuthDigest())
                            .symbol("is_authenticated", String.valueOf(packet.isAuthenticated()))
                            .symbol("is_message_broker_job", String.valueOf(packet.isMessageBrokerJob()))
                            .symbol("archiver_connection_id", packet.getArchiverConnectionId())
                            .symbol("cache_file_name", packet.getCacheFileName())
                            .symbol("master_unit_number", packet.getMasterUnitNumber())
                            .symbol("master_unit_owner_id", packet.getMasterUnitOwnerId())
                            .symbol("master_unit_type", packet.getMasterUnitType())
                            .symbol("address", data.getAddress())

                            // 2) Colonnes numériques ensuite
                            .longColumn("received_time", packet.getReceivedTime())
                            .longColumn("connection_cause", packet.getConnectionCause())
                            .longColumn("sequence", data.getSequence())
                            .longColumn("status", data.getStatus())
                            .longColumn("version", data.getVersion())
                            .doubleColumn("payload", payload)

                            // 3) On termine la ligne
                            .atNow();
                }
            }
            sender.flush();

            System.out.println("Ingested DataPacket to QuestDB");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
