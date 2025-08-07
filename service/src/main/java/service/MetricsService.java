package service;


import config.ConfigRepository;
import config.SimulationConfig;
import org.springframework.scheduling.annotation.Scheduled;
import simulation.*;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.time.Instant;
import java.util.*;


public class MetricsService {

    private final SimulationRepository simulationRunRepository;
    private final ConfigRepository configRepository;
    private ConfigService configService;
    PrometheusService prometheusService;

    private SimulationRun currentRun;

    public MetricsService(SimulationRepository simulationRunRepository, ConfigRepository configRepository, ConfigService configService, PrometheusService prometheusService) {
        this.simulationRunRepository = simulationRunRepository;
        this.configRepository = configRepository;
        this.configService = configService;
        this.prometheusService = prometheusService;
    }

    // Démarrer une nouvelle simulation
    public void startRecording() {
        configRepository.save(configService.getConfig());
        this.currentRun = new SimulationRun(this.configService.getConfig());
        System.out.println("New simulation run started: " + this.currentRun);
    }

    // Stopper la simulation et persister en base
    public void stopRecording() {
        if (currentRun == null) return;

        Instant start = currentRun.getStartedAt();
        Instant end = Instant.now();
        currentRun.setEndedAt(end);

        SimulationConfig config = configRepository.findById(configService.getConfig().getId()).orElseThrow();
        currentRun.setConfig(config);

        String containerName = "sp-service";

        // 🔄 Collecter les séries de Prometheus
        Map<Instant, Double> cpuSeries = prometheusService.queryTimeSeries(
                "rate(engine_daemon_container_cpu_usage_seconds_total)", start, end, containerName);
        Map<Instant, Double> memSeries = prometheusService.queryTimeSeries(
                "engine_daemon_container_memory_usage_bytes", start, end, containerName);
        Map<Instant, Double> diskSeries = prometheusService.queryTimeSeries(
                "engine_daemon_container_fs_usage_bytes", start, end, containerName);
        Map<Instant, Double> netInSeries = prometheusService.queryTimeSeries(
                "rate(engine_daemon_container_network_receive_bytes_total)", start, end, containerName);
        Map<Instant, Double> netOutSeries = prometheusService.queryTimeSeries(
                "rate(engine_daemon_container_network_transmit_bytes_total)", start, end, containerName);

        // 🧩 Fusionner tous les timestamps (triés)
        Set<Instant> allTimestamps = new TreeSet<>();
        allTimestamps.addAll(cpuSeries.keySet());
        allTimestamps.addAll(memSeries.keySet());
        allTimestamps.addAll(diskSeries.keySet());
        allTimestamps.addAll(netInSeries.keySet());
        allTimestamps.addAll(netOutSeries.keySet());

        // 📦 Variables pour conserver la dernière valeur connue
        Double lastCpu = null;
        Long lastMem = null;
        Double lastDisk = null;
        Double lastNetIn = null;
        Double lastNetOut = null;

        List<MetricPoint> points = new ArrayList<>();

        for (Instant t : allTimestamps) {
            MetricPoint point = new MetricPoint();
            point.setTimestamp(t);
            point.setRun(currentRun);

            // ---- CPU (en %) ----
            if (cpuSeries.containsKey(t)) lastCpu = cpuSeries.get(t) * 100;
            point.setCpuUsage(lastCpu != null ? lastCpu : 0.0);

            // ---- RAM ----
            if (memSeries.containsKey(t)) lastMem = memSeries.get(t).longValue();
            point.setMemoryUsed(lastMem != null ? lastMem : 0L);

            // ---- Disque ----
            if (diskSeries.containsKey(t)) lastDisk = diskSeries.get(t);
            point.setDiskUsed(lastDisk != null ? lastDisk : 0.0);

            // ---- Réseau IN ----
            if (netInSeries.containsKey(t)) lastNetIn = netInSeries.get(t);
            point.setNetIn(lastNetIn != null ? lastNetIn : 0.0);

            // ---- Réseau OUT ----
            if (netOutSeries.containsKey(t)) lastNetOut = netOutSeries.get(t);
            point.setNetOut(lastNetOut != null ? lastNetOut : 0.0);

            // ---- Progression de l'injection (ex : 1 point = 1 insert) ----
            point.setInsertedSoFar(points.size() + 1);

            points.add(point);
        }

        currentRun.setMetrics(points);
        currentRun.setTotalInserted(points.size());

        simulationRunRepository.save(currentRun);
        currentRun = null;
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