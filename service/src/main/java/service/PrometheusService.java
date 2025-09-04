package service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.time.Instant;
import java.util.*;

@Service
public class PrometheusService {

    @Value("${prometheus.url}")
    private String prometheusBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String step = "10s";

    //Treating prometheus series
    public Map<Instant, Double> queryTimeSeries(String promql, Instant start, Instant end) {

        URI uri = UriComponentsBuilder
                .fromHttpUrl(prometheusBaseUrl)
                .path("/api/v1/query_range")
                .queryParam("query", promql)
                .queryParam("start", start.toString())
                .queryParam("end", end.toString())
                .queryParam("step", step)
                .build()
                .encode()
                .toUri();

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(uri, JsonNode.class);
        Map<Instant, Double> series = new TreeMap<>();

        if (!response.getStatusCode().is2xxSuccessful()) {
            return series;
        }

        JsonNode body = response.getBody();
        if (body == null || !"success".equals(body.path("status").asText())) {
            return series;
        }

        JsonNode result = body.path("data").path("result");
        if (!result.isArray() || result.size() == 0) {
            return series;
        }

        for (JsonNode serie : result) {
            JsonNode values = serie.path("values");
            for (JsonNode v : values) {
                double tsSec = v.get(0).asDouble();
                Instant ts = Instant.ofEpochMilli((long) (tsSec * 1000.0));

                String vs = v.get(1).asText();

                double val;
                try {
                    val = Double.parseDouble(vs);
                    if (Double.isNaN(val) || Double.isInfinite(val)) continue;
                } catch (NumberFormatException ex) {
                    continue;
                }

                series.merge(ts, val, Double::sum);
            }
        }

        return series;
    }

}
