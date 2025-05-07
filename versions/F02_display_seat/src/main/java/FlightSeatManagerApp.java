import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FlightSeatManagerApp {
    private static final Logger log = LoggerFactory.getLogger(FlightSeatManagerApp.class);
    private final SeatPlan seatPlan;

    public FlightSeatManagerApp() {
        this.seatPlan = new SeatPlan();
    }

    public void start() {
        log.info("Flight Seat Management System started");
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                log.info("\nMenu:");
                log.info("1. Display seat availability");
                log.info("2. Assign seat");
                log.info("3. Change seat assignment");
                log.info("4. Make reservation");
                log.info("5. Exit");
                log.info("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> displaySeatAvailability();
                    case 2 -> assignSeat(scanner);
                    case 3 -> changeAssignment(scanner);
                    case 4 -> makeReservation(scanner);
                    case 5 -> running = false;
                    default -> log.warn("Invalid choice. Please try again.");
                }
            }
        }
        log.info("System exiting...");
    }

   private void displaySeatAvailability() {
    log.info("Seat Availability:");
    seatPlan.getSections().forEach(section -> {
        log.info("\n=== {} Class ===", section.getSeatClass());
        IntStream.rangeClosed(section.getStartRow(), section.getEndRow())
                .forEach(row -> {
                    List<Seat> seatsInRow = seatPlan.getSeatsByClassAndRow(section.getSeatClass(), row);
                    String seatDisplay = seatsInRow.stream()
                            .map(seat -> getStatusSymbol(seat.getStatus()))
                            .collect(Collectors.joining(" "));
                    log.info("Row {}: {}", String.format("%2d", row), seatDisplay);
                });
    });
}

private String getStatusSymbol(SeatStatus status) {
    switch (status) {
        case AVAILABLE: return "[A]";
        case RESERVED: return "[R]";
        case OCCUPIED: return "[O]";
        case BLOCKED: return "[B]";
        default: return "[?]";
    }
}

    private void assignSeat(Scanner scanner) {
        log.info("Enter row number:");
        int row = scanner.nextInt();
        log.info("Enter seat letter:");
        String seatLetter = scanner.next();
        log.info("Assigning seat {}{}...", row, seatLetter);
    }

    private void changeAssignment(Scanner scanner) {
        log.info("Enter current row:");
        int currentRow = scanner.nextInt();
        log.info("Enter current seat letter:");
        String currentSeatLetter = scanner.next();
        log.info("Enter new row:");
        int newRow = scanner.nextInt();
        log.info("Enter new seat letter:");
        String newSeatLetter = scanner.next();
        log.info("Changing assignment from {}{} to {}{}...", currentRow, currentSeatLetter, newRow, newSeatLetter);
    }

    private void makeReservation(Scanner scanner) {
        log.info("Enter passenger name:");
        String name = scanner.nextLine();
        log.info("Enter row number:");
        int row = scanner.nextInt();
        log.info("Enter seat letter:");
        String seatLetter = scanner.next();
        log.info("Reserving seat {}{} for {}...", row, seatLetter, name);
    }

    public static void main(String[] args) {
        new FlightSeatManagerApp().start();
    }
}