import config.SimulationConfig;
import dbmanager.DBManager;
import org.junit.jupiter.api.Test;
import service.DBManagerService;

import static org.junit.jupiter.api.Assertions.*;

public class DBManagerServiceTest {
    @Test
    void updateCreatesNewManager() {
        SimulationConfig cfg = new SimulationConfig();
        cfg.setDbType(SimulationConfig.DatabaseType.QUESTDB);
        DBManagerService service = new DBManagerService(cfg);
        DBManager first = service.getDbManager();
        SimulationConfig newCfg = new SimulationConfig();
        newCfg.setDbType(SimulationConfig.DatabaseType.QUESTDB);
        service.update(newCfg);
        DBManager second = service.getDbManager();
        assertNotSame(first, second);
        assertEquals(newCfg, service.getConfig());
    }
}
