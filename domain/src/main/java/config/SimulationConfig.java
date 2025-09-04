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
                        "hesSynchronized",
                        "hesRateRandomness",
                        "mdmsBatch",
                        "mdmsBatchSize",
                        "probeRate",
                        "hesProbeRate"
                }
        )
)
public class SimulationConfig {

    public enum DatabaseType {
        IOTDB,
        QUESTDB
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private DatabaseType dbType = DatabaseType.QUESTDB;

    //Individual Meters Config
    private boolean meterFlag = true; //Activate
    private int meterRate = 4; //Indiv meters sending rate (per hour)
    private float meterRateRandomness = 0.2f; //sending rate is between (1-x) and (1+x) * meterRate_associated_time_interval
    private int nbrSmartMeters = 5000;
    private int probeRate = 4; //Indiv meters probing rate (must be >= meterRate)

    //HES Config
    private boolean hesFlag = true; //Activate
    private boolean hesSynchronized = false; //all HES at time t (true) or if split at different times
    private int hesRate = 2; //HES sending rate (per day)
    private int nbrHES = 400;
    private int nbrMetersPerHES = 10; // smartmeters par HES
    private float nbrMetersPerHESRandomness = 0.2f; //between (1-x) and (1+x) * nbrMetersPerHES
    private int hesRateRandomness = 50;
    /*[0,100] defines if HES data comes at perfectly regular times during the interval (0) or if it's random over the interval
    Here 0 means at time t=0, one HES sends a datapacket. On time t+(HESRate/nbrHES), a second HES sends data.
    100 means the first sending time for each HES is set completely randomly over the first loop time (i.e. 1/HESRate day)
    Inbetween = linear interpolation
    */

    //HES Meters Config
    private int hesMeterRate = 4; //Sending rate
    private float hesMeterRateRandomness = 0.2f; //sending rate is between (1-x) and (1+x) * meterRate_associated_time_interval
    private int hesProbeRate = 4; //Probing rate


    //MDMS Config
    private String url = "http://sp-service:8080/api/injection/data";
    private boolean clearTablesFlag = false;
    private long retentionWindowMillis = 1000000000; //unused for now
    private boolean mdmsBatch = false;
    private int mdmsBatchSize = 10;

    public SimulationConfig() {}

    public SimulationConfig(
            DatabaseType dbType,
            boolean clearOnStart,
            long retentionWindowMillis,
            int meterRate,
            float meterRateRandomness,
            String url,
            int nbrSmartMeters,
            boolean mdmsBatch,
            int mdmsBatchSize,
            int hesMeterRate,
            float hesMeterRateRandomness,
            int probeRate,
            int hesProbeRate
    ) {
        this.dbType = dbType;
        this.clearTablesFlag = clearOnStart;
        this.retentionWindowMillis = retentionWindowMillis;
        this.meterRateRandomness = meterRateRandomness;
        this.url = url;
        this.nbrSmartMeters = nbrSmartMeters;
        this.mdmsBatch = mdmsBatch;
        this.mdmsBatchSize = mdmsBatchSize;
        this.hesMeterRateRandomness = hesMeterRateRandomness;
        this.probeRate = probeRate;
        this.hesProbeRate = hesProbeRate;
        if (meterRate > probeRate){
            this.meterRate = probeRate;
        }else{
            this.meterRate = meterRate;
        }
        if (hesMeterRate > hesProbeRate){
            this.hesMeterRate = hesProbeRate;
        }else{
            this.hesMeterRate = hesMeterRate;
        }
    }

    //normality check
    public void normalize() {
        if (probeRate < 0) probeRate = 0;
        if (hesProbeRate < 0) hesProbeRate = 0;
        if (meterRate < 0) meterRate = 0;
        if (hesMeterRate < 0) hesMeterRate = 0;

        if (meterRate > probeRate) {
            meterRate = probeRate;
        }
        if (hesMeterRate > hesProbeRate) {
            hesMeterRate = hesProbeRate;
        }
    }

    public int getProbeRate(){
        return probeRate;
    }

    public void setProbeRate(int probeRate){
        this.probeRate = probeRate;
    }

    public int getHesProbeRate(){
        return hesProbeRate;
    }

    public void setHesProbeRate(int hesProbeRate){
        this.hesProbeRate = hesProbeRate;
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
