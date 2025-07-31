package service;

import config.SimulationConfig;
import ingestion.DataPacket;
import ingestion.Injection;
import factories.InjectionFactory;


public class InjectionService {

    private SimulationConfig config;
    private final InjectionFactory injectionFactory = new InjectionFactory();
    private Injection injection;

    public InjectionService(SimulationConfig config) {
        this.config = config;
        this.injection = injectionFactory.createInjection(config);
    }

    public void sendDataToDataBase(DataPacket data){

        try {
            injection.insertData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(SimulationConfig config){
        this.config = config;
        this.injection = injectionFactory.createInjection(config);
    }
}
