package service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class PrometheusService {

    @Value("${prometheus.url}")
    private String prometheusBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String step = "10s"; //définit la fenêtre de avg/

    public Map<Instant, Double> queryTimeSeries(String promql, Instant start, Instant end) {
        // 1) Choisir un step cohérent avec le scrape (ex: >=15s). Tu peux aussi le passer en paramètre.


        // 2) Construire l'URI SANS pré-encoder la requête
        URI uri = UriComponentsBuilder
                .fromHttpUrl(prometheusBaseUrl)
                .path("/api/v1/query_range")
                .queryParam("query", promql)                // <-- pas d'URLEncoder ici
                .queryParam("start", start.toString())      // RFC3339 OK
                .queryParam("end", end.toString())
                .queryParam("step", step)
                .build()
                .encode()                                   // encode une seule fois et proprement
                .toUri();

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(uri, JsonNode.class);
        Map<Instant, Double> series = new TreeMap<>();      // timestamps triés

        if (!response.getStatusCode().is2xxSuccessful()) {
            // à toi de voir : throw, log, etc.
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

        // Beaucoup de métriques retournent plusieurs séries (labels différents).
        // Ici, on AGRÈGE (sum) toutes les séries par timestamp pour rester compatible
        // avec Map<Instant, Double>. Si tu préfères garder chaque série, je te donne une variante plus bas.
        for (JsonNode serie : result) {
            JsonNode values = serie.path("values"); // [[ <ts sec double>, "<value string>" ], ...]
            for (JsonNode v : values) {
                double tsSec = v.get(0).asDouble();              // <-- garder les décimales
                Instant ts = Instant.ofEpochMilli((long) (tsSec * 1000.0));

                String vs = v.get(1).asText();
                // Prometheus peut renvoyer "NaN", "+Inf", "-Inf" -> on ignore ces points
                double val;
                try {
                    val = Double.parseDouble(vs);
                    if (Double.isNaN(val) || Double.isInfinite(val)) continue;
                } catch (NumberFormatException ex) {
                    continue;
                }

                series.merge(ts, val, Double::sum); // somme si plusieurs séries
            }
        }

        return series;
    }

}
