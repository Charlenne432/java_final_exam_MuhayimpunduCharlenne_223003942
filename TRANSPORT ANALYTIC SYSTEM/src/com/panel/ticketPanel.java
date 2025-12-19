package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ticketPanel extends JPanel implements ActionListener {
    private JTextField idTxt = new JTextField(15);
    private JTextField statusTxt = new JTextField(15);
    private JTextField francsTxt = new JTextField(15);
    private JTextField pwTxt = new JTextField(15);
    private JTextField created_atTxt = new JTextField(15);

    private JButton addBtn = new JButton("Add");
    private JButton updateBtn = new JButton("Update");
    private JButton deleteBtn = new JButton("Delete");
    private JButton clearBtn = new JButton("Clear");

    private JTable table;
    private DefaultTableModel model;

    public ticketPanel() {
        initializeUI();
        loadTicket();
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
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        formPanel.add(new JLabel("Ticket ID:"));
        idTxt.setEditable(false);
        formPanel.add(idTxt);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusTxt);

        formPanel.add(new JLabel("Francs:"));
        formPanel.add(francsTxt);

        formPanel.add(new JLabel("PW:"));
        formPanel.add(pwTxt);
        
        formPanel.add(new JLabel("Created At:"));
        created_atTxt.setEditable(false);
        formPanel.add(created_atTxt);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        clearBtn.addActionListener(this);

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        formPanel.add(new JLabel(""));
        formPanel.add(buttonPanel);

        setCurrentDateTime();
        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columns = {"Ticket ID", "Status", "Francs", "PW", "Created At"};
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
                    statusTxt.setText(model.getValueAt(row, 1).toString());
                    francsTxt.setText(model.getValueAt(row, 2).toString());
                    pwTxt.setText(model.getValueAt(row, 3).toString());
                    created_atTxt.setText(model.getValueAt(row, 4).toString());
                }
            }
        });

        return tablePanel;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addBtn) {
                addTicket();
            } else if (e.getSource() == updateBtn) {
                updateTicket();
            } else if (e.getSource() == deleteBtn) {
                deleteTicket();
            } else if (e.getSource() == clearBtn) {
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void addTicket() {
        if (statusTxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter ticket status!");
            return;
        }

        String sql = "INSERT INTO ticket (status, francs, PW, created_at) VALUES (?, ?, ?, ?)";

        try (Connection con = com.util.DB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, statusTxt.getText().trim());
            ps.setString(2, francsTxt.getText().trim());
            ps.setString(3, pwTxt.getText().trim()); 
            ps.setString(4, created_atTxt.getText().trim());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Ticket added successfully!");
            loadTicket();
            clearFields();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding ticket: " + ex.getMessage());
        }
    }

    private void updateTicket() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to update!");
            return;
        }

       
        String sql = "UPDATE ticket SET status=?, francs=?, PW=?, created_at=? WHERE ticket_id=?";

        try (Connection con = com.util.DB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, statusTxt.getText().trim());
            ps.setString(2, francsTxt.getText().trim());
            ps.setString(3, pwTxt.getText().trim()); 
            ps.setString(4, created_atTxt.getText().trim());
            ps.setInt(5, Integer.parseInt(idTxt.getText()));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Ticket updated successfully!");
            loadTicket();
            clearFields();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating ticket: " + ex.getMessage());
        }
    }

    private void deleteTicket() {
        if (idTxt.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this ticket?");
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM ticket WHERE ticket_id=?";

            try (Connection con = com.util.DB.getConnection();
                    PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Ticket deleted successfully!");
                loadTicket();
                clearFields();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting ticket: " + ex.getMessage());
            }
        }
    }

    private void loadTicket() {
        try (Connection con = com.util.DB.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM ticket")) {

            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ticket_id"),
                        rs.getString("status"),
                        rs.getString("francs"),
                        rs.getString("PW"), 
                        rs.getString("created_at")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading tickets: " + ex.getMessage());
        }
    }

    private void clearFields() {
        idTxt.setText("");
        statusTxt.setText("");
        francsTxt.setText("");
        pwTxt.setText("");  
        setCurrentDateTime();
        table.clearSelection();
    }

    private void setCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        created_atTxt.setText(sdf.format(new Date()));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Ticket Panel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);

                ticketPanel panel = new ticketPanel();
                frame.add(panel);

                frame.setVisible(true);
            }
        });
    }
}