import config.Config;
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
        dbManager = Mockito.mock(DBManagerService.class);
        injectionService = Mockito.mock(InjectionService.class);
        service = new ConfigService(new Config(), dbManager, injectionService);
    }

    @Test
    void updateConfigReplacesConfig() {
        Config newConfig = new Config();
        newConfig.setDbType(Config.DatabaseType.QUESTDB);
        service.updateConfig(newConfig);
        assertEquals(newConfig, service.getConfig());
        Mockito.verify(dbManager).update(newConfig);
        Mockito.verify(injectionService).update(newConfig);
    }

    @Test
    void resetConfigCreatesNewInstance() {
        Config original = service.getConfig();
        service.resetConfigToDefault();
        Config reset = service.getConfig();
        assertNotSame(original, reset);
    }
}
