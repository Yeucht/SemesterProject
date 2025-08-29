package extraction;

import config.SimulationConfig;
import dbmanager.IoTDBManager;
import extractionobjects.Invoice;
import jakarta.annotation.PostConstruct;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionDataSetWrapper;
import org.apache.iotdb.tsfile.read.common.Field;
import org.apache.iotdb.tsfile.read.common.RowRecord;
import java.util.Date;

public class IoTDBExtraction extends Extraction {

    private final IoTDBManager manager;
    private static String STORAGE_GROUP;

    public IoTDBExtraction(SimulationConfig config) {
        super(config);
        this.manager = new IoTDBManager(config);
    }

    @PostConstruct
    public void setStorageGroup() {
        this.STORAGE_GROUP = ((manager.getStorageGroup() == null) ? "root.smart_meter" : manager.getStorageGroup());
    }

    @Override
    public Invoice craftInvoice(Date start, Date end, int serialNumber, double priceKwh) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start/end ne peuvent pas être null");
        }
        if (!start.before(end)) {
            throw new IllegalArgumentException("start doit être strictement avant end");
        }

        final long tStart = start.getTime(); // epoch millis
        final long tEnd   = end.getTime();

        // on agrège sur le device du compteur ciblé
        final String device = STORAGE_GROUP + ".meter_" + serialNumber;
        final String sql =
                "SELECT SUM(payload) FROM " + device +
                        " WHERE time >= " + tStart + " AND time < " + tEnd;

        double totalConsumption = 0.0;

        try (SessionDataSetWrapper rs = manager.getSessionPool().executeQueryStatement(sql)) {
            // La requête renvoie au plus une ligne avec une seule colonne : SUM(payload)
            if (rs.hasNext()) {
                RowRecord row = rs.next();
                if (row != null && row.getFields() != null && !row.getFields().isEmpty()) {
                    Field f = row.getFields().get(0);
                    if (f != null) {
                        totalConsumption = f.getDoubleV();
                        if (Double.isNaN(totalConsumption)) totalConsumption = 0.0;
                    }
                }
            }
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            throw new RuntimeException("Erreur IoTDB craftInvoice: " + e.getMessage(), e);
        }

        return new Invoice(start, end, priceKwh, totalConsumption, serialNumber);
    }
}
