package simulation;

import config.SimulationConfig;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SimulationRun {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private SimulationConfig config;

    private Instant startedAt;
    private Instant endedAt;
    private long totalInserted;
    private double avgInsertRate;
    private long finalDbSizeBytes;

    @OneToMany(mappedBy = "run", cascade = CascadeType.ALL)
    private List<MetricPoint> metrics = new ArrayList<>();

    public SimulationRun(SimulationConfig config) {
        this.config = config;
        this.startedAt = Instant.now();
        this.endedAt = null;
        this.totalInserted = 0;
        this.avgInsertRate = 0;
        this.finalDbSizeBytes = 0;
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

    public long getTotalInserted() {
        return totalInserted;
    }

    public void setTotalInserted(long totalInserted) {
        this.totalInserted = totalInserted;
    }

    public double getAvgInsertRate() {
        return avgInsertRate;
    }

    public void setAvgInsertRate(double avgInsertRate) {
        this.avgInsertRate = avgInsertRate;
    }

    public long getFinalDbSizeBytes() {
        return finalDbSizeBytes;
    }

    public void setFinalDbSizeBytes(long finalDbSizeBytes) {
        this.finalDbSizeBytes = finalDbSizeBytes;
    }

    public List<MetricPoint> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<MetricPoint> metrics) {
        this.metrics = metrics;
    }
}
