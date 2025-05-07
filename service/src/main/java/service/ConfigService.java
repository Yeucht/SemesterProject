package service;

import config.Config;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {
    private Config config;

    // Default constructor initializes to a default configuration
    public ConfigService() {
        this.config = new Config(Config.DatabaseType.QUESTDB, false, 86400000); // Example default
    }

    // Get the current configuration
    public Config getConfig() {
        return config;
    }

    // Create a new config (initial configuration, used via POST)
    public void createConfig(Config newConfig) {
        this.config = newConfig;
    }

    // Update the current configuration (full replace), used via PUT
    public void updateConfig(Config newConfig) {
        this.config = newConfig;
    }

    // Reset configuration to default values
    public void resetConfigToDefault() {
        this.config = new Config(Config.DatabaseType.QUESTDB, false, 86400000); // Example default reset
    }
}