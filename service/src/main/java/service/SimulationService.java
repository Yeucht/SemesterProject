package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dbmanager.DBManager;
import jakarta.transaction.Transactional;
import simulation.*;


public class SimulationService {
    // Base URL for Flask simulation service
    private static final String FLASK_BASE_URL = "http://sp-simulation:8000";

    private final ConfigService configService;
    private final MetricsService metricsService;
    private final Counter counter;
    private final DBManagerService dbManagerService;
    private boolean running = false;

    public SimulationService(ConfigService configService, MetricsService metricsService, Counter counter, DBManagerService dbManager) {
        this.configService = configService;
        this.metricsService = metricsService;
        this.counter = counter;
        this.dbManagerService = dbManager;
    }

    /**
     * Starts the external simulation by invoking the Flask '/start' endpoint.
     */
    public int startSimulation() throws Exception {
        metricsService.startRecording();
        if (configService.getConfig().getClearTablesFlag()){
            dbManagerService.clearTables();
            dbManagerService.updateCounter(0);
        }else {
            dbManagerService.updateCounter();
        }

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
        if (responseCode == 200) {
            running = true;
        }
        return responseCode;
    }

    /**
     * Stops the external simulation by invoking the Flask '/stop' endpoint.
     */
    @Transactional
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
        if (responseCode == 200) {
            running = false;
        }
        con.disconnect();
        metricsService.stopRecording();
        return responseCode;
    }


    public boolean isRunning() {
        return running;
    }
}