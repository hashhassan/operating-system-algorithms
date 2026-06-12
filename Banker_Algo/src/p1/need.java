package p1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class need extends JFrame {

    JTextField processcnt, resourcecnt, allocation, max;
    JLabel lb1,lb2,lb3,lb4;
    JButton savebtn,retrivebtn;
    JTable table;
    DefaultTableModel model;
    JScrollPane js;
    Connection connection;

    public need() {
        connection = getConn();

        processcnt  =new JTextField();
        resourcecnt =new JTextField();
        allocation =new JTextField();
        max = new JTextField();
        lb1=new JLabel("Processes");
        lb2=new JLabel("Resources");
        lb3=new JLabel("Allocation (space seperated)");
        lb4=new JLabel("Max (space seperated)");

        savebtn =new JButton("Add");
        retrivebtn=new JButton("Retrieve & Calculate Need");
        model =new DefaultTableModel();
        table =new JTable(model);
        js=new JScrollPane(table);
        
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.add(lb1);
        panel.add(processcnt);
        panel.add(lb2);
        panel.add(resourcecnt);
        panel.add(lb3);
        panel.add(allocation);
        panel.add(lb4);
        panel.add(max);
        panel.add(savebtn);
        panel.add(retrivebtn);
        panel.add(js);

        add(panel);

        savebtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveToDB();
            }
        });

        retrivebtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                retrieveAndCalculate();
            }
        });
        
        setTitle("Banker's Algorithm");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    void saveToDB() {
        String allocationdb =allocation.getText().trim();
        String maxdb =max.getText().trim();

        if (allocationdb.isEmpty() || maxdb.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill both fields.");
            return;
        }

        try {
        		String query="INSERT INTO Banker (allocation, maxx) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, allocationdb);
            statement.setString(2, maxdb);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Added successfully");
        } catch (SQLException error) {
            JOptionPane.showMessageDialog(this, "Error: " + error.getMessage());
        }
    }

    void retrieveAndCalculate() {
        int totalResources;
        try {
            totalResources = Integer.parseInt(resourcecnt.getText().trim());
        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(this, "Enter number of resources first.");
            return;
        }

        String[] columns = new String[totalResources * 3 + 1];
        columns[0] = "Process";
        for (int i = 0; i < totalResources; i++) {
        	columns[i + 1] = "Alloc R"+i;
        }
        for (int i = 0; i < totalResources; i++) {
        	columns[i + totalResources + 1] = "Max R"+i;
        }
        
        for (int i = 0; i < totalResources; i++) {
        	columns[i + totalResources*2 + 1] = "Need R"+i;
        }

        model.setColumnIdentifiers(columns);
        model.setRowCount(0);

        try {
            Statement query =connection.createStatement();
            ResultSet rows =query.executeQuery("SELECT * FROM Banker");

            int processNumber = 0;
            while (rows.next()) {
                String[] allocValues= rows.getString("allocation").split(" ");
                String[] maxValues= rows.getString("maxx").split(" ");

                Object[] row = new Object[totalResources * 3 + 1];
                row[0] = "P" +processNumber;

                for (int i = 0; i < totalResources; i++) {
                    if (i >= allocValues.length || i >= maxValues.length) break;
                    int allocNum = Integer.parseInt(allocValues[i].trim());
                    int maxNum =Integer.parseInt(maxValues[i].trim());
                    int needNum =maxNum - allocNum;
                    row[i + 1]= allocNum;
                    row[i + totalResources + 1]= maxNum;
                    row[i + totalResources*2 + 1] = needNum;
                }

                model.addRow(row);
                processNumber++;
            }
        } catch (SQLException error) {
            JOptionPane.showMessageDialog(this, "Error: " + error.getMessage());
        }
    }

    public static Connection getConn() {
        try {
            String url = "jdbc:sqlserver://DESKTOP-F8N84OK\\HASSANSQL"
                       + ";databaseName=AdvProgramming"
                       + ";integratedSecurity=true"
                       + ";encrypt=true"
                       + ";trustServerCertificate=true";
            return DriverManager.getConnection(url);
        } catch (SQLException error) {
            System.err.println("DB Error: " + error.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        new need();
    }
}