import java.time.LocalDateTime;
import java.util.*;

public class TicketSystemApp {

    static class Ticket {
        int ticketId;
        String issue;
        int priority;
        String status;
        String department;
        LocalDateTime creationTime;
        Ticket next;

        public Ticket(int ticketId, String issue, int priority, String status, String department) {
            this.ticketId = ticketId;
            this.issue = issue;
            this.priority = priority;
            this.status = status;
            this.department = department;
            this.creationTime = LocalDateTime.now();
            this.next = null;
        }
    }

    static class TicketSystem {
        Ticket head = null;
        int ticketCounter = 101;
        List<Ticket> deletedTickets = new ArrayList<>();

        Map<String, String> departments = new LinkedHashMap<>();
        Map<String, Integer> issuePriority = new LinkedHashMap<>();
        Map<String, Map<String, String>> employeeCredentials = new HashMap<>();

        Scanner scanner = new Scanner(System.in);

        public TicketSystem() {
            departments.put("Flight Delay", "Support");
            departments.put("Baggage Lost", "Support");
            departments.put("Seat Change Request", "Management");
            departments.put("Refund Request", "Billing");
            departments.put("Flight Cancellation", "Technical");
            departments.put("Meal Preference Change", "Management");
            departments.put("Overbooking Issue", "Management");
            departments.put("Missed Connecting Flight", "Support");
            departments.put("Damaged Baggage", "Support");
            departments.put("Check-in Issues", "Support");
            departments.put("Wrong Name on Ticket", "Management");
            departments.put("Duplicate Booking", "Technical");
            departments.put("Refund Delay", "Billing");
            departments.put("Upgrade Request", "Management");
            departments.put("Flight Rescheduling", "Technical");

            int[] priorities = {2, 1, 3, 1, 2, 3, 2, 1, 2, 3, 2, 2, 1, 3, 2};
            int index = 0;
            for (String issue : departments.keySet()) {
                issuePriority.put(issue, priorities[index++]);
            }

            addEmployee("support_emp1", "support123", "Support");
            addEmployee("support_emp2", "support456", "Support");
            addEmployee("billing_emp1", "billing123", "Billing");
            addEmployee("billing_emp2", "billing456", "Billing");
            addEmployee("management_emp1", "manage123", "Management");
            addEmployee("management_emp2", "manage456", "Management");
            addEmployee("technical_emp1", "tech123", "Technical");
            addEmployee("technical_emp2", "tech456", "Technical");
        }

        private void addEmployee(String username, String password, String department) {
            Map<String, String> creds = new HashMap<>();
            creds.put("password", password);
            creds.put("department", department);
            employeeCredentials.put(username, creds);
        }

        void addTicket(String issue) {
            if (!issuePriority.containsKey(issue)) {
                System.out.println("Invalid issue. Please select a valid issue.");
                return;
            }

            int priority = issuePriority.get(issue);
            String department = departments.get(issue);
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

            System.out.println("Ticket Added: ID=" + newTicket.ticketId + ", Issue=" + issue + ", Status=" + newTicket.status + ", Department=" + department);
        }

        void deleteTicket(int ticketId, String department) {
            Ticket temp = head, prev = null;

            while (temp != null && temp.ticketId != ticketId) {
                prev = temp;
                temp = temp.next;
            }

            if (temp == null) {
                System.out.println("Ticket ID not found.");
                return;
            }

            if (department != null && !temp.department.equals(department)) {
                System.out.println("Unauthorized: Cannot delete tickets outside your department.");
                return;
            }

            if (prev == null) head = temp.next;
            else prev.next = temp.next;

            deletedTickets.add(temp);
            System.out.println("Deleted Ticket " + ticketId + ".");
        }

        void viewDeletedTickets(String department) {
            boolean found = false;
            System.out.println("\n---- Deleted Tickets for " + department + " Department ----");
            for (Ticket t : deletedTickets) {
                if (t.department.equals(department)) {
                    System.out.println("ID: " + t.ticketId + ", Issue: " + t.issue + ", Status: " + t.status + ", Priority: " + t.priority);
                    found = true;
                }
            }
            if (!found) System.out.println("No deleted tickets for your department.");
            System.out.println("--------------------------------------------");
        }

        void updateTicket(int ticketId, String newStatus, String department) {
            Ticket temp = head;
            while (temp != null) {
                if (temp.ticketId == ticketId) {
                    if (department != null && !temp.department.equals(department)) {
                        System.out.println("Unauthorized: Cannot update tickets outside your department.");
                        return;
                    }
                    List<String> validStatuses = Arrays.asList("Open", "In Progress", "Resolved", "Closed");
                    if (validStatuses.contains(newStatus)) {
                        temp.status = newStatus;
                        System.out.println("Updated Ticket " + ticketId + ": Status=" + newStatus);
                    } else {
                        System.out.println("Invalid status.");
                    }
                    return;
                }
                temp = temp.next;
            }
            System.out.println("Ticket ID not found.");
        }

        void displayTickets(String department) {
            Ticket temp = head;
            boolean found = false;
            System.out.println("\n---- Tickets for " + department + " Department ----");
            while (temp != null) {
                if (temp.department.equals(department)) {
                    System.out.println("ID: " + temp.ticketId + ", Issue: " + temp.issue + ", Status: " + temp.status + ", Priority: " + temp.priority + ", Created At: " + temp.creationTime);
                    found = true;
                }
                temp = temp.next;
            }
            if (!found) System.out.println("No tickets found.");
            System.out.println("--------------------------------------------");
        }

        void trackTicket(int ticketId) {
            Ticket temp = head;
            while (temp != null) {
                if (temp.ticketId == ticketId) {
                    System.out.println("Ticket Status: ID=" + temp.ticketId + ", Issue=" + temp.issue + ", Status=" + temp.status + ", Department=" + temp.department + ", Created At: " + temp.creationTime);
                    return;
                }
                temp = temp.next;
            }
            System.out.println("Ticket ID not found.");
        }

        String authenticateEmployee() {
            System.out.println();
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            System.out.println();

            if (employeeCredentials.containsKey(username)) {
                Map<String, String> creds = employeeCredentials.get(username);
                if (creds.get("password").equals(password)) {
                    System.out.println("Login successful.");
                    return creds.get("department");
                }
            }
            System.out.println("Invalid credentials.");
            return null;
        }

        void menu() {
            while (true) {
                System.out.println("\n1. Customer\n2. Employee\n3. Exit");
                System.out.print("Enter your choice: ");
                String role = scanner.nextLine().trim();
                System.out.println();

                if (role.equals("1")) {
                    while (true) {
                        System.out.println("\nCustomer Menu:\n1. Add Ticket\n2. Delete Ticket\n3. Track Ticket\n4. Exit");
                        System.out.print("Enter your choice: ");
                        String choice = scanner.nextLine().trim();
                        System.out.println();

                        if (choice.equals("1")) {
                            System.out.println("Select an Issue:");
                            List<String> issueList = new ArrayList<>(issuePriority.keySet());
                            for (int i = 0; i < issueList.size(); i++) {
                                System.out.println((i + 1) + ". " + issueList.get(i));
                            }
                            try {
                                System.out.print("Enter your choice: ");
                                int issueChoice = Integer.parseInt(scanner.nextLine().trim());
                                System.out.println();
                                if (issueChoice >= 1 && issueChoice <= issueList.size()) {
                                    addTicket(issueList.get(issueChoice - 1));
                                } else {
                                    System.out.println("Invalid choice.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Enter a valid number.");
                            }
                        } else if (choice.equals("2")) {
                            try {
                                System.out.print("Enter Ticket ID to delete: ");
                                int id = Integer.parseInt(scanner.nextLine().trim());
                                System.out.println();
                                deleteTicket(id, null);
                            } catch (NumberFormatException e) {
                                System.out.println("Enter a valid Ticket ID.");
                            }
                        } else if (choice.equals("3")) {
                            try {
                                System.out.print("Enter Ticket ID to track: ");
                                int id = Integer.parseInt(scanner.nextLine().trim());
                                System.out.println();
                                trackTicket(id);
                            } catch (NumberFormatException e) {
                                System.out.println("Enter a valid Ticket ID.");
                            }
                        } else if (choice.equals("4")) {
                            break;
                        } else {
                            System.out.println("Invalid choice. Please try again.");
                        }
                    }

                } else if (role.equals("2")) {
                    String department = authenticateEmployee();
                    if (department == null) continue;

                    while (true) {
                        System.out.println("\nEmployee Menu [" + department + " Department]:");
                        System.out.println("1. Display Tickets\n2. Update Ticket Status\n3. Delete Ticket\n4. View Deleted Tickets\n5. Logout");
                        System.out.print("Enter your choice: ");
                        String choice = scanner.nextLine().trim();
                        System.out.println();

                        if (choice.equals("1")) {
                            displayTickets(department);
                        } else if (choice.equals("2")) {
                            try {
                                System.out.print("Enter Ticket ID to update: ");
                                int id = Integer.parseInt(scanner.nextLine().trim());
                                System.out.print("Enter new status (Open, In Progress, Resolved, Closed): ");
                                String status = scanner.nextLine().trim();
                                System.out.println();
                                updateTicket(id, status, department);
                            } catch (NumberFormatException e) {
                                System.out.println("Enter a valid Ticket ID.");
                            }
                        } else if (choice.equals("3")) {
                            try {
                                System.out.print("Enter Ticket ID to delete: ");
                                int id = Integer.parseInt(scanner.nextLine().trim());
                                System.out.println();
                                deleteTicket(id, department);
                            } catch (NumberFormatException e) {
                                System.out.println("Enter a valid Ticket ID.");
                            }
                        } else if (choice.equals("4")) {
                            viewDeletedTickets(department);
                        } else if (choice.equals("5")) {
                            System.out.println("Logging out...");
                            break;
                        } else {
                            System.out.println("Invalid choice. Please try again.");
                        }
                    }
                } else if (role.equals("3")) {
                    System.out.println("Exiting system.");
                    break;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    public static void main(String[] args) {
        TicketSystem system = new TicketSystem();
        system.menu();
    }
}
