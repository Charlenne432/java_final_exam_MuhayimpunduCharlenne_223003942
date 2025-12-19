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

public class usersPanel extends JPanel implements ActionListener {
    
    private JTextField idTxt = new JTextField(15);
    private JTextField nameTxt = new JTextField(15);
    private JPasswordField passTxt = new JPasswordField(15);
    private JTextField createdAtTxt = new JTextField(15);
    private JComboBox<String> roleCmb = new JComboBox<String>(new String[]{"ADMIN", "MANAGER", "STAFF"});
    
    private JButton addBtn = new JButton("Add");
    private JButton updateBtn = new JButton("Update");
    private JButton deleteBtn = new JButton("Delete");
    private JButton clearBtn = new JButton("Clear");
    
    private JTable table;
    private DefaultTableModel model;
    
    public usersPanel() {
        initializeUI();
        loadUsers();
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
        formPanel.add(new JLabel("User ID:"), gbc);
        
        gbc.gridx = 1;
        idTxt.setEditable(false);
        formPanel.add(idTxt, gbc);
      
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(nameTxt, gbc);
   
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(passTxt, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Role:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(roleCmb, gbc);
     
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Created At:"), gbc);
        
        gbc.gridx = 1;
        createdAtTxt.setEditable(false);
        formPanel.add(createdAtTxt, gbc);
      
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
        tablePanel.setBorder(BorderFactory.createTitledBorder("Users List"));
        
        String[] columns = {"User ID", "Username", "Password", "Role", "Created At"};
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
                addUser();
            } else if (e.getSource() == updateBtn) {
                updateUser();
            } else if (e.getSource() == deleteBtn) {
                deleteUser();
            } else if (e.getSource() == clearBtn) {
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void addUser() {
        String username = nameTxt.getText().trim();
        String password = new String(passTxt.getPassword());
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter password!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "INSERT INTO users (username, password, role, created_at) VALUES (?, ?, ?, ?)";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, roleCmb.getSelectedItem().toString());
            ps.setString(4, createdAtTxt.getText());
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "User added successfully!");
                loadUsers();
                clearFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void updateUser() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user to update!", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "UPDATE users SET username=?, password=?, role=?, created_at=? WHERE user_id=?";
        
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nameTxt.getText().trim());
            ps.setString(2, new String(passTxt.getPassword()));
            ps.setString(3, roleCmb.getSelectedItem().toString());
            ps.setString(4, createdAtTxt.getText());
            ps.setInt(5, Integer.parseInt(idTxt.getText()));
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "User updated successfully!");
                loadUsers();
                clearFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void deleteUser() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete!", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this user?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM users WHERE user_id=?";
            
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                int rows = ps.executeUpdate();
                
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    loadUsers();
                    clearFields();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void loadUsers() {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM users ORDER BY user_id");
             ResultSet rs = ps.executeQuery()) {
            
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    "********",
                    rs.getString("role"),
                    rs.getString("created_at")
                });
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            idTxt.setText(model.getValueAt(row, 0).toString());
            nameTxt.setText(model.getValueAt(row, 1).toString());
            passTxt.setText("");
            roleCmb.setSelectedItem(model.getValueAt(row, 3).toString());
            createdAtTxt.setText(model.getValueAt(row, 4).toString());
        }
    }
    
    private void clearFields() {
        idTxt.setText("");
        nameTxt.setText("");
        passTxt.setText("");
        roleCmb.setSelectedIndex(0);
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
                JFrame frame = new JFrame("Users Panel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.add(new usersPanel());
                frame.setVisible(true);
            }
        });
    }
}