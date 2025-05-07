import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SeatSection {
    private SeatClass seatClass;
    private int startRow;
    private int endRow;
    private int seatsPerRow;
}