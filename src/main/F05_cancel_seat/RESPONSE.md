# Reasoning

Okay, let's tackle F05 where we need to allow cancellations of bookings and reservations, marking the seat as free again.

First, I need to figure out how the current system tracks bookings and reservations. From previous code, a Seat has a status, and either a Booking or Reservation object. So to cancel, we need to reset those fields and set the status back to AVAILABLE.

Hmm, the Seat class is immutable using Lombok's @Value. That means every time we modify a seat, we have to create a new instance. So in the SeatPlan, when we cancel, we'll need to find the seat, check if it's either reserved or occupied, then create a new seat with status AVAILABLE and null booking/reservation.

Wait, the SeatPlan has a list of seats. So in the bookSeat and reserveSeat methods, they replace the seat in the list. For cancellation, similarly, we'll loop through the seats, find the matching one, and replace it with a new Seat object that's AVAILABLE with no booking or reservation.

But how do we handle both bookings and reservations? The cancel method needs to check the current status. If the seat is RESERVED or OCCUPIED, it can be cancelled. Let's create a method in SeatPlan called cancelSeat(int row, String seatLetter) that does this.

In the FlightSeatManagerApp, the cancel functionality will be a new menu option. The user enters the row and seat letter, then the app calls seatPlan.cancelSeat. Then, the seat's status is set to AVAILABLE, and booking/reservation is null.

Wait, but when a seat is OCCUPIED, it has a Booking; when RESERVED, it has a Reservation. So in the cancel method, regardless of which it is, we just set both to null and status to AVAILABLE.

Testing edge cases: What if the seat is already AVAILABLE? The method should do nothing and return false, perhaps. So the cancelSeat method returns a boolean indicating success.

Also, need to update the menu in FlightSeatManagerApp to include the cancel option. The previous menu had options up to 5 (confirm reservation) and 6 to exit. So adding a new option 6 for cancel, and 7 to exit. Wait, but in the previous step, the menu after F04 had options 1-5, then 5 was confirm, 6 exit. Wait, looking back, in F04's code, the menu options were:

case 1 -> displaySeatAvailability();

case 2 -> assignSeat(scanner);

case 3 -> changeAssignment(scanner);

case 4 -> makeReservation(scanner);

case 5 -> confirmReservation(scanner);

case 6 -> running = false;

So adding a new option 6 for cancel, and changing exit to 7. But maybe the user's previous code might have different numbers. Wait, in the F04 code, the menu after adding confirmReservation was:

The menu in start() had:

log.info("1. Display seat availability");

log.info("2. Assign seat");

log.info("3. Change seat assignment");

log.info("4. Make reservation");

log.info("5. Confirm reservation");

log.info("6. Exit");

So choices 1-6. So to add cancel, insert a new option 6, and exit becomes 7. But the code in the switch statement was:

case 5 -> confirmReservation(scanner);

case 6 -> running = false;

So adding case 6 -> cancelSeat(scanner); and case 7 -> running = false.

Wait, but the user might have to adjust the menu options. So in the FlightSeatManagerApp's start() method, when printing the menu, need to add the new option.

So, the steps are:

1. Add a cancelSeat method in SeatPlan that finds the seat by row and letter, checks if it's RESERVED or OCCUPIED, then creates a new Seat with AVAILABLE status and null booking/reservation.

2. Add a new menu option in FlightSeatManagerApp for canceling, and handle the input.

3. Implement the cancelSeat method in the app, prompting for row and seat letter, then calling SeatPlan's cancel method.

Potential issues: handling the seat existence and current status. Need to validate that the seat exists and is in a cancellable state.

Testing: If the seat is already AVAILABLE, cancel should fail. If it's RESERVED or OCCUPIED, cancel should succeed.

Now, code changes.

In SeatPlan.java:

public boolean cancelSeat(int row, String seatLetter) {

for (int i = 0; i < seats.size(); i++) {

Seat seat = seats.get(i);

if (seat.getRow() == row && seat.getSeatLetter().equalsIgnoreCase(seatLetter)) {

if (seat.getStatus() == SeatStatus.RESERVED || seat.getStatus() == SeatStatus.OCCUPIED) {

Seat newSeat = new Seat(row, seatLetter, SeatStatus.AVAILABLE, seat.getSeatClass(), null, null);

seats.set(i, newSeat);

return true;

} else {

return false; // Seat not reserved or occupied

}

}

}

return false; // Seat not found

}

In FlightSeatManagerApp:

Add a new method:

private void cancelSeat(Scanner scanner) {

log.info("Enter row number:");

int row = scanner.nextInt();

log.info("Enter seat letter:");

String seatLetter = scanner.next().toUpperCase();

scanner.nextLine(); // Consume newline

boolean success = seatPlan.cancelSeat(row, seatLetter);

if (success) {

log.info("Seat {}{} has been cancelled and is now available.", row, seatLetter);

} else {

log.error("Cancellation failed. Seat {}{} may not exist or is not reserved/occupied.", row, seatLetter);

}

}

Update the menu options in start():

log.info("1. Display seat availability");

log.info("2. Assign seat");

log.info("3. Change seat assignment");

log.info("4. Make reservation");

log.info("5. Confirm reservation");

log.info("6. Cancel seat");

log.info("7. Exit");

And in the switch:

case 6 -> cancelSeat(scanner);

case 7 -> running = false;

Also, in Seat.java, since we're using Lombok's @Value, the constructor includes all fields. When canceling, both booking and reservation are set to null.

Testing: If a seat was reserved (status RESERVED with a Reservation object), canceling sets status to AVAILABLE and removes the reservation. Similarly for OCCUPIED with Booking.

This should handle F05 requirements.


# Answer

To implement seat cancellation functionality, we add methods to clear bookings/reservations and update the seat status. Here are the necessary code changes:

Step-by-Step Changes
Add Cancellation Method in SeatPlan
Finds the seat and resets its state if cancellable.

Update Menu Options
Add cancellation as a new menu option with handling logic.

Input Validation
Ensure only reserved/occupied seats can be cancelled.

... CODE ...

Key Improvements
Cancellation Logic

Works for both RESERVED (temporary holds) and OCCUPIED (permanent bookings)

Fully resets seat state to AVAILABLE

Data Integrity

Explicitly nullifies both booking and reservation fields

Immutable seat replacement ensures thread safety

User Feedback

Clear success/error messages using SLF4J

Input normalization (case-insensitive seat letters)

This implementation allows ground staff to free up seats while maintaining audit trails through the existing booking/reservation records until cancellation.
