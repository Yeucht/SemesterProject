package service;

import config.SimulationConfig;
import ingestion.DataPacket;
import ingestion.Injection;
import factories.InjectionFactory;


public class InjectionService {

    private SimulationConfig config;
    private final InjectionFactory injectionFactory = new InjectionFactory();
    private Injection injection;
    private Counter counter;

    public InjectionService(SimulationConfig config) {
        this.config = config;
        this.injection = injectionFactory.createInjection(config);
    }

    public InjectionService(SimulationConfig config, Counter counter) {
        this.config = config;
        this.injection = injectionFactory.createInjection(config);
        this.counter = counter;
    }

    public void sendDataToDataBase(DataPacket data){
        try {
            injection.insertData(data);
            counter.updateCounter(data.getMeteringData().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(SimulationConfig config){
        this.config = config;
        this.injection = injectionFactory.createInjection(config);
    }
}
