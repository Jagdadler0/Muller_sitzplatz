# Reasoning

Okay, let's tackle Feature F03: Book seat. The user wants to allow booking a free seat by assigning it to a passenger with their details. Let me think about how to integrate this into the existing code.

First, I need to track more information for a booked seat. The current Seat class only has row, seatLetter, status, and seatClass. But now, we need to store passenger name, booking number, and special requests. Maybe I should create a new Booking class that holds these details and associate it with the Seat. Alternatively, add these fields directly to Seat. Since Lombok's @Value makes Seat immutable, modifying it would require creating a new instance when booking. That's a good approach because immutability is maintained.

So, I'll update the Seat class to include an optional Booking object. Using Lombok's @With would help create a copy of the Seat with the new booking. Wait, @With generates a method to create a new instance with a changed field. So adding a Booking field to Seat makes sense. Then, when booking, we replace the existing Seat in the list with a new one that has the booking info and status set to RESERVED or OCCUPIED.

Wait, the requirement says "permanently assigned", which might mean setting the status to OCCUPIED. But the user also mentioned "reservations" in previous features. Maybe the status should be RESERVED when booked, and OCCUPIED later. But according to F02, SeatStatus includes RESERVED, OCCUPIED, etc. So booking a seat would set it to RESERVED.

Next, the SeatPlan currently has a list of seats. To update a seat, we need to find it in the list and replace it with the new booked version. So, in SeatPlan, add a method to update a seat by row and seatLetter. Since seats are stored in a list, we can iterate and find the matching seat, then replace it with the new one.

Then, in FlightSeatManagerApp's assignSeat method (which was a placeholder), we need to collect passenger info: name, booking number, special requests. Then find the seat, check if it's available, and if so, update it with the booking info and set status to RESERVED.

Wait, the user's F03 says "book seat" which is different from "assign seat". The existing menu has "Assign seat" (option 2) and "Make reservation" (option 4). Maybe there's a need to clarify, but according to F03, booking is assigning a free seat to a passenger. So perhaps the "assign seat" option is for assigning (booking), and "make reservation" is something else. But the current code's assignSeat is a placeholder. So we need to implement the booking logic in the assignSeat method.

Wait, the user's F03 is "Book seat - A free seat can be permanently assigned to a passenger...". So this feature is about booking, which is the same as assigning. So perhaps the existing menu option 2 (Assign seat) is where this should be handled. The placeholder code for assignSeat needs to be filled in.

So steps:

1. Add Booking record to hold passenger details.

2. Modify Seat class to include Booking and use @With to create a new instance when booking.

3. Add a method in SeatPlan to update a seat's status and booking info.

4. Implement the booking logic in assignSeat method, prompting for passenger details and updating the seat.

Let me check the existing Seat class. It's a Lombok @Value with row, seatLetter, status, seatClass. Adding a Booking field would require modifying the constructor. Since @Value is immutable, adding the booking as an optional field (nullable) makes sense. Then, when a seat is booked, we create a new Seat with the same row, seatLetter, seatClass, status changed to RESERVED, and the booking info.

So:

- Create a Booking record with name, bookingNumber, specialRequests.

- Update Seat to have a Booking field, nullable.

- In SeatPlan, add a method like updateSeatStatus(int row, String seatLetter, SeatStatus newStatus, Booking booking) that finds the seat and replaces it with a new one.

Wait, but in the current SeatPlan, seats are stored as a list. To update a seat, we need to find the index of the seat with the given row and seatLetter, then create a new Seat with the updated status and booking, and replace it in the list.

But lists are not the most efficient for this, but given the scope, it's manageable.

Alternatively, use a map with seat keys (row + seatLetter) for quicker access. But for simplicity, perhaps keep the list and search each time. Since the number of seats isn't too large (A350 has around 300+ seats), linear search is acceptable.

So in SeatPlan:

public boolean bookSeat(int row, String seatLetter, Booking booking) {

for (int i = 0; i < seats.size(); i++) {

Seat seat = seats.get(i);

if (seat.getRow() == row && seat.getSeatLetter().equalsIgnoreCase(seatLetter)) {

if (seat.getStatus() == SeatStatus.AVAILABLE) {

Seat newSeat = seat.withStatus(SeatStatus.RESERVED).withBooking(booking);

seats.set(i, newSeat);

return true;

} else {

return false;

}

}

}

return false;

}

Wait, but the existing Seat class doesn't have a booking field. So first, add the Booking field.

Modify Seat.java:

@Value

public class Seat {

int row;

String seatLetter;

SeatStatus status;

SeatClass seatClass;

Booking booking;

}

But this would break existing code where seats are created without a booking. So in the SeatPlan's initializeSeats method, when creating seats, set booking to null.

Then, in Seat.java, the @Value requires all fields in the constructor. So in the SeatSection loop, when adding new Seat, include null for booking:

seats.add(new Seat(row, String.valueOf(seatLetter), SeatStatus.AVAILABLE, section.getSeatClass(), null));

But wait, in the current code from the previous answer, the Seat constructor doesn't have a booking parameter. So this change would require modifying all places where Seat is created. Hence, the previous code in SeatPlan's initializeSeats() would need to pass null for booking.

So:

In SeatPlan's initializeSeats:

seats.add(new Seat(row, String.valueOf(seatLetter), SeatStatus.AVAILABLE, section.getSeatClass(), null));

But in the current code, the Seat class doesn't have the booking field. So adding that field is necessary.

But the user's previous code for Seat.java was:

@Value

public class Seat {

int row;

String seatLetter;

SeatStatus status;

SeatClass seatClass;

}

So modifying that to include a Booking field. And since it's a Lombok @Value, we need to add it to the constructor.

Then, the Seat class becomes:

@Value

public class Seat {

int row;

String seatLetter;

SeatStatus status;

SeatClass seatClass;

Booking booking;

}

But when initializing seats in SeatPlan, the booking is null. So in the constructor call:

new Seat(row, seatLetter, status, seatClass, null)

Also, create the Booking record:

public record Booking(String passengerName, String bookingNumber, String specialRequests) {}

Now, in the SeatPlan, adding a method to book a seat:

public boolean bookSeat(int row, String seatLetter, Booking booking) {

for (int i = 0; i < seats.size(); i++) {

Seat seat = seats.get(i);

if (seat.getRow() == row && seat.getSeatLetter().equalsIgnoreCase(seatLetter)) {

if (seat.getStatus() == SeatStatus.AVAILABLE) {

Seat newSeat = new Seat(seat.getRow(), seat.getSeatLetter(), SeatStatus.RESERVED, seat.getSeatClass(), booking);

seats.set(i, newSeat);

return true;

} else {

return false;

}

}

}

return false;

}

But wait, using Lombok's @With would allow us to create a new Seat with some fields changed. For example, if Seat has withStatus(SeatStatus) and withBooking(Booking) methods. But @With generates these methods only if the fields are annotated. Wait, Lombok's @With annotation on the class generates methods for all fields. But perhaps better to manually add the with methods if needed, or just create a new instance.

Alternatively, since Seat is immutable, creating a new instance is the way to go.

In the Seat class, since it's a @Value, all fields are final. So when booking, create a new Seat with the updated status and booking.

So, in the bookSeat method, when the seat is found and available, create a new Seat with status RESERVED and the provided booking.

Then, in FlightSeatManagerApp's assignSeat method, we need to collect passenger details, call seatPlan.bookSeat, and handle success or failure.

So in the assignSeat method:

private void assignSeat(Scanner scanner) {

log.info("Enter row number:");

int row = scanner.nextInt();

log.info("Enter seat letter:");

String seatLetter = scanner.next().toUpperCase();

scanner.nextLine(); // consume remaining newline

// Check if seat exists and is available

Seat seat = seatPlan.getSeats().stream()

.filter(s -> s.getRow() == row && s.getSeatLetter().equalsIgnoreCase(seatLetter))

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

But wait, the SeatPlan's bookSeat method needs to be implemented. So in SeatPlan.java, add:

public boolean bookSeat(int row, String seatLetter, Booking booking) {

for (int i = 0; i < seats.size(); i++) {

Seat currentSeat = seats.get(i);

if (currentSeat.getRow() == row && currentSeat.getSeatLetter().equalsIgnoreCase(seatLetter)) {

if (currentSeat.getStatus() == SeatStatus.AVAILABLE) {

Seat newSeat = new Seat(row, currentSeat.getSeatLetter(), SeatStatus.RESERVED, currentSeat.getSeatClass(), booking);

seats.set(i, newSeat);

return true;

} else {

return false;

}

}

}

return false;

}

Also, need to ensure that seatLetter comparison is case-insensitive. So in the loop, use equalsIgnoreCase.

But in the Seat class, the seatLetter is stored as uppercase? Because in the initialization, seatLetter is generated as (char) ('A' + seatNum), which is uppercase. So when the user enters lowercase letters, converting to uppercase is necessary. Hence, in the assignSeat method, seatLetter is converted to uppercase.

Additionally, when displaying seats, the seatLetter is uppercase, so the user should input uppercase letters, but the code can handle lowercase by converting.

Now, testing this code: when a seat is booked, its status becomes RESERVED, and the booking info is attached. The displaySeatAvailability method should show [R] for reserved seats.

But in the display method, the getStatusSymbol maps RESERVED to [R], which is correct.

Potential issues:

- The seat list in SeatPlan is an ArrayList, so replacing elements by index should work.

- Concurrent modification isn't a concern here since it's a single-threaded console app.

- The bookingNumber is a string; maybe it should be validated, but per requirements, no validation is specified.

- Special requests are optional, so the code allows empty input.

Edge Cases:

- Entering a non-existent seat (row/letter combo). Handled by checking if seat exists.

- Booking an already reserved seat. Handled by checking status.

Now, integrating all this into the code changes.

First, create the Booking record.

Then, modify Seat to include the Booking field.

Update SeatPlan's initializeSeats to include null for booking.

Implement the bookSeat method in SeatPlan.

Update the assignSeat method in FlightSeatManagerApp to collect info and call bookSeat.

Also, adjust the SeatPlan's getSeatsByClassAndRow method to include the booking field, but that's already handled as it returns the Seat objects which now include booking.

Testing this would require running the app, selecting option 2, entering details, and verifying the seat status changes to [R].

Another thing: in the current code, the Seat's seatLetter is stored as a String. When initializing, it's String.valueOf(seatLetter), which for seatLetter being a char, gives "A", "B", etc. So comparing with equalsIgnoreCase is safe.

Thus, these changes should implement F03.


# Answer

To implement the seat booking functionality, we'll enhance the existing classes to track passenger details and update seat statuses. Here are the necessary changes:

Step-by-Step Changes
Add Booking Record
Create a new record to hold passenger booking information.

Update Seat Class
Include a Booking field and modify seat initialization.

Enhance SeatPlan
Add a method to update seat status and booking details.

Implement Booking Logic
Modify the assignSeat method to collect passenger data and process the booking.

... CODE ...

Explanation
Booking Record: Stores passenger-specific details like name, booking number, and special requests.

Seat Class Update: Adds a Booking field to associate passenger data with a seat.

Seat Booking Logic:

The bookSeat method in SeatPlan checks seat availability and updates it to RESERVED with booking details.

The assignSeat method in FlightSeatManagerApp guides the user through inputting passenger information and validates the seat before booking.

Status Handling: Reserved seats are marked with [R] in the display, providing clear visual feedback.