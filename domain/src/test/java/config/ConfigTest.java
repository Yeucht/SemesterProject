import config.Config;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {
    @Test
    void defaultValuesAreCorrect() {
        Config config = new Config();
        assertEquals(Config.DatabaseType.QUESTDB, config.getDbType());
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
        Config config = new Config(
                Config.DatabaseType.IOTDB,
                true,
                42L,
                7,
                0.5f,
                "url",
                1,
                true,
                2,
                0.1f
        );

        assertEquals(Config.DatabaseType.IOTDB, config.getDbType());
        assertTrue(config.getClearTablesFlag());
        assertEquals(42L, config.getRetentionWindowMillis());
        assertEquals(7, config.getRate());
        assertEquals(0.5f, config.getRateRandomess());
        assertEquals("url", config.getUrl());
        assertEquals(1, config.getNbrSmartMeters());
        assertTrue(config.isBatch());
        assertEquals(2, config.getBatchSize());
        assertEquals(0.1f, config.getBatchRandomness());

        config.setDbType(Config.DatabaseType.QUESTDB);
        config.setClearTablesFlag(false);
        config.setRetentionWindowMillis(100L);
        config.setRate(8);
        config.setRateRandomess(0.9f);
        config.setUrl("other");
        config.setNbrSmartMeters(5);
        config.setBatch(false);
        config.setBatchSize(3);
        config.setBatchRandomness(0.2f);

        assertEquals(Config.DatabaseType.QUESTDB, config.getDbType());
        assertFalse(config.getClearTablesFlag());
        assertEquals(100L, config.getRetentionWindowMillis());
        assertEquals(8, config.getRate());
        assertEquals(0.9f, config.getRateRandomess());
        assertEquals("other", config.getUrl());
        assertEquals(5, config.getNbrSmartMeters());
        assertFalse(config.isBatch());
        assertEquals(3, config.getBatchSize());
        assertEquals(0.2f, config.getBatchRandomness());
    }
}
