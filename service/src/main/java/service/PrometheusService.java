package service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class PrometheusService {

    @Value("${prometheus.url}")
    private String prometheusBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<Instant, Double> queryTimeSeries(String metricName, Instant start, Instant end) {
        String url = String.format(
                "%s/api/v1/query_range?query=%s&start=%s&end=%s&step=1s",
                prometheusBaseUrl,
                URLEncoder.encode(metricName, StandardCharsets.UTF_8),
                start.toString(),
                end.toString()
        );

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
