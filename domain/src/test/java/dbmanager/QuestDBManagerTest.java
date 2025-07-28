import config.Config;
import dbmanager.QuestDBManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuestDBManagerTest {
    @Test
    void clearTablesDisabledReturnsFalse() {
        Config c = new Config();
        c.setClearTablesFlag(false);
        QuestDBManager manager = new QuestDBManager(c);
        assertFalse(manager.clearTables());
    }
}
