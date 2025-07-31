package config;

import jakarta.persistence.*;

@Entity
public class SimulationConfig {

    public enum DatabaseType {
        IOTDB,
        INFLUXDB,
        QUESTDB
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Enumerated(EnumType.STRING)
    private DatabaseType dbType = DatabaseType.QUESTDB;

    private boolean clearTablesFlag = false;
    private long retentionWindowMillis = 1000000000;
    private float rate = 3.0f;
    private float rateRandomess = 0.8f;
    private String url = "http://sp-service:8080/api/injection/data";
    private int nbrSmartMeters = 5000;
    private boolean batch = false;
    private int batchSize = 10;
    private float batchRandomness = 0.2f;
    private boolean mdmsBatch = false;
    private int mdmsBatchSize = 10;

    public SimulationConfig() {}

    public SimulationConfig(
            DatabaseType dbType,
            boolean clearOnStart,
            long retentionWindowMillis,
            int rate,
            float rateRandomess,
            String url,
            int nbrSmartMeters,
            boolean batch,
            int batchSize,
            float batchRandomness,
            boolean mdmsBatch,
            int mdmsBatchSize
    ) {
        this.dbType = dbType;
        this.clearTablesFlag = clearOnStart;
        this.retentionWindowMillis = retentionWindowMillis;
        this.rate = rate;
        this.rateRandomess = rateRandomess;
        this.url = url;
        this.nbrSmartMeters = nbrSmartMeters;
        this.batch = batch;
        this.batchSize = batchSize;
        this.batchRandomness = batchRandomness;
        this.mdmsBatch = mdmsBatch;
        this.mdmsBatchSize = mdmsBatchSize;
    }

    public DatabaseType getDbType() {
        return dbType;
    }

    public void setDbType(DatabaseType dbType) {
        this.dbType = dbType;
    }

    public boolean getClearTablesFlag() {
        return clearTablesFlag;
    }

    public void setClearTablesFlag(boolean clearTablesFlag) {
        this.clearTablesFlag = clearTablesFlag;
    }

    public long getRetentionWindowMillis() {
        return retentionWindowMillis;
    }

    public void setRetentionWindowMillis(long retentionWindowMillis) {
        this.retentionWindowMillis = retentionWindowMillis;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getRateRandomess() {
        return rateRandomess;
    }

    public void setRateRandomess(float rateRandomess) {
        this.rateRandomess = rateRandomess;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNbrSmartMeters() {
        return nbrSmartMeters;
    }

    public void setNbrSmartMeters(int nbrSmartMeters) {
        this.nbrSmartMeters = nbrSmartMeters;
    }

    public boolean isBatch() {
        return batch;
    }

    public void setBatch(boolean batch) {
        this.batch = batch;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public float getBatchRandomness() {
        return batchRandomness;
    }

    public void setBatchRandomness(float batchRandomness) {
        this.batchRandomness = batchRandomness;
    }

    public void setMdmsBatch(boolean mdmsBatch) {
        this.mdmsBatch = mdmsBatch;
    }

    public boolean getMdmsBatch() {
        return mdmsBatch;
    }

    public void setMdmsBatchSize(int mdmsBatchSize) {
        this.mdmsBatchSize = mdmsBatchSize;
    }

    public int getMdmsBatchSize() {
        return mdmsBatchSize;
    }

}
