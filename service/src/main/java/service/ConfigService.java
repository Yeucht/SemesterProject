package service;

import config.SimulationConfig;
import dto.FlaskConfigResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;


public class ConfigService {
    private SimulationConfig config;
    private DBManagerService dbManagerService;
    private InjectionService injectionService;
    private FlaskClient flaskClient;
    private InvoiceService invoiceService;

    public ConfigService(SimulationConfig config, DBManagerService dbManagerService, InjectionService injectionService, FlaskClient flaskClient, InvoiceService invoiceService) {
        this.config = config;
        this.dbManagerService = dbManagerService;
        this.injectionService = injectionService;
        this.invoiceService = invoiceService;
        this.flaskClient = flaskClient;
    }

    public ConfigService() {
        this.flaskClient = null;
    }

    public SimulationConfig getConfig() {
        return config;
    }

    public void updateConfig(SimulationConfig newConfig) {
        newConfig.normalize(); //checks for impossible configs
        this.config = newConfig;
        if (dbManagerService != null) {
            dbManagerService.update(newConfig);
            dbManagerService.updateCounter();
        }
        if (injectionService != null) {
            injectionService.update(newConfig);
        }
    }

    public void resetConfigToDefault() {
        this.config = new SimulationConfig();
    }

    public ResponseEntity<?> applyAndPushConfig(SimulationConfig newConfig) {
        // 1) mettre à jour local
        updateConfig(newConfig);

        // 2) pousser vers Flask et propager la réponse telle quelle
        try {
            ResponseEntity<FlaskConfigResponse> resp = flaskClient.postConfig(newConfig);
            return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
        } catch (RestClientResponseException ex) {
            return FlaskClient.propagate(ex);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error posting config to Flask: " + e.getMessage());
        }
    }

}