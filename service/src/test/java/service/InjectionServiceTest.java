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

    @Test
    void sendDataDelegatesToInjection() {
        Config cfg = new Config();
        cfg.setDbType(Config.DatabaseType.QUESTDB);
        InjectionService service = new InjectionService(cfg);
        DataPacket packet = Mockito.mock(DataPacket.class);
        // replace injection with mock
        try {
            Field f = InjectionService.class.getDeclaredField("injection");
            f.setAccessible(true);
            ingestion.Injection mock = Mockito.mock(ingestion.Injection.class);
            f.set(service, mock);
            service.sendDataToDataBase(packet);
            Mockito.verify(mock).insertData(packet);
        } catch (Exception e) {
            fail(e);
        }
    }
}
