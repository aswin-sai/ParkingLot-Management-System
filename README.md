# ParkingLot-Management-System
A smart parking lot system managing cars and bikes with varied spot sizes. It issues tickets at entry gates, tracks parking duration, and calculates fees based on vehicle type and time parked. The intuitive GUI allows easy vehicle check-in, check-out, and fee display, ensuring smooth parking management.
Certainly! Here's a detailed **README.md** file for your Parking Lot System project, explaining everything clearly:

````markdown
# Parking Lot System

## Overview
This Parking Lot System manages parking for multiple vehicle types — Cars and Bikes — with different sized parking spots. It supports ticket issuance at entry gates, vehicle parking management, and fee calculation based on vehicle type and duration parked. The system features a graphical user interface (GUI) built with Java Swing for easy interaction.

---

## Features
1. **Multiple Vehicle Types:** Supports Cars and Bikes.
2. **Parking Spots:** Different sized spots assigned to specific vehicle types.
3. **Ticketing System:** Issues unique tickets for parked vehicles, records entry time.
4. **Fee Calculation:** Calculates parking fees based on vehicle type and time spent.
5. **Vehicle Exit:** Validates ticket, calculates fees, and frees parking spots.
6. **GUI Interface:** Allows users to park vehicles, enter tickets, and exit vehicles easily.

---

## How It Works

### Initialization
- The system initializes a list of parking spots with unique IDs and types (Car/Bike).
- The `ParkingManager` handles spot assignment and ticket management.

### Parking a Vehicle
- User inputs vehicle plate number and selects vehicle type.
- The system finds an available spot matching the vehicle type.
- If available, the vehicle is parked, and a ticket with entry time is issued.
- If no spot is free, a message is shown.

### Exiting a Vehicle
- User enters ticket ID to exit.
- The system validates the ticket, calculates parking duration.
- Fees are computed based on:
  - Car: ₹20 base + ₹10 per hour.
  - Bike: ₹10 base + ₹5 per hour.
- The spot is freed, and fee is displayed.

---

## Classes Description

- **Vehicle (abstract):** Base class for `Car` and `Bike`, stores plate number and type.
- **Car / Bike:** Extend `Vehicle` and define specific types.
- **ParkingSpot:** Represents a parking spot with an ID, type, and occupancy status.
- **Ticket:** Records vehicle, spot, entry time, and exit time.
- **FeeCalculator:** Static class calculating fees based on vehicle type and duration.
- **ParkingManager:** Manages all parking spots, tickets, parking, and exit logic.
- **ParkingLotGUI:** Swing GUI class for user interaction.

---

## Running the Project

1. Compile the code:

   ```bash
   javac ParkingLotGUI.java
````

2. Run the program:

   ```bash
   java ParkingLotGUI
   ```
   if you want an app which runs in the console ive added a java file for it aswell
   the repo consists of 2 java files.
   1: ParkingLotGUI.java uses swing and awt
   2: ParkingLotConsoleApp.java uses java and doesnt have a GUI.

4. Use the GUI:

   * Enter plate number and select vehicle type.
   * Click **"Park Vehicle"** to get a ticket.
   * Enter ticket ID and click **"Exit Vehicle"** to pay fees and free the spot.

---

## Notes

* The Rupee symbol is displayed using Unicode `\u20B9` to avoid encoding issues.
* Time calculations use Java’s `LocalDateTime` and `Duration`.
* Parking spots are currently hardcoded but can be extended to dynamic allocation.

---

## Future Enhancements

* Support more vehicle types (trucks, vans).
* Add real-time spot availability display.
* Integrate payment gateways for fee processing.
* Store parking data persistently (database integration).

---

## Author

ASWIN SAI NALLAN CHAKRAVARTHULA

---

