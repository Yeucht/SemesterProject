package infrastructure;

import config.Config;
import factories.DBManagerFactory;
import factories.InjectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import service.ConfigService;
import service.DBManagerService;
import service.InjectionService;
import service.SimulationService;

@Configuration
public class DomainConfig {

    @Bean
    public Config config(){
        return new Config();
    }

    @Bean
    public ConfigService configService(Config config, DBManagerService dbManagerService, InjectionService injectionService) {
        return new ConfigService(config, dbManagerService, injectionService);
    }

    @Bean
    public DBManagerService dbManagerService(Config config) {
        return new DBManagerService(config);
    }

    @Bean
    public InjectionService injectionService(Config config) {
        return new InjectionService(config);
    }

    @Bean
    public SimulationService simulationService(ConfigService configService) {
        return new SimulationService(configService);
    }
}
