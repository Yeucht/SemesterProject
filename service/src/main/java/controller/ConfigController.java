package controller;

import config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.ConfigService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    // Get the current configuration
    @GetMapping
    public Config getConfig() {
        return configService.getConfig();
    }

    // Set the configuration using POST (can be considered as a default/initial configuration)
    @PostMapping
    public void createConfig(@RequestBody Config newConfig) throws IOException {
        try {
            System.out.println("Received config: " + newConfig);
            configService.updateConfig(newConfig);
            URL url = new URL("http://sp-simulation:8000/config");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Reset configuration to default values
    @DeleteMapping
    public void resetConfig() {
        configService.resetConfigToDefault();
    }
}