package service;

import config.SimulationConfig;


public class ConfigService {
    private SimulationConfig config;
    private DBManagerService dbManagerService;
    private InjectionService injectionService;

    public ConfigService(SimulationConfig config, DBManagerService dbManagerService, InjectionService injectionService) {
        this.config = config;
        this.dbManagerService = dbManagerService;
        this.injectionService = injectionService;
    }

    public ConfigService() {}

    public SimulationConfig getConfig() {
        return config;
    }

    public void updateConfig(SimulationConfig newConfig) {
        this.config = newConfig;
        dbManagerService.update(newConfig);
        injectionService.update(newConfig);
        dbManagerService.updateCounter();
    }

    public void resetConfigToDefault() {
        this.config = new SimulationConfig();
    }
}