package dbmanager;

import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.session.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IoTDBManager {
    private static final List<String> nodeUrls = new ArrayList<>();
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private SessionPool sessionPool;
    private boolean clearTablesFlag;

    // Configure connection pool
    static {
        nodeUrls.add("127.0.0.1:6667");
        nodeUrls.add("127.0.0.1:6668");
    }

    public IoTDBManager(boolean clear) {
        this.clearTablesFlag = clear;
        try {
            sessionPool = new SessionPool.Builder()
                    .nodeUrls(nodeUrls)
                    .user(USER)
                    .password(PASSWORD)
                    .maxSize(3)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clear tables in IoTDB (i.e., drop time series)
    public boolean clearTables() {
        if (!clearTablesFlag) {
            System.out.println("Table clearing is disabled. Skipping...");
            return false;
        }

        try (Session session = sessionPool.borrowSession()) {
            // Dropping time series (tables) under a specific root path
            // Example: Drop time series for smart meter
            String path = "root.smartMeter";  // Define the series path here
            session.executeNonQueryStatement("DELETE TIMESERIES " + path + ".*");

            System.out.println("All time series under " + path + " deleted successfully.");
            return true;
        } catch (Exception e) {
            System.out.println("Unable to clear tables in IoTDB.");
            e.printStackTrace();
            return false;
        }
    }
}