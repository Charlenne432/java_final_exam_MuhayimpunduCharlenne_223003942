package com.form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import com.panel.driverPanel;
import com.panel.maintenancePanel;
import com.panel.routePanel;
import com.panel.route_ticketPanel;
import com.panel.ticketPanel;
import com.panel.tripPanel;
import com.panel.usersPanel;
import com.panel.vehiclePanel;
import com.util.DB;

public class LoginForm extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton loginButton, clearButton;
    private JLabel messageLabel;

    public LoginForm() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("TRANSPORT ANALYTIC SYSTEM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.pink);

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("TRANSPORT ANALYTIC SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.black);
        titlePanel.add(titleLabel);

        JLabel welcomeLabel = new JLabel("WELCOME to Transport Analytic System");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.DARK_GRAY);
        titlePanel.add(welcomeLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Login"));

        // Username
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        // Password
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Role
        formPanel.add(new JLabel("Role:"));
        roleCombo = new JComboBox<>(new String[]{"ADMIN", "MANAGER", "STAFF"});
        formPanel.add(roleCombo);

        // Buttons
        formPanel.add(new JLabel("")); // Empty cell
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        clearButton = new JButton("Clear");
        
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.BLACK);
        clearButton.setBackground(Color.BLACK);
        
        loginButton.addActionListener(this);
        clearButton.addActionListener(this);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel);

        // Message label
        messageLabel = new JLabel(" ", JLabel.CENTER);
        messageLabel.setForeground(Color.RED);
        formPanel.add(messageLabel);

        // Add panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        // Add Enter key listener
        passwordField.addActionListener(this);
        
        // Set focus to username field
        usernameField.requestFocusInWindow();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton || e.getSource() == passwordField) {
            performLogin();
        } else if (e.getSource() == clearButton) {
            clearFields();
        }
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String selectedRole = roleCombo.getSelectedItem().toString();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password!");
            return;
        }

        // Authenticate user from database
        if (authenticateUser(username, password, selectedRole)) {
            messageLabel.setText("Login successful! Welcome " + username);
            messageLabel.setForeground(Color.GREEN);
            
            // Open main application based on role
            openMainApplication(username, selectedRole);
            this.dispose(); // Close login window
        } else {
            messageLabel.setText("Invalid username, password or role!");
            messageLabel.setForeground(Color.RED);
            passwordField.setText(""); // Clear password field
            usernameField.requestFocus(); // Focus back to username
        }
    }

    private boolean authenticateUser(String username, String password, String role) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            
            rs = pstmt.executeQuery();
            
            return rs.next(); // Returns true if user exists with given credentials
            
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Database error: " + e.getMessage());
            return false;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void openMainApplication(String username, String role) {
        // Create main application window
        final JFrame mainFrame = new JFrame("Transport Analytics System - " + role);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);
        
        // Create main panel with tabbed interface
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome " + username + " (" + role + ")", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLUE);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        
        // Add welcome panel as first tab
        tabbedPane.addTab("Dashboard", welcomePanel);
        
        // Add different panels based on user role
        if (role.equals("ADMIN")) {
            // Admin has access to all panels
            tabbedPane.addTab("users", new usersPanel());
            tabbedPane.addTab("driver", new driverPanel()); 
            tabbedPane.addTab("maintenance", new maintenancePanel()); 
            tabbedPane.addTab("route_ticket", new route_ticketPanel());
            tabbedPane.addTab("route", new routePanel());
            tabbedPane.addTab("ticket", new ticketPanel());
            tabbedPane.addTab("trip", new tripPanel());
            tabbedPane.addTab("vehicle", new vehiclePanel());
            
        } else if (role.equals("MANAGER")) {
            // Manager has limited access
            tabbedPane.addTab("driver", new driverPanel()); 
            tabbedPane.addTab("vehicle", new vehiclePanel()); 
            tabbedPane.addTab("route", new routePanel()); 
            tabbedPane.addTab("trip", new tripPanel());
            tabbedPane.addTab("ticket", new ticketPanel());
            tabbedPane.addTab("maintenance", new maintenancePanel());
            tabbedPane.addTab("route_ticket", new route_ticketPanel());
            
        } else if (role.equals("STAFF")) {
            // Staff has basic access
            tabbedPane.addTab("trip", new tripPanel()); 
            tabbedPane.addTab("ticket", new ticketPanel());
            tabbedPane.addTab("route_ticket", new route_ticketPanel());
        }
        
        // Add logout button panel
        JPanel logoutPanel = new JPanel(new FlowLayout());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                new LoginForm().setVisible(true);
            }
        });
        logoutPanel.add(logoutButton);
        
        // Add components to main frame
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.add(logoutPanel, BorderLayout.SOUTH);
        
        mainFrame.setVisible(true);
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        roleCombo.setSelectedIndex(0);
        messageLabel.setText(" ");
        usernameField.requestFocus();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }
}