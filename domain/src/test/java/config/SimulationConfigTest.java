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
        assertEquals(3, config.getRate());
        assertEquals(0.8f, config.getRateRandomess());
        assertEquals("http://sp-service:8080/api/injection/data", config.getUrl());
        assertEquals(5000, config.getNbrSmartMeters());
        assertFalse(config.isBatch());
        assertEquals(10, config.getBatchSize());
        assertEquals(0.2f, config.getBatchRandomness());
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
        assertEquals(7, config.getRate());
        assertEquals(0.5f, config.getRateRandomess());
        assertEquals("url", config.getUrl());
        assertEquals(1, config.getNbrSmartMeters());
        assertTrue(config.isBatch());
        assertEquals(2, config.getBatchSize());
        assertEquals(0.1f, config.getBatchRandomness());
        assertEquals(true, config.getMdmsBatch());
        assertEquals(10, config.getMdmsBatchSize());

        config.setDbType(SimulationConfig.DatabaseType.QUESTDB);
        config.setClearTablesFlag(false);
        config.setRetentionWindowMillis(100L);
        config.setRate(8);
        config.setRateRandomess(0.9f);
        config.setUrl("other");
        config.setNbrSmartMeters(5);
        config.setBatch(false);
        config.setBatchSize(3);
        config.setBatchRandomness(0.2f);
        config.setMdmsBatch(false);
        config.setMdmsBatchSize(20);

        assertEquals(SimulationConfig.DatabaseType.QUESTDB, config.getDbType());
        assertFalse(config.getClearTablesFlag());
        assertEquals(100L, config.getRetentionWindowMillis());
        assertEquals(8, config.getRate());
        assertEquals(0.9f, config.getRateRandomess());
        assertEquals("other", config.getUrl());
        assertEquals(5, config.getNbrSmartMeters());
        assertFalse(config.isBatch());
        assertEquals(3, config.getBatchSize());
        assertEquals(0.2f, config.getBatchRandomness());
        assertEquals(false, config.getMdmsBatch());
        assertEquals(20, config.getMdmsBatchSize());
    }
}
