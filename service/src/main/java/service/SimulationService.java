package service;

import org.springframework.stereotype.Service;
import service.ConfigService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SimulationService {
    // Base URL for Flask simulation service
    private static final String FLASK_BASE_URL = "http://sp-simulation:8000";

    private final ConfigService configService;

    public SimulationService(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Starts the external simulation by invoking the Flask '/start' endpoint.
     */
    public int startSimulation() throws Exception {
        String target = FLASK_BASE_URL + "/start";
        URL url = new URL(target);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int responseCode = con.getResponseCode();
        // Optionally read response body
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }
        con.disconnect();
        return responseCode;
    }

    /**
     * Stops the external simulation by invoking the Flask '/stop' endpoint.
     */
    public int stopSimulation() throws Exception {
        String target = FLASK_BASE_URL + "/stop";
        URL url = new URL(target);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int responseCode = con.getResponseCode();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }
        con.disconnect();
        return responseCode;
    }
}