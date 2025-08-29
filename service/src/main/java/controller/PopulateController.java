package controller;

import org.springframework.web.bind.annotation.*;
import simulation.SimulationRepository;


@RestController
@RequestMapping("/populate")
public class PopulateController {

    private final SimulationRepository runRepository;

    public PopulateController(SimulationRepository runRepository) {
        this.runRepository = runRepository;
    }

}

