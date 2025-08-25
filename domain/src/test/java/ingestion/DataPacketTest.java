import ingestion.DataPacket;
import ingestion.MeterData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataPacketTest {
    @Test
    void constructorAndGetters() {
        MeterData meterData = new MeterData();
        ArrayList<MeterData> List = new ArrayList<MeterData>();
        List.add(meterData);
        DataPacket p = new DataPacket(
                "user",
                "serial",
                "digest",
                1L,
                2,
                true,
                false,
                "conn",
                "file",
                "mun",
                "owner",
                "type",
                List
        );

        assertEquals("user", p.getAuthUser());
        assertEquals("serial", p.getAuthSerialNumber());
        assertEquals("digest", p.getAuthDigest());
        assertEquals(1L, p.getReceivedTime());
        assertEquals(2, p.getConnectionCause());
        assertTrue(p.isAuthenticated());
        assertFalse(p.isMessageBrokerJob());
        assertEquals("conn", p.getArchiverConnectionId());
        assertEquals("file", p.getCacheFileName());
        assertEquals("mun", p.getMasterUnitNumber());
        assertEquals("owner", p.getMasterUnitOwnerId());
        assertEquals("type", p.getMasterUnitType());
        assertSame(List, p.getMeteringData());
    }

    @Test
    void setters() {
        DataPacket p = new DataPacket();
        MeterData meterData = new MeterData();
        ArrayList<MeterData> List = new ArrayList<MeterData>();
        List.add(meterData);
        p.setAuthUser("u");
        p.setAuthSerialNumber("s");
        p.setAuthDigest("d");
        p.setReceivedTime(5L);
        p.setConnectionCause(1);
        p.setAuthenticated(true);
        p.setMessageBrokerJob(true);
        p.setArchiverConnectionId("a");
        p.setCacheFileName("c");
        p.setMasterUnitNumber("mu");
        p.setMasterUnitOwnerId("mo");
        p.setMasterUnitType("mt");
        p.setMeteringData(List);

        assertEquals("u", p.getAuthUser());
        assertEquals("s", p.getAuthSerialNumber());
        assertEquals("d", p.getAuthDigest());
        assertEquals(5L, p.getReceivedTime());
        assertEquals(1, p.getConnectionCause());
        assertTrue(p.isAuthenticated());
        assertTrue(p.isMessageBrokerJob());
        assertEquals("a", p.getArchiverConnectionId());
        assertEquals("c", p.getCacheFileName());
        assertEquals("mu", p.getMasterUnitNumber());
        assertEquals("mo", p.getMasterUnitOwnerId());
        assertEquals("mt", p.getMasterUnitType());
        assertNotNull(p.getMeteringData());
    }
}
