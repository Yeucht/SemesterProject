package service;

import com.fasterxml.jackson.databind.JsonNode;
import config.SimulationConfig;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrometheusService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PROMETHEUS_URL = "http://prometheus:9090/api/v1/";
    private final ConfigService configService;

    private final MeterRegistry meterRegistry;

    private volatile double cpuUsagePercent = 0.0;
    private volatile long memoryUsageBytes = 0;

    public PrometheusService(MeterRegistry meterRegistry, ConfigService configService) {
        this.meterRegistry = meterRegistry;
        this.configService = configService;
    }


    public Map<Instant, Double> queryTimeSeries(String metric, Instant start, Instant end, String container) {
        String promQLRaw = String.format("rate(%s{container=\"%s\"}[1s])", metric, container);
        String encodedQuery = URLEncoder.encode(promQLRaw, StandardCharsets.UTF_8);
        String url = PROMETHEUS_URL  + "query_range?query=" + promQLRaw +
                "&start=" + start.getEpochSecond() +
                "&end=" + end.getEpochSecond() +
                "&step=1s";


        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);

        Map<Instant, Double> series = new HashMap<>();

        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode result = response.getBody().path("data").path("result");
            if (result.isArray() && result.size() > 0) {
                JsonNode values = result.get(0).path("values");
                for (JsonNode valueNode : values) {
                    Instant ts = Instant.ofEpochSecond(valueNode.get(0).asLong());
                    double val = Double.parseDouble(valueNode.get(1).asText());
                    series.put(ts, val);
                }
            }
        }

        return series;
    }

}
