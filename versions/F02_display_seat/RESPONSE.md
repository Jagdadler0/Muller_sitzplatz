# Reasoning

Okay, I need to implement F02: Display seat functionality. The user wants the system to show the complete seating plan with each seat's status, using colors or symbols. Let me think about how to approach this.

First, the existing code has SeatStatus with AVAILABLE and RESERVED. But the requirement mentions free, reserved, occupied, blocked. So I need to update SeatStatus enum to include all these statuses. That makes sense. Adding OCCUPIED and BLOCKED.

Next, in the SeatPlan class, when initializing seats, currently all seats are set to AVAILABLE. But maybe some seats should be blocked by default? Or is that part of a different feature? The problem statement doesn't mention initial blocked seats, so perhaps just initializing all as AVAILABLE for now. Wait, but the display needs to show blocked seats. Hmm. Maybe the user needs a way to block seats, but that's another function. For F02, maybe just ensure that the SeatStatus has all required values and that when displaying, it can handle them.

Then, the display should use colors or symbols. Since it's a console app, using ANSI escape codes for colors would be good. But SLF4J might not support ANSI colors directly. Wait, the user specified to avoid System.out.println and use SLF4J. So maybe using symbols with color codes. However, logging frameworks might not always handle ANSI colors unless configured. Alternatively, use symbols without colors. Let me check: the user said "colors or symbols", so maybe symbols are safer if ANSI isn't enabled.

So, for each seat, display a symbol based on status. Like [A] for Available, [R] Reserved, [O] Occupied, [B] Blocked. But how to structure the display.

The Airbus A350-900 seating plan has different classes. The display should show the classes and their rows. The initial code has sections: Business (rows 1-8, 4 seats per row), Premium Economy (9-16, 8 seats), Economy (17-40, 9 seats). So each row in Business has 4 seats, likely arranged as A, B, C, D? Wait, the code initializes seat letters as 'A' + seatNum. For Business, seatsPerRow is 4, so letters A to D. Premium Economy has 8 seats per row: A to H. Economy 9 seats: A to I.

But when displaying, rows are arranged with seat letters. For each row, display the seats in order, showing their status symbols.

So the display needs to iterate through each seat section (Business, Premium, Economy), then for each row in the section, list the seats in order with their symbols.

But how to group seats by row and class? The current SeatPlan has a list of all seats. To display, need to group seats by their seat class, then by row, then by seat letter.

Maybe create a map structure: Map<SeatClass, Map<Integer, Map<String, SeatStatus>>>. But that could be complex. Alternatively, for each SeatClass, iterate through each row in their section, and collect the seats for that row.

Wait, the SeatSection in SeatPlan defines the rows for each class. So when displaying, for each SeatSection, iterate from startRow to endRow, and for each seat in that row, get their status.

But the SeatPlan's seats list contains all seats. So perhaps, for each SeatSection, for each row in the section's range, collect all seats in that row and seat class, then order them by seat letter.

So steps for displaying:

1. Iterate over each SeatSection in order (Business, Premium, Economy).

2. For each section, print the class header.

3. For each row in the section's startRow to endRow:

a. Collect all seats in that row and class.

b. Sort them by seat letter.

c. Display the row number and each seat's symbol.

But how to efficiently get all seats for a row and class? The current SeatPlan has a list of all seats. So for each row and class, we can filter the list.

But that's O(n) for each row, which could be slow for many seats. However, given that the total seats are around (8 rows *4) + (8*8) + (24*9) = 32 +64+ 216= 312 seats. So acceptable for a console app.

Alternatively, pre-process the seats into a structure that allows O(1) access by class, row, and seat letter. But that might complicate the code. For now, since F02 is about displaying, maybe just filter each time.

So, in the display method:

For each SeatSection in sections (Business, Premium, Economy, Economy):

For each row from start to end:

Get all seats in this row and class.

Sort them by seat letter.

Create a string representation for each seat's status.

Print the row number and the seats.

But how to get the sections again? The SeatPlan initializes sections as a list. Maybe store the sections as a field in SeatPlan so that they can be accessed when displaying.

Wait, in the current code, the sections are created inside the initializeSeats method as a local variable. So SeatPlan doesn't have a reference to them after initialization. To display the seating plan properly, the code needs to know the sections to iterate through them in order. Therefore, I should store the sections in SeatPlan as a field.

So modify SeatPlan to have a List<SeatSection> sections, and in the constructor, initialize it. Then, when displaying, iterate through each section in the sections list.

So in SeatPlan.java:

Add a private final List<SeatSection> sections;

In the constructor:

this.sections = List.of(...);

Then, in the display method, loop through sections.

That way, the display knows the order and structure of sections.

Alternatively, hardcode the sections again in the display method, but that's not DRY. Better to store the sections in SeatPlan.

So modifying SeatPlan to include the sections as a field.

Then, in the displaySeatAvailability method, iterate over each section in seatPlan.getSections(), then for each row in the section's range, collect the seats in that row and class, sort by seat letter, and display.

So for the code:

In SeatPlan.java:

Add:

@Getter

private final List<SeatSection> sections;

Then in initializeSeats:

this.sections = List.of(...);

Then, in displaySeatAvailability, loop over the sections.

Now, the display can be structured by sections and rows.

Next, symbols for each status. Let's define a helper method that maps SeatStatus to a symbol. For example:

- AVAILABLE: "[A]" (green)

- RESERVED: "[R]" (yellow)

- OCCUPIED: "[O]" (red)

- BLOCKED: "[B]" (gray)

But using ANSI colors. But since the user said to use SLF4J and avoid System.out.println, but SLF4J doesn't directly support ANSI. However, some logging setups allow it with markers or configurations. Alternatively, use symbols without color.

But the user mentioned "colors or symbols", so perhaps symbols are acceptable. But the assistant's previous code uses log.info with messages. How to format the seat plan with symbols in the log.

But logging each seat as a separate log entry would be messy. The initial F01's displaySeatAvailability logs each seat individually, which would produce a lot of lines. But for a seating plan display, it's better to format the seats per row into a single log entry per row.

So perhaps build a string for each row and log that.

But using SLF4J, each row would be a separate log message. Alternatively, collect all rows into a single string and log once, but that might be too large.

Alternatively, use a logger that allows multi-line messages. However, for the console, it's manageable.

So, modifying the displaySeatAvailability method to loop through each section, each row in the section, collect the seats, format them into a string per row, and log each row.

But how to get the seats for a specific row and class.

In SeatPlan, add a method to get seats by class and row:

public List<Seat> getSeatsByClassAndRow(SeatClass seatClass, int row) {

return seats.stream()

.filter(s -> s.getSeatClass() == seatClass && s.getRow() == row)

.sorted(Comparator.comparing(Seat::getSeatLetter))

.collect(Collectors.toList());

}

But in Java, the Seat's seatLetter is a String, so comparing as strings would sort them lexically. Since seat letters are single characters from A to I, this works.

So for each row in a section's range, call this method.

Then, for each seat in the row, append the symbol.

But the SeatStatus needs to have all four statuses. So first, update SeatStatus enum:

public enum SeatStatus {

AVAILABLE, RESERVED, OCCUPIED, BLOCKED

}

Wait, the initial SeatStatus in F01 had AVAILABLE and RESERVED. Now, adding OCCUPIED and BLOCKED.

In the SeatPlan's initializeSeats method, all seats are initialized with AVAILABLE. But perhaps some seats are blocked by default? The problem statement doesn't specify, so maybe not. The user can later implement a function to block seats.

So for F02, the display just needs to show the statuses, even if they are all AVAILABLE initially. The other functions (assign, reserve, etc.) would change the statuses.

So, in the Seat class, the status is part of the @Value, so it's immutable. Wait, this is a problem. If the seat's status needs to change (e.g., from AVAILABLE to RESERVED), the Seat object must be immutable. Oh no, the current Seat is a @Value from Lombok, which makes it immutable. Therefore, to change the status, a new Seat instance must be created with the updated status.

This complicates things. Because the SeatPlan's seats are stored in a list, and if we need to modify a seat's status, we have to replace the seat in the list with a new instance.

Hmm. That's a design issue. The initial approach used immutable Seat objects. So to change the status, we need to find the seat in the list, create a new one with the new status, and replace it.

This could be inefficient, but for the scope of this problem, it's manageable.

But for F02, the display function is just reading the statuses. So no problem there. The actual modification will be handled in other functions (F03, etc.), which will need to update the seats.

So for now, focus on F02.

So, back to the display. Each row in a section is displayed with its seats, showing their statuses via symbols.

Implementing a helper method to get the symbol for a SeatStatus:

private String getStatusSymbol(SeatStatus status) {

switch (status) {

case AVAILABLE: return "[A]";

case RESERVED: return "[R]";

case OCCUPIED: return "[O]";

case BLOCKED: return "[B]";

default: return "[?]";

}

}

But maybe use colored symbols. For example, using ANSI escape codes:

private String getStatusSymbol(SeatStatus status) {

switch (status) {

case AVAILABLE: return "\u001B[32m[A]\u001B[0m"; // Green

case RESERVED: return "\u001B[33m[R]\u001B[0m"; // Yellow

case OCCUPIED: return "\u001B[31m[O]\u001B[0m"; // Red

case BLOCKED: return "\u001B[37m[B]\u001B[0m"; // White/Gray

default: return "[?]";

}

}

But this depends on the console supporting ANSI colors. The user's requirement allows colors or symbols, so including both would be good. However, if the console doesn't support colors, the escape codes will show as gibberish. So perhaps make it optional or configurable. But that's beyond the current scope. The user might just want symbols without color.

Alternatively, use the symbols without color. Let's proceed with symbols in brackets.

So, the helper method returns [A], [R], etc.

Now, in displaySeatAvailability:

For each section in seatPlan.getSections():

Log the class name.

For each row in the section's startRow to endRow:

Get the seats in this row and class.

Sort them by seatLetter.

Build a string for the row, showing each seat's symbol.

Log the row number and the seat symbols.

But how to collect the seats for a row and class?

In SeatPlan, adding a method:

public List<Seat> getSeatsByClassAndRow(SeatClass seatClass, int row) {

return seats.stream()

.filter(seat -> seat.getSeatClass() == seatClass && seat.getRow() == row)

.sorted(Comparator.comparing(Seat::getSeatLetter))

.collect(Collectors.toList());

}

But the SeatPlan's seats are a list, and this method will filter and sort each time. For small data, it's acceptable.

Then, in displaySeatAvailability:

private void displaySeatAvailability() {

log.info("Seat Availability:");

seatPlan.getSections().forEach(section -> {

log.info("\n=== {} Class ===", section.getSeatClass());

for (int row = section.getStartRow(); row <= section.getEndRow(); row++) {

List<Seat> seatsInRow = seatPlan.getSeatsByClassAndRow(section.getSeatClass(), row);

String seatDisplay = seatsInRow.stream()

.map(seat -> getStatusSymbol(seat.getStatus()))

.collect(Collectors.joining(" "));

log.info("Row {}: {}", row, seatDisplay);

}

});

}

But wait, the SeatPlan's getSeatsByClassAndRow method is needed. So adding that method to SeatPlan.

Also, the sections in SeatPlan need to be accessible via a getter. Previously, in SeatPlan, we added:

@Getter

private final List<SeatSection> sections;

So that other classes can access them.

But in the initial code, the SeatPlan's sections were created in initializeSeats as a local variable. So modifying SeatPlan to have:

public class SeatPlan {

private static final Logger log = LoggerFactory.getLogger(SeatPlan.class);

private final List<Seat> seats = new ArrayList<>();

@Getter

private final List<SeatSection> sections;

public SeatPlan() {

this.sections = initializeSections();

initializeSeats();

}

private List<SeatSection> initializeSections() {

return List.of(

new SeatSection(SeatClass.BUSINESS, 1, 8, 4),

new SeatSection(SeatClass.PREMIUM_ECONOMY, 9, 16, 8),

new SeatSection(SeatClass.ECONOMY, 17, 40, 9)

);

}

private void initializeSeats() {

for (SeatSection section : sections) {

// ... existing code ...

}

}

// ... getSeatsByClassAndRow method ...

}

Wait, no. The SeatPlan's constructor calls initializeSections() which returns the sections, and then initializes seats based on those sections. So the sections are stored as a field.

Now, in the displaySeatAvailability method, when looping through sections, we can get each section's seatClass, startRow, endRow.

Putting it all together:

Modify SeatPlan to include the sections as a field with a getter.

Add getSeatsByClassAndRow method to SeatPlan.

In FlightSeatManagerApp's displaySeatAvailability, loop through each section, then each row in the section's range, collect the seats, and display.

But also, the SeatSection's seatsPerRow is needed to know how many seats per row, but maybe the getSeatsByClassAndRow method will return the correct number.

Wait, when initializing seats, for each section, seatNum runs from 0 to seatsPerRow-1, generating seat letters from 'A' onwards. So for a section with 4 seats per row, letters A-D. So for each row in a section, the seatsInRow should have exactly seatsPerRow seats.

But if for some reason there are missing seats, the code should still handle it.

Now, the display code in FlightSeatManagerApp:

Implement getStatusSymbol as a helper method.

But since SLF4J is used, and we're logging, each row's seat display is built as a string and logged.

So, in FlightSeatManagerApp:

private String getStatusSymbol(SeatStatus status) {

switch (status) {

case AVAILABLE: return "[A]";

case RESERVED: return "[R]";

case OCCUPIED: return "[O]";

case BLOCKED: return "[B]";

default: return "[?]";

}

}

Then, in displaySeatAvailability:

log.info("Seat Availability:");

seatPlan.getSections().forEach(section -> {

log.info("\n=== {} Class ===", section.getSeatClass());

for (int row = section.getStartRow(); row <= section.getEndRow(); row++) {

List<Seat> seatsInRow = seatPlan.getSeatsByClassAndRow(section.getSeatClass(), row);

String seatDisplay = seatsInRow.stream()

.map(seat -> getStatusSymbol(seat.getStatus()))

.collect(Collectors.joining(" "));

log.info("Row {}: {}", row, seatDisplay);

}

});

But wait, the sections in SeatPlan are initialized as BUSINESS (1-8), PREMIUM_ECONOMY (9-16), ECONOMY (17-40). So when looping through each row in the section, from start to end.

But in the initial code, the Economy section's endRow is 40? Let me check the user's original problem statement:

In F01, the user specified:

"Classes (e.g. Economy, Premium Economy, Business) and their seat rows and arrangement are taken into account"

The code in SeatPlan's sections was:

new SeatSection(SeatClass.BUSINESS, 1, 8, 4),

new SeatSection(SeatClass.PREMIUM_ECONOMY, 9, 16, 8),

new SeatSection(SeatClass.ECONOMY, 17, 40, 9)

So Economy is from row 17 to 40. That's 24 rows (40-17+1=24 rows). Each with 9 seats.

So when displaying, Economy class will show rows 17-40.

Now, putting all together, when the user selects option 1 (display seat availability), the app logs each class, then each row in that class with the seat symbols.

Now, testing this, but initially, all seats are AVAILABLE, so all symbols are [A].

But for blocked seats, there are none yet. So maybe in the future, when implementing other functions, seats can be marked as BLOCKED or OCCUPIED.

So this fulfills F02's requirement to display the seating plan with seat statuses using symbols.

Additionally, the user might want to see the layout with aisles. For example, in Economy with 9 seats per row, the arrangement is usually 3-3-3, with aisles between. But the display just shows all seats in order. Maybe adding a space between groups? For example, A B C   D E F   G H I. But the current code joins the symbols with space. For Business (4 seats), it's A B C D. Premium Economy (8 seats): A B C D E F G H. Economy 9: A B C D E F G H I. But real A350-900 might have different seat arrangements. For example, Business class could be 1-2-1 configuration. But the code's initial setup has 4 seats per row (A-D). So the display is a linear list of seats per row. The user might want a more realistic layout, but the problem statement doesn't specify. So proceed as per initial code.

Finally, the code must be updated with these changes. The steps are:

1. Update SeatStatus enum to include OCCUPIED and BLOCKED.

2. In SeatPlan, add sections as a field with getter.

3. Add getSeatsByClassAndRow method in SeatPlan.

4. Modify displaySeatAvailability in FlightSeatManagerApp to loop through sections and rows, displaying symbols.

Now, implementing these changes in code.

Also, note that in the existing code, SeatPlan's seats are initialized with SeatStatus.AVAILABLE. So all seats start as [A].

So the code changes are:

In SeatStatus.java:

public enum SeatStatus {

AVAILABLE, RESERVED, OCCUPIED, BLOCKED

}

In SeatPlan.java:

Add the sections field and getter:

public class SeatPlan {

private static final Logger log = LoggerFactory.getLogger(SeatPlan.class);

private final List<Seat> seats = new ArrayList<>();

@Getter

private final List<SeatSection> sections;

public SeatPlan() {

this.sections = initializeSections();

initializeSeats();

}

private List<SeatSection> initializeSections() {

return List.of(

new SeatSection(SeatClass.BUSINESS, 1, 8, 4),

new SeatSection(SeatClass.PREMIUM_ECONOMY, 9, 16, 8),

new SeatSection(SeatClass.ECONOMY, 17, 40, 9)

);

}

private void initializeSeats() {

for (SeatSection section : sections) {

for (int row = section.getStartRow(); row <= section.getEndRow(); row++) {

for (int seatNum = 0; seatNum < section.getSeatsPerRow(); seatNum++) {

char seatLetter = (char) ('A' + seatNum);

seats.add(new Seat(row, String.valueOf(seatLetter), SeatStatus.AVAILABLE, section.getSeatClass()));

}

}

}

log.info("Initialized seating plan with {} seats", seats.size());

}

public List<Seat> getSeatsByClassAndRow(SeatClass seatClass, int row) {

return seats.stream()

.filter(seat -> seat.getSeatClass() == seatClass && seat.getRow() == row)

.sorted(Comparator.comparing(Seat::getSeatLetter))

.collect(Collectors.toList());

}

// ... existing getSeats() ...

}

In FlightSeatManagerApp.java:

Modify displaySeatAvailability:

private void displaySeatAvailability() {

log.info("Seat Availability:");

seatPlan.getSections().forEach(section -> {

log.info("\n=== {} Class ===", section.getSeatClass());

IntStream.rangeClosed(section.getStartRow(), section.getEndRow())

.forEach(row -> {

List<Seat> seatsInRow = seatPlan.getSeatsByClassAndRow(section.getSeatClass(), row);

String seatDisplay = seatsInRow.stream()

.map(seat -> getStatusSymbol(seat.getStatus()))

.collect(Collectors.joining(" "));

log.info("Row {}: {}", String.format("%2d", row), seatDisplay);

});

});

}

private String getStatusSymbol(SeatStatus status) {

switch (status) {

case AVAILABLE: return "[A]";

case RESERVED: return "[R]";

case OCCUPIED: return "[O]";

case BLOCKED: return "[B]";

default: return "[?]";

}

}

Wait, using String.format("%2d", row) to align row numbers, making the display neater for two-digit rows.

Testing this, rows 1-8 (Business) will show as " 1", " 2", etc., which aligns with two digits. Rows 9 and above will show as "9 ", "10", etc. Wait, no: String.format("%2d", 9) is " 9", 10 is "10".

So the rows are left-padded with spaces for single-digit numbers, making the display aligned.

This improves readability.

In conclusion, the code changes for F02 involve updating the SeatStatus enum, adding sections to SeatPlan, adding a method to get seats by class and row, and modifying the display method to show symbols per seat status in a structured manner.

# Answer

To implement the seat display functionality for the Airbus A350-900 seating plan, we enhance the existing code to show seat statuses using symbols and structure the output by class and rows. Here's how the solution is structured:

Changes and Additions
Update SeatStatus Enum:

Added OCCUPIED and BLOCKED statuses to cover all possible seat states.

Enhance SeatPlan Class:

Store seat sections to organize seats by class and row ranges.

Add method to retrieve seats by class and row for efficient display.

Structured Display Method:

Display seats grouped by class (Business, Premium Economy, Economy).

For each row within a class, show seat statuses using symbols (e.g., [A] for Available).

... CODE ...

Explanation
SeatStatus Expansion: The enum now includes all required states to represent seat availability accurately.

Seat Organization: The SeatPlan class uses sections to group seats by class and row ranges, ensuring the seating plan mirrors the Airbus A350-900 layout.

Display Logic: The displaySeatAvailability method iterates through each class and row, retrieving seats to display their statuses using symbols. This structured approach provides a clear visual representation of the seating plan, making it easy for ground staff to assess availability at a glance.

