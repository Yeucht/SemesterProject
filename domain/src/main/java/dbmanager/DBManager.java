package dbmanager;

import config.Config;

public abstract class DBManager {
    protected Config config = new Config();


    public DBManager(Config config) {
        this.config = config;
    }

    //overload for IoTDBManager
    public DBManager() {}

    public abstract boolean clearTables();
}