// QuestDBExtraction.java
package extraction;

import config.SimulationConfig;
import dbmanager.QuestDBManager;
import extractionobjects.Invoice;

import java.sql.*;
import java.util.Date;

public class QuestDBExtraction extends Extraction {

    private final QuestDBManager manager;

    public QuestDBExtraction(SimulationConfig config) {
        super(config);
        this.manager = new QuestDBManager(config);
    }

    @Override
    public Invoice craftInvoice(Date start, Date end, int serialNumber, double priceKwh) {
        // 1) Sécuriser les bornes
        if (start == null || end == null) {
            throw new IllegalArgumentException("start et end ne peuvent pas être null");
        }
        if (!start.before(end)) {
            throw new IllegalArgumentException("start doit être strictement avant end");
        }

        // 2) Conversion Date -> Timestamp (QuestDB accepte java.sql.Timestamp via wire PG)
        final Timestamp tsStart = Timestamp.from(start.toInstant());
        final Timestamp tsEnd   = Timestamp.from(end.toInstant());

        final String sql =
                "SELECT SUM(payload) AS total_consumption " +
                        "FROM smart_meter " +
                        "WHERE auth_serial_number = ? AND timestamp >= ? AND timestamp < ?";

        double totalConsumption = 0.0;

        try (Connection conn = manager.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, String.valueOf(serialNumber));
            ps.setTimestamp(2, tsStart);
            ps.setTimestamp(3, tsEnd);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // SUM(...) peut être NULL si aucune ligne : on fallback à 0
                    totalConsumption = rs.getDouble("total_consumption");
                    if (rs.wasNull()) {
                        totalConsumption = 0.0;
                    }
                }
            }
        } catch (SQLException e) {
            // À toi de voir si tu préfères logger et remonter, ou transformer en RuntimeException
            throw new RuntimeException("Erreur lors de l'agrégation QuestDB: " + e.getMessage(), e);
        }

        // 3) Construire la facture
        return new Invoice(start, end, priceKwh, totalConsumption, serialNumber);
    }
}
