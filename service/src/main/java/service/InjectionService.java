package service;

import config.SimulationConfig;
import dbmanager.DBManager;
import ingestion.DataPacket;
import ingestion.Injection;
import factories.InjectionFactory;


public class InjectionService {

    private SimulationConfig config;
    private final InjectionFactory injectionFactory = new InjectionFactory();
    private Injection injection;
    private Counter counter;

    //debug
    DBManagerService dbManagerService;

    public InjectionService(SimulationConfig config) {
        this.config = config;
        this.injection = injectionFactory.createInjection(config);
    }

    public InjectionService(SimulationConfig config, Counter counter, DBManagerService dbManagerService) {
        this.config = config;
        this.injection = injectionFactory.createInjection(config);
        this.counter = counter;
        this.dbManagerService = dbManagerService;
    }

    public void sendDataToDataBase(DataPacket data){
        try {
            injection.insertData(data);
            counter.updateCounter(data.getMeterData().getPayload().size());
            System.out.println("added : " + data.getMeterData().getPayload() + " with counter = " + counter.getCounter());
            System.out.println("Supposed to be : " + dbManagerService.getDbManager().getRowCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(SimulationConfig config){
        this.config = config;
        this.injection = injectionFactory.createInjection(config);
    }
}
