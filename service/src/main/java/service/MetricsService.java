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
    private final String questDbInstance = "questdb:9003";
    private final String iotDbInstance = "iotdb:9092";
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
                          Counter counter,
                          DBManagerService dbManagerService) {
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
        currentRun.setFinalDbSize(dbManagerService.getDbManager().getRowCount());
        currentRun.setAvgInsertRateRelative();

        SimulationConfig config = getOrCreate(configService.getConfig());
        currentRun.setConfig(config);

        // ========= 1) Récupérer les séries =========
        Map<String, Map<Instant, Double>> app = fetchAppSeries(springbootInstance, start, end);
        Map<String, Map<Instant, Double>> db  = fetchDbSeries(config, start, end);

        // ========= 2) Union des timestamps =========
        Set<Instant> allTimestamps = new TreeSet<>();
        app.values().forEach(m -> allTimestamps.addAll(m.keySet()));
        db.values().forEach(m  -> allTimestamps.addAll(m.keySet()));

        // ========= 3) LVCF + build points =========
        List<MetricPoint> points = new ArrayList<>();

        // --- APP last values ---
        Double  lastCpu = null;
        Long    lastMemLegacy = null; // totalMemoryUsed legacy (heap)
        Double  lastInserted = null;
        Double  lastDisk = null;
        Double  lastRps = null;
        Long    lastHeap = null;
        Long    lastNonHeap = null;
        Integer lastThreads = null;

        // --- DB last values ---
        Double  lastDbQps = null;
        Double  lastDbConns = null;
        Double  lastDbErr = null;
        Long    lastDbHeap = null;
        Long    lastDbWalBacklog = null;

        for (Instant t : allTimestamps) {
            MetricPoint p = new MetricPoint();
            p.setTimestamp(t);
            p.setRun(currentRun);

            // ---- APP ----
            if (v(app, "cpu").containsKey(t)) lastCpu = v(app, "cpu").get(t);
            p.setCpuUsage10s(lastCpu != null ? lastCpu : 0.0);

            if (v(app, "memLegacy").containsKey(t)) lastMemLegacy = v(app, "memLegacy").get(t).longValue();
            p.setTotalMemoryUsed(lastMemLegacy != null ? lastMemLegacy : 0L);

            if (v(app, "inserted").containsKey(t)) lastInserted = v(app, "inserted").get(t);
            p.setInsertedSoFar(lastInserted != null ? lastInserted.longValue() : points.size());

            if (v(app, "diskUsed").containsKey(t)) lastDisk = v(app, "diskUsed").get(t);
            p.setDiskUsed(lastDisk != null ? lastDisk : 0.0);

            if (v(app, "rps").containsKey(t)) lastRps = v(app, "rps").get(t);
            p.setRps10s(lastRps != null ? lastRps : 0.0);

            if (v(app, "heapUsed").containsKey(t)) lastHeap = v(app, "heapUsed").get(t).longValue();
            p.setHeapUsedBytes(lastHeap != null ? lastHeap : 0L);

            if (v(app, "nonHeapUsed").containsKey(t)) lastNonHeap = v(app, "nonHeapUsed").get(t).longValue();
            p.setNonHeapUsedBytes(lastNonHeap != null ? lastNonHeap : 0L);

            if (v(app, "threadsLive").containsKey(t)) lastThreads = v(app, "threadsLive").get(t).intValue();
            p.setThreadsLive(lastThreads != null ? lastThreads : 0);

            // ---- DB (comparables) ----
            if (v(db, "db_qps").containsKey(t)) lastDbQps = v(db, "db_qps").get(t);
            p.setDbQueryQps10s(lastDbQps != null ? lastDbQps : 0.0);

            if (v(db, "db_conns").containsKey(t)) lastDbConns = v(db, "db_conns").get(t);
            p.setDbConnections(lastDbConns != null ? lastDbConns.intValue() : 0);

            if (v(db, "db_err_qps").containsKey(t)) lastDbErr = v(db, "db_err_qps").get(t);
            p.setDbErrorQps10s(lastDbErr != null ? lastDbErr : 0.0);

            if (v(db, "db_heap").containsKey(t)) lastDbHeap = v(db, "db_heap").get(t).longValue();
            p.setDbHeapUsedBytes(lastDbHeap != null ? lastDbHeap : 0L);

            if (v(db, "db_wal_backlog").containsKey(t)) lastDbWalBacklog = v(db, "db_wal_backlog").get(t).longValue();
            p.setDbWalBacklog(lastDbWalBacklog != null ? lastDbWalBacklog : 0L);

            points.add(p);
        }

        currentRun.setMetrics(points);
        currentRun.setMetricPointsInserted(points.size());
        simulationRunRepository.save(currentRun);

        System.out.println("✅ Simulation stored: " + points.size() + " points.");
        currentRun = null;
    }

    /** Petite utilité pour éviter les get/containsKey verbeux */
    private static Map<Instant, Double> v(Map<String, Map<Instant, Double>> group, String key) {
        return group.getOrDefault(key, Map.of());
    }

    /** =======================
     *  Helpers : séries APP
     *  ======================= */
    private Map<String, Map<Instant, Double>> fetchAppSeries(String springbootInstance, Instant start, Instant end) {
        Map<String, Map<Instant, Double>> m = new HashMap<>();

        // avg_over_time -> besoin d'une fenêtre
        m.put("cpu", prometheusService.queryTimeSeries(
                "avg_over_time(process_cpu_usage{instance=~\"" + springbootInstance + "\"}[10s]) * 100", start, end));

        // legacy totalMemoryUsed = heap (gauge)
        m.put("memLegacy", prometheusService.queryTimeSeries(
                "sum(jvm_memory_used_bytes{area=\"heap\",instance=~\"" + springbootInstance + "\"})", start, end));

        m.put("inserted", prometheusService.queryTimeSeries(
                "spservice_inserted_so_far{instance=~\"" + springbootInstance + "\"}", start, end));

        // Disque (gauges)
        m.put("diskUsed", prometheusService.queryTimeSeries(
                "100 * (1 - (disk_free_bytes{instance=~\"" + springbootInstance + "\"} / " +
                        "disk_total_bytes{instance=~\"" + springbootInstance + "\"}))", start, end));

        // rate(...) -> besoin d'une fenêtre
        m.put("rps", prometheusService.queryTimeSeries(
                "sum(rate(http_server_requests_seconds_count{status=\"200\",instance=~\"" + springbootInstance + "\"}[10s]))", start, end));

        // Heap / Non-heap (gauges)
        m.put("heapUsed", prometheusService.queryTimeSeries(
                "sum without (id) (jvm_memory_used_bytes{area=\"heap\",instance=~\"" + springbootInstance + "\"})", start, end));

        m.put("nonHeapUsed", prometheusService.queryTimeSeries(
                "sum without (id) (jvm_memory_used_bytes{area=\"nonheap\",instance=~\"" + springbootInstance + "\"})", start, end));

        // Threads (gauge)
        m.put("threadsLive", prometheusService.queryTimeSeries(
                "sum(jvm_threads_live_threads{instance=~\"" + springbootInstance + "\"})", start, end));

        return m;
    }


    /** =======================
     *  Helpers : séries DB (comparables)
     *  ======================= */
    private Map<String, Map<Instant, Double>> fetchDbSeries(SimulationConfig config, Instant start, Instant end) {
        Map<String, Map<Instant, Double>> m = new HashMap<>();
        SimulationConfig.DatabaseType dbType = config.getDbType();

        switch (dbType) {
            case QUESTDB: {
                // QPS = PGWire OR REST JSON (évite '+')
                m.put("db_qps", prometheusService.queryTimeSeries(
                        "sum( rate(questdb_pg_wire_queries_completed_total{instance=~\"" + questDbInstance + "\"}[10s])"
                                + " or rate(questdb_json_queries_completed_total{instance=~\"" + questDbInstance + "\"}[10s]) )",
                        start, end));

                // Connexions = union des 3 compteurs
                m.put("db_conns", prometheusService.queryTimeSeries(
                        "sum( questdb_pg_wire_connections{instance=~\"" + questDbInstance + "\"}"
                                + " or questdb_line_tcp_connections{instance=~\"" + questDbInstance + "\"}"
                                + " or questdb_http_connections{instance=~\"" + questDbInstance + "\"} )",
                        start, end));

                // Erreurs/s
                m.put("db_err_qps", prometheusService.queryTimeSeries(
                        "sum( rate(questdb_unhandled_errors_total{instance=~\"" + questDbInstance + "\"}[10s])"
                                + " or rate(questdb_pg_wire_errors_total{instance=~\"" + questDbInstance + "\"}[10s]) )",
                        start, end));

                // Heap JVM utilisée (gauge)
                m.put("db_heap", prometheusService.queryTimeSeries(
                        "(questdb_memory_jvm_total{instance=~\"" + questDbInstance + "\"}"
                                + " - questdb_memory_jvm_free{instance=~\"" + questDbInstance + "\"})",
                        start, end));

                // Backlog WAL (gauge)
                m.put("db_wal_backlog", prometheusService.queryTimeSeries(
                        "(questdb_wal_apply_seq_txn{instance=~\"" + questDbInstance + "\"}"
                                + " - questdb_wal_apply_writer_txn{instance=~\"" + questDbInstance + "\"})",
                        start, end));
                break;
            }

            case IOTDB: {
                // QPS (rate -> [10s]) sans '+'
                m.put("db_qps", prometheusService.queryTimeSeries(
                        "max by (instance) (rate(query_execution_seconds_count{instance=~\"" + iotDbInstance + "\"}[10s]))",
                        start, end));

                // Connexions (gauge)
                m.put("db_conns", prometheusService.queryTimeSeries(
                        "sum(thrift_connections{instance=~\"" + iotDbInstance + "\"})",
                        start, end));

                // Erreurs/s (rate -> [10s])
                m.put("db_err_qps", prometheusService.queryTimeSeries(
                        "sum(rate(logback_events_total{level=\"error\",instance=~\"" + iotDbInstance + "\"}[10s]))",
                        start, end));

                // Heap JVM (gauge)
                m.put("db_heap", prometheusService.queryTimeSeries(
                        "sum by (instance) (jvm_memory_used_bytes{area=\"heap\",instance=~\"" + iotDbInstance + "\"})",
                        start, end));

                // Backlog (gauge)
                m.put("db_wal_backlog", prometheusService.queryTimeSeries(
                        "sum(queue{name=\"flush\",status=\"waiting\",instance=~\"" + iotDbInstance + "\"})",
                        start, end));
                break;
            }

            default:
                m.put("db_qps", Map.of());
                m.put("db_conns", Map.of());
                m.put("db_err_qps", Map.of());
                m.put("db_heap", Map.of());
                m.put("db_wal_backlog", Map.of());
        }

        return m;
    }


    public long getInsertedSoFar() {
        return counter.getCounter();
    }

    @Transactional
    public SimulationConfig getOrCreate(SimulationConfig wanted) {
        wanted.normalize();
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnorePaths("id");

        Example<SimulationConfig> example = Example.of(wanted, matcher);

        return configRepository.findOne(example).orElseGet(() -> {
            try {
                return configRepository.save(wanted);
            } catch (DataIntegrityViolationException e) {
                return configRepository.findOne(example).orElseThrow(() -> e);
            }
        });
    }
}
