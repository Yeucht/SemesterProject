package ingestion;

import config.SimulationConfig;

import java.util.List;


public abstract class Injection {
    protected SimulationConfig config = new SimulationConfig();

    Injection(SimulationConfig config){
        this.config = config;
    }

    public abstract void insertData(List<DataPacket> data);

}
