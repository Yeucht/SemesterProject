package simulation;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class MetricPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant timestamp;

    //App
    private long insertedSoFar;
    private double cpuUsage10s;          // %
    private long totalMemoryUsed;          // legacy: heap bytes (tu peux déprécier plus tard)
    private double diskUsed;          // %
    private double rps10s;            // req/s (status=200)
    private long heapUsedBytes;       // bytes
    private long nonHeapUsedBytes;    // bytes
    private int threadsLive;          // count

    //DB Comparables - might add DBMetricPoint subclasses with db specific metrics
    private double dbQueryQps10s;     // req/s
    private int    dbConnections;  // count
    private double dbErrorQps10s;     // errors/s
    private long   dbHeapUsedBytes;// bytes
    private long   dbWalBacklog;   // (txns en retard QDB / tasks en attente IoTDB)


    //Network, not set for now
    private double netIn;
    private double netOut;

    @ManyToOne
    @JoinColumn
    private SimulationRun run;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public long getInsertedSoFar() { return insertedSoFar; }
    public void setInsertedSoFar(long insertedSoFar) { this.insertedSoFar = insertedSoFar; }

    public double getCpuUsage10s() { return cpuUsage10s; }
    public void setCpuUsage10s(double cpuUsage) { this.cpuUsage10s = cpuUsage; }

    public long getTotalMemoryUsed() { return totalMemoryUsed; } // (legacy)
    public void setTotalMemoryUsed(long memoryUsed) { this.totalMemoryUsed = memoryUsed; }

    public double getDiskUsed(){ return diskUsed; }
    public void setDiskUsed(double diskUsage){ this.diskUsed = diskUsage; }

    public double getRps10s() { return rps10s; }
    public void setRps10s(double rps2xx) { this.rps10s = rps2xx; }

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

    public void setDbQueryQps10s(double v) {
        this.dbQueryQps10s = v;
    }

    public double getDbQueryQps10s() { return dbQueryQps10s; }

    public void setDbConnections(int v) { this.dbConnections = v; }

    public int getDbConnections() { return dbConnections; }

    public void setDbHeapUsedBytes(long v) { this.dbHeapUsedBytes = v; }

    public long getDbHeapUsedBytes() { return dbHeapUsedBytes; }
    public void setDbWalBacklog(long v) { this.dbWalBacklog = v; }
    public long getDbWalBacklog() { return dbWalBacklog; }

    public void setDbErrorQps10s(double dbErrorQps) {
        this.dbErrorQps10s = dbErrorQps;
    }
    public double getDbErrorQps10s() { return dbErrorQps10s; }
}
