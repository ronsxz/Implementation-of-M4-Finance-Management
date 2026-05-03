package src.service;

import src.db.DBConnection;
import src.payment.MuseumPayment;
import java.sql.*;
import java.util.Scanner;

public class ReservationService {

    private final double fee = 50.0;

    public int createReservation(String name, String schedule) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Select Customer Type:");
        System.out.println("1. Student (10%)");
        System.out.println("2. Senior (20%)");
        System.out.println("3. Regular");
        System.out.print("Enter your choice: ");
        int type = sc.nextInt();
        sc.nextLine();

        String customerType = "REGULAR";

        if (type == 1) customerType = "STUDENT";
        else if (type == 2) customerType = "SENIOR";

        MuseumPayment payment = new MuseumPayment(
                fee,
                customerType,   
                "CREDIT",
                100.0
        );

        boolean paid = payment.process();

        if (!paid) {
            System.out.println("Payment failed. Reservation not created.");
            return -1;
        }

        String sql = "INSERT INTO reservations(visitor_name, schedule, status, reservation_fee, created_at) VALUES(?,?, 'PAID', ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, schedule);
            ps.setDouble(3, fee);
            ps.setString(4, java.time.LocalDateTime.now().toString());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                System.out.println("Reservation failed.");
                return -1;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    int id = rs.getInt(1);

                    TransactionService ts = new TransactionService();
                    ts.recordReservation(name, id, fee);

                    System.out.println("\nRESERVATION SUCCESS (PAID)");
                    System.out.println("ID: " + id);

                    return id;
                }
            }

        } catch (Exception e) {
            System.out.println("Reservation error:");
            e.printStackTrace();
        }

        return -1;
    }

    public void viewReservations() {

        String sql = "SELECT * FROM reservations";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- RESERVATIONS ---");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                        rs.getString("visitor_name") + " | " +
                        rs.getString("schedule") + " | " +
                        rs.getString("status") + " | " +
                        rs.getInt("is_used")
                );
            }

        } catch (Exception e) {
            System.out.println("Error loading reservations:");
            e.printStackTrace();
        }
    }

    public void cancelReservation(int id) {

        String name = null;
        double fee = 0;

        String sql = "SELECT visitor_name, reservation_fee, status FROM reservations WHERE id=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Reservation not found.");
                return;
            }

            if ("CANCELLED".equalsIgnoreCase(rs.getString("status"))) {
                System.out.println("Already cancelled.");
                return;
            }

            name = rs.getString("visitor_name");
            fee = rs.getDouble("reservation_fee");

        } catch (Exception e) {
            System.out.println("Error reading reservation:");
            e.printStackTrace();
            return;
        }

        try (Connection conn = DBConnection.connect();
             PreparedStatement update = conn.prepareStatement(
                     "UPDATE reservations SET status='CANCELLED' WHERE id=?")) {

            update.setInt(1, id);
            update.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error cancelling reservation:");
            e.printStackTrace();
            return;
        }

        TransactionService ts = new TransactionService();
        ts.recordRefund(name, "RES-" + id, fee);

        System.out.println("✔ Reservation cancelled + refund processed.");
    }
}