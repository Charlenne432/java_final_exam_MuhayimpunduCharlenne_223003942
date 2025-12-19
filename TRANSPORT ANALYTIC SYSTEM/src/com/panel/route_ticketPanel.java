package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class route_ticketPanel extends JPanel implements ActionListener {
    private JTextField routeIdTxt = new JTextField(15);
    private JTextField ticketIdTxt = new JTextField(15);
    
    private JButton addBtn = new JButton("Add");
    private JButton deleteBtn = new JButton("Delete");
    private JButton clearBtn = new JButton("Clear");
    
    private JTable table;
    private DefaultTableModel model;
    
    public route_ticketPanel() {
        initializeUI();
        loadRouteTickets();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Route ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Route ID:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(routeIdTxt, gbc);
        
        // Ticket ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Ticket ID:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(ticketIdTxt, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Route-Ticket Relationships"));
        
        String[] columns = {"Route ID", "Ticket ID"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    routeIdTxt.setText(model.getValueAt(row, 0).toString());
                    ticketIdTxt.setText(model.getValueAt(row, 1).toString());
                }
            }
        });
        
        return tablePanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addBtn) {
                addRouteTicket();
            } else if (e.getSource() == deleteBtn) {
                deleteRouteTicket();
            } else if (e.getSource() == clearBtn) {
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void addRouteTicket() {
        if (routeIdTxt.getText().trim().isEmpty() || ticketIdTxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Route ID and Ticket ID!");
            return;
        }
        
        String sql = "INSERT INTO route_ticket (route_id, ticket_id) VALUES (?, ?)";
        
        try (Connection con = com.util.DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, Integer.parseInt(routeIdTxt.getText()));
            ps.setInt(2, Integer.parseInt(ticketIdTxt.getText()));
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Route-Ticket relationship added successfully!");
            loadRouteTickets();
            clearFields();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding relationship: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Route ID and Ticket ID!");
        }
    }
    
    private void deleteRouteTicket() {
        if (routeIdTxt.getText().isEmpty() || ticketIdTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a relationship to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this relationship?");
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM route_ticket WHERE route_id=? AND ticket_id=?";
            
            try (Connection con = com.util.DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, Integer.parseInt(routeIdTxt.getText()));
                ps.setInt(2, Integer.parseInt(ticketIdTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Relationship deleted successfully!");
                loadRouteTickets();
                clearFields();
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting relationship: " + ex.getMessage());
            }
        }
    }
    
    private void loadRouteTickets() {
        try (Connection con = com.util.DB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM route_ticket")) {
            
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("route_id"),
                    rs.getInt("ticket_id")
                });
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading route-ticket relationships: " + ex.getMessage());
        }
    }
    
    private void clearFields() {
        routeIdTxt.setText("");
        ticketIdTxt.setText("");
        table.clearSelection();
    }
    
    // Main method for testing
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and display the frame
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Route-Ticket Panel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 500);
                frame.setLocationRelativeTo(null); 

                route_ticketPanel panel = new route_ticketPanel();
                frame.add(panel);
                
                frame.setVisible(true);
            }
        });
    }
}