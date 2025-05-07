import lombok.Value;

@Value
public class Seat {
    int row;
    String seatLetter;
    SeatStatus status;
    SeatClass seatClass;
    Booking booking;
    Reservation reservation;

    public SeatPosition getPosition() {
        int seatIndex = seatLetter.charAt(0) - 'A';
        switch (seatClass) {
            case BUSINESS:
                return (seatIndex == 0 || seatIndex == 3) ? SeatPosition.WINDOW : SeatPosition.AISLE;
            case PREMIUM_ECONOMY:
                if (seatIndex == 0 || seatIndex == 7) return SeatPosition.WINDOW;
                return (seatIndex == 3 || seatIndex == 4) ? SeatPosition.AISLE : SeatPosition.MIDDLE;
            case ECONOMY:
                if (seatIndex == 0 || seatIndex == 8) return SeatPosition.WINDOW;
                return (seatIndex == 3 || seatIndex == 6) ? SeatPosition.AISLE : SeatPosition.MIDDLE;
            default: return SeatPosition.MIDDLE;
        }
    }

    public boolean isExitRow() {
        switch (seatClass) {
            case PREMIUM_ECONOMY: return row == 15 || row == 16;
            case ECONOMY: return row == 17 || row == 25 || row == 40;
            default: return false;
        }
    }
}