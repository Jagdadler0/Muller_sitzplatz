# Muller_sitzplatz

## Promt
Translated via DeepL in the Schwarz Digits/ Lidl translator


### German
Ich möchte, dass du mir ein Programm in Java schreibst.
Das Programm soll folgendes machen:

> Das System verwaltet die Sitzplatzbelegung eines Fluges mit einem Airbus A350-900. Es ermöglicht dem Bodenpersonal, Sitzplätze für Passagiere zuzuweisen, Reservierungen vorzunehmen, Belegungen zu ändern und Verfügbarkeiten jederzeit anzuzeigen. Ziel ist die sichere, übersichtliche und konfliktfreie Sitzplatzverwaltung für einzelne Flüge.


Als Voraussetzung habe ich folgendes:

- Es soll eine Konsolen anwendung sein
- Es soll nur den Source Code angegeben werden
- Nur Änderungen sollen angegeben werden (ähnlich wie in Git)
- Nutze Lobok und vermeide Boilerplate Code
- Nutze KEIN System.out.println sondern SLF4J um Text auszugeben


Folgende Funktionen sollen eingebaut sein:

- F01: Sitzplan initialisieren                          -   Der komplette Sitzplan eines Airbus A350-900 wird beim Start des Systems erzeugt. Dabei werden Klassen (z.B. Economy, Premium Economy, Business) und deren Sitzreihen und Anordnung berücksichtigt
- F02: Sitzplatz anzeigen                               -   Das System zeigt den vollständigen Sitzplan für den aktuellen Flug mit jedem Sitzplatzstatus (frei, reserviert, belegt, gesperrt). Farben oder Symbole kennzeichnen den Status.
- F03: Sitzplatz buchen                                     -   Ein freier Sitzplatz kann einem Passagier fest zugewiesen werden. Die Zuordnung enthält Informationen wie Name, Buchungsnummer, Klasse, ggf. Sonderwünsche.
- F04: Sitzplatz reservieren                            -  Sitzplätze können vorläufig reserviert werden (z.B. während der OnlineBuchung oder beim Check-in). Reservierungen haben ein Zeitlimit oder müssen aktiv bestätigt werden.
- F05: Sitzplatz stornieren                             -     Eine Buchung oder Reservierung kann wieder entfernt werden. Der Sitzplatz wird wieder als „frei“ markiert.
- F06: Sitzplatz tauschen                               -   Zwei Sitzplätze können getauscht werden, z.B. auf Kundenwunsch. Beide Belegungen werden aktualisiert.
- F07: Sitzplatz sperren/freigeben              -   Einzelne Sitzplätze können manuell als gesperrt markiert werden, z. B. wegen technischer Einschränkungen, VIP-Blockade oder Crew-Bedarf. Diese Plätze stehen dann nicht zur Buchung zur Verfügung
- F08: Freie Plätze suchen                              -   Das System listet alle verfügbaren Sitzplätze nach Klasse, Position (Fenster, Gang, Mitte) oder besonderen Kriterien (z.B. Beinfreiheit).
- F09: Passagierliste verwalten                     -    Für jeden belegten Platz wird der zugehörige Passagier mit Name, Ticketnummer und ggf. Sonderwünschen angezeigt.
- F10: Belegungsstatistik erstellen             -   Das System erstellt eine Übersicht über Sitzplatzauslastung je Klasse, Anzahl freier und belegter Sitze und prozentuale Belegung.
- F11: Exportieren von Belegungsdaten   -   Belegungspläne und Passagierlisten können für den Ausdruck oder die Weitergabe als Textdatei exportiert werden.

### English
I would like you to write me a program in Java.
The program should do the following:

> The system manages the seat assignment of a flight with an Airbus A350-900. It enables the ground staff to assign seats to passengers, make reservations, change assignments and display availability at any time. The aim is safe, clear and conflict-free seat management for individual flights.


The requirements are as follows:

- It should be a console application
- Only the source code should be specified
- Only changes should be specified (similar to Git)
- Use Lobok and avoid boilerplate code
- Do NOT use System.out.println but SLF4J to output text


The following functions should be built in:

- F01: Initialize seating plan - The complete seating plan of an Airbus A350-900 is generated when the system is started. Classes (e.g. Economy, Premium Economy, Business) and their seat rows and arrangement are taken into account
- F02: Display seat - The system displays the complete seating plan for the current flight with each seat status (free, reserved, occupied, blocked). Colors or symbols indicate the status.
- F03: Book seat - A free seat can be permanently assigned to a passenger. The assignment contains information such as name, booking number, class and any special requests.
- F04: Reserve seat - Seats can be provisionally reserved (e.g. during online booking or at check-in). Reservations have a time limit or must be actively confirmed.
- F05: Cancel seat - A booking or reservation can be removed again. The seat is marked as "free" again.
- F06: Swap seat - Two seats can be swapped, e.g. at the customer's request. Both reservations are updated.
- F07: Block/release seat - Individual seats can be manually marked as blocked, e.g. due to technical restrictions, VIP blockade or crew requirements. These seats are then not available for booking
- F08: Search for available seats - The system lists all available seats by class, position (window, aisle, middle) or special criteria (e.g. legroom).
- F09: Manage passenger list - For each occupied seat, the corresponding passenger is displayed with name, ticket number and any special requests.
- F10: Create occupancy statistics - The system creates an overview of seat occupancy per class, number of free and occupied seats and percentage occupancy.
- F11: Export occupancy data - Occupancy plans and passenger lists can be exported as a text file for printing or forwarding.


