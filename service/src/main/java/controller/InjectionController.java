package controller;

import ingestion.DataPacket;
import ingestion.Injection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.InjectionService;

@RestController
@RequestMapping("/injection")
public class InjectionController {

    private InjectionService injectionService;

    public InjectionController(InjectionService injectionService) {
        this.injectionService = injectionService;
    }

    @PostMapping("/data")
    public ResponseEntity<String> injection(@RequestBody DataPacket data) {
        System.out.println("Received data from Flask:");
        System.out.println(data);

        injectionService.sendDataToDataBase(data);

        return ResponseEntity.ok("Received");
    }

    @GetMapping("/data")
    public ResponseEntity<String> injection() {
        System.out.println("Ready to serve trafic");
        return ResponseEntity.ok("Ready to serve trafic");
    }
}
