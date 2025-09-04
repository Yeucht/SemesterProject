package service;

import dto.FlaskConfigResponse;
import config.SimulationConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class FlaskClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public FlaskClient(RestTemplate restTemplate,
                       @Value("${flask.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<FlaskConfigResponse> postConfig(SimulationConfig cfg) {
        String url = baseUrl + "/config";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SimulationConfig> entity = new HttpEntity<>(cfg, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, FlaskConfigResponse.class);
    }

    public ResponseEntity<String> start() {
        String url = baseUrl + "/start";
        return restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
    }

    public ResponseEntity<String> stop() {
        String url = baseUrl + "/stop";
        return restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
    }

    public ResponseEntity<String> getFlaskConfigRaw() {
        String url = baseUrl + "/config";
        return restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
    }

    //Error propagate
    public static ResponseEntity<?> propagate(RestClientResponseException ex) {
        return ResponseEntity.status(ex.getRawStatusCode()).body(ex.getResponseBodyAsString());
    }
}
