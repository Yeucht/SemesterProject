import config.Config;
import dbmanager.QuestDBManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.ConfigService;
import service.DBManagerService;
import service.InjectionService;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigServiceTest {
    private DBManagerService dbManager;
    private InjectionService injectionService;
    private ConfigService service;

    @BeforeEach
    void setUp() {
        dbManager = new DBManagerService(new Config());
        injectionService = new InjectionService(new Config());
        service = new ConfigService(new Config(), dbManager, injectionService);
    }

    @Test
    void updateConfigReplacesConfig() {
        Config newConfig = new Config();
        newConfig.setDbType(Config.DatabaseType.QUESTDB);
        service.updateConfig(newConfig);
        assertEquals(newConfig, service.getConfig());
    }

    @Test
    void resetConfigCreatesNewInstance() {
        Config original = service.getConfig();
        service.resetConfigToDefault();
        Config reset = service.getConfig();
        assertNotSame(original, reset);
    }
}
