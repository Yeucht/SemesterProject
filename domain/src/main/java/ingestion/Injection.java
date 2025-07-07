package ingestion;

import io.questdb.client.Sender;

import java.time.Instant;
import java.util.Random;


public abstract class Injection {
    protected static final Random RANDOM = new Random();
    protected boolean ClearTables = false;

    Injection(boolean CleanTable){
        this.ClearTables = CleanTable;
    }

    // Method to be overridden for inserting records
    public abstract void insertData(DataPacket data);

    // Generates a single meter data record
    protected String generateMeterData() {
        Instant timestamp = Instant.now();
        int meterId = RANDOM.nextInt(1000);
        double powerConsumption = 1.5 + RANDOM.nextDouble() * 3;
        double voltage = 220 + RANDOM.nextDouble() * 10;
        double current = 5 + RANDOM.nextDouble() * 2;
        return String.format("%s,%d,%.3f,%.2f,%.2f", timestamp, meterId, powerConsumption, voltage, current);
    }

    protected boolean clearTables(){return ClearTables;};
}
