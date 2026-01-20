package ui;

import javax.swing.*;
import java.awt.*;
import dao.EmployeeDao;
import exception.DataMissingException;

/**
 * Panel to add a new employee
 */
public class AddEmployeePanel extends JPanel {
    
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField usernameField;
    private JPasswordField mdpField;
    private JTextField phoneField;
    private JTextField adresseField;
    private JComboBox<String> accessCombo;
    private EmployeeDao employeeDao;

    public AddEmployeePanel() {
        super(new BorderLayout());
        this.employeeDao = new EmployeeDao();
        
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates the form panel with all input fields
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Nom
        addFormField(panel, gbc, row++, "Nom:", nomField = new JTextField(20));
        
        // Prenom
        addFormField(panel, gbc, row++, "Prenom:", prenomField = new JTextField(20));
        
        // Username
        addFormField(panel, gbc, row++, "Username:", usernameField = new JTextField(20));
        
        // Password
        addFormField(panel, gbc, row++, "Password:", mdpField = new JPasswordField(20));
        
        // Phone
        addFormField(panel, gbc, row++, "Phone:", phoneField = new JTextField(20));
        
        // Adresse
        addFormField(panel, gbc, row++, "Adresse:", adresseField = new JTextField(20));
        
        // Access (Role)
        JPanel accessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accessCombo = new JComboBox<>(new String[]{"admin", "employee", "manager"});
        accessPanel.add(accessCombo);
        addFormFieldPanel(panel, gbc, row++, "Access:", accessPanel);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addBtn = new JButton("Add Employee");
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.setPreferredSize(new Dimension(120, 35));
        addBtn.addActionListener(e -> handleAddEmployee());
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.setPreferredSize(new Dimension(120, 35));
        clearBtn.addActionListener(e -> clearForm());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(clearBtn);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    /**
     * Helper method to add a text field to the form
     */
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        field.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(field, gbc);
    }
    
    /**
     * Helper method to add a panel field to the form
     */
    private void addFormFieldPanel(JPanel panel, GridBagConstraints gbc, int row, String label, JPanel fieldPanel) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        panel.add(fieldPanel, gbc);
    }
    
    /**
     * Handle adding an employee
     */
    private void handleAddEmployee() {
        // Get input values
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String username = usernameField.getText().trim();
        String mdp = new String(mdpField.getPassword());
        String phone = phoneField.getText().trim();
        String adresse = adresseField.getText().trim();
        String access = (String) accessCombo.getSelectedItem();
        
        try {
            boolean success = employeeDao.addEmployee(username, nom, prenom, adresse, phone, mdp, access);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Employee added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to add employee. Please try again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch(DataMissingException e) {
            JOptionPane.showMessageDialog(this, 
                e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Clear all form fields
     */
    private void clearForm() {
        nomField.setText("");
        prenomField.setText("");
        usernameField.setText("");
        mdpField.setText("");
        phoneField.setText("");
        adresseField.setText("");
        accessCombo.setSelectedIndex(0);
        nomField.requestFocus();
    }
}