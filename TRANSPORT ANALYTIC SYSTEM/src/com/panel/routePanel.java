package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class routePanel extends JPanel implements ActionListener {
    private JTextField idTxt = new JTextField(15);
    private JTextField locationTxt = new JTextField(15);
    private JTextField distanceTxt = new JTextField(15);
    private JTextField timeTxt = new JTextField(15);
    
    private JButton addBtn = new JButton("Add");
    private JButton updateBtn = new JButton("Update");
    private JButton deleteBtn = new JButton("Delete");
    private JButton clearBtn = new JButton("Clear");
    
    private JTable table;
    private DefaultTableModel model;
    
    public routePanel() {
        initializeUI();
        loadRoute();
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
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Route ID:"), gbc);
        
        gbc.gridx = 1;
        idTxt.setEditable(false);
        formPanel.add(idTxt, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Location:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(locationTxt, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Distance:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(distanceTxt, gbc);
   
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Time:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(timeTxt, gbc);
     
        gbc.gridx = 0;
        gbc.gridy = 4;
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
        
        String[] columns = {"Route ID", "Location", "Distance", "Time"};
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
                    locationTxt.setText(model.getValueAt(row, 1).toString());
                    distanceTxt.setText(model.getValueAt(row, 2).toString());
                    timeTxt.setText(model.getValueAt(row, 3).toString());
                }
            }
        });
        
        return tablePanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addBtn) {
                addRoute();
            } else if (e.getSource() == updateBtn) {
                updateRoute();
            } else if (e.getSource() == deleteBtn) {
                deleteRoute();
            } else if (e.getSource() == clearBtn) {
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void addRoute() {
        if (locationTxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter route location!");
            return;
        }
        
        String sql = "INSERT INTO route (location, distance, time) VALUES (?, ?, ?)";
        
        try (Connection con = com.util.DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, locationTxt.getText().trim());
            ps.setString(2, distanceTxt.getText().trim());
            ps.setString(3, timeTxt.getText().trim());
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Route added successfully!");
            loadRoute();
            clearFields();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding route: " + ex.getMessage());
        }
    }
    
    private void updateRoute() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a route to update!");
            return;
        }
        
        String sql = "UPDATE route SET location=?, distance=?, time=? WHERE route_id=?";
        
        try (Connection con = com.util.DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, locationTxt.getText().trim());
            ps.setString(2, distanceTxt.getText().trim());
            ps.setString(3, timeTxt.getText().trim());
            ps.setInt(4, Integer.parseInt(idTxt.getText()));
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Route updated successfully!");
            loadRoute();
            clearFields();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating route: " + ex.getMessage());
        }
    }
    
    private void deleteRoute() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a route to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this route?");
        if (confirm == JOptionPane.YES_OPTION) {
        	
            String sql = "DELETE FROM route WHERE route_id=?";
            
            try (Connection con = com.util.DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Route deleted successfully!");
                loadRoute();
                clearFields();
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting route: " + ex.getMessage());
            }
        }
    }
    
    private void loadRoute() {
        try (Connection con = com.util.DB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM route")) {
            
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("route_id"),
                    rs.getString("location"),
                    rs.getString("distance"),
                    rs.getString("time")
                });
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading route: " + ex.getMessage());
        }
    }
    
    private void clearFields() {
        idTxt.setText("");
        locationTxt.setText("");
        distanceTxt.setText("");
        timeTxt.setText("");
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
                JFrame frame = new JFrame("Route Panel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                routePanel panel = new routePanel();
                frame.add(panel);
                
                frame.setVisible(true);
            }
        });
    }
}