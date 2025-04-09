package simulation;

import ingestion.*;
import dbmanager.*;

import java.util.*;

///We will have vectors of simulations (we want one simulation to be able to compare 2 DB with the same presets)
public class Simulation {
    private final int numberOfRecords;
    private final boolean clearData;
    private final Injection dbInjection;

    // Mapping database choices to their classes (for scalability)
    private static final Map<String, Class<? extends Injection>> DATABASES = new HashMap<>();
    static {
        DATABASES.put("questdb", QuestDBInjection.class);
        DATABASES.put("influxdb", InfluxDBInjection.class);
    }

    // Constructor receives configuration instead of scanning user input
    public Simulation(String databaseType, int numberOfRecords, boolean clearData) {
        this.numberOfRecords = numberOfRecords;
        this.clearData = clearData;

        // Instantiate the correct database injection class
        this.dbInjection = createDatabaseInstance(databaseType);
    }

    // Factory method to create the database instance dynamically
    private Injection createDatabaseInstance(String databaseType) {
        Class<? extends Injection> dbClass = DATABASES.get(databaseType.toLowerCase());

        if (dbClass == null) {
            throw new IllegalArgumentException("Unsupported database: " + databaseType);
        }

        try {
            return dbClass.getDeclaredConstructor(boolean.class).newInstance(clearData);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing database injection for " + databaseType, e);
        }
    }

    // Start the simulation
    public void run() {
        System.out.println("Starting simulation with " + numberOfRecords + " records...");
        dbInjection.insertData(numberOfRecords);
        System.out.println("Simulation completed!");
    }
}
