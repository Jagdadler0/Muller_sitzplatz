import lombok.Value;

@Value
public class Seat {
    int row;
    String seatLetter;
    SeatStatus status;
    SeatClass seatClass;
}