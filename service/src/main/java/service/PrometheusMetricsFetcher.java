package service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

public class PrometheusMetricsFetcher {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PROMETHEUS_URL = "http://prometheus:9090/api/v1/query";

    private final MeterRegistry meterRegistry;

    private volatile double cpuUsagePercent = 0.0;
    private volatile long memoryUsageBytes = 0;

    public PrometheusMetricsFetcher(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        // Gauge exposé pour Prometheus (/actuator/prometheus)
        Gauge.builder("container_cpu_usage_percent", this, PrometheusMetricsFetcher::getCpuUsagePercent)
                .description("CPU usage % of the container (via Prometheus)")
                .register(meterRegistry);

        Gauge.builder("container_memory_usage_bytes", this, PrometheusMetricsFetcher::getMemoryUsageBytes)
                .description("Memory usage in bytes of the container (via Prometheus)")
                .register(meterRegistry);
    }

    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public long getMemoryUsageBytes() {
        return memoryUsageBytes;
    }

    // Appelé toutes les 5s par MetricsService
    public double fetchCpuUsage() {
        String query = "rate(container_cpu_usage_seconds_total{name=\"sp-service\"}[1m])";
        double value = fetchSingleValueDouble(query) * 100.0;
        this.cpuUsagePercent = value;
        return value;
    }

    public long fetchMemoryUsage(String containerName) {
        String query = String.format("container_memory_usage_bytes{name=\"%s\"}", containerName);
        long value = (long) fetchSingleValueDouble(query);
        this.memoryUsageBytes = value;
        return value;
    }

    private double fetchSingleValueDouble(String query) {
        String url = UriComponentsBuilder
                .fromHttpUrl(PROMETHEUS_URL)
                .queryParam("query", query)
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();
                if (body == null) return 0.0;

                Map<String, Object> data = (Map<String, Object>) body.get("data");
                List<Map<String, Object>> result = (List<Map<String, Object>>) data.get("result");

                if (!result.isEmpty()) {
                    List<Object> value = (List<Object>) result.get(0).get("value");
                    return Double.parseDouble((String) value.get(1));
                }
            }
        } catch (Exception e) {
            System.err.println("Prometheus fetch error: " + e.getMessage());
        }

        return 0.0;
    }
}
