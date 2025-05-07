package controller;

import config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.ConfigService;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private final ConfigService configService;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    // Get the current configuration
    @GetMapping
    public Config getConfig() {
        return configService.getConfig();
    }

    // Update the configuration (full replace)
    @PutMapping
    public void updateConfig(@RequestBody Config newConfig) {
        configService.updateConfig(newConfig);
    }

    // Set the configuration using POST (can be considered as a default/initial configuration)
    @PostMapping
    public void createConfig(@RequestBody Config newConfig) {
        configService.createConfig(newConfig); // Fixed missing semicolon
    }

    // Reset configuration to default values
    @DeleteMapping
    public void resetConfig() {
        configService.resetConfigToDefault();
    }
}