package controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ConfigService;
import service.MetricsService;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

@RestController
public class MetricsController {

    public MetricsService metricsService;
    public ConfigService configService;

    private final MetricsService cpuUsageService;

    public MetricsController(MetricsService cpuUsageService) {
        this.cpuUsageService = cpuUsageService;
    }

    @GetMapping("/cpu-usage")
    public double getCpuUsage() {
        return cpuUsageService.getProcessCpuLoad();
    }

}
