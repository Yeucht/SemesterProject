package simulation;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class MetricPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant timestamp;
    private long insertedSoFar;
    private double cpuUsage;
    private long memoryUsed;

    @ManyToOne
    @JoinColumn
    private SimulationRun run;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public long getInsertedSoFar() {
        return insertedSoFar;
    }

    public void setInsertedSoFar(long insertedSoFar) {
        this.insertedSoFar = insertedSoFar;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public SimulationRun getRun() {
        return run;
    }

    public void setRun(SimulationRun run) {
        this.run = run;
    }
}
