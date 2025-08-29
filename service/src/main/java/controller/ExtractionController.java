package controller;

import extractionobjects.Invoice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import service.DBManagerService;
import service.InvoiceService;

import java.util.Date;

@CrossOrigin(origins = "http://localhost:7000")
@RestController
@RequestMapping("/extraction")
public class ExtractionController {

    private final DBManagerService dbManagerService;
    private InvoiceService invoiceService;

    public ExtractionController(InvoiceService invoiceService, DBManagerService dbManagerService) {
        this.invoiceService = invoiceService;
        this.dbManagerService = dbManagerService;
    }

    @GetMapping(value = "/invoice", params = {"start", "end", "serialNumber", "priceKwh"})
    public Invoice getInvoiceExplicitWindow(
            @RequestParam("start")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date start,

            @RequestParam("end")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date end,

            @RequestParam("serialNumber") int serialNumber,

            @RequestParam("priceKwh") double priceKwh
    ) {
        return invoiceService.craftInvoice(start, end, serialNumber, priceKwh);
    }

    //7 jours
    @GetMapping(value = "/invoice", params = {"serialNumber", "priceKwh", "!start", "!end"})
    public Invoice getInvoiceDefaultWindow(
            @RequestParam("serialNumber") int serialNumber,
            @RequestParam("priceKwh") double priceKwh
    ) {
        return invoiceService.craftInvoice(serialNumber, priceKwh);
    }

    @GetMapping(value = "/nbrMeters")
    public int getNbrMeters(){
        return dbManagerService.getDbManager().getNumberMeters();
    }

}

