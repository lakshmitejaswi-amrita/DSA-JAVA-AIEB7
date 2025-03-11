import java.util.*;

class Ticket {
    int ticketId;
    String issue;
    int priority;
    String status;
    String department;
    Ticket next;

    public Ticket(int ticketId, String issue, int priority, String status, String department) {
        this.ticketId = ticketId;
        this.issue = issue;
        this.priority = priority;
        this.status = status;
        this.department = department;
        this.next = null;
    }
}

class TicketSystem {
    private Ticket head;
    private int ticketCounter = 101;
    private HashMap<String, Integer> issuePriority = new HashMap<>();
    private HashMap<String, String> issueDepartment = new HashMap<>();
    private List<Ticket> deletedTickets = new ArrayList<>();
    private String[] issueArray;

    public TicketSystem() {
        String[][] preStoredTickets = {
            {"Flight Delay", "2", "Support"}, {"Baggage Lost", "1", "Support"}, {"Seat Change Request", "3", "Management"},
            {"Refund Request", "1", "Billing"}, {"Flight Cancellation", "2", "Technical"}, {"Meal Preference Change", "3", "Management"},
            {"Overbooking Issue", "2", "Management"}, {"Missed Connecting Flight", "1", "Support"}, {"Damaged Baggage", "2", "Support"},
            {"Check-in Issues", "3", "Support"}, {"Wrong Na15me on Ticket", "2", "Management"}, {"Duplicate Booking", "2", "Technical"},
            {"Refund Delay", "1", "Billing"}, {"Upgrade Request", "3", "Management"}, {"Flight Rescheduling", "2", "Technical"}
        };

        issueArray = new String[preStoredTickets.length];
        for (int i = 0; i < preStoredTickets.length; i++) {
            issuePriority.put(preStoredTickets[i][0], Integer.parseInt(preStoredTickets[i][1]));
            issueDepartment.put(preStoredTickets[i][0], preStoredTickets[i][2]);
            issueArray[i] = preStoredTickets[i][0];
        }
    }

    public void addTicket(int issueNumber) {
        if (issueNumber < 1 || issueNumber > issueArray.length) {
            System.out.println("Invalid issue number. Please try again.");
            return;
        }
        String issue = issueArray[issueNumber - 1];
        int priority = issuePriority.get(issue);
        String department = issueDepartment.get(issue);
        Ticket newTicket = new Ticket(ticketCounter++, issue, priority, "Open", department);
        
        if (head == null || head.priority > newTicket.priority) {
            newTicket.next = head;
            head = newTicket;
        } else {
            Ticket temp = head;
            while (temp.next != null && temp.next.priority <= newTicket.priority) {
                temp = temp.next;
            }
            newTicket.next = temp.next;
            temp.next = newTicket;
        }
        System.out.println("Ticket Added Successfully: ID=" + newTicket.ticketId + ", Issue=" + newTicket.issue + ", Priority=" + newTicket.priority + ", Status=" + newTicket.status + ", Department=" + newTicket.department);
    }

    public void deleteTicket(int ticketId) {
        Ticket temp = head, prev = null;
        while (temp != null && temp.ticketId != ticketId) {
            prev = temp;
            temp = temp.next;
        }
        if (temp == null) {
            System.out.println("Ticket ID not found.");
            return;
        }
        if (prev == null) {
            head = temp.next;
        } else {
            prev.next = temp.next;
        }
        deletedTickets.add(temp);
        System.out.println("Ticket " + ticketId + " deleted successfully.");
    }

    public void updateTicket(int ticketId, String newStatus) {
        Ticket temp = head;
        while (temp != null) {
            if (temp.ticketId == ticketId) {
                temp.status = newStatus;
                System.out.println("Ticket " + ticketId + " updated successfully: Status=" + temp.status);
                return;
            }
            temp = temp.next;
        }
        System.out.println("Ticket ID not found.");
    }

    public void displayTickets() {
        Ticket temp = head;
        if (temp == null) {
            System.out.println("No tickets available.");
            return;
        }
        System.out.println("\n---- Ticket List ----");
        while (temp != null) {
            System.out.println("ID: " + temp.ticketId + ", Issue: " + temp.issue + ", Priority: " + temp.priority + ", Status: " + temp.status + ", Department=" + temp.department);
            temp = temp.next;
        }
        System.out.println("----------------------");
    }

    public void displayDeletedTickets() {
        if (deletedTickets.isEmpty()) {
            System.out.println("No deleted tickets available.");
            return;
        }
        System.out.println("\n---- Deleted Tickets ----");
        for (Ticket ticket : deletedTickets) {
            System.out.println("ID: " + ticket.ticketId + ", Issue: " + ticket.issue + ", Priority: " + ticket.priority + ", Status: " + ticket.status + ", Department=" + ticket.department);
        }
        System.out.println("----------------------");
    }

    public void menu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. Customer\n2. Employee\n3. Exit");
            
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();  // Consume invalid input
                continue;
            }
            int role = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (role == 1) {
                System.out.println("\nCustomer Menu:\n1. Add Ticket\n2. Delete Ticket\n3. Exit");
                
                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next();
                    continue;
                }
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    System.out.println("Select an Issue:");
                    for (int i = 0; i < issueArray.length; i++) {
                        System.out.println((i + 1) + ". " + issueArray[i]);
                    }

                    if (!scanner.hasNextInt()) {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.next();
                        continue;
                    }
                    addTicket(scanner.nextInt());
                    scanner.nextLine();
                } else if (choice == 2) {
                    System.out.print("Enter Ticket ID to delete: ");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Invalid input. Please enter a valid Ticket ID.");
                        scanner.next();
                        continue;
                    }
                    deleteTicket(scanner.nextInt());
                    scanner.nextLine();
                }
            } else if (role == 2) {
                System.out.println("\nEmployee Menu:\n1. Display Tickets\n2. Update Ticket Status\n3. View Deleted Tickets\n4. Exit");
                
                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next();
                    continue;
                }
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) displayTickets();
                else if (choice == 2) {
                    System.out.print("Enter Ticket ID to update: ");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Invalid input. Please enter a valid Ticket ID.");
                        scanner.next();
                        continue;
                    }
                    int ticketId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new status: ");
                    String status = scanner.nextLine();
                    updateTicket(ticketId, status);
                } else if (choice == 3) displayDeletedTickets();
            } else {
                break;
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        TicketSystem ticketSystem = new TicketSystem();
        ticketSystem.menu();
    }
}
