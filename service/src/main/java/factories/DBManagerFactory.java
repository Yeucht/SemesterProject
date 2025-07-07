package factories;

import config.Config;
import config.Config.DatabaseType;
import dbmanager.DBManager;
import dbmanager.InfluxDBManager;
import dbmanager.IoTDBManager;
import dbmanager.QuestDBManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DBManagerFactory {

    public DBManagerFactory() {}

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
