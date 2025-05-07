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
}