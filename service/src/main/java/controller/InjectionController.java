package controller;

import ingestion.DataPacket;
import ingestion.Injection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.InjectionService;

@RestController
@RequestMapping("/injection")
public class InjectionController {

    private InjectionService injectionService;

    @PostMapping("/data")
    public ResponseEntity<String> injection(@RequestBody DataPacket data) {
        System.out.println("Received data from Flask:");
        System.out.println(data);

        injectionService.sendDataToDataBase(data);

        return ResponseEntity.ok("Received");
    }
}
