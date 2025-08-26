package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.SimulationConfig;
import org.springframework.web.bind.annotation.*;
import service.ConfigService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "http://localhost:7000")
@RestController
@RequestMapping("/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    // Get the current configuration
    @GetMapping
    public SimulationConfig getConfig() {
        return configService.getConfig();
    }

    // Set the configuration using POST (can be considered as a default/initial configuration)
    @PostMapping
    public ResponseEntity<?> createConfig(@RequestBody SimulationConfig newConfig) {
        System.out.println("Received config: " + newConfig);
        return configService.applyAndPushConfig(newConfig);
    }

    @PutMapping
    public ResponseEntity<?> updateConfig(@RequestBody SimulationConfig newConfig) {
        System.out.println("Received config: " + newConfig);
        return configService.applyAndPushConfig(newConfig);
    }

    // Reset configuration to default values
    @DeleteMapping
    public void resetConfig() {
        configService.resetConfigToDefault();
    }
}