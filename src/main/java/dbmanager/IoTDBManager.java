package dbmanager;

import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.session.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IoTDBManager extends DBManager {
    private static final List<String> nodeUrls = new ArrayList<>();
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private SessionPool sessionPool;

    static {
        nodeUrls.add("127.0.0.1");
    }

    public IoTDBManager(boolean clear) {
        // Create session pool directly
        this.sessionPool = new SessionPool(nodeUrls.get(0), 6667, USER, PASSWORD, 3);
        super.clearTablesFlag = clear;
    }

    // Clear time series under a specific root path
    public boolean clearTables() {
        if (!clearTablesFlag) {
            System.out.println("Table clearing is disabled. Skipping...");
            return false;
        }

        String path = "root.smart_meter";

        try {
            sessionPool.executeNonQueryStatement("DELETE DATABASE " + path);
            System.out.println("DataBase deleted successfully.");

        } catch (Exception e) {
            System.out.println("Unable to clear tables in IoTDB.");
            e.printStackTrace();
        }

        try {
            prepareStorageGroupAndSchema();
            System.out.println("All time series under " + path + " created successfully.");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to prepare storage group and schema.");
            e.printStackTrace();
            return false;
        }
    }

    //Recreates storage schema
    public boolean prepareStorageGroupAndSchema() {
        try {
            // Define storage group (like a namespace/database in IoTDB)
            String path = "root.smart_meter";
            sessionPool.executeNonQueryStatement("SET STORAGE GROUP TO " + path);


            for (int i = 0; i < 100; i++) {
                String meter = "meter_" + i;
                String basePath = "root.smart_meter." + meter;
                sessionPool.executeNonQueryStatement("CREATE TIMESERIES " + basePath + ".powerConsumption WITH DATATYPE=DOUBLE, ENCODING=RLE");
                sessionPool.executeNonQueryStatement("CREATE TIMESERIES " + basePath + ".voltage WITH DATATYPE=DOUBLE, ENCODING=RLE");
                sessionPool.executeNonQueryStatement("CREATE TIMESERIES " + basePath + ".current WITH DATATYPE=DOUBLE, ENCODING=RLE");
            }

            System.out.println("Storage group and time series created successfully.");
            return true;
        } catch (Exception e) {
            System.out.println("Error creating storage group or time series:");
            e.printStackTrace();
            return false;
        }
    }
    public SessionPool getSessionPool() {
        return sessionPool;
    }
}