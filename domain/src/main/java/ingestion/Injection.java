package ingestion;

import config.SimulationConfig;


public abstract class Injection {
    protected SimulationConfig config = new SimulationConfig();

    Injection(SimulationConfig config){
        this.config = config;
    }

    // Method to be overridden for inserting records
    public abstract void insertData(DataPacket data);

}
