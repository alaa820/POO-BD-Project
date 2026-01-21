package ui;

import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.CustomerDao;

import model.Customer;

/**
 * Panel to display all customers in a table
 */
public class AllCustomersPanel extends JPanel {
  /*  
    private DefaultTableModel tableModel;
    private JTable table;
    private CustomerDao customerDao;

    public AllCustomersPanel() {
        super(new BorderLayout());
        this.customerDao = new CustomerDao();
        
        // Create table
        String[] columnNames = {"Nom", "Prenom", "Telephone", "Adresse", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadAllCustomers());
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.NORTH);
        
        // Load data on startup
        loadAllCustomers();
    }
    
    /**
     * Load all customers from database and display in table
     
    private void loadAllCustomers() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerDao.getAllCustomers();
        
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                c.getNom(),
                c.getPrenom(),
                c.getTelephone(),
                c.getAdresse(),
                c.getDescription()
            });
        }
    }*/
}
