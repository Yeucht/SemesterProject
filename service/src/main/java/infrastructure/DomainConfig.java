package infrastructure;

import config.Config;
import factories.DBManagerFactory;
import factories.InjectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import service.ConfigService;
import service.DBManagerService;
import service.InjectionService;

@Configuration
public class DomainConfig {

    @Bean
    public ConfigService configService() {
        // Initial default config
        return new ConfigService(new Config(Config.DatabaseType.QUESTDB, false, 86400000));
    }

    @Bean
    public DBManagerFactory dbManagerFactory() {
        return new DBManagerFactory();
    }

    @Bean
    public InjectionFactory injectionFactory() {
        return new InjectionFactory();
    }

    @Bean
    public DBManagerService dbManagerService(ConfigService configService, DBManagerFactory dbManagerFactory) {
        return new DBManagerService(configService, dbManagerFactory);
    }

    @Bean
    public InjectionService injectionService(ConfigService configService, InjectionFactory injectionFactory) {
        return new InjectionService(configService, injectionFactory);
    }
}
