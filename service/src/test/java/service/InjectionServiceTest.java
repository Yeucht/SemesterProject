import config.SimulationConfig;
import org.junit.jupiter.api.Test;
import service.InjectionService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class InjectionServiceTest {
    @Test
    void updateChangesInjection() throws Exception {
        SimulationConfig cfg1 = new SimulationConfig();
        cfg1.setDbType(SimulationConfig.DatabaseType.QUESTDB);
        InjectionService service = new InjectionService(cfg1);
        Field field = InjectionService.class.getDeclaredField("injection");
        field.setAccessible(true);
        Object inj1 = field.get(service);

        SimulationConfig cfg2 = new SimulationConfig();
        cfg2.setDbType(SimulationConfig.DatabaseType.QUESTDB);
        service.update(cfg2);
        Object inj2 = field.get(service);
        assertNotSame(inj1, inj2);
    }

}
