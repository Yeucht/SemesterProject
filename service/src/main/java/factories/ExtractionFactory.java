package factories;

import config.SimulationConfig;
import extraction.Extraction;
import extraction.IoTDBExtraction;
import extraction.QuestDBExtraction;

public class ExtractionFactory {

    public ExtractionFactory(){}

    public Extraction createExtraction(SimulationConfig config) {
        switch (config.getDbType()) {
            case QUESTDB:
                return new QuestDBExtraction(config);
            case IOTDB:
                return new IoTDBExtraction(config);
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + config.getDbType());
        }
    }
}
