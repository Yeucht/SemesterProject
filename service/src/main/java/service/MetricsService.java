package service;


import config.ConfigRepository;
import config.SimulationConfig;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import simulation.*;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.time.Instant;
import java.util.List;


public class MetricsService {

    private final SimulationRepository simulationRunRepository;
    private final ConfigRepository configRepository;
    private ConfigService configService;

    private SimulationRun currentRun;

    public MetricsService(SimulationRepository simulationRunRepository, ConfigRepository configRepository, ConfigService configService) {
        this.simulationRunRepository = simulationRunRepository;
        this.configRepository = configRepository;
        this.configService = configService;
    }

    // Démarrer une nouvelle simulation
    public void startRecording() {
        configRepository.save(configService.getConfig());
        this.currentRun = new SimulationRun(this.configService.getConfig());
        System.out.println("New simulation run started: " + this.currentRun);
    }

    // Stopper la simulation et persister en base
    public void stopRecording() {
        if (currentRun != null) {
            currentRun.setEndedAt(Instant.now());
            SimulationConfig config = configRepository.findById(configService.getConfig().getId()).orElseThrow();
            currentRun.setConfig(config);
            simulationRunRepository.save(currentRun);
            currentRun = null;
        }
    }

    // Appel automatique toutes les 1s
    @Scheduled(fixedRate = 1000)
    public void collectMetrics() {
        //System.out.println("Collecting metrics");
        if (currentRun == null) return;

        MetricPoint point = new MetricPoint();
        point.setTimestamp(Instant.now());
        point.setCpuUsage(getCpuUsage());
        point.setMemoryUsed(getMemoryUsed());
        point.setInsertedSoFar(getInsertedSoFar());
        point.setRun(currentRun);

        currentRun.add_point(point);
    }

    public MetricPoint getLastMetricPoint() {
        if (currentRun == null || currentRun.getMetrics().isEmpty()){
            System.out.println("No metric found");
            return null;
        }
        List<MetricPoint> list = currentRun.getMetrics();
        return list.get(list.size() - 1);
    }

    private double getCpuUsage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean)
                ManagementFactory.getOperatingSystemMXBean();

        double cpuLoad = osBean.getProcessCpuLoad();
        // Valeur retournée entre 0.0 et 1.0, ou -1 si non disponible
        if (cpuLoad < 0) return 0.0;
        return cpuLoad * 100.0;
    }

    private long getMemoryUsed() {
        // Utilisation de mémoire heap
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        return used;
    }

    private long getInsertedSoFar() {
        // À adapter selon ton système de comptage réel
        // Pour l'instant, on peut simplement renvoyer currentRun.getTotalInserted()
        // ou calculer via currentRun.getMetrics().size() si 1 point = 1 insertion
        if (currentRun == null) return 0;
        return currentRun.getTotalInserted(); // ou : currentRun.getMetrics().size();
    }
}


