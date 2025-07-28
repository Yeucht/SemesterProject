import ingestion.QuestDBInjection;
import org.junit.jupiter.api.Test;
import simulation.Simulation;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {
    @Test
    void unknownDatabaseThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Simulation("unknown", 10, false));
    }

    @Test
    void questDbCreatesCorrectInjection() throws Exception {
        Simulation sim = new Simulation("questdb", 1, false);
        Field f = Simulation.class.getDeclaredField("dbInjection");
        f.setAccessible(true);
        Object inj = f.get(sim);
        assertTrue(inj instanceof QuestDBInjection);
    }
}
