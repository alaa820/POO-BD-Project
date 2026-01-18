package ui;

import model.Supplier;
import service.SupplierService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SupplierPanel extends JPanel {

    private JTextField nomField;
    private JTextField prenomField;
    private JTextField societeField;
    private JTextField emailField;
    private JTextField telField;
    private JTextField adresseField;
    private JTextArea descrArea;

    private JButton addButton;

    private SupplierService service;

    public SupplierPanel() {
        service = new SupplierService();
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ---------- TITRE ----------
        JLabel title = new JLabel("Ajouter un Fournisseur");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // ---------- FORMULAIRE ----------
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nomField = new JTextField(20);
        prenomField = new JTextField(20);
        societeField = new JTextField(20);
        emailField = new JTextField(20);
        telField = new JTextField(20);
        adresseField = new JTextField(20);
        descrArea = new JTextArea(3, 20);
        descrArea.setLineWrap(true);
        descrArea.setWrapStyleWord(true);
        int row = 0;
        addRow(formPanel, gbc, row++, "Nom :", nomField);
        addRow(formPanel, gbc, row++, "Prénom :", prenomField);
        addRow(formPanel, gbc, row++, "Société :", societeField);
        addRow(formPanel, gbc, row++, "Email :", emailField);
        addRow(formPanel, gbc, row++, "Téléphone :", telField);
        addRow(formPanel, gbc, row++, "Adresse :", adresseField);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description :"), gbc);

        gbc.gridx = 1;
        formPanel.add(new JScrollPane(descrArea), gbc);

        add(formPanel, BorderLayout.CENTER);

        // ---------- BOUTON ----------
        addButton = new JButton("Ajouter Fournisseur");
        addButton.setPreferredSize(new Dimension(180, 35));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> handleAjouterFournisseur());
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row,
                        String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void handleAjouterFournisseur() {
        Supplier s = new Supplier(
                nomField.getText(),
                prenomField.getText(),
                societeField.getText(),
                emailField.getText(),
                telField.getText(),
                adresseField.getText(),
                descrArea.getText()
        );

        try {
            service.ajouterFournisseur(s);
            JOptionPane.showMessageDialog(this, "Fournisseur ajouté avec succès !");
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }

    private void clearFields() {
        nomField.setText("");
        prenomField.setText("");
        societeField.setText("");
        emailField.setText("");
        telField.setText("");
        adresseField.setText("");
        descrArea.setText("");
    }
}
