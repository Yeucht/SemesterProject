import config.SimulationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ConfigService;
import service.DBManagerService;
import service.FlaskClient;
import service.InjectionService;

import static org.junit.jupiter.api.Assertions.*;
/*
public class SimulationConfigServiceTest {
    private DBManagerService dbManager;
    private InjectionService injectionService;
    private ConfigService service;

    @BeforeEach
    void setUp() {
        dbManager = new DBManagerService(new SimulationConfig());
        injectionService = new InjectionService(new SimulationConfig());
        flaskClient = new FlaskClient();
        service = new ConfigService(new SimulationConfig(), dbManager, injectionService,);
    }


    @Test
    void resetConfigCreatesNewInstance() {
        SimulationConfig original = service.getConfig();
        service.resetConfigToDefault();
        SimulationConfig reset = service.getConfig();
        assertNotSame(original, reset);
    }
}
*/