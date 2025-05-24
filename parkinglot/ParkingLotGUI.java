import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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

public class ParkingLotGUI extends JFrame {
    private ParkingManager manager;
    private JTextField plateField, ticketField;
    private JComboBox<String> vehicleTypeBox;
    private JTextArea outputArea;

    public ParkingLotGUI() {
        setTitle("Parking Lot System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        List<ParkingSpot> spots = new ArrayList<>();
        spots.add(new ParkingSpot(1, VehicleType.CAR));
        spots.add(new ParkingSpot(2, VehicleType.BIKE));
        spots.add(new ParkingSpot(3, VehicleType.CAR));
        spots.add(new ParkingSpot(4, VehicleType.BIKE));
        manager = new ParkingManager(spots);

        // Top Panel
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        topPanel.add(new JLabel("Plate Number:"));
        plateField = new JTextField();
        topPanel.add(plateField);

        topPanel.add(new JLabel("Vehicle Type:"));
        vehicleTypeBox = new JComboBox<>(new String[]{"CAR", "BIKE"});
        topPanel.add(vehicleTypeBox);

        JButton parkButton = new JButton("Park Vehicle");
        parkButton.addActionListener(e -> parkVehicle());
        topPanel.add(parkButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> plateField.setText(""));
        topPanel.add(clearButton);

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(new JLabel("Ticket ID:"));
        ticketField = new JTextField(5);
        bottomPanel.add(ticketField);

        JButton exitButton = new JButton("Exit Vehicle");
        exitButton.addActionListener(e -> exitVehicle());
        bottomPanel.add(exitButton);

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void parkVehicle() {
        String plate = plateField.getText().trim();
        String type = (String) vehicleTypeBox.getSelectedItem();

        if (plate.isEmpty()) {
            outputArea.append("Please enter a plate number.\n");
            return;
        }

        Vehicle vehicle = type.equals("CAR") ? new Car(plate) : new Bike(plate);
        Ticket ticket = manager.parkVehicle(vehicle);

        if (ticket != null) {
            outputArea.append("Vehicle parked. Ticket ID: " + ticket.getTicketId() + "\n");
        } else {
            outputArea.append("No available spot for this vehicle type.\n");
        }
    }

    private void exitVehicle() {
        try {
            int ticketId = Integer.parseInt(ticketField.getText().trim());
            double fee = manager.exitVehicle(ticketId);
            if (fee >= 0) {
                outputArea.append("Vehicle exited. Fee: \u20B9" + fee + "\n");
            } else {
                outputArea.append("Invalid ticket ID.\n");
            }
        } catch (NumberFormatException e) {
            outputArea.append("Enter a valid ticket ID.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ParkingLotGUI gui = new ParkingLotGUI();
            gui.setVisible(true);
        });
    }
}
