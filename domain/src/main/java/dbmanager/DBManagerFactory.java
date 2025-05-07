package dbmanager;

import config.Config;
import config.Config.DatabaseType;

public class DBManagerFactory {

    public static DBManager createManager(Config config) {
        DatabaseType dbtype = config.getDbType();
        switch (dbtype) {
            case IOTDB:
                return new IoTDBManager(config.getClearTablesFlag());
            case INFLUXDB:
                return new InfluxDBManager(config.getClearTablesFlag());
            case QUESTDB:
                return new QuestDBManager(config.getClearTablesFlag());
            default: throw new IllegalArgumentException("Unsupported database type: " + dbtype);
        }
    }
}
