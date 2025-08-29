package controller;

import ingestion.DataPacket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.InjectionService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:7000")
@RestController
@RequestMapping("/injection")
public class InjectionController {

    private InjectionService injectionService;

    public InjectionController(InjectionService injectionService) {
        this.injectionService = injectionService;
    }

    @PostMapping("/data")
    public ResponseEntity<String> injection(@RequestBody List<DataPacket> dataPackets) {
        if (dataPackets == null || dataPackets.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty payload");
        }
        System.out.println("Received " + dataPackets.size() + " packet(s) from Flask.");
        injectionService.sendDataToDataBase(dataPackets);
        return ResponseEntity.ok("Received " + dataPackets.size());
    }


    @GetMapping("/data")
    public ResponseEntity<String> injection() {
        System.out.println("Ready to serve trafic");
        return ResponseEntity.ok("Ready to serve trafic");
    }
}
