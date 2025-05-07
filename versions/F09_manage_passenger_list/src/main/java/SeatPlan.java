import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SeatPlan {
    private static final Logger log = LoggerFactory.getLogger(SeatPlan.class);
    private final List<Seat> seats = new ArrayList<>();
    @Getter
    private final List<SeatSection> sections;

    public SeatPlan() {
        this.sections = initializeSections();
        initializeSeats();
    }

    private List<SeatSection> initializeSections() {
        return List.of(
                new SeatSection(SeatClass.BUSINESS, 1, 8, 4),
                new SeatSection(SeatClass.PREMIUM_ECONOMY, 9, 16, 8),
                new SeatSection(SeatClass.ECONOMY, 17, 40, 9)
        );
    }

    private void initializeSeats() {
        for (SeatSection section : sections) {
            for (int row = section.getStartRow(); row <= section.getEndRow(); row++) {
                for (int seatNum = 0; seatNum < section.getSeatsPerRow(); seatNum++) {
                    char seatLetter = (char) ('A' + seatNum);
                    seats.add(new Seat(row, String.valueOf(seatLetter), SeatStatus.AVAILABLE, section.getSeatClass(), null));
                }
            }
        }
        log.info("Initialized seating plan with {} seats", seats.size());
    }

    public List<Seat> getSeatsByClassAndRow(SeatClass seatClass, int row) {
        return seats.stream()
                .filter(seat -> seat.getSeatClass() == seatClass && seat.getRow() == row)
                .sorted(Comparator.comparing(Seat::getSeatLetter))
                .collect(Collectors.toList());
    }

    public List<Seat> getSeats() {
        return new ArrayList<>(seats);
    }

    public boolean bookSeat(int row, String seatLetter, Booking booking) {
        for (int i = 0; i < seats.size(); i++) {
            Seat currentSeat = seats.get(i);
            if (currentSeat.getRow() == row && currentSeat.getSeatLetter().equalsIgnoreCase(seatLetter)) {
                if (currentSeat.getStatus() == SeatStatus.AVAILABLE) {
                    Seat newSeat = new Seat(
                        currentSeat.getRow(),
                        currentSeat.getSeatLetter(),
                        SeatStatus.RESERVED,
                        currentSeat.getSeatClass(),
                        booking
                    );
                    seats.set(i, newSeat);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean reserveSeat(int row, String seatLetter, Reservation reservation) {
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            if (seat.getRow() == row && seat.getSeatLetter().equalsIgnoreCase(seatLetter)) {
                if (seat.getStatus() == SeatStatus.AVAILABLE) {
                    Seat newSeat = new Seat(row, seatLetter, SeatStatus.RESERVED, 
                                         seat.getSeatClass(), null, reservation);
                    seats.set(i, newSeat);
                    return true;
                }
                return false;
            }
        }
        return false;
    }
    public boolean confirmReservation(int row, String seatLetter, String bookingNumber) {
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            if (seat.getRow() == row && seat.getSeatLetter().equalsIgnoreCase(seatLetter) 
                && seat.getStatus() == SeatStatus.RESERVED) {
                
                Reservation res = seat.getReservation();
                Booking booking = new Booking(res.passengerName(), bookingNumber, 
                                            res.specialRequests());
                Seat newSeat = new Seat(row, seatLetter, SeatStatus.OCCUPIED, 
                                      seat.getSeatClass(), booking, null);
                seats.set(i, newSeat);
                return true;
            }
        }
        return false;
    }
    public void releaseExpiredReservations() {
        Instant now = Instant.now();
        seats.replaceAll(seat -> {
            if (seat.getStatus() == SeatStatus.RESERVED 
                && seat.getReservation().expirationTime().isBefore(now)) {
                return new Seat(seat.getRow(), seat.getSeatLetter(), SeatStatus.AVAILABLE,
                              seat.getSeatClass(), null, null);
            }
            return seat;
        });
    }
    public boolean cancelSeat(int row, String seatLetter) {
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            if (seat.getRow() == row && seat.getSeatLetter().equalsIgnoreCase(seatLetter)) {
                if (seat.getStatus() == SeatStatus.RESERVED || seat.getStatus() == SeatStatus.OCCUPIED) {
                    seats.set(i, new Seat(
                        row,
                        seatLetter,
                        SeatStatus.AVAILABLE,
                        seat.getSeatClass(),
                        null,  // Clear booking
                        null   // Clear reservation
                    ));
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean swapSeats(int row1, String seatLetter1, int row2, String seatLetter2) {
        if (row1 == row2 && seatLetter1.equalsIgnoreCase(seatLetter2)) return false;
        
        int index1 = -1, index2 = -1;
        Seat seat1 = null, seat2 = null;
        
        // Find both seats
        for (int i = 0; i < seats.size(); i++) {
            Seat s = seats.get(i);
            if (s.getRow() == row1 && s.getSeatLetter().equalsIgnoreCase(seatLetter1)) {
                index1 = i;
                seat1 = s;
            }
            if (s.getRow() == row2 && s.getSeatLetter().equalsIgnoreCase(seatLetter2)) {
                index2 = i;
                seat2 = s;
            }
        }
        
        if (index1 == -1 || index2 == -1) return false;
        if (seat1.getStatus() != seat2.getStatus()) return false;
        
        // Perform swap based on status
        if (seat1.getStatus() == SeatStatus.RESERVED) {
            Seat new1 = new Seat(seat1.getRow(), seat1.getSeatLetter(), SeatStatus.RESERVED,
                               seat1.getSeatClass(), null, seat2.getReservation());
            Seat new2 = new Seat(seat2.getRow(), seat2.getSeatLetter(), SeatStatus.RESERVED,
                               seat2.getSeatClass(), null, seat1.getReservation());
            seats.set(index1, new1);
            seats.set(index2, new2);
            return true;
        } else if (seat1.getStatus() == SeatStatus.OCCUPIED) {
            Seat new1 = new Seat(seat1.getRow(), seat1.getSeatLetter(), SeatStatus.OCCUPIED,
                               seat1.getSeatClass(), seat2.getBooking(), null);
            Seat new2 = new Seat(seat2.getRow(), seat2.getSeatLetter(), SeatStatus.OCCUPIED,
                               seat2.getSeatClass(), seat1.getBooking(), null);
            seats.set(index1, new1);
            seats.set(index2, new2);
            return true;
        }
        return false;
    }
    public boolean blockSeat(int row, String seatLetter) {
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            if (seat.getRow() == row && seat.getSeatLetter().equalsIgnoreCase(seatLetter)) {
                seats.set(i, new Seat(
                    row,
                    seatLetter,
                    SeatStatus.BLOCKED,
                    seat.getSeatClass(),
                    null,  // Clear booking
                    null   // Clear reservation
                ));
                return true;
            }
        }
        return false;
    }
    
    public boolean releaseSeat(int row, String seatLetter) {
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            if (seat.getRow() == row && seat.getSeatLetter().equalsIgnoreCase(seatLetter)
                && seat.getStatus() == SeatStatus.BLOCKED) {
                seats.set(i, new Seat(
                    row,
                    seatLetter,
                    SeatStatus.AVAILABLE,
                    seat.getSeatClass(),
                    null,
                    null
                ));
                return true;
            }
        }
        return false;
    }

    public List<Seat> findAvailableSeats(SeatClass seatClass, SeatPosition position, boolean exitOnly) {
        return seats.stream()
            .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
            .filter(seat -> seatClass == null || seat.getSeatClass() == seatClass)
            .filter(seat -> position == null || seat.getPosition() == position)
            .filter(seat -> !exitOnly || seat.isExitRow())
            .collect(Collectors.toList());
    }

    public List<Seat> getOccupiedSeats() {
        return seats.stream()
                .filter(seat -> seat.getStatus() == SeatStatus.OCCUPIED)
                .collect(Collectors.toList());
    }
}