# Reasoning

Okay, I need to implement the feature where seats can be blocked and released. Let me start by understanding the requirements. Blocking a seat means marking it as unavailable for booking or reservation, maybe due to technical issues, VIP needs, or crew requirements. Releasing would change it back to available.

First, looking at the existing code, the SeatStatus enum already has BLOCKED. So I need a way to change a seat's status to BLOCKED and back.

In the SeatPlan class, there are methods for booking and reserving seats. I should add methods to block and unblock seats. Blocking a seat would involve finding the seat and changing its status to BLOCKED, regardless of its current state (unless it's already blocked). Releasing would set it back to AVAILABLE, but maybe only if it's blocked. Wait, but what if the seat was previously reserved or occupied? Maybe blocking overrides any existing status, and releasing would set it to available regardless. Or perhaps blocking is only allowed if the seat is available. The user story says "manually marked as blocked", so maybe ground staff can block any seat, even if it's reserved or occupied. But that might conflict with existing bookings. Hmm, the requirement says blocked seats are not available for booking, so maybe blocking a seat that's already reserved or occupied would need to first cancel the existing reservation/booking. But that might be more complex. Alternatively, perhaps blocking is only allowed on available seats. The problem statement isn't clear. Let me check the original requirement again.

The user story says: "Individual seats can be manually marked as blocked... These seats are then not available for booking." So perhaps when a seat is blocked, it's regardless of its current state. For example, if a seat is already reserved, blocking it would cancel the reservation and mark it as blocked. But that might require additional steps, like handling the cancellation of existing bookings. Alternatively, maybe blocking is only allowed on available seats. The problem statement isn't explicit. Given that, perhaps the simplest approach is to allow blocking any seat, and when blocked, any existing booking or reservation is removed, and the status is set to BLOCKED. Similarly, releasing a blocked seat would set it to AVAILABLE.

So, in the SeatPlan class, I need a method to block a seat. This method would find the seat, set its status to BLOCKED, and clear any booking or reservation. Similarly, a method to release the seat would set status to AVAILABLE, but only if it's currently blocked.

Wait, but when releasing, should the seat go back to its previous state? Probably not, since blocking is a manual override. So blocking a seat makes it BLOCKED, and releasing sets it to AVAILABLE, regardless of prior state. That seems more straightforward.

So the steps are:

1. Add a blockSeat method in SeatPlan that sets the seat's status to BLOCKED and clears booking/reservation.

2. Add a releaseSeat method that sets the seat's status to AVAILABLE if it's BLOCKED.

3. Update the menu in FlightSeatManagerApp to include options for blocking and releasing seats.

4. Ensure that when displaying seats, BLOCKED status is shown correctly.

5. Make sure that blocked seats cannot be booked or reserved.

Wait, looking at existing code, when booking or reserving, the system checks if the seat is AVAILABLE. Since blocked seats have status BLOCKED, they won't be available, so the existing checks should prevent booking. So no changes needed there.

Testing scenarios: If a seat is blocked, it should show as [B]. When released, it becomes [A]. Blocking a reserved seat would remove the reservation and mark it as blocked.

So, implementing blockSeat:

In SeatPlan, loop through seats, find the seat by row and letter. When found, create a new Seat with status BLOCKED, and null booking and reservation.

For releaseSeat, find the seat, check if status is BLOCKED, then set to AVAILABLE, keeping booking/reservation as null.

Wait, but if a seat was blocked when it had a reservation, releasing it would make it available, but the original reservation is gone. That's correct because blocking is a manual override. So blocking a seat effectively cancels any existing reservation or booking and blocks it.

So the blockSeat method would:

- Find the seat

- If found, replace it with a new Seat object with status BLOCKED, booking and reservation null.

Similarly, releaseSeat would:

- Find the seat

- If status is BLOCKED, replace it with a new Seat object with status AVAILABLE, booking and reservation null.

Wait, but when releasing, maybe the user wants to set it back to its previous state? But that's not specified. The requirement is to mark as free again. So perhaps releasing a blocked seat makes it available, regardless of prior state.

So the methods would be:

blockSeat(int row, String seatLetter): sets to BLOCKED, clears any booking/reservation.

releaseSeat(int row, String seatLetter): sets to AVAILABLE only if currently BLOCKED.

Now, updating SeatPlan with these methods.

Then, in FlightSeatManagerApp, adding new menu options for block and release.

Wait, the original menu in the last code had options up to 7 (swap) and 8 (exit). Now adding two new options: block and release. So the menu would need to be adjusted. But according to the problem statement, F07 is Block/release seat. So the feature includes both blocking and releasing. So the menu should have two options, or maybe a submenu. Alternatively, one option for block and another for release. Let me check the previous steps. For example, in F05, cancel seat was a single option. Similarly, here, perhaps two separate menu options: "Block seat" and "Release seat".

But the user might want to have them under one menu item, but the problem states F07 as a single feature. However, the functions are two: block and release. So adding two new menu options.

But looking at previous steps, each function added a single menu option. For example, F03 was book seat (assign), F04 reserve, F05 cancel, F06 swap. So F07 would need two new menu items: block and release.

Thus, in the menu:

After the current options (1-7), add:

8. Block seat

9. Release seat

10. Exit

But the current code after F06 has menu options up to 7 (swap) and 8 (exit). So adding block and release would require renumbering. Alternatively, inserting new options. Let me check the previous code.

In the last step (F06), the code had:

case 7 -> swapSeats(scanner);

case 8 -> running = false;

So to add F07, we need to insert two new menu options before exit. So:

case 7 -> swapSeats(scanner);

case 8 -> blockSeat(scanner);

case 9 -> releaseSeat(scanner);

case 10 -> running = false;

But this would require updating the menu display and the switch cases.

Alternatively, perhaps the user intended F07 as a single feature with two functions, but according to the problem statement, it's a single function (block/release), but the implementation requires two separate actions. Therefore, two new menu options.

So in FlightSeatManagerApp's start() method, update the menu:

log.info("1. Display seat availability");

log.info("2. Assign seat");

log.info("3. Change seat assignment");

log.info("4. Make reservation");

log.info("5. Confirm reservation");

log.info("6. Cancel seat");

log.info("7. Swap seats");

log.info("8. Block seat");

log.info("9. Release seat");

log.info("10. Exit");

Then, in the switch:

case 8 -> blockSeat(scanner);

case 9 -> releaseSeat(scanner);

case 10 -> running = false;

Now, implementing blockSeat and releaseSeat methods.

In SeatPlan, adding:

public boolean blockSeat(int row, String seatLetter) {

for (int i = 0; i < seats.size(); i++) {

Seat seat = seats.get(i);

if (seat.getRow() == row && seat.getSeatLetter().equalsIgnoreCase(seatLetter)) {

// Block regardless of current state

Seat blockedSeat = new Seat(row, seatLetter, SeatStatus.BLOCKED, seat.getSeatClass(), null, null);

seats.set(i, blockedSeat);

return true;

}

}

return false;

}

public boolean releaseSeat(int row, String seatLetter) {

for (int i = 0; i < seats.size(); i++) {

Seat seat = seats.get(i);

if (seat.getRow() == row && seat.getSeatLetter().equalsIgnoreCase(seatLetter)) {

if (seat.getStatus() == SeatStatus.BLOCKED) {

Seat releasedSeat = new Seat(row, seatLetter, SeatStatus.AVAILABLE, seat.getSeatClass(), null, null);

seats.set(i, releasedSeat);

return true;

}

return false;

}

}

return false;

}

Wait, but releasing should only work if the seat is blocked. So in releaseSeat, check if the seat's status is BLOCKED before changing to AVAILABLE.

In the blockSeat method, replace the seat with a new one with status BLOCKED and null booking/reservation.

In the FlightSeatManagerApp, adding the new methods:

private void blockSeat(Scanner scanner) {

log.info("Enter row number:");

int row = scanner.nextInt();

log.info("Enter seat letter:");

String seatLetter = scanner.next().toUpperCase();

scanner.nextLine(); // consume newline

boolean success = seatPlan.blockSeat(row, seatLetter);

if (success) {

log.info("Seat {}{} blocked successfully.", row, seatLetter);

} else {

log.error("Seat {}{} not found.", row, seatLetter);

}

}

private void releaseSeat(Scanner scanner) {

log.info("Enter row number:");

int row = scanner.nextInt();

log.info("Enter seat letter:");

String seatLetter = scanner.next().toUpperCase();

scanner.nextLine(); // consume newline

boolean success = seatPlan.releaseSeat(row, seatLetter);

if (success) {

log.info("Seat {}{} released to available.", row, seatLetter);

} else {

log.error("Seat {}{} is not blocked or not found.", row, seatLetter);

}

}

Then, update the menu options as discussed.

Testing this, when a seat is blocked, its status becomes BLOCKED, and in the display, it shows [B]. When released, it becomes [A].

Also, ensure that blocked seats cannot be booked or reserved. Since in the bookSeat and reserveSeat methods, they check if the seat is AVAILABLE. Blocked seats are not AVAILABLE, so those methods would return false, which is handled in the UI with error messages.

Now, this should fulfill F07 requirements.


# Answer

To implement seat blocking and releasing functionality, we add direct status manipulation while maintaining data integrity. Here are the changes:

Step-by-Step Changes
Add Block/Release Methods in SeatPlan
Force-set status to BLOCKED/AVAILABLE and clear associated data

New Menu Options
Add dedicated options for blocking and releasing seats

Input Handling
Validate seat existence and provide clear feedback

... CODE ...

Key Features
Forced Blocking

Overrides any existing reservations/bookings

Immediate status change to BLOCKED ([B] in display)

Safe Release

Only works on currently blocked seats

Resets to AVAILABLE ([A]) with clean state

Data Cleanliness

Explicit nullification of booking/reservation data

Immutable seat updates prevent partial state changes

Integration

Blocked seats automatically excluded from booking/reservation flows

Real-time status updates visible in seat display

This implementation provides ground staff with direct control over seat availability while maintaining system consistency. Blocked seats appear clearly in the interface and cannot be accidentally modified through normal booking flows.