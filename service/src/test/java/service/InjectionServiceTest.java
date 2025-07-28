import config.Config;
import ingestion.DataPacket;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.InjectionService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class InjectionServiceTest {
    @Test
    void updateChangesInjection() throws Exception {
        Config cfg1 = new Config();
        cfg1.setDbType(Config.DatabaseType.QUESTDB);
        InjectionService service = new InjectionService(cfg1);
        Field field = InjectionService.class.getDeclaredField("injection");
        field.setAccessible(true);
        Object inj1 = field.get(service);

        Config cfg2 = new Config();
        cfg2.setDbType(Config.DatabaseType.QUESTDB);
        service.update(cfg2);
        Object inj2 = field.get(service);
        assertNotSame(inj1, inj2);
    }

}
