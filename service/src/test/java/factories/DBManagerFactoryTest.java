import config.Config;
import dbmanager.DBManager;
import dbmanager.QuestDBManager;
import factories.DBManagerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DBManagerFactoryTest {
    @Test
    void createQuestDBManager() {
        Config config = new Config();
        config.setDbType(Config.DatabaseType.QUESTDB);
        DBManager manager = DBManagerFactory.createManager(config);
        assertTrue(manager instanceof QuestDBManager);
    }

    @Test
    void invalidDatabaseThrows() {
        Config config = new Config();
        assertThrows(NullPointerException.class, () -> {
            config.setDbType(null);
            DBManagerFactory.createManager(config);
        });
    }
}
