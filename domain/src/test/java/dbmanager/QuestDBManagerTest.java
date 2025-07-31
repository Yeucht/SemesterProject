import config.SimulationConfig;
import dbmanager.QuestDBManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuestDBManagerTest {
    @Test
    void clearTablesDisabledReturnsFalse() {
        SimulationConfig c = new SimulationConfig();
        c.setClearTablesFlag(false);
        QuestDBManager manager = new QuestDBManager(c);
        assertFalse(manager.clearTables());
    }
}
