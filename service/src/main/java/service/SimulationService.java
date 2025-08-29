package service;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;


public class SimulationService {

    private static final String FLASK_BASE_URL = "http://sp-simulation:8000";

    private final ConfigService configService;
    private final MetricsService metricsService;
    private final Counter counter;
    private final DBManagerService dbManagerService;
    private boolean running = false;
    private FlaskClient flaskClient;

    public SimulationService(ConfigService configService, MetricsService metricsService, Counter counter, DBManagerService dbManager, FlaskClient flaskClient) {
        this.configService = configService;
        this.metricsService = metricsService;
        this.counter = counter;
        this.dbManagerService = dbManager;
        this.flaskClient = flaskClient;
    }

    /**
     * Starts the external simulation by invoking the Flask '/start' endpoint.
     */
    public int startSimulation() throws Exception {
        metricsService.startRecording();

        if (configService.getConfig().getClearTablesFlag()){
            dbManagerService.clearTables();
            dbManagerService.updateCounter(0);
        } else {
            dbManagerService.updateCounter();
        }

        try {
            ResponseEntity<String> resp = flaskClient.start();
            if (resp.getStatusCode().is2xxSuccessful()) {
                running = true;
            }
            return resp.getStatusCode().value();
        } catch (RestClientResponseException ex) {
            return ex.getRawStatusCode();
        }
    }


    /**
     * Stops the external simulation by invoking the Flask '/stop' endpoint.
     */
    @Transactional
    public int stopSimulation() throws Exception {
        try {
            ResponseEntity<String> resp = flaskClient.stop();
            if (resp.getStatusCode().is2xxSuccessful()) {
                running = false;
            }
            metricsService.stopRecording();
            return resp.getStatusCode().value();
        } catch (RestClientResponseException ex) {
            metricsService.stopRecording();
            return ex.getRawStatusCode();
        }
    }



    public boolean isRunning() {
        return running;
    }
}