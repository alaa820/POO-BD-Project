package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.SupplierDao;
import model.Supplier;

/**
 * Panel to search suppliers by last name, first name, or company
 */
public class SearchSupplierPanel extends JPanel {
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField societeField;
    private JButton searchBtn;
    private JButton clearBtn;
    private JTable table;
    private DefaultTableModel tableModel;
    private SupplierDao dao;
    
    public SearchSupplierPanel() {
        super(new BorderLayout());
        dao = new SupplierDao();
        
        // Top panel with search fields
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.setBackground(new Color(245, 245, 245));
        
        // Last Name field
        JLabel nomLabel = new JLabel("Last Name:");
        nomLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(nomLabel);
        
        nomField = new JTextField(12);
        nomField.setFont(new Font("Arial", Font.PLAIN, 12));
        nomField.addActionListener(e -> searchSupplier());
        topPanel.add(nomField);
        
        // First Name field
        JLabel prenomLabel = new JLabel("First Name:");
        prenomLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(prenomLabel);
        
        prenomField = new JTextField(12);
        prenomField.setFont(new Font("Arial", Font.PLAIN, 12));
        prenomField.addActionListener(e -> searchSupplier());
        topPanel.add(prenomField);
        
        // Company field
        JLabel societeLabel = new JLabel("Company:");
        societeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(societeLabel);
        
        societeField = new JTextField(12);
        societeField.setFont(new Font("Arial", Font.PLAIN, 12));
        societeField.addActionListener(e -> searchSupplier());
        topPanel.add(societeField);
        
        // Buttons
        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.addActionListener(e -> searchSupplier());
        topPanel.add(searchBtn);
        
        clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.addActionListener(e -> clearFields());
        topPanel.add(clearBtn);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Last Name", "First Name", "Company", "Email", "Phone", "Address", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    private void searchSupplier() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String societe = societeField.getText().trim();
        
        // Convert empty strings to null
        nom = nom.isEmpty() ? null : nom;
        prenom = prenom.isEmpty() ? null : prenom;
        societe = societe.isEmpty() ? null : societe;
        
        // Check if at least one field is filled
        if (nom == null && prenom == null && societe == null) {
            JOptionPane.showMessageDialog(this, 
                "Please enter at least one search criteria", 
                "Empty Search", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        tableModel.setRowCount(0);
        List<Supplier> results = dao.searchSuppliers(nom, prenom, societe);
        
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No suppliers found", 
                "No Results", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Supplier s : results) {
                tableModel.addRow(new Object[]{
                    s.getNom(), 
                    s.getPrenom(), 
                    s.getSociete(), 
                    s.getEmail(),
                    s.getTelephone(), 
                    s.getAdresse(), 
                    s.getDescription()
                });
            }
        }
    }
    
    private void clearFields() {
        nomField.setText("");
        prenomField.setText("");
        societeField.setText("");
        tableModel.setRowCount(0);
        nomField.requestFocus();
    }
}