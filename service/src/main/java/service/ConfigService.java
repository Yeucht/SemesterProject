package service;

import config.Config;
import dbmanager.DBManager;
import factories.DBManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


public class ConfigService {
    private Config config;
    private DBManagerService dbManagerService;
    private InjectionService injectionService;

    public ConfigService(Config config, DBManagerService dbManagerService, InjectionService injectionService) {
        this.config = config;
        this.dbManagerService = dbManagerService;
        this.injectionService = injectionService;
    }

    public ConfigService() {}

    // Get the current configuration
    public Config getConfig() {
        return config;
    }


    // Update the current configuration (full replace), used via PUT
    public void updateConfig(Config newConfig) {
        this.config = newConfig;
        dbManagerService.update(newConfig);
        injectionService.update(newConfig);
    }

    // Reset configuration to default values
    public void resetConfigToDefault() {
        this.config = new Config();
    }
}