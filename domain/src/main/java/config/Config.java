package config;

public class Config {
    public enum DatabaseType {
        IOTDB,
        INFLUXDB,
        QUESTDB
    }

    private DatabaseType dbType;
    private boolean clearTablesFlag = false;
    private long retentionWindowMillis;

    public Config(DatabaseType dbType, boolean clearOnStart, long retentionWindowMillis) {
        this.dbType = dbType;
        this.clearTablesFlag = clearOnStart;
        this.retentionWindowMillis = retentionWindowMillis;
    }

    public DatabaseType getDbType() {
        return dbType;
    }

    public boolean getClearTablesFlag() {
        return clearTablesFlag;
    }

    public long getRetentionWindowMillis() {
        return retentionWindowMillis;
    }
}
