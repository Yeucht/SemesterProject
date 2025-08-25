package service;

import config.ConfigRepository;
import config.SimulationConfig;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import simulation.MetricPoint;
import simulation.SimulationRun;
import simulation.SimulationRepository;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.time.Instant;
import java.util.*;

@Service
public class MetricsService {

    private final SimulationRepository simulationRunRepository;
    private final ConfigRepository configRepository;
    private final ConfigService configService;
    private final PrometheusService prometheusService;
    private final MeterRegistry meterRegistry;
    private final Counter counter;

    private SimulationRun currentRun;

    private volatile double cpuUsage = 0.0;
    private volatile long memoryUsed = 0;
    private volatile long insertedSoFar = 0;

    public MetricsService(SimulationRepository simRepo,
                          ConfigRepository configRepo,
                          ConfigService configService,
                          PrometheusService promService,
                          MeterRegistry registry,
                          Counter counter) {
        this.simulationRunRepository = simRepo;
        this.configRepository = configRepo;
        this.configService = configService;
        this.prometheusService = promService;
        this.meterRegistry = registry;
        this.counter = counter;

        Gauge.builder("spservice_inserted_so_far", this, MetricsService::getInsertedSoFar)
                .register(registry);
    }

    public void startRecording() {
        configRepository.save(configService.getConfig());
        this.currentRun = new SimulationRun(this.configService.getConfig());
        System.out.println("📈 Simulation started at: " + this.currentRun.getStartedAt());
    }

    @Transactional
    public void stopRecording() {
        if (currentRun == null) return;

        Instant start = currentRun.getStartedAt();
        Instant end = Instant.now();
        currentRun.setEndedAt(end);

        SimulationConfig config = getOrCreate(configService.getConfig());
        currentRun.setConfig(config);

        // 🔁 Prometheus-scraped metrics exposées par notre app
        Map<Instant, Double> cpuSeries = prometheusService.queryTimeSeries("avg_over_time(process_cpu_usage[15s]) * 100", start, end);
        Map<Instant, Double> memSeries = prometheusService.queryTimeSeries("sum(jvm_memory_used_bytes{area=\"heap\"})", start, end);
        Map<Instant, Double> insertSeries = prometheusService.queryTimeSeries("spservice_inserted_so_far", start, end);

        // Fusionner tous les timestamps
        Set<Instant> allTimestamps = new TreeSet<>();
        allTimestamps.addAll(cpuSeries.keySet());
        allTimestamps.addAll(memSeries.keySet());
        allTimestamps.addAll(insertSeries.keySet());

        List<MetricPoint> points = new ArrayList<>();
        Double lastCpu = null;
        Long lastMem = null;
        Double lastInsert = null;

        for (Instant t : allTimestamps) {
            MetricPoint point = new MetricPoint();
            point.setTimestamp(t);
            point.setRun(currentRun);

            if (cpuSeries.containsKey(t)) lastCpu = cpuSeries.get(t);
            point.setCpuUsage(lastCpu != null ? lastCpu : 0.0);

            if (memSeries.containsKey(t)) lastMem = memSeries.get(t).longValue();
            point.setMemoryUsed(lastMem != null ? lastMem : 0L);

            if (insertSeries.containsKey(t)) lastInsert = insertSeries.get(t);
            point.setInsertedSoFar(lastInsert != null ? lastInsert.longValue() : points.size());

            points.add(point);
        }

        currentRun.setMetrics(points);
        currentRun.setTotalInserted(points.size());

        simulationRunRepository.save(currentRun);
        System.out.println("✅ Simulation stored: " + points.size() + " points.");
        currentRun = null;
    }

    @Scheduled(fixedRate = 1000)
    public void collectMetrics() {
        if (currentRun == null) return;

        this.cpuUsage = getCpuUsage();
        this.memoryUsed = getMemoryUsed();
        this.insertedSoFar = getInsertedSoFar();

        System.out.printf("📊 CPU: %.2f%% | RAM: %d | Inserted: %d\n",
                cpuUsage, memoryUsed, insertedSoFar);
    }

    public double getCpuUsage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean)
                ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getProcessCpuLoad();
        return cpuLoad < 0 ? 0.0 : cpuLoad * 100.0;
    }

    public long getMemoryUsed() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public long getInsertedSoFar() {
        return counter.getCounter();
    }

    public void updateInserts(int nbr){
        currentRun.setTotalInserted(currentRun.getTotalInserted() + nbr);
    }

    @Transactional
    public SimulationConfig getOrCreate(SimulationConfig wanted) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnorePaths("id"); // on ignore l'id

        Example<SimulationConfig> example = Example.of(wanted, matcher);

        return configRepository.findOne(example).orElseGet(() -> {
            try {
                return configRepository.save(wanted);
            } catch (DataIntegrityViolationException e) {
                // Concurrence : quelqu’un a inséré la même config entre temps
                return configRepository.findOne(example).orElseThrow(() -> e);
            }
        });
    }
}
