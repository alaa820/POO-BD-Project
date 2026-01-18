package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import model.Supplier;
import dao.SupplierDao;

/**
 * Panel to add a new supplier
 */
public class AddSupplierPanel extends JPanel {

    private JTextField nomField, prenomField, societeField, emailField, telField, adresseField;
    private JTextArea descriptionArea;
    private SupplierDao supplierDao;

    public AddSupplierPanel() {
        super(new BorderLayout());
        supplierDao = new SupplierDao();

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

        // Fields
        addFormField(panel, gbc, row++, "Nom :", nomField = new JTextField(20));
        addFormField(panel, gbc, row++, "Prénom :", prenomField = new JTextField(20));
        addFormField(panel, gbc, row++, "Société :", societeField = new JTextField(20));
        addFormField(panel, gbc, row++, "Email :", emailField = new JTextField(20));
        addFormField(panel, gbc, row++, "Téléphone :", telField = new JTextField(20));
        addFormField(panel, gbc, row++, "Adresse :", adresseField = new JTextField(20));

        // Description
        gbc.gridx = 0;
        gbc.gridy = row++;
        JLabel descLabel = new JLabel("Description :");
        descLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(descLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 11));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(descriptionArea), gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addBtn = new JButton("Ajouter Fournisseur");
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.setPreferredSize(new Dimension(140, 35));
        addBtn.addActionListener(e -> handleAddSupplier());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.setPreferredSize(new Dimension(140, 35));
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
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        field.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(field, gbc);
    }

    private void handleAddSupplier() {
        Supplier s = new Supplier(
                nomField.getText(),
                prenomField.getText(),
                societeField.getText(),
                emailField.getText(),
                telField.getText(),
                adresseField.getText(),
                descriptionArea.getText()
        );
        try {
            supplierDao.addSupplier(s);
            JOptionPane.showMessageDialog(this, "Fournisseur ajouté avec succès !");
            clearForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + e.getMessage());
        }
    }

    private void clearForm() {
        nomField.setText("");
        prenomField.setText("");
        societeField.setText("");
        emailField.setText("");
        telField.setText("");
        adresseField.setText("");
        descriptionArea.setText("");
        nomField.requestFocus();
    }
}
