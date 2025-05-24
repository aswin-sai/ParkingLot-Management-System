import java.time.LocalDateTime;
import java.util.*;

enum VehicleType {
    CAR, BIKE
}

abstract class Vehicle {
    private String plateNumber;

    public Vehicle(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public abstract VehicleType getType();
}

class Car extends Vehicle {
    public Car(String plateNumber) {
        super(plateNumber);
    }

    public VehicleType getType() {
        return VehicleType.CAR;
    }
}

class Bike extends Vehicle {
    public Bike(String plateNumber) {
        super(plateNumber);
    }

    public VehicleType getType() {
        return VehicleType.BIKE;
    }
}

class ParkingSpot {
    private int spotId;
    private boolean isOccupied;
    private VehicleType spotType;
    private Vehicle currentVehicle;

    public ParkingSpot(int spotId, VehicleType spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.isOccupied = false;
    }

    public boolean isAvailable() {
        return !isOccupied;
    }

    public boolean fit(Vehicle vehicle) {
        return vehicle.getType() == spotType && isAvailable();
    }

    public void parkVehicle(Vehicle vehicle) {
        this.currentVehicle = vehicle;
        this.isOccupied = true;
    }

    public void removeVehicle() {
        this.currentVehicle = null;
        this.isOccupied = false;
    }

    public int getSpotId() {
        return spotId;
    }
}

class Ticket {
    private static int counter = 0;
    private int ticketId;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = ++counter;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
    }

    public void markExit() {
        this.exitTime = LocalDateTime.now();
    }

    public long getDurationMinutes() {
        return java.time.Duration.between(entryTime, exitTime).toMinutes();
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public int getTicketId() {
        return ticketId;
    }
}

class FeeCalculator {
    public static double calculateFee(Ticket ticket) {
        long minutes = ticket.getDurationMinutes();
        VehicleType type = ticket.getVehicle().getType();

        if (type == VehicleType.CAR) {
            return 20 + (minutes / 60) * 10;
        } else if (type == VehicleType.BIKE) {
            return 10 + (minutes / 60) * 5;
        }
        return 0;
    }
}

class ParkingManager {
    private List<ParkingSpot> spots;
    private Map<Integer, Ticket> activeTickets;

    public ParkingManager(List<ParkingSpot> spots) {
        this.spots = spots;
        this.activeTickets = new HashMap<>();
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        for (ParkingSpot spot : spots) {
            if (spot.fit(vehicle)) {
                spot.parkVehicle(vehicle);
                Ticket ticket = new Ticket(vehicle, spot);
                activeTickets.put(ticket.getTicketId(), ticket);
                return ticket;
            }
        }
        return null;
    }

    public double exitVehicle(int ticketId) {
        Ticket ticket = activeTickets.get(ticketId);
        if (ticket != null) {
            ticket.markExit();
            double fee = FeeCalculator.calculateFee(ticket);
            ticket.getSpot().removeVehicle();
            activeTickets.remove(ticketId);
            return fee;
        }
        return -1;
    }
}

public class ParkingLotConsoleApp {

    private static ParkingManager manager;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize parking spots
        List<ParkingSpot> spots = new ArrayList<>();
        spots.add(new ParkingSpot(1, VehicleType.CAR));
        spots.add(new ParkingSpot(2, VehicleType.BIKE));
        spots.add(new ParkingSpot(3, VehicleType.CAR));
        spots.add(new ParkingSpot(4, VehicleType.BIKE));
        manager = new ParkingManager(spots);

        System.out.println("Welcome to Parking Lot System (Console Version)");

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Park Vehicle");
            System.out.println("2. Exit Vehicle");
            System.out.println("3. Quit");

            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    parkVehicle(scanner);
                    break;
                case "2":
                    exitVehicle(scanner);
                    break;
                case "3":
                    System.out.println("BOiii");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void parkVehicle(Scanner scanner) {
        System.out.print("Enter Plate Number: ");
        String plate = scanner.nextLine().trim();

        if (plate.isEmpty()) {
            System.out.println("Plate number cannot be empty.");
            return;
        }

        System.out.print("Enter Vehicle Type (CAR/BIKE): ");
        String typeStr = scanner.nextLine().trim().toUpperCase();

        Vehicle vehicle;
        if (typeStr.equals("CAR")) {
            vehicle = new Car(plate);
        } else if (typeStr.equals("BIKE")) {
            vehicle = new Bike(plate);
        } else {
            System.out.println("Invalid vehicle type. Only CAR or BIKE allowed.");
            return;
        }

        Ticket ticket = manager.parkVehicle(vehicle);
        if (ticket != null) {
            System.out.println("Vehicle parked successfully. Your Ticket ID: " + ticket.getTicketId());
        } else {
            System.out.println("Sorry, no available spot for this vehicle type.");
        }
    }

    private static void exitVehicle(Scanner scanner) {
        System.out.print("Enter Ticket ID: ");
        String ticketStr = scanner.nextLine().trim();

        try {
            int ticketId = Integer.parseInt(ticketStr);
            double fee = manager.exitVehicle(ticketId);
            if (fee >= 0) {
                System.out.printf("Vehicle exited. Fee: \u20B9%.2f\n", fee);
            } else {
                System.out.println("Invalid Ticket ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric Ticket ID.");
        }
    }
}
