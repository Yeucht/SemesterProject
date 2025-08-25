package config;

import jakarta.persistence.*;

@Entity
@Table(
        name = "simulation_config",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_simulation_config",
                columnNames = {
                        "dbType",
                        "clearTablesFlag",
                        "retentionWindowMillis",
                        "meterFlag",
                        "hesFlag",
                        "meterRate",
                        "meterRateRandomness",
                        "hesRate",
                        "url",
                        "nbrSmartMeters",
                        "nbrHES",
                        "nbrMetersPerHES",
                        "hesMeterRate",
                        "hesMeterRateRandomness",
                        "nbrMetersPerHESRandomness",
                        "meterPayloadBatch",
                        "meterPayloadBatchSize",
                        "meterPayloadBatchRandomness",
                        "hesSynchronized",
                        "hesRateRandomness",
                        "mdmsBatch",
                        "mdmsBatchSize"
                }
        )
)
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

    //would be suitable to create meterConfig and HESConfig eventually

    //Individual Meters Config
    private boolean meterFlag = true; //active les smartmeters individuels
    private int meterRate = 4; //probe par heure
    private float meterRateRandomness = 0.2f; //between (1-x) and (1+x) * meterRate
    private int nbrSmartMeters = 5000;
    private boolean meterPayloadBatch = false;
    private int meterPayloadBatchSize = 10;
    private float meterPayloadBatchRandomness = 0.2f; //between (1-x) and (1+x) * batch size

    //HES Config
    private boolean hesFlag = true; // active les Head-end systems
    private boolean hesSynchronized = false; //all HES at time t or if split at different times
    private int hesRate = 2; //par jour
    private int nbrHES = 400;
    private float nbrMetersPerHESRandomness = 0.2f; //between (1-x) and (1+x) * nbrMetersPerHES

    //HES Meters Config
    private int hesMeterRate = 4; //probe par heure
    private float hesMeterRateRandomness = 0.2f; //between (1-x) and (1+x) * meterRate
    private int nbrMetersPerHES = 10; // smartmeters par HES
    private int hesRateRandomness = 50; //[0,100] defines if HES data comes at perfectly regular times (0) or if it's random over the timeframe
    //Here 0 means at time t=0, one HES sends a datapacket. On time t+(HESRate/nbrHES), a second HES sends data.
    //100 means the first sending time for each HES is set completely randomly over the first loop time (i.e. 1/HESRate day)
    //private int hesMeterPayloadBatchSize = 10;
    //private float hesMeterPayloadBatchRandomness = 0.2f;

    //MDMS Config
    private String url = "http://sp-service:8080/api/injection/data";
    private boolean clearTablesFlag = false;
    private long retentionWindowMillis = 1000000000; //unused for now
    private boolean mdmsBatch = false; //unused for now (cache data in mdms)
    private int mdmsBatchSize = 10; //unused for now

    public SimulationConfig() {}

    public SimulationConfig(
            DatabaseType dbType,
            boolean clearOnStart,
            long retentionWindowMillis,
            int meterRate,
            float meterRateRandomness,
            String url,
            int nbrSmartMeters,
            boolean meterPayloadBatch,
            int meterPayloadBatchSize,
            float meterPayloadBatchRandomness,
            boolean mdmsBatch,
            int mdmsBatchSize,
            int hesMeterRate,
            float hesMeterRateRandomness
    ) {
        this.dbType = dbType;
        this.clearTablesFlag = clearOnStart;
        this.retentionWindowMillis = retentionWindowMillis;
        this.meterRate = meterRate;
        this.meterRateRandomness = meterRateRandomness;
        this.url = url;
        this.nbrSmartMeters = nbrSmartMeters;
        this.meterPayloadBatch = meterPayloadBatch;
        this.meterPayloadBatchSize = meterPayloadBatchSize;
        this.meterPayloadBatchRandomness = meterPayloadBatchRandomness;
        this.mdmsBatch = mdmsBatch;
        this.mdmsBatchSize = mdmsBatchSize;
        this.hesMeterRate = hesMeterRate;
        this.hesMeterRateRandomness = hesMeterRateRandomness;
    }

    public float getHesMeterRateRandomness(){return hesMeterRateRandomness;}

    public void setHesMeterRateRandomness(float HESMeterRateRandomness){
        this.hesMeterRateRandomness = HESMeterRateRandomness;
    }

    public int getHesMeterRate(){return hesMeterRate;}

    public void setHesMeterRate(int HESMeterRate){
        this.hesMeterRate = HESMeterRate;
    }

    public int getHesRate(){return hesRate;}

    public void setHesRate(int HESRate){
        this.hesRate = HESRate;
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

    public int getMeterRate() {
        return meterRate;
    }

    public void setMeterRate(int rate) {
        this.meterRate = rate;
    }

    public float getMeterRateRandomness() {
        return meterRateRandomness;
    }

    public void setMeterRateRandomness(float rateRandomness) {
        this.meterRateRandomness = rateRandomness;
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

    public boolean isMeterFlag(){ return meterFlag; }
    public void setMeterFlag(boolean meterFlag){ this.meterFlag = meterFlag; }

    public boolean isHesFlag(){ return hesFlag; }
    public void setHesFlag(boolean HESFlag){ this.hesFlag = HESFlag; }

    public int getNbrHES(){ return nbrHES; }
    public void setNbrHES(int nbrHES){ this.nbrHES = nbrHES; }

    public int getNbrMetersPerHES(){ return nbrMetersPerHES; }
    public void setNbrMetersPerHES(int n){ this.nbrMetersPerHES = n; }

    public boolean isHesSynchronized(){ return hesSynchronized; }
    public void setHesSynchronized(boolean v){ this.hesSynchronized = v; }

    public int getHesRateRandomness(){ return hesRateRandomness; }
    public void setHesRateRandomness(int v){ this.hesRateRandomness = v; }

    public float getNbrMetersPerHESRandomness(){ return nbrMetersPerHESRandomness; }
    public void setNbrMetersPerHESRandomness(float v){ this.nbrMetersPerHESRandomness = v; }


}
