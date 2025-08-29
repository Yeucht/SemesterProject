package factories;

import config.SimulationConfig;
import config.SimulationConfig.DatabaseType;
import dbmanager.DBManager;
import dbmanager.InfluxDBManager;
import dbmanager.IoTDBManager;
import dbmanager.QuestDBManager;


public class DBManagerFactory {

    public DBManagerFactory() {}

    public static DBManager createManager(SimulationConfig config) {
        DatabaseType dbtype = config.getDbType();
        switch (dbtype) {
            case IOTDB:
                return new IoTDBManager(config);
            case QUESTDB:
                return new QuestDBManager(config);
            default: throw new IllegalArgumentException("Unsupported database type: " + dbtype);
        }
    }
}
