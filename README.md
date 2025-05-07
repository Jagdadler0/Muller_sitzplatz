# Muller_sitzplatz

## Promt
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



