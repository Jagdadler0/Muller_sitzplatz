# Reasoning

Okay, I need to add the ability to search for available seats based on class, position (window, aisle, middle), and special criteria like extra legroom. Let's start by figuring out how to determine a seat's position.

First, each seat has a seat letter (like A, B, C, etc.) and a row. For position, window seats are typically the first and last seats in a row. Aisle seats are next to the aisles, and middle seats are in between. So I need to categorize each seat based on its seat letter and the total seats per row in its class.

Wait, the Airbus A350-900 has different seat configurations per class. Business might have 4 seats per row, Premium Economy 8, and Economy 9. So the position (window, aisle, middle) depends on the class's seat layout. For example, in Economy with 9 seats, the layout might be ABC DEFGH JK, where A and K are window seats, C and D are aisle, etc. Hmm, maybe I should model the seat positions based on the number of seats per row in each class.

So, for each seat, based on its seat class and the number of seats per row in that class, determine if it's a window, aisle, or middle seat. Then allow filtering available seats by these positions.

Also, special criteria like legroom. Maybe certain rows have extra legroom, like exit rows. For the A350-900, exit rows could be predefined. So I need a way to tag seats in exit rows as having extra legroom.

How to model this? Maybe add a method in Seat to determine its position and whether it's in an exit row. Let's see:

For the seat position:

- In Business class with 4 seats per row (e.g., A, B, C, D), window seats are A and D, aisle are B and C (assuming aisle in the middle).

Wait, actually, in a typical Business class configuration with 2-2-2 layout, but the initial code has 4 seats per row. Hmm, maybe the original setup was 4 seats per row for Business. Wait, looking back at F01, the SeatSections are:

- Business: rows 1-8, 4 seats per row

- Premium Economy: rows 9-16, 8 seats per row

- Economy: rows 17-40, 9 seats per row

So for Business with 4 seats per row, maybe the layout is A B C D, with two aisles. But in reality, Business class on A350 is usually 1-2-1, but maybe the initial code is simplified. For the purpose of this feature, I need to define window, aisle, middle based on seat letter and seats per row.

So, for a given seat, determine its position:

- Window: first or last seat in the row (based on seats per row)

- Aisle: seats adjacent to the aisle(s)

- Middle: seats between window and aisle, if applicable

But how exactly? Let's think for each class:

Business (4 seats per row):

Assuming the layout is A B C D. If the aisle is between B and C, then A and D are windows, B and C are aisle seats.

Premium Economy (8 seats per row):

Possibly A B C D E F G H. If the aisles are between D and E, then A and H are windows, D and E are aisle seats. B, C, F, G could be middle or aisle depending on configuration. Maybe in 2-4-2 layout, so A and H are window, B and G are middle, C and F are aisle, D and E are aisle? Not sure. Maybe for simplicity, in 8 seats per row, window seats are A and H, aisle are D and E, and the rest are middle.

Economy (9 seats per row):

Possibly A B C D E F G H J. Maybe in a 3-3-3 layout, aisles between C/D and G/H. So A and J are window seats, D and G are aisle seats, others are middle.

But this can vary. Since the exact layout isn't specified, perhaps we can define the seat positions based on the seat letter's position in the row. For example:

- Window: first and last seat letters (A and last letter)

- Aisle: seats adjacent to the middle of the row (depends on seats per row)

Alternatively, maybe for each seat in a row, based on the number of seats per row, determine window, aisle, middle.

Alternatively, predefine for each class the positions. For example:

For Business (4 seats):

- Window: A, D

- Aisle: B, C

Premium Economy (8 seats):

- Window: A, H

- Aisle: D, E

- Middle: B, C, F, G

Economy (9 seats):

- Window: A, J

- Aisle: D, G

- Middle: B, C, E, F, H

This is a simplified approach. Then, for each seat, determine its position based on its class and seat letter.

So, add a method in Seat to determine the position (window, aisle, middle) based on the seat's class and seat letter.

Also, for exit rows: maybe certain rows are exit rows with extra legroom. For example, rows 17 and 40 in Economy might be exit rows. Wait, but rows 17-40 are Economy. So maybe rows 17 and 40 are exit rows. Or perhaps rows 25 and 40. Need to define which rows are exit rows. For the sake of this feature, perhaps hardcode that certain rows (like rows 10, 25, 40) are exit rows. Or maybe add a method in Seat to check if it's an exit row based on its row number.

So, steps:

1. Add a method in Seat to determine seat position (window, aisle, middle) based on class and seat letter.

2. Add a method in Seat to check if it's in an exit row (extra legroom).

3. Add search methods in SeatPlan to filter available seats by class, position, and exit row status.

4. Update the FlightSeatManagerApp to allow users to input search criteria and display results.

Let's start by modifying the Seat class to include position and exit row info.

But since Seat is a Lombok @Value (immutable), we can compute these properties on the fly via helper methods.

So, add methods in Seat:

public SeatPosition getPosition() {

// determine based on seatClass and seatLetter

}

public boolean isExitRow() {

// check if row is an exit row based on class

}

But since Seat is a record (or Lombok @Value), methods can be added.

Alternatively, compute these in the search filters.

Wait, maybe better to compute these dynamically when searching, to avoid storing redundant data.

So, in SeatPlan, when searching, for each seat, determine its position and exit row status.

But first, define an enum SeatPosition:

public enum SeatPosition {

WINDOW, AISLE, MIDDLE

}

Then, in SeatPlan, create a helper method to determine the SeatPosition for a given seat.

Similarly, determine if the seat is in an exit row.

For exit rows, perhaps:

- Business: none

- Premium Economy: rows 15, 16

- Economy: rows 17, 25, 40

But this is arbitrary. For the example, we can define exit rows as follows:

Business: none.

Premium Economy: rows 15 and 16.

Economy: rows 17, 25, 40.

So, in a helper method isExitRow(Seat seat):

if seatClass is BUSINESS → false

else if PREMIUM_ECONOMY → row is 15 or 16

else if ECONOMY → row is 17, 25, 40

Now, to implement the search:

In SeatPlan, add methods:

public List<Seat> findAvailableSeats(SeatClass seatClass, SeatPosition position, boolean exitRowOnly) {

return seats.stream()

.filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)

.filter(seat -> seatClass == null || seat.getSeatClass() == seatClass)

.filter(seat -> position == null || getSeatPosition(seat) == position)

.filter(seat -> !exitRowOnly || isExitRow(seat))

.collect(Collectors.toList());

}

But need to implement getSeatPosition(Seat seat) and isExitRow(Seat seat).

Implementing getSeatPosition(Seat seat):

private SeatPosition getSeatPosition(Seat seat) {

int seatsPerRow = getSeatsPerRow(seat.getSeatClass());

char seatChar = seat.getSeatLetter().charAt(0);

int seatIndex = seatChar - 'A';

switch (seat.getSeatClass()) {

case BUSINESS:

if (seatsPerRow == 4) {

if (seatIndex == 0 || seatIndex == 3) {

return SeatPosition.WINDOW;

} else {

return SeatPosition.AISLE;

}

}

break;

case PREMIUM_ECONOMY:

if (seatsPerRow == 8) {

if (seatIndex == 0 || seatIndex == 7) {

return SeatPosition.WINDOW;

} else if (seatIndex == 3 || seatIndex == 4) {

return SeatPosition.AISLE;

} else {

return SeatPosition.MIDDLE;

}

}

break;

case ECONOMY:

if (seatsPerRow == 9) {

if (seatIndex == 0 || seatIndex == 8) { // Assuming letters go up to I (9th seat: A-I)

return SeatPosition.WINDOW;

} else if (seatIndex == 3 || seatIndex == 5) { // D and F (assuming 3-3-3 layout, aisles at D and G)

// Wait, in a 3-3-3 layout, seats are ABC DEFG HJ (but letters may vary). Maybe the aisle is between C/D and G/H.

// So for 9 seats, letters A, B, C, D, E, F, G, H, J (skipping I for some airlines).

// So seats A and J are window, D and G are aisle.

// So seat letters for 9 seats: A, B, C, D, E, F, G, H, J (indexes 0-8)

// So index 0 (A) → window

// index 8 (J) → window

// aisle seats: D (index 3) and G (index 6)

if (seatIndex == 3 || seatIndex == 6) {

return SeatPosition.AISLE;

} else if (seatIndex == 0 || seatIndex == 8) {

return SeatPosition.WINDOW;

} else {

return SeatPosition.MIDDLE;

}

}

}

break;

}

return SeatPosition.MIDDLE; // default

}

But need to make sure the seat letters are correctly mapped. For example, in Economy with 9 seats, the letters might be A, B, C, D, E, F, G, H, J (skipping I), so indexes 0-8.

Then, for each class, determine window, aisle, middle based on seat letter's position.

For Business (4 seats):

Letters A, B, C, D (indexes 0-3).

Window: A (0) and D (3)

Aisle: B (1) and C (2)

Premium Economy (8 seats):

Letters A-H (0-7).

Window: A (0) and H (7)

Aisle: D (3) and E (4)

Middle: B (1), C (2), F (5), G (6)

Economy (9 seats):

Letters A, B, C, D, E, F, G, H, J (indexes 0-8).

Window: A (0), J (8)

Aisle: D (3), G (6)

Middle: B (1), C (2), E (4), F (5), H (7)

So the getSeatPosition method can be implemented accordingly.

Now, for the isExitRow method:

private boolean isExitRow(Seat seat) {

SeatClass sc = seat.getSeatClass();

int row = seat.getRow();

switch (sc) {

case BUSINESS:

return false;

case PREMIUM_ECONOMY:

return row == 15 || row == 16;

case ECONOMY:

return row == 17 || row == 25 || row == 40;

default:

return false;

}

}

Now, in the SeatPlan class, add the findAvailableSeats method as above.

Then, in the FlightSeatManagerApp, add a new menu option for searching available seats, prompt the user for filters (class, position, exit row), and display the results.

So, adding a new menu option, say option 8 (assuming previous features took up to 7), but looking back, previous F07 had up to menu option 10 for exit. Wait, let's check:

In the previous F07, the menu options were:

1. Display seat availability

2. Assign seat

3. Change seat assignment

4. Make reservation

5. Confirm reservation

6. Cancel seat

7. Swap seats

8. Block seat

9. Release seat

10. Exit

So adding F08 as a new feature would require adding a new menu option. Let's adjust:

Add a new option 8. Search available seats, then renumber the subsequent options.

Wait, the user's F08 is the next feature. The previous answer for F07 ended with:

// Updated menu in start() method:

log.info("8. Block seat");

log.info("9. Release seat");

log.info("10. Exit");

So after F07, the menu has options up to 10. Now adding F08 (search) would require inserting a new option 8, pushing the others down.

But in the actual code, the current menu options are:

case 8 -> blockSeat(scanner);

case 9 -> releaseSeat(scanner);

case 10 -> running = false;

So to add the new search feature, we need to insert it as a new menu option. Let's adjust the menu numbering:

After F07, the menu has options 1-10. Now for F08, we need to add "Search for available seats" as a new option. Let's insert it as option 8, and renumber the existing 8,9,10 to 9,10,11.

But the user's answer should adjust the code accordingly.

In the FlightSeatManagerApp's start() method, the menu would be updated to include the new search option.

Now, in the code, add:

log.info("8. Search available seats");

log.info("9. Block seat");

log.info("10. Release seat");

log.info("11. Exit");

And in the switch:

case 8 -> searchAvailableSeats(scanner);

case 9 -> blockSeat(scanner);

case 10 -> releaseSeat(scanner);

case 11 -> running = false;

Then, implement the searchAvailableSeats method.

In this method, prompt the user to enter search criteria: class (optional), position (optional), exit row only (yes/no).

For example:

private void searchAvailableSeats(Scanner scanner) {

log.info("Search available seats by:");

log.info("Class (1-Business, 2-Premium Economy, 3-Economy, 0-Any):");

int classChoice = scanner.nextInt();

SeatClass seatClass = null;

if (classChoice >= 1 && classChoice <=3) {

seatClass = SeatClass.values()[classChoice-1];

}

log.info("Position (1-Window, 2-Aisle, 3-Middle, 0-Any):");

int posChoice = scanner.nextInt();

SeatPosition position = null;

if (posChoice >=1 && posChoice <=3) {

position = SeatPosition.values()[posChoice-1];

}

log.info("Only exit rows with extra legroom? (1-Yes, 0-No):");

boolean exitOnly = scanner.nextInt() == 1;

List<Seat> available = seatPlan.findAvailableSeats(seatClass, position, exitOnly);

log.info("Found {} available seats:", available.size());

available.forEach(seat -> {

String exitInfo = isExitRow(seat) ? " (Exit row)" : "";

log.info("Row {}{} - {} Class{}{}",

seat.getRow(), seat.getSeatLetter(),

seat.getSeatClass(),

getSeatPosition(seat),

exitInfo);

});

}

But wait, the SeatPlan's findAvailableSeats method needs to accept SeatClass, SeatPosition, and exitRowOnly.

But in the current code, the SeatPlan doesn't have the findAvailableSeats method yet. So need to add that.

So in SeatPlan:

public List<Seat> findAvailableSeats(SeatClass seatClass, SeatPosition position, boolean exitRowOnly) {

return seats.stream()

.filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)

.filter(seat -> seatClass == null || seat.getSeatClass() == seatClass)

.filter(seat -> position == null || getSeatPosition(seat) == position)

.filter(seat -> !exitRowOnly || isExitRow(seat))

.collect(Collectors.toList());

}

But also implement the helper methods getSeatPosition and isExitRow in SeatPlan.

But these helper methods need to be added to SeatPlan.

So in SeatPlan:

private SeatPosition getSeatPosition(Seat seat) {

int seatsPerRow = 0;

for (SeatSection section : sections) {

if (section.getSeatClass() == seat.getSeatClass()) {

seatsPerRow = section.getSeatsPerRow();

break;

}

}

String seatLetter = seat.getSeatLetter();

int seatIndex = seatLetter.charAt(0) - 'A';

switch (seat.getSeatClass()) {

case BUSINESS:

if (seatsPerRow == 4) {

if (seatIndex == 0 || seatIndex == 3) {

return SeatPosition.WINDOW;

} else {

return SeatPosition.AISLE;

}

}

break;

case PREMIUM_ECONOMY:

if (seatsPerRow == 8) {

if (seatIndex == 0 || seatIndex == 7) {

return SeatPosition.WINDOW;

} else if (seatIndex == 3 || seatIndex == 4) {

return SeatPosition.AISLE;

} else {

return SeatPosition.MIDDLE;

}

}

break;

case ECONOMY:

if (seatsPerRow == 9) {

if (seatIndex == 0 || seatIndex == 8) { // A and J

return SeatPosition.WINDOW;

} else if (seatIndex == 3 || seatIndex == 6) { // D and G

return SeatPosition.AISLE;

} else {

return SeatPosition.MIDDLE;

}

}

break;

}

return SeatPosition.MIDDLE;

}

private boolean isExitRow(Seat seat) {

int row = seat.getRow();

SeatClass sc = seat.getSeatClass();

switch (sc) {

case BUSINESS:

return false;

case PREMIUM_ECONOMY:

return row == 15 || row == 16;

case ECONOMY:

return row == 17 || row == 25 || row == 40;

default:

return false;

}

}

This way, when a user searches, the system filters available seats based on their criteria.

Testing this, for example, searching for all available window seats in Economy class that are exit rows.

Now, the FlightSeatManagerApp's searchAvailableSeats method would prompt the user for these options and display the results.

Also, in the display, each seat is shown with its class, position, and whether it's an exit row.

Potential issues:

- Seat letters in Economy may go up to 9 seats (A to J, skipping I). So seatLetter for index 8 is J.

But the code uses seatLetter from the Seat object, which is generated as 'A' + seatNum. For 9 seats, seatNum ranges from 0 to 8, so letters A (0), B (1), ..., H (7), J (8) if I is skipped. Wait, in the initial code for SeatPlan's initializeSeats, for a section with seatsPerRow=9, seatNum ranges from 0 to 8. So seatLetter is (char) ('A' + seatNum). So for seatNum=8, that's 'A' +8 = 'I'. But some airlines skip 'I' to avoid confusion with '1'. So perhaps in the code, the seat letters for Economy should be A, B, C, D, E, F, G, H, J. But according to the current code, seatNum=8 would generate 'I'.

Hmm, this is a discrepancy. Because in the original code for Economy seatsPerRow=9, the seat letters are from 'A' to 'I' (0-8). But in reality, some airlines skip 'I'. So this could be a problem. However, since the initial code doesn't account for this, maybe we proceed with 'A'-'I' for Economy.

But in that case, for Economy 9 seats, the letters are A, B, C, D, E, F, G, H, I. So the window seats would be A and I (index 0 and 8). Aisle seats D (3) and G (6).

So the code's getSeatPosition method for Economy would check seatIndex 0 (A) and 8 (I) as window, 3 (D) and 6 (G) as aisle.

But in reality, this might not match actual configurations. However, given the initial setup, we proceed with the code as written.

Another thing: when displaying the search results, the code should format the output clearly, showing each seat's row, letter, class, position, and exit row status.

In the searchAvailableSeats method, the log line would be:

log.info("Row {}{} - {} Class {}{}", seat.getRow(), seat.getSeatLetter(), seat.getSeatClass(), seatPosition, exitInfo);

But seatPosition is obtained via getSeatPosition(seat), which is a helper method in SeatPlan.

But in the FlightSeatManagerApp, to get the seat position and exit row status, it needs to call the SeatPlan's helper methods. Alternatively, those methods could be moved to Seat, but since Seat is a Lombok @Value, adding methods is possible.

Alternatively, perhaps the Seat class should have methods to determine its position and exit row status. But since the Seat class is generated with Lombok's @Value, which makes it immutable, we can add methods to it.

So modify the Seat class:

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

if (seatIndex == 0 || seatIndex == 3) { // A or D

return SeatPosition.WINDOW;

} else {

return SeatPosition.AISLE;

}

case PREMIUM_ECONOMY:

if (seatIndex == 0 || seatIndex == 7) { // A or H

return SeatPosition.WINDOW;

} else if (seatIndex == 3 || seatIndex == 4) { // D or E

return SeatPosition.AISLE;

} else {

return SeatPosition.MIDDLE;

}

case ECONOMY:

if (seatIndex == 0 || seatIndex == 8) { // A or I

return SeatPosition.WINDOW;

} else if (seatIndex == 3 || seatIndex == 6) { // D or G

return SeatPosition.AISLE;

} else {

return SeatPosition.MIDDLE;

}

default:

return SeatPosition.MIDDLE;

}

}

public boolean isExitRow() {

switch (seatClass) {

case BUSINESS:

return false;

case PREMIUM_ECONOMY:

return row == 15 || row == 16;

case ECONOMY:

return row == 17 || row == 25 || row == 40;

default:

return false;

}

}

}

This way, the Seat class itself knows its position and exit row status, making the code cleaner.

Then, in SeatPlan's findAvailableSeats method, we can use these methods:

public List<Seat> findAvailableSeats(SeatClass seatClass, SeatPosition position, boolean exitRowOnly) {

return seats.stream()

.filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)

.filter(seat -> seatClass == null || seat.getSeatClass() == seatClass)

.filter(seat -> position == null || seat.getPosition() == position)

.filter(seat -> !exitRowOnly || seat.isExitRow())

.collect(Collectors.toList());

}

This simplifies the code, as the logic is encapsulated within the Seat class.

Then, in the FlightSeatManagerApp's searchAvailableSeats method:

private void searchAvailableSeats(Scanner scanner) {

log.info("Search available seats by:");

log.info("Class (1-Business, 2-Premium Economy, 3-Economy, 0-Any):");

int classChoice = scanner.nextInt();

SeatClass seatClass = null;

if (classChoice >= 1 && classChoice <= 3) {

seatClass = SeatClass.values()[classChoice - 1];

}

log.info("Position (1-Window, 2-Aisle, 3-Middle, 0-Any):");

int posChoice = scanner.nextInt();

SeatPosition position = null;

if (posChoice >= 1 && posChoice <= 3) {

position = SeatPosition.values()[posChoice - 1];

}

log.info("Only exit rows with extra legroom? (1-Yes, 0-No):");

boolean exitOnly = scanner.nextInt() == 1;

scanner.nextLine();  // Consume any leftover newline

List<Seat> available = seatPlan.findAvailableSeats(seatClass, position, exitOnly);

if (available.isEmpty()) {

log.info("No available seats match the criteria.");

} else {

log.info("Available seats:");

available.forEach(seat -> {

String exitInfo = seat.isExitRow() ? " (Exit row)" : "";

log.info("Row {}{} - {} Class, {}{}",

seat.getRow(),

seat.getSeatLetter(),

seat.getSeatClass(),

seat.getPosition(),

exitInfo);

});

}

}

Also, add the SeatPosition enum:

public enum SeatPosition {

WINDOW, AISLE, MIDDLE

}

Now, the user can search for available seats by class, position, and exit row status.

Testing this, for example:

User selects to search for Economy class, window seats, exit rows only. The system displays all available window seats in Economy that are in exit rows (rows 17,25,40).

Potential edge cases:

- A seat is in an exit row but already occupied/reserved/blocked: it won't appear in the search.

- Seat letters beyond 'I' in Economy: but since Economy has 9 seats, the seat letters go up to 'I'.

- Handling user input correctly, especially when they enter invalid options.

This should fulfill F08's requirements.


# Answer

To implement seat search functionality with filters for class, position, and exit rows, we make these key additions:

Step-by-Step Changes
Add SeatPosition Enum
Defines seat types: Window, Aisle, Middle

Enhance Seat Class
Add methods to calculate position and exit row status

Search Method in SeatPlan
Filter available seats using criteria

New Search Menu Option
Interactive search interface with filters

... CODE ...

Key Features
Smart Seat Detection

Automatically determines window/aisle/middle based on class and seat letter

Identifies exit rows using airline-standard row numbers

Flexible Search

Combine any filters: class + position + exit rows

Partial filters (e.g., all Economy seats regardless of position)

Clear Visual Feedback

Marks exit rows in results

Shows seat class and position type for each match

Real-Time Results

Always reflects current seat availability

Works with existing booking/reservation system