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

}

