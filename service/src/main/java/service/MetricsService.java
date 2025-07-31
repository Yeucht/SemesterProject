package service;


import config.SimulationConfig;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import simulation.*;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.time.Instant;


public class MetricsService {

    private final SimulationRepository simulationRepository;
    private final MetricPointRepository metricPointRepository;
    private final PrometheusMetricsFetcher prometheusFetcher;
    private final SimulationConfig simulationConfig;

    private SimulationRun currentRun;

    public MetricsService(
            SimulationRepository simulationRepository,
            MetricPointRepository metricPointRepository,
            PrometheusMetricsFetcher prometheusFetcher,
            SimulationConfig simulationConfig
    ) {
        this.simulationRepository = simulationRepository;
        this.metricPointRepository = metricPointRepository;
        this.prometheusFetcher = prometheusFetcher;
        this.simulationConfig = simulationConfig;
    }
/*
    public void startSimulation() {
        this.currentRun = new SimulationRun(this.simulationConfig);
        System.out.println("starting new simulation RUN3");
        collectMetrics();
    }

    @Transactional
    public void stopSimulation() {
        System.out.println("Saving simulation with " + currentRun.getMetrics().size() + " metric points");
        currentRun.setEndedAt(Instant.now());
        simulationRepository.save(currentRun);
        this.currentRun = null;
    }


    @Scheduled(fixedRate = 5000)
    public void collectMetrics() {
        if (currentRun == null) return;

        // 👇 Nom du container Docker tel que vu par cAdvisor
        String containerName = "sp-service";

        double cpuUsage = prometheusFetcher.fetchCpuUsage();
        long memoryUsed = prometheusFetcher.fetchMemoryUsage(containerName);

        MetricPoint point = new MetricPoint();
        point.setTimestamp(Instant.now());
        point.setCpuUsage(cpuUsage);
        point.setMemoryUsed(memoryUsed);
        point.setRun(currentRun);
        System.out.println("New point: " + point);
        currentRun.add_point(point);
    }
*/
    public double getProcessCpuLoad() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean)
                ManagementFactory.getOperatingSystemMXBean();
        // Load du process Java en pourcentage
        return osBean.getProcessCpuLoad() * 100;
    }
}

