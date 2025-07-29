package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.ConfigService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    public Config getConfig() {
        return configService.getConfig();
    }

    // Set the configuration using POST (can be considered as a default/initial configuration)
    @PostMapping
    public void createConfig(@RequestBody Config newConfig) throws IOException {
        try {
            System.out.println("Received config: " + newConfig);
            configService.updateConfig(newConfig);

            // Envoi de la nouvelle config au Flask
            URL url = new URL("http://sp-simulation:8000/config");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Convertir l'objet Java en JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(newConfig);

            // Écrire le corps de la requête
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Lire la réponse (facultatif)
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Flask response: " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping
    public void updateConfig(@RequestBody Config newConfig) throws IOException {
        try {
            System.out.println("Received config: " + newConfig);
            configService.updateConfig(newConfig);

            // Envoi de la nouvelle config au Flask
            URL url = new URL("http://sp-simulation:8000/config");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Convertir l'objet Java en JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(newConfig);

            // Écrire le corps de la requête
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Lire la réponse (facultatif)
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Flask response: " + response);
            }

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