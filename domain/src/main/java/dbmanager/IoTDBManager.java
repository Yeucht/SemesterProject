package dbmanager;

import config.SimulationConfig;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionDataSetWrapper;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.read.common.Field;
import org.apache.iotdb.tsfile.read.common.RowRecord;

/**
 * IoTDB config
 enable_auto_create_schema=true
 default_storage_group_level=1 (partioning by device)
 dn_metric_reporter_list=PROMETHEUS
 dn_metric_level=IMPORTANT
 dn_metric_prometheus_reporter_port=9091
 */
public class IoTDBManager extends DBManager {
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final SessionPool sessionPool;
    private static final String STORAGE_GROUP = "root.smart_meter";

    public IoTDBManager(SimulationConfig config) {
        super(config);
        this.host = System.getenv().getOrDefault("IOTDB_HOST", "iotdb");
        this.port = Integer.parseInt(
                System.getenv().getOrDefault("IOTDB_PORT", "6667")
        );
        this.user = System.getenv().getOrDefault("IOTDB_USER", "root");
        this.password = System.getenv().getOrDefault("IOTDB_PASSWORD", "root");
        int poolSize = Integer.parseInt(
                System.getenv().getOrDefault("IOTDB_POOL_SIZE", "5")
        );
        this.sessionPool = new SessionPool(host, port, user, password, poolSize);

    }

    @Override
    public boolean clearTables() {
        if (!config.getClearTablesFlag()) {
            System.out.println("Table clearing disabled, skipping.");
            return false;
        }

        try {
            // Supprime storage group + toutes les time‑series définies
            sessionPool.executeNonQueryStatement(
                    "DELETE DATABASE " + STORAGE_GROUP
            );
            System.out.println("Storage group and tables deleted successfully.");
            return true;
        } catch (StatementExecutionException | IoTDBConnectionException e) {
            System.err.println("Failed to delete schemas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public SessionPool getSessionPool() {
        return sessionPool;
    }

    @Override
    public int getRowCount() {
        final String sql = "SELECT COUNT(payload) FROM " + STORAGE_GROUP + ".**";

        long total = 0L;
        try (SessionDataSetWrapper rs = sessionPool.executeQueryStatement(sql)) {
            while (rs.hasNext()) {
                RowRecord row = rs.next();
                // L’agrégat renvoie une ligne dont chaque Field est le count d’un timeseries
                for (Field f : row.getFields()) {
                    if (f != null) {
                        total += f.getLongV(); // COUNT(...) est de type long
                    }
                }
            }
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            e.printStackTrace();
        }

        return (total > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) total;
    }

    @Override
    public int getNumberMeters() {
        //Try: COUNT DEVICES
        final String sqlCount = "COUNT DEVICES root.smart_meter.**";
        try (SessionDataSetWrapper rs = sessionPool.executeQueryStatement(sqlCount)) {
            if (rs.hasNext()) {
                RowRecord row = rs.next();
                Field f = row.getFields().get(0);
                if (f != null) {
                    long cnt = f.getLongV(); // ou getIntV/getLongV selon version
                    return (cnt > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) cnt;
                }
            }
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            e.printStackTrace();
        }

        //Fallback : SHOW DEVICES & count lines
        final String sqlShow = "SHOW DEVICES root.smart_meter.**";
        int count = 0;
        try (SessionDataSetWrapper rs = sessionPool.executeQueryStatement(sqlShow)) {
            while (rs.hasNext()) {
                rs.next();
                count++;
            }
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            e.printStackTrace();
        }
        return count;
    }


    public String getStorageGroup(){
        return STORAGE_GROUP;
    }


}