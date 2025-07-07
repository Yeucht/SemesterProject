package service;

import config.Config;
import ingestion.DataPacket;
import ingestion.Injection;
import factories.InjectionFactory;
import ingestion.QuestDBInjection;
import org.springframework.stereotype.Service;

@Service
public class InjectionService {

    private final ConfigService configService;
    private final InjectionFactory injectionFactory;

    public InjectionService(ConfigService configService, InjectionFactory injectionFactory) {
        this.configService = configService;
        this.injectionFactory = injectionFactory;
    }

    public void sendDataToDataBase(DataPacket data) {
        Config config = configService.getConfig();
        Injection injection = injectionFactory.createInjection(config);

        if (injection instanceof QuestDBInjection) {
            ((QuestDBInjection) injection).insertData(data);
        } else {
            throw new UnsupportedOperationException("Injection type not supported.");
        }
    }
}
