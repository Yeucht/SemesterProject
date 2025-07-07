package service;

import config.Config;
import dbmanager.DBManager;
import factories.DBManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DBManagerService {

    private final ConfigService configService;
    private final DBManagerFactory dbManagerFactory;

    @Autowired
    public DBManagerService(ConfigService configService, DBManagerFactory dbManagerFactory) {
        this.configService = configService;
        this.dbManagerFactory = dbManagerFactory;
    }

    public boolean clearTables() {
        Config config = configService.getConfig();
        DBManager dbManager = dbManagerFactory.createManager(config);
        return dbManager.clearTables();
    }
}
