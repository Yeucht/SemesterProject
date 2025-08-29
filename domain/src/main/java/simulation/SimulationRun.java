package simulation;

import config.SimulationConfig;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SimulationRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private SimulationConfig config;

    private Instant startedAt;
    private Instant endedAt;
    private long metricPointsInserted;
    private double avgInsertRate;
    private long finalDbSize;
    private long originDbSize;

    @OneToMany(mappedBy = "run", cascade = CascadeType.ALL)
    private List<MetricPoint> metrics = new ArrayList<>();

    public SimulationRun(SimulationConfig config) {
        this.config = config;
        this.startedAt = Instant.now();
        this.endedAt = null;
        this.metricPointsInserted = 0;
        this.avgInsertRate = 0;
        this.finalDbSize = 0;
    }

    public SimulationRun(){}

    public void add_point(MetricPoint point){
        this.metrics.add(point);
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SimulationConfig getConfig() {
        return config;
    }

    public void setConfig(SimulationConfig config) {
        this.config = config;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public long getMetricPointsInserted() {
        return metrics.size();
    }

    public void setMetricPointsInserted(long totalInserted) {
        this.metricPointsInserted = totalInserted;
    }

    public double getAvgInsertRate() {
        return avgInsertRate;
    }

    public void setAvgInsertRate(double avgInsertRate) {
        this.avgInsertRate = avgInsertRate;
    }

    public void setAvgInsertRateRelative() {
        this.avgInsertRate = ratePerSecond(finalDbSize-originDbSize, startedAt, endedAt);
    }

    public long getFinalDbSize() {
        return finalDbSize;
    }

    public void setFinalDbSize(long finalDbSizeBytes) {
        this.finalDbSize = finalDbSizeBytes;
    }

    public List<MetricPoint> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<MetricPoint> metrics) {
        this.metrics = metrics;
    }

    public void setOriginDbSize(int originDBSize) {
        this.originDbSize = originDBSize;
    }

    public long getOriginDbSize() {
        return originDbSize;
    }

    static double ratePerSecond(long count, Instant start, Instant end) {
        double seconds = Duration.between(start, end).toNanos() / 1_000_000_000.0; // secondes fractionnaires
        if (seconds <= 0) return 0.0; // ou Double.NaN selon ton choix
        return count / seconds; // req/s (ou items/s)
    }

}
