package dbmanager;

import config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuestDBManager extends DBManager {
    // On récupère les credentials depuis l'env ou on tombe sur des valeurs par défaut
    private final String url;
    private final String user;
    private final String password;

    public QuestDBManager(Config config) {
        super(config);
        this.url = System.getenv().getOrDefault(
                "QUESTDB_URL",
                "jdbc:postgresql://questdb:8812/qdb"    // nom de service + port JDBC
        );
        this.user = System.getenv().getOrDefault("QUESTDB_USER", "admin");
        this.password = System.getenv().getOrDefault("QUESTDB_PASSWORD", "quest");
    }

    @Override
    public boolean clearTables() {


        if (!this.config.getClearTablesFlag()) {
            System.out.println("Table clearing is disabled. Skipping...");
            return false;
        }

        String fetchTables =
                "SELECT table_name " +
                "FROM information_schema.tables " +
                "WHERE table_schema = 'public'";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(fetchTables)) {

            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                tables.add(rs.getString("table_name"));
            }

            for (String table : tables) {
                // On quote le nom de table pour éviter les erreurs si nom bizarre
                String drop = "DROP TABLE IF EXISTS \"" + table + "\"";
                try (Statement dropStmt = conn.createStatement()) {
                    dropStmt.executeUpdate(drop);
                    System.out.println("Dropped table: " + table);
                }
            }

            System.out.println("All tables dropped successfully.");
            return true;

        } catch (SQLException e) {
            System.err.println("Unable to drop tables: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
