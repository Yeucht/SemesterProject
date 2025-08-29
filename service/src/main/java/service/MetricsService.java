package service;

import config.ConfigRepository;
import config.SimulationConfig;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import simulation.MetricPoint;
import simulation.SimulationRun;
import simulation.SimulationRepository;

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
    private final String springbootInstance = "sp-service:8080";
    private final DBManagerService dbManagerService;

    private SimulationRun currentRun;

    private volatile double cpuUsage = 0.0;
    private volatile long memoryUsed = 0;
    private volatile long insertedSoFar = 0;

    public MetricsService(SimulationRepository simRepo,
                          ConfigRepository configRepo,
                          ConfigService configService,
                          PrometheusService promService,
                          MeterRegistry registry,
                          Counter counter, DBManagerService dbManagerService) {
        this.simulationRunRepository = simRepo;
        this.configRepository = configRepo;
        this.configService = configService;
        this.prometheusService = promService;
        this.meterRegistry = registry;
        this.counter = counter;


        Gauge.builder("spservice_inserted_so_far", this, MetricsService::getInsertedSoFar)
                .register(registry);
        this.dbManagerService = dbManagerService;
    }

    @Transactional
    public void startRecording() {
        configRepository.save(getOrCreate(configService.getConfig()));
        this.currentRun = new SimulationRun(getOrCreate(this.configService.getConfig()));
        this.currentRun.setOriginDbSize(dbManagerService.getDbManager().getRowCount());
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



        // =======================
        // Requêtes PromQL (1 série par métrique)
        // =======================

        // CPU (%) — moyenne glissante
        Map<Instant, Double> cpuSeries = prometheusService.queryTimeSeries(
                "avg_over_time(process_cpu_usage{instance=~\"" + springbootInstance + "\"}[15s]) * 100",
                start, end
        );

        // Legacy compat: heap bytes dans memoryUsed
        Map<Instant, Double> memSeries = prometheusService.queryTimeSeries(
                "sum(jvm_memory_used_bytes{area=\"heap\",instance=~\"" + springbootInstance + "\"})",
                start, end
        );

        // Inserts cumulés (exposés par l'app)
        Map<Instant, Double> insertSeries = prometheusService.queryTimeSeries(
                "spservice_inserted_so_far{instance=~\"" + springbootInstance + "\"}",
                start, end
        );

        // Disk used (%) — calcul direct sur l’instance
        Map<Instant, Double> diskUsedSeries = prometheusService.queryTimeSeries(
                "100 * (1 - (disk_free_bytes{instance=~\"" + springbootInstance + "\"} / disk_total_bytes{instance=~\"" + springbootInstance + "\"}))",
                start, end
        );

        // RPS 200 — somme sur toutes les séries (uri/method/…) de l’instance
        Map<Instant, Double> rps2xxSeries = prometheusService.queryTimeSeries(
                "sum(rate(http_server_requests_seconds_count{status=\"200\",instance=~\"" + springbootInstance + "\"}[15s]))",
                start, end
        );
        // Si tu veux exclure l’actuator :
        // "sum(rate(http_server_requests_seconds_count{status=\"200\",instance=~\""+springbootInstance+"\",uri!~\".*actuator.*\"}[15s]))"

        // Heap / Non-heap (bytes) — agrégés sans (id) sur l’instance
        Map<Instant, Double> heapUsedSeries = prometheusService.queryTimeSeries(
                "sum without (id) (jvm_memory_used_bytes{area=\"heap\",instance=~\"" + springbootInstance + "\"})",
                start, end
        );

        Map<Instant, Double> nonHeapSeries = prometheusService.queryTimeSeries(
                "sum without (id) (jvm_memory_used_bytes{area=\"nonheap\",instance=~\"" + springbootInstance + "\"})",
                start, end
        );

        // Threads live — 1 série (une par instance), on somme pour être robustes
        Map<Instant, Double> threadsLiveSeries = prometheusService.queryTimeSeries(
                "sum(jvm_threads_live_threads{instance=~\"" + springbootInstance + "\"})",
                start, end
        );

        // =======================
        // Union des timestamps + LVCF
        // =======================
        Set<Instant> allTimestamps = new TreeSet<>();
        allTimestamps.addAll(cpuSeries.keySet());
        allTimestamps.addAll(memSeries.keySet());
        allTimestamps.addAll(insertSeries.keySet());
        allTimestamps.addAll(diskUsedSeries.keySet());
        allTimestamps.addAll(rps2xxSeries.keySet());
        allTimestamps.addAll(heapUsedSeries.keySet());
        allTimestamps.addAll(nonHeapSeries.keySet());
        allTimestamps.addAll(threadsLiveSeries.keySet());

        List<MetricPoint> points = new ArrayList<>();

        Double  lastCpu = null;
        Long    lastMem = null;          // legacy heap -> memoryUsed
        Double  lastInsert = null;

        Double  lastDiskPct = null;
        Double  lastRps2xx = null;
        Long    lastHeap = null;
        Long    lastNonHeap = null;
        Integer lastThreads = null;

        for (Instant t : allTimestamps) {
            MetricPoint p = new MetricPoint();
            p.setTimestamp(t);
            p.setRun(currentRun);

            if (cpuSeries.containsKey(t)) lastCpu = cpuSeries.get(t);
            p.setCpuUsage(lastCpu != null ? lastCpu : 0.0);

            if (memSeries.containsKey(t)) lastMem = memSeries.get(t).longValue();
            p.setTotalMemoryUsed(lastMem != null ? lastMem : 0L); // compat historique

            if (insertSeries.containsKey(t)) lastInsert = insertSeries.get(t);
            p.setInsertedSoFar(lastInsert != null ? lastInsert.longValue() : points.size());

            if (diskUsedSeries.containsKey(t)) lastDiskPct = diskUsedSeries.get(t);
            p.setDiskUsed(lastDiskPct != null ? lastDiskPct : 0.0);

            if (rps2xxSeries.containsKey(t)) lastRps2xx = rps2xxSeries.get(t);
            p.setRps(lastRps2xx != null ? lastRps2xx : 0.0);

            if (heapUsedSeries.containsKey(t)) lastHeap = heapUsedSeries.get(t).longValue();
            p.setHeapUsedBytes(lastHeap != null ? lastHeap : 0L);

            if (nonHeapSeries.containsKey(t)) lastNonHeap = nonHeapSeries.get(t).longValue();
            p.setNonHeapUsedBytes(lastNonHeap != null ? lastNonHeap : 0L);

            if (threadsLiveSeries.containsKey(t)) lastThreads = threadsLiveSeries.get(t).intValue();
            p.setThreadsLive(lastThreads != null ? lastThreads : 0);

            points.add(p);
        }

        currentRun.setMetrics(points);
        currentRun.setMetricPointsInserted(points.size());

        simulationRunRepository.save(currentRun);
        System.out.println("✅ Simulation stored: " + points.size() + " points.");
        currentRun = null;
    }


    public long getInsertedSoFar() {
        return counter.getCounter();
    }


    @Transactional
    public SimulationConfig getOrCreate(SimulationConfig wanted) {
        wanted.normalize();
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
