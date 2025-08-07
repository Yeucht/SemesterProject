package dbmanager;

import config.SimulationConfig;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;

/**
 * Manager pour IoTDB alimenté par Config et variables d’environnement.
 * Avec schema_auto_create=true, IoTDB créera
 * automatiquement storage group et timeseries au premier INSERT.
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
                System.getenv().getOrDefault("IOTDB_POOL_SIZE", "3")
        );
        this.sessionPool = new SessionPool(host, port, user, password, poolSize);

        // Note : plus besoin de SET STORAGE GROUP ici,
        // IoTDB le fera automatiquement au premier INSERT
    }

    @Override
    public boolean clearTables() {
        if (!config.getClearTablesFlag()) {
            System.out.println("Suppression des schémas désactivée. Skip.");
            return false;
        }

        try {
            // Supprime storage group + toutes les time‑series définies
            sessionPool.executeNonQueryStatement(
                    "DELETE DATABASE " + STORAGE_GROUP
            );
            System.out.println("Storage group et schémas supprimés avec succès.");
            return true;
        } catch (StatementExecutionException | IoTDBConnectionException e) {
            System.err.println("Échec de la suppression des schémas : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public SessionPool getSessionPool() {
        return sessionPool;
    }

    @Override
    public int getRowCount() { return 1;}

}