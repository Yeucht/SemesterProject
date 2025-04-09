import ingestion.*;
import simulation.Simulation;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask user for database choice
        System.out.println("Choose database (questdb / influxdb / iotdb): ");
        String databaseType = scanner.next().toLowerCase();

        // Ask if tables should be cleared
        System.out.println("Clean tables before launching simulation? (Y/N): ");
        boolean clean = scanner.next().equalsIgnoreCase("Y");

        // Ask for the number of records
        System.out.println("Enter number of records to insert: ");
        int recordCount = getPositiveInt(scanner);

        // Initialize and run the simulation
        try {
            Simulation simulation = new Simulation(databaseType, recordCount, clean);
            simulation.run();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid database selection. Exiting...");
        }

        scanner.close();
    }

    // Utility function to ensure a valid number
    private static int getPositiveInt(Scanner scanner) {
        int number;
        while (true) {
            if (scanner.hasNextInt()) {
                number = scanner.nextInt();
                if (number > 0) {
                    return number;
                }
            } else {
                scanner.next(); // Clear invalid input
            }
            System.out.println("Invalid input. Please enter a positive number.");
        }
    }
}
