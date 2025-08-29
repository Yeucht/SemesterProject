package extraction;

import config.SimulationConfig;
import extractionobjects.Invoice;

import java.util.Date;

public abstract class Extraction {
    private SimulationConfig config;

    public Extraction(SimulationConfig config) {
        this.config = config;
    }

    public abstract Invoice craftInvoice(Date start, Date end, int serialNumber, double priceKwh);

}
