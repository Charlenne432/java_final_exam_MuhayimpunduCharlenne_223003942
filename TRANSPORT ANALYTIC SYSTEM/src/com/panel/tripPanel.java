package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class tripPanel extends JPanel implements ActionListener {
    private JTextField idTxt = new JTextField(15);
    private JTextField orderNumberTxt = new JTextField(15);
    private JTextField dateTxt = new JTextField(15);
    private JTextField statusTxt = new JTextField(15);
    private JTextField totalAmountTxt = new JTextField(15);
    private JTextField paymentMethodTxt = new JTextField(15);
    private JTextField driverIdTxt = new JTextField(15);
    private JTextField vehicleIdTxt = new JTextField(15);
    private JTextField routeIdTxt = new JTextField(15);
    
    private JButton addBtn = new JButton("Add");
    private JButton updateBtn = new JButton("Update");
    private JButton deleteBtn = new JButton("Delete");
    private JButton clearBtn = new JButton("Clear");
    
    private JTable table;
    private DefaultTableModel model;
    
    public tripPanel() {
        initializeUI();
        loadTrips();
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
        
        // Trip ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Trip ID:"), gbc);
        gbc.gridx = 1;
        idTxt.setEditable(false);
        formPanel.add(idTxt, gbc);
        
        // Order Number
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Order Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(orderNumberTxt, gbc);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateTxt, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statusTxt, gbc);
        
        // Total Amount
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(totalAmountTxt, gbc);
        
        // Payment Method
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        formPanel.add(paymentMethodTxt, gbc);
        
        // Driver ID
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Driver ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(driverIdTxt, gbc);
        
        // Vehicle ID
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Vehicle ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(vehicleIdTxt, gbc);
        
        // Route ID
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Route ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(routeIdTxt, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(""));
        
        String[] columns = {"Trip ID", "Order Number", "Date", "Status", "Total Amount", "Payment Method", "Driver ID", "Vehicle ID", "Route ID"};
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
                    idTxt.setText(model.getValueAt(row, 0).toString());
                    orderNumberTxt.setText(model.getValueAt(row, 1).toString());
                    dateTxt.setText(model.getValueAt(row, 2).toString());
                    statusTxt.setText(model.getValueAt(row, 3).toString());
                    totalAmountTxt.setText(model.getValueAt(row, 4).toString());
                    paymentMethodTxt.setText(model.getValueAt(row, 5).toString());
                    driverIdTxt.setText(model.getValueAt(row, 6).toString());
                    vehicleIdTxt.setText(model.getValueAt(row, 7).toString());
                    routeIdTxt.setText(model.getValueAt(row, 8).toString());
                }
            }
        });
        
        return tablePanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addBtn) {
                addTrip();
            } else if (e.getSource() == updateBtn) {
                updateTrip();
            } else if (e.getSource() == deleteBtn) {
                deleteTrip();
            } else if (e.getSource() == clearBtn) {
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void addTrip() {
        if (orderNumberTxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter order number!");
            return;
        }
        
        String sql = "INSERT INTO trip (order_number, date, status, total_amount, payment_method, driver_id, vehicle_id, route_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = com.util.DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, orderNumberTxt.getText().trim());
            ps.setString(2, dateTxt.getText().trim());
            ps.setString(3, statusTxt.getText().trim());
            ps.setString(4, totalAmountTxt.getText().trim());
            ps.setString(5, paymentMethodTxt.getText().trim());
            ps.setInt(6, Integer.parseInt(driverIdTxt.getText()));
            ps.setInt(7, Integer.parseInt(vehicleIdTxt.getText()));
            ps.setInt(8, Integer.parseInt(routeIdTxt.getText()));
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Trip added successfully!");
            loadTrips();
            clearFields();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding trip: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Driver ID, Vehicle ID, and Route ID!");
        }
    }
    
    private void updateTrip() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a trip to update!");
            return;
        }
        
        String sql = "UPDATE trip SET order_number=?, date=?, status=?, total_amount=?, payment_method=?, driver_id=?, vehicle_id=?, route_id=? WHERE trip_id=?";
        
        try (Connection con = com.util.DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, orderNumberTxt.getText().trim());
            ps.setString(2, dateTxt.getText().trim());
            ps.setString(3, statusTxt.getText().trim());
            ps.setString(4, totalAmountTxt.getText().trim());
            ps.setString(5, paymentMethodTxt.getText().trim());
            ps.setInt(6, Integer.parseInt(driverIdTxt.getText()));
            ps.setInt(7, Integer.parseInt(vehicleIdTxt.getText()));
            ps.setInt(8, Integer.parseInt(routeIdTxt.getText()));
            ps.setInt(9, Integer.parseInt(idTxt.getText()));
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Trip updated successfully!");
            loadTrips();
            clearFields();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating trip: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Driver ID, Vehicle ID, and Route ID!");
        }
    }
    
    private void deleteTrip() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a trip to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this trip?");
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM trip WHERE trip_id=?";
            
            try (Connection con = com.util.DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Trip deleted successfully!");
                loadTrips();
                clearFields();
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting trip: " + ex.getMessage());
            }
        }
    }
    
    private void loadTrips() {
        try (Connection con = com.util.DB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM trip")) {
            
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("trip_id"),
                    rs.getString("order_number"),
                    rs.getString("date"),
                    rs.getString("status"),
                    rs.getString("total_amount"),
                    rs.getString("payment_method"),
                    rs.getInt("driver_id"),
                    rs.getInt("vehicle_id"),
                    rs.getInt("route_id")
                });
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading trips: " + ex.getMessage());
        }
    }
    
    private void clearFields() {
        idTxt.setText("");
        orderNumberTxt.setText("");
        dateTxt.setText("");
        statusTxt.setText("");
        totalAmountTxt.setText("");
        paymentMethodTxt.setText("");
        driverIdTxt.setText("");
        vehicleIdTxt.setText("");
        routeIdTxt.setText("");
        table.clearSelection();
    }
  
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Trip Panel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1000, 700); 
                frame.setLocationRelativeTo(null); 
     
                tripPanel panel = new tripPanel();
                frame.add(panel);
                
                frame.setVisible(true);
            }
        });
    }
}