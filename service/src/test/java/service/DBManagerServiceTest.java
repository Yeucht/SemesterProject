import config.Config;
import dbmanager.DBManager;
import org.junit.jupiter.api.Test;
import service.DBManagerService;

import static org.junit.jupiter.api.Assertions.*;

public class DBManagerServiceTest {
    @Test
    void updateCreatesNewManager() {
        Config cfg = new Config();
        cfg.setDbType(Config.DatabaseType.QUESTDB);
        DBManagerService service = new DBManagerService(cfg);
        DBManager first = service.getDbManager();
        Config newCfg = new Config();
        newCfg.setDbType(Config.DatabaseType.QUESTDB);
        service.update(newCfg);
        DBManager second = service.getDbManager();
        assertNotSame(first, second);
        assertEquals(newCfg, service.getConfig());
    }
}
