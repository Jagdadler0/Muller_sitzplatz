public record ClassStats(
    int totalSeats,
    int available,
    int occupied,
    int reserved,
    int blocked,
    double occupancyPercentage
) {}