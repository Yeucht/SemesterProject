package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import jakarta.transaction.Transactional;
import simulation.*;


public class SimulationService {
    // Base URL for Flask simulation service
    private static final String FLASK_BASE_URL = "http://sp-simulation:8000";

    private final ConfigService configService;
    private final MetricsService metricsService;

    public SimulationService(ConfigService configService, MetricsService metricsService) {
        this.configService = configService;
        this.metricsService = metricsService;
    }

    /**
     * Starts the external simulation by invoking the Flask '/start' endpoint.
     */
    public int startSimulation() throws Exception {
        //metricsService.startSimulation();
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
                System.out.println("Hello" + line);
            }
        }
        con.disconnect();
        return responseCode;
    }

    /**
     * Stops the external simulation by invoking the Flask '/stop' endpoint.
     */
    @Transactional
    public int stopSimulation() throws Exception {
        //metricsService.stopSimulation();
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