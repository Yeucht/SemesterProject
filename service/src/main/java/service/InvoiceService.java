package service;

import config.SimulationConfig;
import extraction.Extraction;
import extractionobjects.Invoice;
import factories.ExtractionFactory;

import java.util.Date;

public class InvoiceService {

    private SimulationConfig config;
    private final ExtractionFactory extractionFactory= new ExtractionFactory();
    private Extraction extraction;
    private DBManagerService dbManagerService;

    public InvoiceService(SimulationConfig config, DBManagerService dbManagerService) {
        this.config = config;
        this.dbManagerService = dbManagerService;
        this.extraction = extractionFactory.createExtraction(config);
    }


    public Invoice craftInvoice(Date start, Date end, int serialNumber, double priceKwh){
        return extraction.craftInvoice(start, end, serialNumber, priceKwh);
    }

    public Invoice craftInvoice(int serialNumber, double priceKwh){
        Date end = new Date(); // maintenant
        Date start = new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);
        return extraction.craftInvoice(start, end, serialNumber, priceKwh);
    }

    public void update(SimulationConfig config){
        this.config = config;
        this.extraction = extractionFactory.createExtraction(config);
    }
}
