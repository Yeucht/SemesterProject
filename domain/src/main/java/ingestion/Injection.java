package ingestion;

import config.Config;
import io.questdb.client.Sender;

import java.time.Instant;
import java.util.Random;


public abstract class Injection {
    protected Config config = new Config();

    Injection(Config config){
        this.config = config;
    }

    // Method to be overridden for inserting records
    public abstract void insertData(DataPacket data);

}
