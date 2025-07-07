package service;

import config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {
    private volatile Config config = new Config();

    public ConfigService(Config config) {
        this.config = config;
    }

    public ConfigService() {
        this.config = new Config(Config.DatabaseType.QUESTDB, false, 86400000); // Example default
    }

    // Get the current configuration
    public Config getConfig() {
        return config;
    }


    // Update the current configuration (full replace), used via PUT
    public void updateConfig(Config newConfig) {
        this.config = newConfig;
    }

    // Reset configuration to default values
    public void resetConfigToDefault() {
        this.config = new Config();
    }
}