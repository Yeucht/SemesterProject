package service;

import config.SimulationConfig;
import dbmanager.DBManager;
import factories.DBManagerFactory;
import jakarta.annotation.PostConstruct;


public class DBManagerService {

    private SimulationConfig config;
    private DBManagerFactory dbManagerFactory = new DBManagerFactory();
    private DBManager dbManager;
    private Counter counter;

    public DBManagerService(SimulationConfig config) {
        this.config = config;
        this.dbManager = dbManagerFactory.createManager(config);
    }

    public DBManagerService(SimulationConfig config, Counter counter) {
        this.config = config;
        this.dbManager = dbManagerFactory.createManager(config);
        this.counter = counter;
    }

    public boolean clearTables() {
        updateCounter(0);
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

    public void updateCounter(int i){
        counter.setCounter(i);
    }

    public void updateCounter(){this.counter.setCounter(dbManager.getRowCount());}

    @PostConstruct
    public void init() {
        counter.setCounter(dbManager.getRowCount());
    }
}
