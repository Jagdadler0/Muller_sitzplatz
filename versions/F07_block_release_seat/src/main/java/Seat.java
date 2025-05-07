import lombok.Value;

@Value
public class Seat {
    int row;
    String seatLetter;
    SeatStatus status;
    SeatClass seatClass;
    Booking booking;       // Populated when OCCUPIED
    Reservation reservation; // Populated when RESERVED
}