import config.SimulationConfig;
import dbmanager.DBManager;
import dbmanager.QuestDBManager;
import factories.DBManagerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DBManagerFactoryTest {
    @Test
    void createQuestDBManager() {
        SimulationConfig config = new SimulationConfig();
        config.setDbType(SimulationConfig.DatabaseType.QUESTDB);
        DBManager manager = DBManagerFactory.createManager(config);
        assertTrue(manager instanceof QuestDBManager);
    }

    @Test
    void invalidDatabaseThrows() {
        SimulationConfig config = new SimulationConfig();
        assertThrows(NullPointerException.class, () -> {
            config.setDbType(null);
            DBManagerFactory.createManager(config);
        });
    }
}
