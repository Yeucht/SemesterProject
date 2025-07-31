import config.SimulationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ConfigService;
import service.DBManagerService;
import service.InjectionService;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationConfigServiceTest {
    private DBManagerService dbManager;
    private InjectionService injectionService;
    private ConfigService service;

    @BeforeEach
    void setUp() {
        dbManager = new DBManagerService(new SimulationConfig());
        injectionService = new InjectionService(new SimulationConfig());
        service = new ConfigService(new SimulationConfig(), dbManager, injectionService);
    }

    @Test
    void updateConfigReplacesConfig() {
        SimulationConfig newConfig = new SimulationConfig();
        newConfig.setDbType(SimulationConfig.DatabaseType.QUESTDB);
        service.updateConfig(newConfig);
        assertEquals(newConfig, service.getConfig());
    }

    @Test
    void resetConfigCreatesNewInstance() {
        SimulationConfig original = service.getConfig();
        service.resetConfigToDefault();
        SimulationConfig reset = service.getConfig();
        assertNotSame(original, reset);
    }
}
