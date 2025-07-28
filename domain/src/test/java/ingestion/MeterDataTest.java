import ingestion.MeterData;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class MeterDataTest {
    @Test
    void constructorAndGetters() {
        MeterData data = new MeterData(1,2,3,"addr", Arrays.asList(4,5));
        assertEquals(1, data.getSequence());
        assertEquals(2, data.getStatus());
        assertEquals(3, data.getVersion());
        assertEquals("addr", data.getAddress());
        assertEquals(Arrays.asList(4,5), data.getPayload());
    }

    @Test
    void setters() {
        MeterData data = new MeterData();
        data.setSequence(7);
        data.setStatus(8);
        data.setVersion(9);
        data.setAddress("x");
        data.setPayload(Collections.singletonList(1));

        assertEquals(7, data.getSequence());
        assertEquals(8, data.getStatus());
        assertEquals(9, data.getVersion());
        assertEquals("x", data.getAddress());
        assertEquals(Collections.singletonList(1), data.getPayload());
    }
}
