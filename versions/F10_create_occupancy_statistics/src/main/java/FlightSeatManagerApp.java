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
                seatPlan.releaseExpiredReservations();  // Auto-clean before every operation
                log.info("\nMenu:");
                log.info("1. Display seat availability");
                log.info("2. Assign seat");
                log.info("3. Change seat assignment");
                log.info("4. Make reservation");
                log.info("5. Confirm reservation");
                log.info("6. Cancel seat");
                log.info("7. Swap reservation");
                log.info("8. Search available seats");
                log.info("9. Show passenger list");
                log.info("10. Show occupancy stats");
                log.info("11. Block seat");
                log.info("12. Release seat");
                log.info("13. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> displaySeatAvailability();
                    case 2 -> assignSeat(scanner);
                    case 3 -> changeAssignment(scanner);
                    case 4 -> makeReservation(scanner);
                    case 5 -> confirmReservation(scanner);
                    case 6 -> cancelSeat(scanner);
                    case 7 -> swapSeats(scanner);
                    case 8 -> searchAvailableSeats(scanner);
                    case 9 -> displayPassengerList();
                    case 10 -> showOccupancyStats();
                    case 11 -> blockSeat(scanner);
                    case 12 -> releaseSeat(scanner);
                    case 13 -> running = false;
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
        String seatLetter = scanner.next().toUpperCase();
        scanner.nextLine(); // Consume remaining newline

        // Validate seat existence
        Seat seat = seatPlan.getSeats().stream()
                .filter(s -> s.getRow() == row && s.getSeatLetter().equals(seatLetter))
                .findFirst()
                .orElse(null);

        if (seat == null) {
            log.error("Seat {}{} does not exist.", row, seatLetter);
            return;
        }

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            log.error("Seat {}{} is not available.", row, seatLetter);
            return;
        }

        // Collect passenger details
        log.info("Enter passenger name:");
        String name = scanner.nextLine();
        log.info("Enter booking number:");
        String bookingNumber = scanner.nextLine();
        log.info("Enter special requests (if any):");
        String specialRequests = scanner.nextLine();

        Booking booking = new Booking(name, bookingNumber, specialRequests);
        boolean success = seatPlan.bookSeat(row, seatLetter, booking);
        
        if (success) {
            log.info("Successfully booked seat {}{} for {}", row, seatLetter, name);
        } else {
            log.error("Failed to book seat {}{}.", row, seatLetter);
        }
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
        log.info("Enter reservation ID:");
        String resId = scanner.nextLine();
        log.info("Reservation duration (minutes):");
        long minutes = scanner.nextLong();
        scanner.nextLine(); // Clear buffer
        
        Instant expiration = Instant.now().plusSeconds(minutes * 60);
        
        log.info("Enter row and seat:");
        int row = scanner.nextInt();
        String seatLetter = scanner.next().toUpperCase();
        scanner.nextLine();

        // ... seat validation ...
        
        Reservation res = new Reservation(name, resId, expiration, "");
        if (seatPlan.reserveSeat(row, seatLetter, res)) {
            log.info("Reserved until {}", expiration);
        } else {
            log.error("Reservation failed");
        }
    }

    private void confirmReservation(Scanner scanner) {
        log.info("Enter row and seat:");
        int row = scanner.nextInt();
        String seatLetter = scanner.next().toUpperCase();
        scanner.nextLine(); // Clear buffer
        
        log.info("Enter booking number:");
        String bookNum = scanner.nextLine();
        
        if (seatPlan.confirmReservation(row, seatLetter, bookNum)) {
            log.info("Reservation confirmed!");
        } else {
            log.error("Confirmation failed");
        }
    }

    private void cancelSeat(Scanner scanner) {
        log.info("Enter row number:");
        int row = scanner.nextInt();
        log.info("Enter seat letter:");
        String seatLetter = scanner.next().toUpperCase();
        scanner.nextLine(); // Consume leftover newline

        boolean success = seatPlan.cancelSeat(row, seatLetter);
        if (success) {
            log.info("Seat {}{} cancelled successfully", row, seatLetter);
        } else {
            log.error("Cancellation failed - seat not found or not reserved/occupied");
        }
    }

    private void swapSeats(Scanner scanner) {
        log.info("Enter first seat (row letter):");
        int row1 = scanner.nextInt();
        String sl1 = scanner.next().toUpperCase();
        log.info("Enter second seat (row letter):");
        int row2 = scanner.nextInt();
        String sl2 = scanner.next().toUpperCase();
        scanner.nextLine();  // Clear input buffer
    
        boolean success = seatPlan.swapSeats(row1, sl1, row2, sl2);
        if (success) {
            log.info("Swapped {}{} ↔ {}{} successfully", row1, sl1, row2, sl2);
        } else {
            log.error("Swap failed. Check seat statuses/availability");
        }
    }
    private void blockSeat(Scanner scanner) {
        log.info("Enter row number:");
        int row = scanner.nextInt();
        log.info("Enter seat letter:");
        String seatLetter = scanner.next().toUpperCase();
        scanner.nextLine(); // Clear input buffer
    
        boolean success = seatPlan.blockSeat(row, seatLetter);
        if (success) {
            log.info("Blocked seat {}{}", row, seatLetter);
        } else {
            log.error("Seat {}{} not found", row, seatLetter);
        }
    }
    private void releaseSeat(Scanner scanner) {
        log.info("Enter row number:");
        int row = scanner.nextInt();
        log.info("Enter seat letter:");
        String seatLetter = scanner.next().toUpperCase();
        scanner.nextLine(); // Clear input buffer
    
        boolean success = seatPlan.releaseSeat(row, seatLetter);
        if (success) {
            log.info("Released seat {}{} to available", row, seatLetter);
        } else {
            log.error("Seat {}{} not blocked or not found", row, seatLetter);
        }
    }

    private void searchAvailableSeats(Scanner scanner) {
        log.info("Search filters:\n1. Class\n2. Position\n3. Exit rows");
        
        // Class filter
        SeatClass seatClass = null;
        log.info("Class (1-Business, 2-Premium Economy, 3-Economy, 0-Any):");
        int classChoice = scanner.nextInt();
        if (classChoice > 0 && classChoice <= 3) {
            seatClass = SeatClass.values()[classChoice - 1];
        }
    
        // Position filter
        SeatPosition position = null;
        log.info("Position (1-Window, 2-Aisle, 3-Middle, 0-Any):");
        int posChoice = scanner.nextInt();
        if (posChoice > 0 && posChoice <= 3) {
            position = SeatPosition.values()[posChoice - 1];
        }
    
        // Exit row filter
        log.info("Only exit rows? (1-Yes, 0-No):");
        boolean exitOnly = scanner.nextInt() == 1;
        scanner.nextLine();  // Clear input buffer
    
        List<Seat> results = seatPlan.findAvailableSeats(seatClass, position, exitOnly);
        
        log.info("\nFound {} available seats:", results.size());
        results.forEach(seat -> {
            String exitMark = seat.isExitRow() ? " [EXIT]" : "";
            log.info("Row {}{} - {} Class {} Seat{}",
                seat.getRow(),
                seat.getSeatLetter(),
                seat.getSeatClass(),
                seat.getPosition(),
                exitMark);
        });
    }

    private void displayPassengerList() {
        List<Seat> occupiedSeats = seatPlan.getOccupiedSeats();
        
        if (occupiedSeats.isEmpty()) {
            log.info("No occupied seats - passenger list is empty");
            return;
        }
    
        log.info("\nPassenger List ({} occupied seats):", occupiedSeats.size());
        occupiedSeats.forEach(seat -> {
            Booking booking = seat.getBooking();
            String requests = booking.specialRequests().isBlank() 
                             ? "No special requests" 
                             : booking.specialRequests();
            log.info("""
                Seat {}{} ({})
                Passenger: {}
                Ticket: {}
                Requests: {}""",
                seat.getRow(), seat.getSeatLetter(), seat.getSeatClass(),
                booking.passengerName(), booking.bookingNumber(), requests);
        });
    }
    private void showOccupancyStats() {
        Map<SeatClass, ClassStats> stats = seatPlan.calculateOccupancyStats();
        
        log.info("\nSeat Occupancy Statistics:");
        stats.forEach((clazz, stat) -> {
            log.info("┌───────────────────────────────────┐");
            log.info("│ {:30} │", clazz + " Class");
            log.info("├───────────────────┬───────────────┤");
            log.info("│ Total Seats       │ {:13} │", stat.totalSeats());
            log.info("│ Available         │ {:13} │", stat.available());
            log.info("│ Occupied          │ {:13} │", stat.occupied());
            log.info("│ Reserved          │ {:13} │", stat.reserved());
            log.info("│ Blocked           │ {:13} │", stat.blocked());
            log.info("├───────────────────┼───────────────┤");
            log.info("│ Occupancy         │ {:12}% │", String.format("%.1f", stat.occupancyPercentage()));
            log.info("└───────────────────┴───────────────┘\n");
        });
    }

    public static void main(String[] args) {
        new FlightSeatManagerApp().start();
    }
}