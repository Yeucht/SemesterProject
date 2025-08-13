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

    //would be suitable to create meterConfig and HESConfig
    private boolean clearTablesFlag = false;
    private long retentionWindowMillis = 1000000000; //usued for now
    private boolean meterFlag = true; //active les smartmeters individuels
    private boolean HESFlag = true; // active les Head-end systems
    private int meterRate = 4; //probe par heure
    private float meterRateRandomess = 0.2f; //between (1-x) and (1+x) * nbrMetersPerHES
    private int HESRate = 2; //par jour
    private String url = "http://sp-service:8080/api/injection/data";
    private int nbrSmartMeters = 5000;
    private int nbrHES = 400;
    private int nbrMetersPerHES = 10; // smartmeters par HES
    private float nbrMetersPerHESRandomness = 0.2f; //between (1-x) and (1+x) * nbrMetersPerHES
    private boolean meterPayloadBatch = false;
    private int meterPayloadBatchSize = 10;
    private float meterPayloadBatchRandomness = 0.2f; //between (1-x) and (1+x) * batch size
    private boolean HESSynchronized = false; //all HES at time t or if split at different times
    private int HESRateRandomness = 50; //[0,100] defines if HES data comes at perfectly regular times (0) or if it's random over the timeframe
    //Here 0 means at time t=0, one HES sends a datapacket. On time t+(HESRate/nbrHES), a second HES sends data.
    //100 means the first sending time for each HES is set completely randomly over the first loop time (i.e. 1/HESRate day)

    private boolean mdmsBatch = false; //unused for now (cache data in mdms)
    private int mdmsBatchSize = 10; //unused for now

    public SimulationConfig() {}

    public SimulationConfig(
            DatabaseType dbType,
            boolean clearOnStart,
            long retentionWindowMillis,
            int meterRate,
            float meterRateRandomess,
            String url,
            int nbrSmartMeters,
            boolean meterPayloadBatch,
            int meterPayloadBatchSize,
            float meterPayloadBatchRandomness,
            boolean mdmsBatch,
            int mdmsBatchSize
    ) {
        this.dbType = dbType;
        this.clearTablesFlag = clearOnStart;
        this.retentionWindowMillis = retentionWindowMillis;
        this.meterRate = meterRate;
        this.meterRateRandomess = meterRateRandomess;
        this.url = url;
        this.nbrSmartMeters = nbrSmartMeters;
        this.meterPayloadBatch = meterPayloadBatch;
        this.meterPayloadBatchSize = meterPayloadBatchSize;
        this.meterPayloadBatchRandomness = meterPayloadBatchRandomness;
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

    public float getMeterRate() {
        return meterRate;
    }

    public void setMeterRate(float rate) {
        this.meterRate = rate;
    }

    public float getMeterRateRandomess() {
        return meterRateRandomess;
    }

    public void setMeterRateRandomess(float rateRandomess) {
        this.meterRateRandomess = rateRandomess;
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

    public boolean isMeterPayloadBatch() {
        return meterPayloadBatch;
    }

    public void setMeterPayloadBatch(boolean batch) {
        this.meterPayloadBatch = batch;
    }

    public int getMeterPayloadBatchSize() {
        return meterPayloadBatchSize;
    }

    public void setMeterPayloadBatchSize(int batchSize) {
        this.meterPayloadBatchSize = batchSize;
    }

    public float getMeterPayloadBatchRandomness() {
        return meterPayloadBatchRandomness;
    }

    public void setMeterPayloadBatchRandomness(float batchRandomness) {
        this.meterPayloadBatchRandomness = batchRandomness;
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

    public Long getId() {
        return id;
    }


}
