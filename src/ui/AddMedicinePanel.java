package ui;
import exception.CodeBarreExistsException;
import exception.DataMissingException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import dao.MedicineDao;
import dao.SupplierDao;
import model.Medicine;
import model.Supplier;

/**
 * Panel to add a new medicine with supplier suggestion bar
 */
public class AddMedicinePanel extends JPanel {

    private JTextField codeBarreField, nomField, prixAchatField, prixVenteField;
    private JTextField tauxTVAField, dosageField, quantiteField, seuilField;
    private JTextField formePharmaceutiqueField, emplacementField;
    private JCheckBox prescriptionCheckBox;
    private JTextField supplierField;
    private JList<String> supplierSuggestionList;
    private DefaultListModel<String> suggestionModel;
    private MedicineDao medicineDao;
    private SupplierDao supplierDao;
    private Supplier selectedSupplier;

    public AddMedicinePanel() {
        super(new BorderLayout());
        medicineDao = new MedicineDao();
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

        // Basic information
        addFormField(panel, gbc, row++, "Code Barre :", codeBarreField = new JTextField(20));
        addFormField(panel, gbc, row++, "Nom :", nomField = new JTextField(20));
        addFormField(panel, gbc, row++, "Dosage :", dosageField = new JTextField(20));
        addFormField(panel, gbc, row++, "Forme Pharmaceutique :", formePharmaceutiqueField = new JTextField(20));

        // Pricing
        addFormField(panel, gbc, row++, "Prix d'Achat :", prixAchatField = new JTextField(20));
        addFormField(panel, gbc, row++, "Prix de Vente :", prixVenteField = new JTextField(20));
        addFormField(panel, gbc, row++, "Taux TVA (%) :", tauxTVAField = new JTextField(20));

        // Quantity and storage
        addFormField(panel, gbc, row++, "Quantité :", quantiteField = new JTextField(20));
        addFormField(panel, gbc, row++, "Seuil :", seuilField = new JTextField(20));
        addFormField(panel, gbc, row++, "Emplacement :", emplacementField = new JTextField(20));

        // Prescription requirement
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        prescriptionCheckBox = new JCheckBox("Nécessite Prescription");
        prescriptionCheckBox.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(prescriptionCheckBox, gbc);

        // Supplier suggestion section
        row = addSupplierSuggestionField(panel, gbc, row);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addBtn = new JButton("Ajouter Médicament");
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.setPreferredSize(new Dimension(160, 35));
        addBtn.addActionListener(e -> handleAddMedicine());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.setPreferredSize(new Dimension(140, 35));
        clearBtn.addActionListener(e -> clearForm());

        buttonPanel.add(addBtn);
        buttonPanel.add(clearBtn);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private int addSupplierSuggestionField(JPanel panel, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel supplierLabel = new JLabel("Fournisseur :");
        supplierLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(supplierLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        supplierField = new JTextField(20);
        supplierField.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(supplierField, gbc);
        
        // Create suggestion list
        suggestionModel = new DefaultListModel<>();
        supplierSuggestionList = new JList<>(suggestionModel);
        supplierSuggestionList.setFont(new Font("Arial", Font.PLAIN, 10));
        supplierSuggestionList.setVisibleRowCount(4);
        supplierSuggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane suggestionScroll = new JScrollPane(supplierSuggestionList);
        suggestionScroll.setPreferredSize(new Dimension(250, 80));
        
        // Add to panel below the text field
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.1;
        panel.add(suggestionScroll, gbc);
        gbc.weighty = 0;
        
        // Add document listener for real-time suggestions
        supplierField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            private void updateSuggestions() {
                String text = supplierField.getText().trim();
                suggestionModel.clear();
                
                if (!text.isEmpty()) {
                    List<Supplier> suppliers = supplierDao.getAllSuppliers();
                    if (suppliers != null) {
                        for (Supplier s : suppliers) {
                            if (s.getSociete() != null && s.getSociete().toLowerCase().contains(text.toLowerCase())) {
                                suggestionModel.addElement(s.getSociete());
                            }
                        }
                    }
                    suggestionScroll.setVisible(!suggestionModel.isEmpty());
                } else {
                    suggestionScroll.setVisible(false);
                    selectedSupplier = null;
                }
                
                // Revalidate to ensure layout updates
                panel.revalidate();
                panel.repaint();
            }
        });
        
        // Add list selection listener
        supplierSuggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = supplierSuggestionList.getSelectedValue();
                if (selected != null) {
                    supplierField.setText(selected);
                    // Get the supplier object
                    List<Supplier> suppliers = supplierDao.getAllSuppliers();
                    if (suppliers != null) {
                        for (Supplier s : suppliers) {
                            if (s.getSociete() != null && s.getSociete().equals(selected)) {
                                selectedSupplier = s;
                                break;
                            }
                        }
                    }
                    suggestionScroll.setVisible(false);
                    panel.revalidate();
                }
            }
        });
        
        // Initially hide the suggestions
        suggestionScroll.setVisible(false);

        return row;
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

    private void handleAddMedicine() {
        try {
            String codeBarre = codeBarreField.getText().trim();
            String nom = nomField.getText().trim();
            double prixAchat = Double.parseDouble(prixAchatField.getText().trim());
            double prixVente = Double.parseDouble(prixVenteField.getText().trim());
            double tauxTVA = Double.parseDouble(tauxTVAField.getText().trim());
            String dosage = dosageField.getText().trim();
            int quantite = Integer.parseInt(quantiteField.getText().trim());
            int seuil = Integer.parseInt(seuilField.getText().trim());
            String formePharmaceutique = formePharmaceutiqueField.getText().trim();
            String emplacement = emplacementField.getText().trim();
            boolean necessitePrescription = prescriptionCheckBox.isSelected();

            if (selectedSupplier == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fournisseur !");
                return;
            }
            try {
            boolean success = medicineDao.addMedicine(codeBarre, nom, prixAchat, prixVente, tauxTVA, 
                        dosage, quantite, seuil, formePharmaceutique, 
                        emplacement, necessitePrescription, selectedSupplier);
            	
                JOptionPane.showMessageDialog(this, "medicament ajouté avec succès !");
                clearForm();
            	} 
            catch(DataMissingException e)
            {
            	JOptionPane.showMessageDialog(this, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
            catch(CodeBarreExistsException e)
            {
            	JOptionPane.showMessageDialog(this, "Medicament existe deja !"," Medicine Error",JOptionPane.ERROR_MESSAGE);}
            	
       } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs correctement !");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
        
    }

    private void clearForm() {
        codeBarreField.setText("");
        nomField.setText("");
        prixAchatField.setText("");
        prixVenteField.setText("");
        tauxTVAField.setText("");
        dosageField.setText("");
        quantiteField.setText("");
        seuilField.setText("");
        formePharmaceutiqueField.setText("");
        emplacementField.setText("");
        prescriptionCheckBox.setSelected(false);
        supplierField.setText("");
        selectedSupplier = null;
        suggestionModel.clear();
    }
}
