package dbmanager;

public abstract class DBManager {
    protected boolean clearTablesFlag;


    public DBManager(boolean clean) {
        this.clearTablesFlag = clean;
        clearTables();
    }

    //overload for IoTDBManager
    public DBManager() {}

    public abstract boolean clearTables();
}