import config.SimulationConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationConfigTest {
    @Test
    void defaultValuesAreCorrect() {
        SimulationConfig config = new SimulationConfig();
        assertEquals(SimulationConfig.DatabaseType.QUESTDB, config.getDbType());
        assertFalse(config.getClearTablesFlag());
        assertEquals(1000000000L, config.getRetentionWindowMillis());
        assertEquals(3, config.getMeterRate());
        assertEquals(0.8f, config.getMeterRateRandomess());
        assertEquals("http://sp-service:8080/api/injection/data", config.getUrl());
        assertEquals(5000, config.getNbrSmartMeters());
        assertFalse(config.isMeterPayloadBatch());
        assertEquals(10, config.getMeterPayloadBatchSize());
        assertEquals(0.2f, config.getMeterPayloadBatchRandomness());
    }

    @Test
    void parameterizedConstructorAndSetters() {
        SimulationConfig config = new SimulationConfig(
                SimulationConfig.DatabaseType.IOTDB,
                true,
                42L,
                7,
                0.5f,
                "url",
                1,
                true,
                2,
                0.1f,
                true,
                10
        );

        assertEquals(SimulationConfig.DatabaseType.IOTDB, config.getDbType());
        assertTrue(config.getClearTablesFlag());
        assertEquals(42L, config.getRetentionWindowMillis());
        assertEquals(7, config.getMeterRate());
        assertEquals(0.5f, config.getMeterRateRandomess());
        assertEquals("url", config.getUrl());
        assertEquals(1, config.getNbrSmartMeters());
        assertTrue(config.isMeterPayloadBatch());
        assertEquals(2, config.getMeterPayloadBatchSize());
        assertEquals(0.1f, config.getMeterPayloadBatchRandomness());
        assertEquals(true, config.getMdmsBatch());
        assertEquals(10, config.getMdmsBatchSize());

        config.setDbType(SimulationConfig.DatabaseType.QUESTDB);
        config.setClearTablesFlag(false);
        config.setRetentionWindowMillis(100L);
        config.setMeterRate(8);
        config.setMeterRateRandomess(0.9f);
        config.setUrl("other");
        config.setNbrSmartMeters(5);
        config.setMeterPayloadBatch(false);
        config.setMeterPayloadBatchSize(3);
        config.setMeterPayloadBatchRandomness(0.2f);
        config.setMdmsBatch(false);
        config.setMdmsBatchSize(20);

        assertEquals(SimulationConfig.DatabaseType.QUESTDB, config.getDbType());
        assertFalse(config.getClearTablesFlag());
        assertEquals(100L, config.getRetentionWindowMillis());
        assertEquals(8, config.getMeterRate());
        assertEquals(0.9f, config.getMeterRateRandomess());
        assertEquals("other", config.getUrl());
        assertEquals(5, config.getNbrSmartMeters());
        assertFalse(config.isMeterPayloadBatch());
        assertEquals(3, config.getMeterPayloadBatchSize());
        assertEquals(0.2f, config.getMeterPayloadBatchRandomness());
        assertEquals(false, config.getMdmsBatch());
        assertEquals(20, config.getMdmsBatchSize());
    }
}
