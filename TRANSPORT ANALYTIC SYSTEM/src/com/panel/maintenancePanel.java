package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class maintenancePanel extends JPanel implements ActionListener {
    private JTextField idTxt = new JTextField(15);
    private JTextField referenceTxt = new JTextField(15);
    private JTextField descriptionTxt = new JTextField(15);
    private JTextField dateTxt = new JTextField(15);
    private JTextField statusTxt = new JTextField(15);
    private JTextField costTxt = new JTextField(15);
    private JTextField driverIdTxt = new JTextField(15);
    private JTextField vehicleIdTxt = new JTextField(15);
    private JTextField ticketIdTxt = new JTextField(15);
    
    private JButton addBtn = new JButton("Add");
    private JButton updateBtn = new JButton("Update");
    private JButton deleteBtn = new JButton("Delete");
    private JButton clearBtn = new JButton("Clear");
    
    private JTable table;
    private DefaultTableModel model;
    
    public maintenancePanel() {
        initializeUI();
        loadMaintenance();
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
        
        // Maintenance ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Maintenance ID:"), gbc);
        gbc.gridx = 1;
        idTxt.setEditable(false);
        formPanel.add(idTxt, gbc);
        
        // Reference
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Reference:"), gbc);
        gbc.gridx = 1;
        formPanel.add(referenceTxt, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionTxt, gbc);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateTxt, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statusTxt, gbc);
        
        // Cost
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Cost:"), gbc);
        gbc.gridx = 1;
        formPanel.add(costTxt, gbc);
        
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
        
        // Ticket ID
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Ticket ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(ticketIdTxt, gbc);
        
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
        tablePanel.setBorder(BorderFactory.createTitledBorder("Maintenance Records"));
        
        String[] columns = {"Maintenance ID", "Reference", "Description", "Date", "Status", "Cost", "Driver ID", "Vehicle ID", "Ticket ID"};
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
                    referenceTxt.setText(model.getValueAt(row, 1).toString());
                    descriptionTxt.setText(model.getValueAt(row, 2).toString());
                    dateTxt.setText(model.getValueAt(row, 3).toString());
                    statusTxt.setText(model.getValueAt(row, 4).toString());
                    costTxt.setText(model.getValueAt(row, 5).toString());
                    driverIdTxt.setText(model.getValueAt(row, 6).toString());
                    vehicleIdTxt.setText(model.getValueAt(row, 7).toString());
                    ticketIdTxt.setText(model.getValueAt(row, 8).toString());
                }
            }
        });
        
        return tablePanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addBtn) {
                addMaintenance();
            } else if (e.getSource() == updateBtn) {
                updateMaintenance();
            } else if (e.getSource() == deleteBtn) {
                deleteMaintenance();
            } else if (e.getSource() == clearBtn) {
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void addMaintenance() {
        if (referenceTxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter reference number!");
            return;
        }
        
        String sql = "INSERT INTO maintenance (reference_id, description, date, status, cost, driver_id, vehicle_id, ticket_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = com.util.DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, referenceTxt.getText().trim());
            ps.setString(2, descriptionTxt.getText().trim());
            ps.setString(3, dateTxt.getText().trim());
            ps.setString(4, statusTxt.getText().trim());
            ps.setString(5, costTxt.getText().trim());
            ps.setInt(6, Integer.parseInt(driverIdTxt.getText()));
            ps.setInt(7, Integer.parseInt(vehicleIdTxt.getText()));
            ps.setInt(8, Integer.parseInt(ticketIdTxt.getText()));
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Maintenance record added successfully!");
            loadMaintenance();
            clearFields();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding maintenance: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Driver ID, Vehicle ID, and Ticket ID!");
        }
    }
    
    private void updateMaintenance() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a maintenance record to update!");
            return;
        }
        
        String sql = "UPDATE maintenance SET reference_id=?, description=?, date=?, status=?, cost=?, driver_id=?, vehicle_id=?, ticket_id=? WHERE maintenance_id=?";
        
        try (Connection con = com.util.DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, referenceTxt.getText().trim());
            ps.setString(2, descriptionTxt.getText().trim());
            ps.setString(3, dateTxt.getText().trim());
            ps.setString(4, statusTxt.getText().trim());
            ps.setString(5, costTxt.getText().trim());
            ps.setInt(6, Integer.parseInt(driverIdTxt.getText()));
            ps.setInt(7, Integer.parseInt(vehicleIdTxt.getText()));
            ps.setInt(8, Integer.parseInt(ticketIdTxt.getText()));
            ps.setInt(9, Integer.parseInt(idTxt.getText()));
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Maintenance record updated successfully!");
            loadMaintenance();
            clearFields();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating maintenance: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Driver ID, Vehicle ID, and Ticket ID!");
        }
    }
    
    private void deleteMaintenance() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a maintenance record to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this maintenance record?");
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM maintenance WHERE maintenance_id=?";
            
            try (Connection con = com.util.DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Maintenance record deleted successfully!");
                loadMaintenance();
                clearFields();
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting maintenance: " + ex.getMessage());
            }
        }
    }
    
    private void loadMaintenance() {
        try (Connection con = com.util.DB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM maintenance")) {
            
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("maintenance_id"),
                    rs.getString("reference_id"),
                    rs.getString("description"),
                    rs.getString("date"),
                    rs.getString("status"),
                    rs.getString("cost"),
                    rs.getInt("driver_id"),
                    rs.getInt("vehicle_id"),
                    rs.getInt("ticket_id")
                });
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading maintenance records: " + ex.getMessage());
        }
    }
    
    private void clearFields() {
        idTxt.setText("");
        referenceTxt.setText("");
        descriptionTxt.setText("");
        dateTxt.setText("");
        statusTxt.setText("");
        costTxt.setText("");
        driverIdTxt.setText("");
        vehicleIdTxt.setText("");
        ticketIdTxt.setText("");
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
                JFrame frame = new JFrame("Maintenance Panel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1000, 700); 
                frame.setLocationRelativeTo(null); 
               
                maintenancePanel panel = new maintenancePanel();
                frame.add(panel);
                
                frame.setVisible(true);
            }
        });
    }
}