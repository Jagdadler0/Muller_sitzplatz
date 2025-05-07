# Reasoning

Okay, I need to implement feature F09: Manage passenger list. The goal is to display all occupied seats along with their passenger details like name, ticket number, and special requests. Let me look at the existing code structure to see where changes are needed.

First, the Seat class has a Booking field which is populated when a seat is occupied. The Booking record already contains passengerName, bookingNumber, and specialRequests. So, the data is there; I just need to retrieve and display it.

I should add a method in the SeatPlan class to get all occupied seats. That method would filter the seats list for those with status OCCUPIED. Then, in the FlightSeatManagerApp, I can create a new menu option to trigger this display.

Let me check the existing menu options. The last one was F08, which added search functionality. The menu in start() goes up to 11 for exit. So, adding a new option would require updating the menu numbers. Let's see:

Current menu options in start():

1. Display seat availability

2. Assign seat

3. Change seat assignment

4. Make reservation

5. Confirm reservation

6. Cancel seat

7. Swap seats

8. Search available seats

9. Block seat

10. Release seat

11. Exit

So F09 would be a new option, probably number 9, shifting the existing 9-11 to 10-12. Wait, no, the current case 8 is search, 9 is block, 10 release, 11 exit. So inserting a new option for passenger list would require adjusting the numbers. Let me confirm the current code:

In the previous F08 step, the menu options after adding F08 were:

case 8 -> searchAvailableSeats(scanner);

case 9 -> blockSeat(scanner);

case 10 -> releaseSeat(scanner);

case 11 -> running = false;

So adding F09 as a new option would need to insert it into the menu, say as option 9, and shift the others. Wait, the user's latest code after F08 had options up to 11 for exit. So inserting a new option would require:

Update the menu display in start() to include the new option, then adjust the case numbers in the switch.

Wait, let me check the exact code from the last answer. In the F08 implementation, the menu was updated to:

log.info("8. Search available seats");

log.info("9. Block seat");

log.info("10. Release seat");

log.info("11. Exit");

And the switch cases:

case 8 -> searchAvailableSeats(scanner);

case 9 -> blockSeat(scanner);

case 10 -> releaseSeat(scanner);

case 11 -> running = false;

So to add a new option for managing passenger list, the new option should be inserted, say as option 9, and the subsequent numbers incremented. But the user might have added other features, but according to the history, the latest is F08, which added up to option 11.

So for F09, the new option would be inserted at position 9, making the menu:

1. Display seat availability

2. Assign seat

3. Change seat assignment

4. Make reservation

5. Confirm reservation

6. Cancel seat

7. Swap seats

8. Search available seats

9. Manage passenger list

10. Block seat

11. Release seat

12. Exit

But in the code, the current last case is 11 for exit. So the new case would be 9, and the existing 9-11 become 10-12.

But the user's current code may have different numbering. Let me check the exact code from the previous answer.

In the previous step (F08), the FlightSeatManagerApp's start() method had:

log.info("8. Search available seats");

log.info("9. Block seat");

log.info("10. Release seat");

log.info("11. Exit");

And the switch cases:

case 8 -> searchAvailableSeats(scanner);

case 9 -> blockSeat(scanner);

case 10 -> releaseSeat(scanner);

case 11 -> running = false;

So inserting the new option (Manage passenger list) as option 9 would require:

Updating the menu display:

log.info("8. Search available seats");

log.info("9. Manage passenger list");

log.info("10. Block seat");

log.info("11. Release seat");

log.info("12. Exit");

And the switch cases:

case 8 -> searchAvailableSeats(scanner);

case 9 -> displayPassengerList();

case 10 -> blockSeat(scanner);

case 11 -> releaseSeat(scanner);

case 12 -> running = false;

Wait, but the user's current code may have different numbers. But according to the last code provided for F08, after adding F08, the menu options are 8,9,10,11. So adding F09 would be inserting a new option at 9, pushing the rest to 10,11,12.

But the user's current code in the FlightSeatManagerApp's start() method has:

In F08, the code after adding search:

private void start() {

// ... existing setup ...

while (running) {

log.info("\nMenu:");

log.info("1. Display seat availability");

log.info("2. Assign seat");

log.info("3. Change seat assignment");

log.info("4. Make reservation");

log.info("5. Confirm reservation");

log.info("6. Cancel seat");

log.info("7. Swap seats");

log.info("8. Search available seats");

log.info("9. Block seat");

log.info("10. Release seat");

log.info("11. Exit");

// ... rest of menu handling ...

}

}

And the switch cases:

case 8 -> searchAvailableSeats(scanner);

case 9 -> blockSeat(scanner);

case 10 -> releaseSeat(scanner);

case 11 -> running = false;

So to add F09 as "Manage passenger list", the new option should be inserted at, say, option 9, shifting the existing 9-11 to 10-12. Therefore, updating the menu to:

log.info("8. Search available seats");

log.info("9. Manage passenger list");

log.info("10. Block seat");

log.info("11. Release seat");

log.info("12. Exit");

And updating the switch:

case 8 -> searchAvailableSeats(scanner);

case 9 -> displayPassengerList();

case 10 -> blockSeat(scanner);

case 11 -> releaseSeat(scanner);

case 12 -> running = false;

But I need to make sure the code changes are correctly numbered. Now, to implement the passenger list display.

In SeatPlan, add a method to get all occupied seats:

public List<Seat> getOccupiedSeats() {

return seats.stream()

.filter(seat -> seat.getStatus() == SeatStatus.OCCUPIED)

.collect(Collectors.toList());

}

Then, in FlightSeatManagerApp, add a method to display passengers:

private void displayPassengerList() {

List<Seat> occupiedSeats = seatPlan.getOccupiedSeats();

if (occupiedSeats.isEmpty()) {

log.info("No passengers currently booked.");

return;

}

log.info("Passenger List:");

occupiedSeats.forEach(seat -> {

Booking booking = seat.getBooking();

log.info("Seat {}{}: {} (Ticket: {}) - Requests: {}",

seat.getRow(),

seat.getSeatLetter(),

booking.passengerName(),

booking.bookingNumber(),

booking.specialRequests().isEmpty() ? "None" : booking.specialRequests());

});

}

Then, update the menu options in start() to include the new option and adjust the numbers accordingly.

Wait, but in the current code, the menu options after F08 are up to 11. So inserting the new option as 9 would require:

In the log.info statements:

log.info("9. Manage passenger list");

log.info("10. Block seat");

log.info("11. Release seat");

log.info("12. Exit");

And the switch cases:

case 9 -> displayPassengerList(scanner); // Wait, but the method doesn't take scanner. Wait, no, displayPassengerList doesn't need scanner input. So the method would be called without parameters.

Wait, the method displayPassengerList doesn't require any input from the user, so it can be a parameter-less method.

But in the code above, the method is:

private void displayPassengerList() {

// code

}

So in the switch case, it's case 9 -> displayPassengerList();

But in the current code, the start() method's switch uses scanner for some options. For example:

case 1 -> displaySeatAvailability();

case 2 -> assignSeat(scanner);

So methods that require user input take the scanner, others don't.

Thus, the new method displayPassengerList() doesn't need the scanner, so the case is case 9 -> displayPassengerList();

So adding this method and updating the menu.

Putting it all together:

In SeatPlan.java, add the getOccupiedSeats method.

In FlightSeatManagerApp.java, add the displayPassengerList method.

Update the menu options and switch cases.

Now, test if this works. When the user selects the new option, it should list all occupied seats with their booking details.

Edge cases: If no seats are occupied, it should display a message. Also, ensure that only OCCUPIED seats are considered, not RESERVED ones.

Yes, because getOccupiedSeats filters on SeatStatus.OCCUPIED.

This should fulfill F09's requirements.


# Answer

To implement passenger list management showing booked seat details, here are the necessary code changes:

Step-by-Step Implementation
Add Occupied Seats Retrieval
New method in SeatPlan to filter occupied seats

Passenger Display Method
Format and output booking details for occupied seats

Menu Integration
Add new menu option and adjust navigation

... CODE ...

Key Features
Comprehensive Display
Shows all critical passenger details in a readable format:

Seat 25A (ECONOMY)
Passenger: John Doe
Ticket: TK789456
Requests: Vegetarian meal
Automatic Filtering
Only shows permanently booked seats (OCCUPIED status), ignoring temporary reservations

Empty State Handling
Clearly communicates when no passengers are booked

Data Security
Maintains encapsulation - passenger details only exposed through controlled display method

This implementation provides ground staff with instant access to critical passenger information while maintaining data privacy and system integrity.