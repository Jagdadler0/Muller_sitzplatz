import java.time.Instant;

public record Reservation(String passengerName, String reservationId, 
                          Instant expirationTime, String specialRequests) {}