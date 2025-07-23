package ingestion;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import config.Config;

import java.time.Instant;

public class InfluxDBInjection extends Injection {
    private static final String INFLUX_URL = "http://localhost:8086";
    private static final String TOKEN = "_h_kvD14Rbr7nxhfM1hm4P1S1liKtQq6y2cK8k32cGwaj_V_puoB1cewvnp6KFEJfUOyaA2P4feO-HwG4134yg=="; // Set InfluxDB token here
    private static final String ORG = "UNIL";
    private static final String BUCKET = "smart_meters";

    public InfluxDBInjection(Config config){
        super(config);
    }

    @Override
    public void insertData(DataPacket data) {}
/*
        try (InfluxDBClient client = InfluxDBClientFactory.create(INFLUX_URL, TOKEN.toCharArray(), ORG, BUCKET)) {
            var writeApi = client.getWriteApiBlocking();
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < recordCount; i++) {
                String[] data = generateMeterData().split(",");
                Point point = Point.measurement("smart_meter")
                        .addTag("meter_id", data[1])
                        .addField("power_consumption", Double.parseDouble(data[2]))
                        .addField("voltage", Double.parseDouble(data[3]))
                        .addField("current", Double.parseDouble(data[4]))
                        .time(Instant.parse(data[0]), WritePrecision.MS);

                writeApi.writePoint(point);
            }

            System.out.println("InfluxDB Ingestion Time: " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }*/
}