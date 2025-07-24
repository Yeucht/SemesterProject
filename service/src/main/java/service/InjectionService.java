package service;

import config.Config;
import ingestion.DataPacket;
import ingestion.Injection;
import factories.InjectionFactory;
import ingestion.IoTDBInjection;
import ingestion.QuestDBInjection;
import org.springframework.stereotype.Service;


public class InjectionService {

    private Config config;
    private final InjectionFactory injectionFactory = new InjectionFactory();
    private Injection injection;

    public InjectionService(Config config) {
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

    public void update(Config config){
        this.config = config;
        this.injection = injectionFactory.createInjection(config);
    }
}
