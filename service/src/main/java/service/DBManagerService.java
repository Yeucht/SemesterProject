package service;

import config.Config;
import dbmanager.DBManager;
import dbmanager.QuestDBManager;
import factories.DBManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


public class DBManagerService {

    private Config config;
    private DBManagerFactory dbManagerFactory = new DBManagerFactory();
    private DBManager dbManager;

    public DBManagerService(Config config) {
        this.config = config;
        this.dbManager = dbManagerFactory.createManager(config);
    }

    public boolean clearTables() {
        return dbManager.clearTables();
    }

    public DBManager getDbManager() {
            return dbManager;
    }

    public void update(Config config) {
        this.config = config;
        this.dbManager = dbManagerFactory.createManager(config);
    }

    public Config getConfig() {
        return config;
    }
}
