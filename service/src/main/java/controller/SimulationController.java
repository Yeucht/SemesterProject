package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.SimulationService;

@CrossOrigin(origins = "http://localhost:7000")
@RestController
@RequestMapping("/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/start")
    public ResponseEntity<String> start() {
        try {
            int code = simulationService.startSimulation();
            return ResponseEntity.ok("Simulation started, HTTP code: " + code);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error starting simulation: " + e.getMessage());
        }
    }

    @GetMapping("/stop")
    public ResponseEntity<String> stop() {
        try {
            int code = simulationService.stopSimulation();
            return ResponseEntity.ok("Simulation stopped, HTTP code: " + code);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error stopping simulation: " + e.getMessage());
        }
    }

    @GetMapping("/running")
    public ResponseEntity<String> getRunningSimulation() {
        try {
            boolean running = simulationService.isRunning();
            return ResponseEntity.ok("running: " + running);
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting bool ruuning: " + e.getMessage());
        }
    }
}