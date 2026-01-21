package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.EmployeeDao;
import model.Employee;

/**
 * Panel to search employees by last name, first name, and/or username
 */
public class SearchEmployeePanel extends JPanel {
    
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField usernameField;
    private DefaultTableModel tableModel;
    private JTable table;
    private EmployeeDao employeeDao;

    public SearchEmployeePanel() {
        super(new BorderLayout());
        this.employeeDao = new EmployeeDao();
        
        // Create search panel with three fields (labels on top, fields below)
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Last Name label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nomLabel = new JLabel("Last Name (optional):");
        nomLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(nomLabel, gbc);
        
        gbc.gridy = 1;
        nomField = new JTextField(12);
        nomField.setFont(new Font("Arial", Font.PLAIN, 12));
        nomField.addActionListener(e -> performSearch());
        searchPanel.add(nomField, gbc);
        
        // First Name label and field
        gbc.gridx = 1;
        gbc.gridy = 0;
        JLabel prenomLabel = new JLabel("First Name (optional):");
        prenomLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(prenomLabel, gbc);
        
        gbc.gridy = 1;
        prenomField = new JTextField(12);
        prenomField.setFont(new Font("Arial", Font.PLAIN, 12));
        prenomField.addActionListener(e -> performSearch());
        searchPanel.add(prenomField, gbc);
        
        // Username label and field
        gbc.gridx = 2;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username (optional):");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(usernameLabel, gbc);
        
        gbc.gridy = 1;
        usernameField = new JTextField(12);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameField.addActionListener(e -> performSearch());
        searchPanel.add(usernameField, gbc);
        
        // Search button
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn, gbc);
        
        // Create results table
        String[] columnNames = {"Username", "Last Name", "First Name", "Phone", "Address", "Access"};
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
     * Perform employee search with optional fields
     */
    private void performSearch() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String username = usernameField.getText().trim();
        
        // Convert empty strings to null
        nom = nom.isEmpty() ? null : nom;
        prenom = prenom.isEmpty() ? null : prenom;
        username = username.isEmpty() ? null : username;
        
        // Check if at least one field is filled
        if (nom == null && prenom == null && username == null) {
            JOptionPane.showMessageDialog(this, "Please enter at least one search criteria", "Empty Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        tableModel.setRowCount(0);
        List<Employee> results = employeeDao.searchEmployees(nom, prenom, username);
        
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No employees found matching the search criteria", "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Employee e : results) {
                tableModel.addRow(new Object[]{
                    e.getUsername(),
                    e.getNom(),
                    e.getPrenom(),
                    e.getPhone(),
                    e.getAdresse(),
                    e.getAccess()
                });
            }
        }
    }
}