package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


import dao.CustomerDao;
import model.Customer;

/**
 * Panel to search customers by name, prenom, and/or telephone
 */
public class SearchCustomerPanel extends JPanel {
    
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField telephoneField;
    private DefaultTableModel tableModel;
    private JTable table;
    private CustomerDao customerDao;

    public SearchCustomerPanel() {
        super(new BorderLayout());
        this.customerDao = new CustomerDao();
        
        // Create search panel with three fields
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Nom field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nomLabel = new JLabel("Name:");
        nomLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(nomLabel, gbc);
        
        gbc.gridx = 1;
        nomField = new JTextField(12);
        nomField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchPanel.add(nomField, gbc);
        
        // Prenom field
        gbc.gridx = 2;
        gbc.gridy = 0;
        JLabel prenomLabel = new JLabel("Surname :");
        prenomLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(prenomLabel, gbc);
        
        gbc.gridx = 3;
        prenomField = new JTextField(12);
        prenomField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchPanel.add(prenomField, gbc);
        
        // Telephone field
        gbc.gridx = 4;
        gbc.gridy = 0;
        JLabel telephoneLabel = new JLabel("Phone :");
        telephoneLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(telephoneLabel, gbc);
        
        gbc.gridx = 5;
        telephoneField = new JTextField(12);
        telephoneField.setFont(new Font("Arial", Font.PLAIN, 12));
        telephoneField.addActionListener(e -> performSearch());
        searchPanel.add(telephoneField, gbc);
        
        // Search button
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn, gbc);
        
        // Add Enter key listener to all fields
        nomField.addActionListener(e -> performSearch());
        prenomField.addActionListener(e -> performSearch());
        
        // Create results table
        String[] columnNames = {"Name", "Surname", "Phone", "Address", "Description"};
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
        
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Perform customer search with optional fields
     */
    private void performSearch() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        
        // Convert empty strings to null
        nom = nom.isEmpty() ? null : nom;
        prenom = prenom.isEmpty() ? null : prenom;
        telephone = telephone.isEmpty() ? null : telephone;
        
        // Check if at least one field is filled
        if (nom == null && prenom == null && telephone == null) {
            JOptionPane.showMessageDialog(this, "Please enter at least one search criteria", "Empty Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        tableModel.setRowCount(0);
        List<Customer> results = customerDao.searchCustomers(nom, prenom, telephone);
        
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No customers found matching the search criteria", "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Customer c : results) {
                tableModel.addRow(new Object[]{
                    c.getNom(),
                    c.getPrenom(),
                    c.getTelephone(),
                    c.getAdresse(),
                    c.getDescription()
                });
            }
        }
    }
}