package dbmanager;

import config.Config;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;

public class InfluxDBManager extends DBManager {
    private static final String URL = "http://localhost:8086"; // InfluxDB endpoint
    private static final String USER = "admin";
    private static final String PASSWORD = "adminPassword";
    private static final String DATABASE = "smart_meters";

    private InfluxDB influxDB;

    public InfluxDBManager(Config config) {
        super(config);
        this.influxDB = InfluxDBFactory.connect(URL, USER, PASSWORD);
    }

    @Override
    public boolean clearTables() {
        if (!config.getClearTablesFlag()) {
            System.out.println("Table clearing is disabled. Skipping...");
            return false;
        }

        try {
            influxDB.query(new Query("DROP DATABASE " + DATABASE));
            influxDB.query(new Query("CREATE DATABASE " + DATABASE));
            System.out.println("InfluxDB database cleared successfully.");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to clear InfluxDB database.");
            e.printStackTrace();
            return false;
        }
    }
}