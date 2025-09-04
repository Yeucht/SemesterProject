package dbmanager;

import config.SimulationConfig;

public abstract class DBManager {
    protected SimulationConfig config = new SimulationConfig();

    public DBManager(SimulationConfig config) {
        this.config = config;
    }

    public DBManager() {}

    public abstract boolean clearTables();

    public SimulationConfig getConfig() {
        return config;
    }

    public abstract int getRowCount();

    public abstract int getNumberMeters();
}