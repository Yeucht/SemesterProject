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

    // Get the current configuration
    public SimulationConfig getConfig() {
        return config;
    }


    // Update the current configuration (full replace), used via PUT
    public void updateConfig(SimulationConfig newConfig) {
        this.config = newConfig;
        dbManagerService.update(newConfig);
        injectionService.update(newConfig);
    }

    // Reset configuration to default values
    public void resetConfigToDefault() {
        this.config = new SimulationConfig();
    }
}