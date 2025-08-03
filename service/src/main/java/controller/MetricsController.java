package controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ConfigService;
import service.MetricsService;
import simulation.MetricPoint;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    // → Vue.js va appeler ça régulièrement (ex: chaque seconde)
    @GetMapping("/live")
    public MetricPoint getLiveMetrics() {
        return metricsService.getLastMetricPoint();
    }
}
