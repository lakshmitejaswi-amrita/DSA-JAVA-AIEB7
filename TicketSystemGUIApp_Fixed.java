import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class TicketSystemGUIApp_Fixed {
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
        }
    }

    static class TicketSystem {
        Ticket head = null;
        int ticketCounter = 101;
        List<Ticket> deletedTickets = new ArrayList<>();

        Map<String, String> departments = new LinkedHashMap<>();
        Map<String, Integer> issuePriority = new LinkedHashMap<>();
        Map<String, Map<String, String>> employeeCredentials = new HashMap<>();

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
            addEmployee("billing_emp1", "billing123", "Billing");
            addEmployee("management_emp1", "manage123", "Management");
            addEmployee("technical_emp1", "tech123", "Technical");
        }

        private void addEmployee(String username, String password, String department) {
            Map<String, String> creds = new HashMap<>();
            creds.put("password", password);
            creds.put("department", department);
            employeeCredentials.put(username, creds);
        }

        public Ticket addTicket(String issue) {
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
            return newTicket;
        }

        public boolean deleteTicket(int id, String dept) {
            Ticket temp = head, prev = null;
            while (temp != null && temp.ticketId != id) {
                prev = temp;
                temp = temp.next;
            }
            if (temp == null || (dept != null && !temp.department.equals(dept))) return false;
            if (prev == null) head = temp.next;
            else prev.next = temp.next;
            deletedTickets.add(temp);
            return true;
        }

        public Ticket trackTicket(int id) {
            Ticket temp = head;
            while (temp != null) {
                if (temp.ticketId == id) return temp;
                temp = temp.next;
            }
            return null;
        }

        public boolean updateTicket(int id, String status, String dept) {
            List<String> validStatuses = Arrays.asList("Open", "In Progress", "Resolved", "Closed");
            Ticket temp = head;
            while (temp != null) {
                if (temp.ticketId == id && temp.department.equals(dept)) {
                    if (validStatuses.contains(status)) {
                        temp.status = status;
                        return true;
                    }
                }
                temp = temp.next;
            }
            return false;
        }

        public List<Ticket> getTickets(String dept) {
            List<Ticket> list = new ArrayList<>();
            Ticket temp = head;
            while (temp != null) {
                if (temp.department.equals(dept)) list.add(temp);
                temp = temp.next;
            }
            return list;
        }

        public List<Ticket> getDeletedTickets(String dept) {
            List<Ticket> list = new ArrayList<>();
            for (Ticket t : deletedTickets) {
                if (t.department.equals(dept)) list.add(t);
            }
            return list;
        }

        public String authenticate(String username, String password) {
            if (!employeeCredentials.containsKey(username)) return null;
            Map<String, String> creds = employeeCredentials.get(username);
            return creds.get("password").equals(password) ? creds.get("department") : null;
        }

        public String[] getIssueArray() {
            return departments.keySet().toArray(new String[0]);
        }
    }
    // ... (already defined above, keeping them intact)

    public void buildGUI() {
        TicketSystem system = new TicketSystem();

        JFrame frame = new JFrame("Ticket System");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        // ---------------- CUSTOMER PANEL ----------------
        JPanel customerPanel = new JPanel(new BorderLayout(10, 10));
        JTextArea customerOutput = new JTextArea();
        customerOutput.setEditable(false);

        JComboBox<String> issueDropdown = new JComboBox<>(system.getIssueArray());
        JTextField deleteField = new JTextField();
        JTextField trackField = new JTextField();
        JButton addBtn = new JButton("Add Ticket");
        JButton delBtn = new JButton("Delete Ticket");
        JButton trackBtn = new JButton("Track Ticket");

        JPanel custForm = new JPanel(new GridLayout(6, 2, 5, 5));
        custForm.add(new JLabel("Select Issue:"));
        custForm.add(issueDropdown);
        custForm.add(addBtn);
        custForm.add(new JLabel("Delete Ticket ID:"));
        custForm.add(deleteField);
        custForm.add(delBtn);
        custForm.add(new JLabel("Track Ticket ID:"));
        custForm.add(trackField);
        custForm.add(trackBtn);

        customerPanel.add(custForm, BorderLayout.NORTH);
        customerPanel.add(new JScrollPane(customerOutput), BorderLayout.CENTER);
        tabs.add("Customer", customerPanel);

        addBtn.addActionListener(e -> {
            String issue = (String) issueDropdown.getSelectedItem();
            Ticket t = system.addTicket(issue);
            customerOutput.append("Added Ticket: ID=" + t.ticketId + ", Issue=" + t.issue + "\n");
        });

        delBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(deleteField.getText().trim());
                boolean result = system.deleteTicket(id, null);
                customerOutput.append(result ? "Ticket deleted.\n" : "Ticket not found.\n");
            } catch (Exception ex) {
                customerOutput.append("Invalid ID.\n");
            }
        });

        trackBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(trackField.getText().trim());
                Ticket t = system.trackTicket(id);
                customerOutput.append(t != null ? "Status: " + t.status + ", Department: " + t.department + "\n" : "Ticket not found.\n");
            } catch (Exception ex) {
                customerOutput.append("Invalid ID.\n");
            }
        });

        // ---------------- EMPLOYEE PANEL ----------------
        JPanel employeePanel = new JPanel(new CardLayout());

        // Login Panel
        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        loginPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel());
        loginPanel.add(loginBtn);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JTextArea empOutput = new JTextArea();
        empOutput.setEditable(false);
        JTextField updateIdField = new JTextField();
        JTextField statusField = new JTextField();
        JTextField delEmpField = new JTextField();
        JButton viewTicketsBtn = new JButton("View Tickets");
        JButton updateBtn = new JButton("Update Ticket");
        JButton delEmpBtn = new JButton("Delete Ticket");
        JButton viewDeletedBtn = new JButton("View Deleted");
        JButton logoutBtn = new JButton("Logout");

        JPanel empForm = new JPanel(new GridLayout(6, 2, 5, 5));
        empForm.add(new JLabel("Ticket ID to Update:"));
        empForm.add(updateIdField);
        empForm.add(new JLabel("New Status:"));
        empForm.add(statusField);
        empForm.add(updateBtn);
        empForm.add(new JLabel("Ticket ID to Delete:"));
        empForm.add(delEmpField);
        empForm.add(delEmpBtn);
        empForm.add(viewTicketsBtn);
        empForm.add(viewDeletedBtn);
        empForm.add(logoutBtn);

        mainPanel.add(empForm, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(empOutput), BorderLayout.CENTER);

        JPanel empContainer = new JPanel(new CardLayout());
        empContainer.add(loginPanel, "login");
        empContainer.add(mainPanel, "main");
        ((CardLayout) empContainer.getLayout()).show(empContainer, "login");

        loginBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());
            String dept = system.authenticate(user, pass);
            if (dept != null) {
                empOutput.setText("Logged in as: " + dept + " department\n");
                empOutput.putClientProperty("dept", dept);
                ((CardLayout) empContainer.getLayout()).show(empContainer, "main");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid login", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        logoutBtn.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            ((CardLayout) empContainer.getLayout()).show(empContainer, "login");
        });

        viewTicketsBtn.addActionListener(e -> {
            String dept = (String) empOutput.getClientProperty("dept");
            List<Ticket> list = system.getTickets(dept);
            empOutput.setText(list.isEmpty() ? "No tickets.\n" : "");
            for (Ticket t : list) {
                empOutput.append("ID=" + t.ticketId + ", Issue=" + t.issue + ", Status=" + t.status + "\n");
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(updateIdField.getText().trim());
                String status = statusField.getText().trim();
                String dept = (String) empOutput.getClientProperty("dept");
                boolean result = system.updateTicket(id, status, dept);
                empOutput.append(result ? "Ticket updated.\n" : "Ticket not found or unauthorized.\n");
            } catch (Exception ex) {
                empOutput.append("Invalid ID.\n");
            }
        });

        delEmpBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(delEmpField.getText().trim());
                String dept = (String) empOutput.getClientProperty("dept");
                boolean result = system.deleteTicket(id, dept);
                empOutput.append(result ? "Ticket deleted.\n" : "Ticket not found or unauthorized.\n");
            } catch (Exception ex) {
                empOutput.append("Invalid ID.\n");
            }
        });

        viewDeletedBtn.addActionListener(e -> {
            String dept = (String) empOutput.getClientProperty("dept");
            List<Ticket> list = system.getDeletedTickets(dept);
            empOutput.setText(list.isEmpty() ? "No deleted tickets.\n" : "");
            for (Ticket t : list) {
                empOutput.append("[DELETED] ID=" + t.ticketId + ", Issue=" + t.issue + ", Status=" + t.status + "\n");
            }
        });

        employeePanel.add(empContainer);
        tabs.add("Employee", employeePanel);

        frame.add(tabs);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicketSystemGUIApp_Fixed().buildGUI());
    }
}
