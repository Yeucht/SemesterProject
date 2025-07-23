package service;

import config.Config;
import dbmanager.DBManager;
import dbmanager.QuestDBManager;
import factories.DBManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public class DBManagerService {

    private ConfigService configService;
    private DBManagerFactory dbManagerFactory;
    private DBManager dbManager = new QuestDBManager(new Config());

    public DBManagerService(ConfigService configService, DBManagerFactory dbManagerFactory) {
        this.configService = configService;
        this.dbManagerFactory = dbManagerFactory;
        this.dbManager = dbManagerFactory.createManager(configService.getConfig());
    }

    public boolean clearTables() {
        return dbManager.clearTables();
    }


}
