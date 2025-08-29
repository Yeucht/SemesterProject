package simulation;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class MetricPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant timestamp;

    // App
    private long insertedSoFar;
    private double cpuUsage;          // %
    private long totalMemoryUsed;          // legacy: heap bytes (tu peux déprécier plus tard)

    // Nouveaux champs (alignés aux panels)
    private double diskUsed;          // %
    private double rps;            // req/s (status=200)
    private long heapUsedBytes;       // bytes
    private long nonHeapUsedBytes;    // bytes
    private int threadsLive;          // count

    // Réseau si tu veux les remplir plus tard
    private double netIn;
    private double netOut;

    @ManyToOne
    @JoinColumn
    private SimulationRun run;

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public long getInsertedSoFar() { return insertedSoFar; }
    public void setInsertedSoFar(long insertedSoFar) { this.insertedSoFar = insertedSoFar; }

    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }

    public long getTotalMemoryUsed() { return totalMemoryUsed; } // (legacy)
    public void setTotalMemoryUsed(long memoryUsed) { this.totalMemoryUsed = memoryUsed; }

    public double getDiskUsed(){ return diskUsed; }
    public void setDiskUsed(double diskUsage){ this.diskUsed = diskUsage; }

    public double getRps() { return rps; }
    public void setRps(double rps2xx) { this.rps = rps2xx; }

    public long getHeapUsedBytes() { return heapUsedBytes; }
    public void setHeapUsedBytes(long heapUsedBytes) { this.heapUsedBytes = heapUsedBytes; }

    public long getNonHeapUsedBytes() { return nonHeapUsedBytes; }
    public void setNonHeapUsedBytes(long nonHeapUsedBytes) { this.nonHeapUsedBytes = nonHeapUsedBytes; }

    public int getThreadsLive() { return threadsLive; }
    public void setThreadsLive(int threadsLive) { this.threadsLive = threadsLive; }

    public double getNetIn(){ return netIn; }
    public void setNetIn(double netIn){ this.netIn = netIn; }

    public double getNetOut(){ return netOut; }
    public void setNetOut(double netOut){ this.netOut = netOut; }

    public SimulationRun getRun() { return run; }
    public void setRun(SimulationRun run) { this.run = run; }
}
