package src.service;


import src.db.DBConnection;
import java.sql.*;


public class TicketService {


    public void issueTicket(int reservationId) {


    String sql = "SELECT visitor_name, status, is_used FROM reservations WHERE id=?";


    try (Connection conn = DBConnection.connect();
         PreparedStatement ps = conn.prepareStatement(sql)) {


        ps.setInt(1, reservationId);
        ResultSet rs = ps.executeQuery();


        if (!rs.next()) {
            System.out.println("Reservation not found.");
            return;
        }


        String name = rs.getString("visitor_name");
        String status = rs.getString("status");
        int isUsed = rs.getInt("is_used");


        if ("CANCELLED".equalsIgnoreCase(status)) {
            System.out.println("Reservation is cancelled.");
            return;
        }
        if (isUsed == 1) {
            System.out.println("This reservation ID is already used. Cannot issue ticket again.");
            return;
        }


        String ticketCode = "TCK-" + System.currentTimeMillis();


        String insert = "INSERT INTO tickets(ticket_code, visitor_name, status) VALUES(?,?, 'ACTIVE')";


        try (PreparedStatement insertPs = conn.prepareStatement(insert)) {
            insertPs.setString(1, ticketCode);
            insertPs.setString(2, name);
            insertPs.executeUpdate();
        }


        String update = "UPDATE reservations SET is_used=1 WHERE id=?";


        try (PreparedStatement updatePs = conn.prepareStatement(update)) {
            updatePs.setInt(1, reservationId);
            updatePs.executeUpdate();
        }


        System.out.println("Ticket issued successfully!");
        System.out.println("Ticket Code: " + ticketCode);


        } catch (Exception e) {
            System.out.println("Issue ticket error:");
            e.printStackTrace();
        }
    }


    public boolean validateTicket(String code) {


    String sql = "SELECT * FROM tickets WHERE ticket_code=? AND status='ACTIVE'";


    try (Connection conn = DBConnection.connect();
         PreparedStatement ps = conn.prepareStatement(sql)) {


        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();


        return rs.next();


        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }


    public void markTicketUsed(String code) {


    String sql = "UPDATE tickets SET status='USED' WHERE ticket_code=?";


    try (Connection conn = DBConnection.connect();
         PreparedStatement ps = conn.prepareStatement(sql)) {


        ps.setString(1, code);
        ps.executeUpdate();


        System.out.println("Ticket marked as USED.");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
