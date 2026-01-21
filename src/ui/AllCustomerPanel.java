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
public class AllCustomerPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private CustomerDao customerDao;

    public AllCustomerPanel() {
        super(new BorderLayout());
        this.customerDao = new CustomerDao();

        // Create table
        String[] columnNames = {"ID", "Last Name", "First Name", "Birth Date", "Gender", "Phone", "Email", "Address", "Description"};
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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Set column widths for better visibility
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(100);  // Last Name
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // First Name
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Birth Date
        table.getColumnModel().getColumn(4).setPreferredWidth(70);   // Gender
        table.getColumnModel().getColumn(5).setPreferredWidth(120);  // Phone
        table.getColumnModel().getColumn(6).setPreferredWidth(150);  // Email
        table.getColumnModel().getColumn(7).setPreferredWidth(150);  // Address
        table.getColumnModel().getColumn(8).setPreferredWidth(200);  // Description

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
     */
    private void loadAllCustomers() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerDao.getAllCustomers();

        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                c.getIdClient(),
                c.getNom(),
                c.getPrenom(),
                c.getDateNaissance(),
                c.getSexe(),
                c.getTelephone(),
                c.getEmail(),
                c.getAdresse(),
                c.getDescription()
            });
        }
    }
}