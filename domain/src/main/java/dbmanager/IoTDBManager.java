package dbmanager;

import config.Config;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IoTDBManager extends DBManager {
    private static final List<String> nodeUrls = new ArrayList<>();
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private final SessionPool sessionPool;


    public IoTDBManager(Config config) {
        // Create session pool directly
        super(config);
        this.sessionPool = new SessionPool("127.0.0.1", 6667, USER, PASSWORD, 3);
    }

    // Clear time series under a specific root path
    public boolean clearTables() {
        if (!config.getClearTablesFlag()) {
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
        } catch (StatementExecutionException e) {
            System.out.println("Error creating storage group or time series: statement error");
            e.printStackTrace();
            return false;
        } catch (IoTDBConnectionException e){
            System.out.println("Error creating storage group or time series: connection error");
            e.printStackTrace();
            return false;
        }
    }
    public SessionPool getSessionPool() {
        return sessionPool;
    }
}