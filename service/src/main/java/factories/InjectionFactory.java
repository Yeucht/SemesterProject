package factories;

import config.Config;
import dbmanager.DBManager;
import dbmanager.QuestDBManager;
import ingestion.Injection;
import ingestion.QuestDBInjection;
import org.springframework.stereotype.Component;

@Component
public class InjectionFactory {

    public Injection createInjection(Config config) {
        switch (config.getDbType()) {
            case QUESTDB:
                DBManager questDbManager = new QuestDBManager(config.getClearTablesFlag());
                return new QuestDBInjection(config.getClearTablesFlag(), questDbManager);
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + config.getDbType());
        }
    }
}
