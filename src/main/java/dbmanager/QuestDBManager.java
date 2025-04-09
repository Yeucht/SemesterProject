package dbmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuestDBManager extends DBManager {
    private static final String URL = "jdbc:postgresql://localhost:8812/qdb"; // QuestDB PostgreSQL endpoint
    private static final String USER = "admin";
    private static final String PASSWORD = "quest";

    public QuestDBManager(boolean clean) {
        super(clean);
    }

    @Override
    public boolean clearTables() {
        if (!clearTablesFlag) {
            System.out.println("Table clearing is disabled. Skipping...");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmtSelect = conn.createStatement();
             Statement stmtDrop = conn.createStatement();
             ResultSet rs = stmtSelect.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'")) {

            List<String> tableNames = new ArrayList<>();
            while (rs.next()) {
                tableNames.add(rs.getString("table_name"));
            }

            for (String tableName : tableNames) {
                stmtDrop.executeUpdate("DROP TABLE IF EXISTS " + tableName);
                System.out.println("Dropped table: " + tableName);
            }

            System.out.println("All tables dropped successfully.");
            return true;

        } catch (Exception e) {
            System.out.println("Unable to drop tables.");
            e.printStackTrace();
            return false;
        }
    }
}