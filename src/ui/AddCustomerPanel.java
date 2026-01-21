package ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import dao.CustomerDao;
import exception.DataMissingException;



public class AddCustomerPanel extends JPanel {
    
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField dateNaissanceField;
    private JComboBox<String> sexeCombo;
    private JTextField telephoneField;
    private JTextField emailField;
    private JTextField adresseField;
    private JTextArea descriptionArea;
    private CustomerDao customerDao;

    public AddCustomerPanel() {
        super(new BorderLayout());
        this.customerDao = new CustomerDao();
        
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        
        addFormField(panel, gbc, row++, "Name:", nomField = new JTextField(20));
        
       
        addFormField(panel, gbc, row++, "Surname:", prenomField = new JTextField(20));
        
    
        addFormField(panel, gbc, row++, "Birth Date (YYYY-MM-DD):", dateNaissanceField = new JTextField(20));
        
        
        JPanel sexePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sexeCombo = new JComboBox<>(new String[]{"M", "F"});
        sexePanel.add(sexeCombo);
        addFormFieldPanel(panel, gbc, row++, "Gender:", sexePanel);
        
        
        addFormField(panel, gbc, row++, "Phone Number:", telephoneField = new JTextField(20));
        
       
        addFormField(panel, gbc, row++, "Email:", emailField = new JTextField(20));
        
        
        addFormField(panel, gbc, row++, "Address:", adresseField = new JTextField(20));
        
       
        gbc.gridx = 0;
        gbc.gridy = row++;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(descLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 11));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        panel.add(descScrollPane, gbc);
        
        
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addBtn = new JButton("Add Customer");
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.setPreferredSize(new Dimension(120, 35));
        addBtn.addActionListener(e -> handleAddCustomer());
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.setPreferredSize(new Dimension(120, 35));
        clearBtn.addActionListener(e -> clearForm());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(clearBtn);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
 
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
    
   
    private void handleAddCustomer() {
        // Get input values
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String dateStr = dateNaissanceField.getText().trim();
        String sexe = (String) sexeCombo.getSelectedItem();
        String telephone = telephoneField.getText().trim();
        String email = emailField.getText().trim();
        String adresse = adresseField.getText().trim();
        String description = descriptionArea.getText().trim();
        
      
        LocalDate dateNaissance;
        try {
            dateNaissance = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid date format. Use YYYY-MM-DD", 
                "Date Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        try {
            boolean success = customerDao.addCustomer(nom, prenom, dateNaissance, sexe, 
                                                      telephone, email, adresse, description);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Customer added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to add customer", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch(DataMissingException e) {
            JOptionPane.showMessageDialog(this, 
                e.getMessage(), 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
   
    private void clearForm() {
        nomField.setText("");
        prenomField.setText("");
        dateNaissanceField.setText("");
        sexeCombo.setSelectedIndex(0);
        telephoneField.setText("");
        emailField.setText("");
        adresseField.setText("");
        descriptionArea.setText("");
        nomField.requestFocus();
    }
}