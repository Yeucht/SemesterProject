package ingestion;

import dbmanager.QuestDBManager;
import io.questdb.client.Sender;

public class QuestDBInjection extends Injection {
    private static final String QUESTDB_URL = "localhost:9000;";  // Update to HTTP connection URL for QuestDB
    private static final String TABLE_NAME = "smart_meter";
    private QuestDBManager QuestDBManager;

    public QuestDBInjection(boolean clean){
        super(clean);
        this.QuestDBManager = new QuestDBManager(clean);
    }

    @Override
    protected boolean clearTables(){
        return QuestDBManager.clearTables();
    }


    @Override
    public void insertData(int recordCount) {

        // Create a Sender instance with the configuration (HTTP URL)
        try (Sender sender = Sender.fromConfig("http::addr=" + QUESTDB_URL)) {

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < recordCount; i++) {
                // Generate data for smart meter, assuming generateMeterData() returns comma-separated values like: "timestamp,meter_id,power_consumption,voltage,current"
                String[] data = generateMeterData().split(",");

                // Use the Sender API to insert data into the "smart_meter" table
                sender.table(TABLE_NAME) // Assuming timestamp is in milliseconds
                        .symbol("meter_id", String.valueOf(data[1]))  // Assuming meter_id is an integer
                        .doubleColumn("power_consumption", Double.parseDouble(data[2]))  // power_consumption as double
                        .doubleColumn("voltage", Double.parseDouble(data[3]))  // voltage as double
                        .doubleColumn("current", Double.parseDouble(data[4]))  // current as double
                        .atNow();  // Sends the data immediately
            }

            // Print the time taken to insert data
            System.out.println("QuestDB Ingestion Time: " + (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}