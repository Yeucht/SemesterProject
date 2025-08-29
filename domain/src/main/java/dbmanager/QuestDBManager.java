package dbmanager;

import config.SimulationConfig;
import jakarta.annotation.PostConstruct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestDBManager extends DBManager {
    // On récupère les credentials depuis l'env ou on tombe sur des valeurs par défaut
    private final String url;
    private final String user;
    private final String password;
    private static final String TABLE_NAME = "smart_meter";
    private static final String QUESTDB_URL = "jdbc:postgresql://questdb:8812/qdb";

    public QuestDBManager(SimulationConfig config) {
        super(config);
        this.url = System.getenv().getOrDefault(
                "QUESTDB_URL",
                "jdbc:postgresql://questdb:8812/qdb"    // nom de service + port JDBC
        );
        this.user = System.getenv().getOrDefault("QUESTDB_USER", "admin");
        this.password = System.getenv().getOrDefault("QUESTDB_PASSWORD", "quest");
    }

    @PostConstruct
    public void createTableIfNotExist() {


        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt  = conn.createStatement();
             Statement addStmt = conn.createStatement()) {

            String recreateTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + ";";
            addStmt.executeUpdate(recreateTable);
            System.out.println("Created table smart_meter.");

        }catch (SQLException e) {
            System.out.println("Couldn't create table: " + e.getMessage());
        }
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

            createTableIfNotExist();

            System.out.println("All tables dropped successfully. Recreated smart_meter.");
            return true;

        } catch (SQLException e) {
            createTableIfNotExist();
            System.err.println("Unable to drop tables: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int getRowCount() {
        int count = 0;
        String sql = "SELECT count(*) FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(QUESTDB_URL, "admin", "quest");
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {

        }

        return count;
    }

    @Override
    public int getNumberMeters() {
        int count = 0;
        String sql = "SELECT count(DISTINCT auth_serial_number) FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(QUESTDB_URL, "admin", "quest");
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {

        }

        return count;
    }

    // QuestDBManager.java
    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}
