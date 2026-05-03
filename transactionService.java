package src.service;


import src.db.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;


public class TransactionService {


    private final double VAT_RATE = 0.12;


    public void recordReservation(String name, int reservationId, double fee) {


        double vat = fee * VAT_RATE;
        double total = fee + vat;


        insert("RES-" + reservationId, name, "RESERVATION",
                fee, vat, 0, total);
    }


    public void recordRefund(String name, String refCode, double fee) {


        double vat = fee * VAT_RATE;


        insert(refCode, name, "REFUND",
                -fee, -vat, 0, -(fee + vat));
    }


    private void insert(String ref, String name, String type,
                        double amount, double vat, double discount, double total) {


        String sql = "INSERT INTO transactions(ref_code, visitor_name, type, amount, vat, discount, total, time) VALUES(?,?,?,?,?,?,?,?)";


        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, ref);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setDouble(4, amount);
            ps.setDouble(5, vat);
            ps.setDouble(6, discount);
            ps.setDouble(7, total);
            ps.setString(8, LocalDateTime.now().toString());


            ps.executeUpdate();


            System.out.println("Transaction recorded: " + type);


        } catch (Exception e) {
            System.out.println("Transaction insert error:");
            e.printStackTrace();
        }
    }


    public double getIncome() {
        String sql = "SELECT COALESCE(SUM(total), 0) as income FROM transactions";


        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {


            return rs.getDouble("income");


        } catch (Exception e) {
            System.out.println("Income calculation error:");
            e.printStackTrace();
        }
        return 0;
    }
}
