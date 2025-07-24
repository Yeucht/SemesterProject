package factories;

import config.Config;
import dbmanager.DBManager;
import dbmanager.IoTDBManager;
import dbmanager.QuestDBManager;
import ingestion.Injection;
import ingestion.IoTDBInjection;
import ingestion.QuestDBInjection;
import org.springframework.stereotype.Component;


public class InjectionFactory {

    public Injection createInjection(Config config) {
        switch (config.getDbType()) {
            case QUESTDB:
                return new QuestDBInjection(config);
            case IOTDB:
                return new IoTDBInjection(config);
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + config.getDbType());
        }
    }
}
