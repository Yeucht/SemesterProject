package service;

import config.SimulationConfig;
import dbmanager.DBManager;
import factories.DBManagerFactory;


public class DBManagerService {

    private SimulationConfig config;
    private DBManagerFactory dbManagerFactory = new DBManagerFactory();
    private DBManager dbManager;

    public DBManagerService(SimulationConfig config) {
        this.config = config;
        this.dbManager = dbManagerFactory.createManager(config);
    }

    public boolean clearTables() {
        return dbManager.clearTables();
    }

    public DBManager getDbManager() {
            return dbManager;
    }

    public void update(SimulationConfig config) {
        this.config = config;
        this.dbManager = dbManagerFactory.createManager(config);
    }

    public SimulationConfig getConfig() {
        return config;
    }
}
