package ingestion;

import dbmanager.IoTDBManager;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.session.Session;

public class IoTDBInjection extends Injection {

    private static final String IOTDB_PATH = "root.smartMeter"; // Path where the time series data will be stored
    private IoTDBManager ioTDBManager;

    public IoTDBInjection(boolean clean) {
        super(clean);
        this.ioTDBManager = new IoTDBManager(clean);
    }

    // Override the clearTables method to use IoTDBManager's clearTables function
    @Override
    protected boolean clearTables() {
        return ioTDBManager.clearTables();
    }

    @Override
    public void insertData(int recordCount) {
        clearTables();

        try (SessionPool sessionPool = new SessionPool.Builder()
                .nodeUrls("127.0.0.1:6667", "127.0.0.1:6668")
                .user("root")
                .password("root")
                .maxSize(3)
                .build()) {

            long startTime = System.currentTimeMillis();

            // Insert data into IoTDB
            try (Session session = sessionPool.borrowSession()) {
                // Creating time series for each data point (e.g., power consumption)
                for (int i = 0; i < recordCount; i++) {
                    String[] data = generateMeterData().split(",");

                    // Timestamp: System.currentTimeMillis(), meterId: data[1], powerConsumption: data[2], etc.
                    long timestamp = Long.parseLong(data[0]);
                    String meterId = data[1];
                    double powerConsumption = Double.parseDouble(data[2]);
                    double voltage = Double.parseDouble(data[3]);
                    double current = Double.parseDouble(data[4]);

                    // Create a time series entry
                    String insertQuery = String.format(
                            "INSERT INTO %s(timestamp, meterId, powerConsumption, voltage, current) VALUES(%d, '%s', %.3f, %.2f, %.2f)",
                            IOTDB_PATH, timestamp, meterId, powerConsumption, voltage, current);

                    // Execute insert query
                    session.executeNonQueryStatement(insertQuery);
                }
            }

            System.out.println("IoTDB Ingestion Time: " + (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}