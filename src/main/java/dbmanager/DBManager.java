package dbmanager;

public abstract class DBManager {
    protected boolean clearTablesFlag;

    public DBManager(boolean clean) {
        this.clearTablesFlag = clean;
    }

    public abstract boolean clearTables();
}