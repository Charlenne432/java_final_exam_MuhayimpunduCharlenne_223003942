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

public class driverPanel extends JPanel implements ActionListener {
    
    private JTextField idTxt = new JTextField(15);
    private JTextField nameTxt = new JTextField(15);
    private JTextField licenseTxt = new JTextField(15);
    private JTextField experienceTxt = new JTextField(15);
    private JTextField createdAtTxt = new JTextField(15);
    
    private JButton addBtn = new JButton("Add");
    private JButton updateBtn = new JButton("Update");
    private JButton deleteBtn = new JButton("Delete");
    private JButton clearBtn = new JButton("Clear");
    
    private JTable table;
    private DefaultTableModel model;
    
    public driverPanel() {
        initializeUI();
        loadDrivers();
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
        
        // Driver ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Driver ID:"), gbc);
        
        gbc.gridx = 1;
        idTxt.setEditable(false);
        formPanel.add(idTxt, gbc);
        
        // Driver Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Driver Name:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(nameTxt, gbc);
        
        // Driving License
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Driving License:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(licenseTxt, gbc);
        
        // Experience
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Experience:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(experienceTxt, gbc);
        
        // Created At
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Created At:"), gbc);
        
        gbc.gridx = 1;
        createdAtTxt.setEditable(false);
        formPanel.add(createdAtTxt, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
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
        tablePanel.setBorder(BorderFactory.createTitledBorder("Drivers List"));
        
        String[] columns = {"Driver ID", "Driver Name", "Driving License", "Experience", "Created At"};
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
                addDriver();
            } else if (e.getSource() == updateBtn) {
                updateDriver();
            } else if (e.getSource() == deleteBtn) {
                deleteDriver();
            } else if (e.getSource() == clearBtn) {
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void addDriver() {
        String name = nameTxt.getText().trim();
        String license = licenseTxt.getText().trim();
        String experience = experienceTxt.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter driver name!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (license.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter driving license!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (experience.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter experience!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "INSERT INTO drivers (names, driving_licence, experience, created_at) VALUES (?, ?, ?, ?)";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, name);
            ps.setString(2, license);
            ps.setString(3, experience);
            ps.setString(4, createdAtTxt.getText());
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Driver added successfully!");
                loadDrivers();
                clearFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding driver: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void updateDriver() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a driver to update!", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "UPDATE drivers SET names=?, driving_licence=?, experience=?, created_at=? WHERE driver_id=?";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nameTxt.getText().trim());
            ps.setString(2, licenseTxt.getText().trim());
            ps.setString(3, experienceTxt.getText().trim());
            ps.setString(4, createdAtTxt.getText());
            ps.setInt(5, Integer.parseInt(idTxt.getText()));
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Driver updated successfully!");
                loadDrivers();
                clearFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating driver: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void deleteDriver() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a driver to delete!", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this driver?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM drivers WHERE driver_id=?";
            
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                int rows = ps.executeUpdate();
                
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Driver deleted successfully!");
                    loadDrivers();
                    clearFields();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting driver: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void loadDrivers() {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM drivers ORDER BY driver_id");
             ResultSet rs = ps.executeQuery()) {
            
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("driver_id"),
                    rs.getString("names"),
                    rs.getString("driving_licence"),
                    rs.getString("experience"),
                    rs.getString("created_at")
                });
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading drivers: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            idTxt.setText(model.getValueAt(row, 0).toString());
            nameTxt.setText(model.getValueAt(row, 1).toString());
            licenseTxt.setText(model.getValueAt(row, 2).toString());
            experienceTxt.setText(model.getValueAt(row, 3).toString());
            createdAtTxt.setText(model.getValueAt(row, 4).toString());
        }
    }
    
    private void clearFields() {
        idTxt.setText("");
        nameTxt.setText("");
        licenseTxt.setText("");
        experienceTxt.setText("");
        setCurrentDateTime();
        table.clearSelection();
    }
    
    private void setCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        createdAtTxt.setText(sdf.format(new Date()));
    }
    
    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Drivers Panel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.add(new driverPanel());
                frame.setVisible(true);
            }
        });
    }
}