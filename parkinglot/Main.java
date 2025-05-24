package parkinglot;

import java.time.LocalDateTime;
import java.util.*;

// Enum for vehicle types
enum VehicleType {
    CAR, BIKE
}

// Abstract base class
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

// Car class
class Car extends Vehicle {
    public Car(String plateNumber) {
        super(plateNumber);
    }

    public VehicleType getType() {
        return VehicleType.CAR;
    }
}

// Bike class
class Bike extends Vehicle {
    public Bike(String plateNumber) {
        super(plateNumber);
    }

    public VehicleType getType() {
        return VehicleType.BIKE;
    }
}

// Parking spot
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

// Ticket class
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

// Fee calculator
class FeeCalculator {
    public static double calculateFee(Ticket ticket) {
        long minutes = ticket.getDurationMinutes();
        VehicleType type = ticket.getVehicle().getType();

        if (type == VehicleType.CAR) {
            return 20 + (minutes / 60) * 10; // ₹20 base + ₹10 per hour
        } else if (type == VehicleType.BIKE) {
            return 10 + (minutes / 60) * 5; // ₹10 base + ₹5 per hour
        }
        return 0;
    }
}

// Manager
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
        return null; // No spot
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
        return -1; // Invalid ticket
    }
}

// Main class
public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<ParkingSpot> spots = new ArrayList<>();
        spots.add(new ParkingSpot(1, VehicleType.CAR));
        spots.add(new ParkingSpot(2, VehicleType.BIKE));
        spots.add(new ParkingSpot(3, VehicleType.CAR));
        spots.add(new ParkingSpot(4, VehicleType.BIKE));

        ParkingManager manager = new ParkingManager(spots);

        Vehicle car = new Car("KA01AB1234");
        Ticket carTicket = manager.parkVehicle(car);
        if (carTicket != null) {
            System.out.println("Car parked. Ticket ID: " + carTicket.getTicketId());
        } else {
            System.out.println("No spot available for car.");
        }

        Thread.sleep(2000); // Simulate time in parking

        double fee = manager.exitVehicle(carTicket.getTicketId());
        System.out.println("Car exited. Fee: ₹" + fee);
    }
}
