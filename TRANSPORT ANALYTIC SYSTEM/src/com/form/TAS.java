package com.form;

import java.awt.*;
import javax.swing.*;
import com.panel.*;

public class TAS extends JFrame {
    JTabbedPane tabs = new JTabbedPane();

    // Constructor
    public TAS(String role, int userid) {
        setTitle("Transport Analytic System");
        setSize(900, 600);
        setLayout(new BorderLayout());

        // Show different panels depending on role
        if (role.equalsIgnoreCase("admin")) {
            tabs.addTab("Users", new usersPanel());
            tabs.addTab("Drivers", new driverPanel());
            tabs.addTab("Vehicles", new vehiclePanel());
            tabs.addTab("Trips", new tripPanel());
            tabs.addTab("Routes", new routePanel());
            tabs.addTab("Tickets", new ticketPanel());
            tabs.addTab("Route_Tickets", new route_ticketPanel());
            tabs.addTab("Maintenance", new maintenancePanel());
        } 
        else if (role.equalsIgnoreCase("manager")) {
        	 tabs.addTab("Maintenance", new maintenancePanel());
        	 tabs.addTab("Drivers", new driverPanel());
        	 tabs.addTab("Vehicles", new vehiclePanel());
            tabs.addTab("Tickets", new ticketPanel());
            tabs.addTab("Trips", new tripPanel());
            tabs.addTab("Routes", new routePanel());
            tabs.addTab("Route_Tickets", new route_ticketPanel());
        } 
        else if (role.equalsIgnoreCase("staff")) {
            tabs.addTab("Trips", new tripPanel());
            tabs.addTab("Tickets", new routePanel());
            tabs.addTab("Route_Tickets", new maintenancePanel());
        }

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
