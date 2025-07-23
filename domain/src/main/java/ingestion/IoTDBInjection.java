package ingestion;

import config.Config;
import dbmanager.IoTDBManager;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
//import org.apache.iotdb.tsfile.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IoTDBInjection extends Injection {

    private static final String IOTDB_PATH = "root.smart_meter"; // Ensure consistency with IoTDBManager
    private final IoTDBManager ioTDBManager;

    public IoTDBInjection(Config config) {
        super(config);
        this.ioTDBManager = new IoTDBManager(config);
    }


    @Override
    public void insertData(DataPacket data) {

        SessionPool sessionPool = ioTDBManager.getSessionPool();
        long startTime = System.currentTimeMillis();}
/*
        try {
            for (int i = 0; i < recordCount; i++) {
                String[] data = generateMeterData().split(",");

                long timestamp = Long.parseLong(data[0]);
                String meterId = data[1];
                double powerConsumption = Double.parseDouble(data[2]);
                double voltage = Double.parseDouble(data[3]);
                double current = Double.parseDouble(data[4]);

                String deviceId = IOTDB_PATH + "." + meterId;
                List<String> measurements = Arrays.asList("powerConsumption", "voltage", "current");
                List<String> values = Arrays.asList(
                        String.valueOf(powerConsumption),
                        String.valueOf(voltage),
                        String.valueOf(current)
                );
                sessionPool.insertRecord(deviceId, timestamp, measurements, values);
            }
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            System.out.println("Error during IoTDB data insertion:");
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("IoTDB Ingestion Time: " + (endTime - startTime) + " ms");
    }
*/

}