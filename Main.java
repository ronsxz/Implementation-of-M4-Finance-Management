import src.service.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        TicketService ticketService = new TicketService();
        ExhibitService exhibitService = new ExhibitService();
        VisitorService visitorService = new VisitorService();
        AdminService adminService = new AdminService();
        ReservationService reservationService = new ReservationService();

        while (true) {
            System.out.println("\n============================================");
            System.out.println("      WELCOME TO THE NATIONAL MUSEUM");
            System.out.println("============================================");
            System.out.println("1. Create Reservation (₱50 fee)");
            System.out.println("2. Issue Ticket (from reservation)");
            System.out.println("3. View Exhibit");
            System.out.println("4. Admin Mode");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Schedule: ");
                    String schedule = sc.nextLine();
                    reservationService.createReservation(name, schedule);
                    break;
                case 2:
                    System.out.print("Reservation ID: ");
                    int resId = sc.nextInt();
                    ticketService.issueTicket(resId);
                    break;
                case 3:
                    System.out.print("Enter your Ticket Code: ");
                    String ticketCode = sc.nextLine();

                    if (ticketService.validateTicket(ticketCode)) {
                        System.out.println("Ticket verified. Welcome to the museum!");
                        boolean back = exhibitService.virtualGallery(sc);
                        if (back) {
                            System.out.print("Enter your name: ");
                            String visitor = sc.nextLine();
                            visitorService.recordEntry(visitor, ticketCode);
                            ticketService.markTicketUsed(ticketCode);
                        }
                    } else {
                        System.out.println("Invalid or inactive ticket. Access denied.");
                    }
                    break;
                case 4:
                    System.out.print("Username: ");
                    String user = sc.nextLine();
                    System.out.print("Password: ");
                    String pass = sc.nextLine();

                    if (adminService.login(user, pass)) {

                        while (true) {
                            System.out.println("\n--- ADMIN MENU ---");
                            System.out.println("1. View Reservations");
                            System.out.println("2. Cancel Reservation (Refund)");
                            System.out.println("3. View Income Statement");
                            System.out.println("4. Monitor Visitors Entry");
                            System.out.println("0. Back");

                            System.out.print("Enter your choice: ");
                            int a = sc.nextInt();
                            sc.nextLine();

                            switch (a) {

                                case 1:
                                    reservationService.viewReservations();
                                    break;
                                case 2:
                                    System.out.print("Reservation ID: ");
                                    int id = sc.nextInt();
                                    reservationService.cancelReservation(id);
                                    break;
                                case 3:
                                    adminService.viewIncomeStatement();
                                    break;
                                case 4:
                                    visitorService.viewEntries();
                                    break;
                                case 0:
                                    break;
                            }
                            if (a == 0) break;
                        }
                    } else {
                        System.out.println("Invalid login.");
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}