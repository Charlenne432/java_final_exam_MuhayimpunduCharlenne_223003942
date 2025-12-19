package com.panel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.*;
import com.util.DB;

public class vehiclePanel extends JPanel implements ActionListener {
    
    private JTextField idTxt = new JTextField(15);
    private JTextField nameTxt = new JTextField(15);
    private JTextField identifierTxt = new JTextField(15);
    private JComboBox<String> statusCmb = new JComboBox<String>(new String[]{"ACTIVE", "MAINTENANCE", "INACTIVE"});
    private JTextField locationTxt = new JTextField(15);
    private JTextField assigned_sinceTxt = new JTextField(15);
    
    private JButton addBtn = new JButton("Add");
    private JButton updateBtn = new JButton("Update");
    private JButton deleteBtn = new JButton("Delete");
    private JButton clearBtn = new JButton("Clear");
    
    private JTable table;
    private DefaultTableModel model;
    
    public vehiclePanel() {
        initializeUI();
        loadVehicles();
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
        
        // Vehicle ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Vehicle ID:"), gbc);
        
        gbc.gridx = 1;
        idTxt.setEditable(false);
        formPanel.add(idTxt, gbc);
        
        // Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(nameTxt, gbc);
        
        // Identifier
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Identifier:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(identifierTxt, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Status:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(statusCmb, gbc);
        
        // Location
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Location:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(locationTxt, gbc);
        
        // Assigned Since
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Assigned Since:"), gbc);
        
        gbc.gridx = 1;
        assigned_sinceTxt.setEditable(false);
        formPanel.add(assigned_sinceTxt, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
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
        
        setCurrentDateTime();
        return formPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Vehicles List"));
        
        String[] columns = {"Vehicle ID", "Name", "Identifier", "Status", "Location", "Assigned Since"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        table.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    fillFormFromTable();
                }
            }
        });
        
        return tablePanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addBtn) {
                addVehicle();
            } else if (e.getSource() == updateBtn) {
                updateVehicle();
            } else if (e.getSource() == deleteBtn) {
                deleteVehicle();
            } else if (e.getSource() == clearBtn) {
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void addVehicle() {
        String name = nameTxt.getText().trim();
        String identifier = identifierTxt.getText().trim();
        String location = locationTxt.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter vehicle's name!", 
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (identifier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the identifier!", 
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter location!", 
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "INSERT INTO vehicles (name, identifier, status, location, assigned_since) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, name);
            ps.setString(2, identifier);
            ps.setString(3, statusCmb.getSelectedItem().toString());
            ps.setString(4, location);
            ps.setString(5, assigned_sinceTxt.getText());
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Vehicle added successfully!");
                loadVehicles();
                clearFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding vehicle: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void updateVehicle() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to update!", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "UPDATE vehicles SET name=?, identifier=?, status=?, location=?, assigned_since=? WHERE vehicle_id=?";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nameTxt.getText().trim());
            ps.setString(2, identifierTxt.getText().trim());
            ps.setString(3, statusCmb.getSelectedItem().toString());
            ps.setString(4, locationTxt.getText().trim());
            ps.setString(5, assigned_sinceTxt.getText());
            ps.setInt(6, Integer.parseInt(idTxt.getText()));
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Vehicle updated successfully!");
                loadVehicles();
                clearFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating vehicle: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void deleteVehicle() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to delete!", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this vehicle?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM vehicles WHERE vehicle_id=?";
            
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                int rows = ps.executeUpdate();
                
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Vehicle deleted successfully!");
                    loadVehicles();
                    clearFields();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting vehicle: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void loadVehicles() {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM vehicles ORDER BY vehicle_id");
             ResultSet rs = ps.executeQuery()) {
            
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("vehicle_id"),
                    rs.getString("name"),
                    rs.getString("identifier"),
                    rs.getString("status"),
                    rs.getString("location"),
                    rs.getString("assigned_since")
                });
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            idTxt.setText(model.getValueAt(row, 0).toString());
            nameTxt.setText(model.getValueAt(row, 1).toString());
            identifierTxt.setText(model.getValueAt(row, 2).toString());
            statusCmb.setSelectedItem(model.getValueAt(row, 3).toString());
            locationTxt.setText(model.getValueAt(row, 4).toString());
            assigned_sinceTxt.setText(model.getValueAt(row, 5).toString());
        }
    }
    
    private void clearFields() {
        idTxt.setText("");
        nameTxt.setText("");
        identifierTxt.setText("");
        locationTxt.setText("");
        statusCmb.setSelectedIndex(0);
        setCurrentDateTime();
        table.clearSelection();
    }
    
    private void setCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assigned_sinceTxt.setText(sdf.format(new Date()));
    }
    
    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Vehicles Panel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(900, 600);
                frame.setLocationRelativeTo(null);
                frame.add(new vehiclePanel());
                frame.setVisible(true);
            }
        });
    }
}