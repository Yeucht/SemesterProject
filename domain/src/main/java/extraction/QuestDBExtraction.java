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

        if (start == null || end == null) {
            throw new IllegalArgumentException("start and end cannot be null");
        }
        if (!start.before(end)) {
            throw new IllegalArgumentException("start must come before end");
        }

        //date conversion
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
                    totalConsumption = rs.getDouble("total_consumption");
                    if (rs.wasNull()) {
                        totalConsumption = 0.0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("QuestDB aggreagtion failed: " + e.getMessage(), e);
        }

        return new Invoice(start, end, priceKwh, totalConsumption, serialNumber);
    }
}
