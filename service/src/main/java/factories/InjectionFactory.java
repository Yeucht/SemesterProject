package factories;

import config.SimulationConfig;
import ingestion.Injection;
import ingestion.IoTDBInjection;
import ingestion.QuestDBInjection;


public class InjectionFactory {

    public Injection createInjection(SimulationConfig config) {
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
