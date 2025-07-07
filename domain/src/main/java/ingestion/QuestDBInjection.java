package ingestion;

import dbmanager.DBManager;
import dbmanager.QuestDBManager;
import io.questdb.client.Sender;
import java.util.List;

public class QuestDBInjection extends Injection {
    private static final String QUESTDB_URL = "localhost:9000";
    private static final String TABLE_NAME = "smart_meter";
    private QuestDBManager questDBManager;

    public QuestDBInjection(boolean clean, DBManager dbManager) {
        super(clean);
        this.questDBManager = (QuestDBManager) dbManager;
    }

    @Override
    protected boolean clearTables() {
        return questDBManager.clearTables();
    }

    // New method to insert real DataPacket received from controller
    public void insertData(DataPacket packet) {
        try (Sender sender = Sender.fromConfig("http::addr=" + QUESTDB_URL)) {
            List<MeterData> meterDataList = packet.getMeteringData();

            for (MeterData data : meterDataList) {
                sender.table(TABLE_NAME)
                        .symbol("meter_id", packet.getAuthSerialNumber())
                        .doubleColumn("sequence", data.getSequence())
                        .doubleColumn("status", data.getStatus())
                        .doubleColumn("version", data.getVersion())
                        .longColumn("received_time", packet.getReceivedTime())
                        .doubleColumn("payload_avg", averagePayload(data.getPayload())) // Example
                        .atNow();
            }

            System.out.println("Ingested DataPacket to QuestDB");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double averagePayload(List<Integer> payload) {
        if (payload == null || payload.isEmpty()) return 0.0;
        return payload.stream().mapToInt(i -> i).average().orElse(0.0);
    }
}
