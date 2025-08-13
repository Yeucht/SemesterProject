package controller;

import config.SimulationConfig;
import org.springframework.web.bind.annotation.*;
import simulation.SimulationRepository;
import simulation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/populate")
public class PopulateController {

    private final SimulationRepository runRepository;

    public PopulateController(SimulationRepository runRepository) {
        this.runRepository = runRepository;
    }


    @PostMapping
    public String populateTestRun() {
        // 1. Création de la config
        SimulationConfig config = new SimulationConfig();
        config.setMeterRate(5.0f);
        config.setMeterPayloadBatchSize(64);
        config.setNbrSmartMeters(10000);
        config.setUrl("http://localhost:8080/api/injection/data");

        // 2. Début et fin de simulation
        Instant start = Instant.now();
        Instant end = start.plusSeconds(60);

        // 3. Création de 3 points de métriques (timestamp, CPU, mémoire, etc.)
        MetricPoint point1 = new MetricPoint();
        point1.setTimestamp(start.plusSeconds(10));
        point1.setCpuUsage(40.0);
        point1.setMemoryUsed(120_000_000L);
        point1.setInsertedSoFar(1000);

        MetricPoint point2 = new MetricPoint();
        point2.setTimestamp(start.plusSeconds(20));
        point2.setCpuUsage(55.2);
        point2.setMemoryUsed(130_000_000L);
        point2.setInsertedSoFar(2000);

        MetricPoint point3 = new MetricPoint();
        point3.setTimestamp(start.plusSeconds(30));
        point3.setCpuUsage(70.0);
        point3.setMemoryUsed(145_000_000L);
        point3.setInsertedSoFar(3500);

        // 4. Création du run
        SimulationRun run = new SimulationRun(config);
        run.setConfig(config);
        run.setStartedAt(start);
        run.setEndedAt(end);
        run.setTotalInserted(3500);
        run.setAvgInsertRate(58.3);
        run.setFinalDbSizeBytes(50_000_000L);

        // 5. Association des points au run
        point1.setRun(run);
        point2.setRun(run);
        point3.setRun(run);

        run.setMetrics(List.of(point1, point2, point3));

        // 6. Sauvegarde dans la DB
        runRepository.save(run);

        return "SimulationRun test saved with 3 metric points.";
    }
}

