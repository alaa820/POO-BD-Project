package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import dao.MedicineDao;
import dao.SupplierDao;
import model.Medicine;
import model.Supplier;

/**
 * Panel to modify an existing medicine
 */
public class ModifyMedicinePanel extends JPanel {
    
    private MedicineDao medicineDao;
    private SupplierDao supplierDao;
    private MedicinePanel parentPanel;
    
    // Search components
    private JTextField searchField;
    private JList<String> suggestionList;
    private DefaultListModel<String> suggestionModel;
    
    // Form fields
    private JTextField codeBarreField;
    private JTextField nomField;
    private JTextField prixAchatField;
    private JTextField prixVenteField;
    private JTextField tauxTVAField;
    private JTextField dosageField;
    private JTextField quantiteField;
    private JTextField seuilField;
    private JTextField formeField;
    private JTextField emplacementField;
    private JCheckBox prescriptionCheckBox;
    private JComboBox<String> supplierCombo;
    
    private Medicine selectedMedicine;
    
    public ModifyMedicinePanel(MedicinePanel parent) {
        super(new BorderLayout(10, 10));
        this.parentPanel = parent;
        this.medicineDao = new MedicineDao();
        this.supplierDao = new SupplierDao();
        
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);
        
        // Create main container
        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBackground(Color.WHITE);
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        mainContainer.add(searchPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        mainContainer.add(formPanel, BorderLayout.CENTER);
        
        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        // Initially disable form
        setFormEnabled(false);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Rechercher Médicament"));
        panel.setBackground(Color.WHITE);
        
        // Search field
        JPanel searchTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchTop.setBackground(Color.WHITE);
        
        JLabel label = new JLabel("Nom du médicament:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchTop.add(label);
        
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchTop.add(searchField);
        
        panel.add(searchTop, BorderLayout.NORTH);
        
        // Suggestion list
        suggestionModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionModel);
        suggestionList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setVisibleRowCount(5);
        
        JScrollPane listScroll = new JScrollPane(suggestionList);
        listScroll.setPreferredSize(new Dimension(400, 100));
        panel.add(listScroll, BorderLayout.CENTER);
        
        // Add listeners
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateSuggestions();
            }
        });
        
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelectedMedicine();
                }
            }
        });
        
        JButton loadBtn = new JButton("Charger");
        loadBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loadBtn.addActionListener(e -> loadSelectedMedicine());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(loadBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Informations du Médicament"));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Code Barre
        addFormField(panel, gbc, row++, "Code Barre:", codeBarreField = new JTextField(20));
        codeBarreField.setEditable(false);
        codeBarreField.setBackground(new Color(240, 240, 240));
        
        // Nom
        addFormField(panel, gbc, row++, "Nom:", nomField = new JTextField(20));
        
        // Prix Achat
        addFormField(panel, gbc, row++, "Prix Achat:", prixAchatField = new JTextField(20));
        
        // Prix Vente
        addFormField(panel, gbc, row++, "Prix Vente:", prixVenteField = new JTextField(20));
        
        // Taux TVA
        addFormField(panel, gbc, row++, "Taux TVA (%):", tauxTVAField = new JTextField(20));
        
        // Dosage
        addFormField(panel, gbc, row++, "Dosage:", dosageField = new JTextField(20));
        
        // Quantité
        addFormField(panel, gbc, row++, "Quantité:", quantiteField = new JTextField(20));
        
        // Seuil
        addFormField(panel, gbc, row++, "Seuil:", seuilField = new JTextField(20));
        
        // Forme Pharmaceutique
        addFormField(panel, gbc, row++, "Forme Pharmaceutique:", formeField = new JTextField(20));
        
        // Emplacement
        addFormField(panel, gbc, row++, "Emplacement:", emplacementField = new JTextField(20));
        
        // Prescription
        gbc.gridx = 0;
        gbc.gridy = row++;
        JLabel prescLabel = new JLabel("Nécessite Prescription:");
        prescLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(prescLabel, gbc);
        
        gbc.gridx = 1;
        prescriptionCheckBox = new JCheckBox();
        panel.add(prescriptionCheckBox, gbc);
        
        // Supplier
        gbc.gridx = 0;
        gbc.gridy = row++;
        JLabel supplierLabel = new JLabel("Fournisseur:");
        supplierLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(supplierLabel, gbc);
        
        gbc.gridx = 1;
        supplierCombo = new JComboBox<>();
        loadSuppliers();
        panel.add(supplierCombo, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton updateBtn = new JButton("Confirmer Modifications");
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updateBtn.setPreferredSize(new Dimension(180, 35));
        updateBtn.addActionListener(e -> handleUpdate());
        
        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cancelBtn.setPreferredSize(new Dimension(120, 35));
        cancelBtn.addActionListener(e -> clearForm());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(field, gbc);
    }
    
    private void updateSuggestions() {
        String query = searchField.getText().trim();
        suggestionModel.clear();
        
        if (query.isEmpty()) {
            return;
        }
        
        List<Medicine> medicines = medicineDao.searchMedicines(null,query,null );
        for (Medicine med : medicines) {
            suggestionModel.addElement(med.getNom() + " (" + med.getCodeBarre() + ")");
        }
    }
    
    private void loadSelectedMedicine() {
        String selected = suggestionList.getSelectedValue();
        if (selected == null || selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un médicament", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Extract code barre from selection
        String codeBarre = selected.substring(selected.lastIndexOf("(") + 1, selected.lastIndexOf(")"));
        
        selectedMedicine = medicineDao.getMedecineByCode(codeBarre);
        if (selectedMedicine == null) {
            JOptionPane.showMessageDialog(this, 
                "Médicament introuvable", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Populate form
        codeBarreField.setText(selectedMedicine.getCodeBarre());
        nomField.setText(selectedMedicine.getNom());
        prixAchatField.setText(String.valueOf(selectedMedicine.getPrixAchat()));
        prixVenteField.setText(String.valueOf(selectedMedicine.getPrixVente()));
        tauxTVAField.setText(String.valueOf(selectedMedicine.getTauxTVA()));
        dosageField.setText(selectedMedicine.getDosage());
        quantiteField.setText(String.valueOf(selectedMedicine.getQuantite()));
        seuilField.setText(String.valueOf(selectedMedicine.getSeuil()));
        formeField.setText(selectedMedicine.getFormePharmaceutique());
        emplacementField.setText(selectedMedicine.getEmplacement());
        prescriptionCheckBox.setSelected(selectedMedicine.isNecessitePrescription());
        
        // Set supplier
        Supplier supplier = selectedMedicine.getSupplier();
        if (supplier != null) {
            for (int i = 0; i < supplierCombo.getItemCount(); i++) {
                if (supplierCombo.getItemAt(i).startsWith(supplier.getSociete())) {
                    supplierCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        setFormEnabled(true);
    }
    
    private void loadSuppliers() {
        supplierCombo.removeAllItems();
        List<Supplier> suppliers = supplierDao.getAllSuppliers();
        for (Supplier s : suppliers) {
            supplierCombo.addItem(s.getSociete() + " (" + s.getIdFournisseur() + ")");
        }
    }
    
    private void handleUpdate() {
        if (selectedMedicine == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez d'abord charger un médicament", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Get updated values
            String nom = nomField.getText().trim();
            double prixAchat = Double.parseDouble(prixAchatField.getText().trim());
            double prixVente = Double.parseDouble(prixVenteField.getText().trim());
            double tauxTVA = Double.parseDouble(tauxTVAField.getText().trim());
            String dosage = dosageField.getText().trim();
            int quantite = Integer.parseInt(quantiteField.getText().trim());
            int seuil = Integer.parseInt(seuilField.getText().trim());
            String forme = formeField.getText().trim();
            String emplacement = emplacementField.getText().trim();
            boolean prescription = prescriptionCheckBox.isSelected();
                        // Get supplier ID
            String supplierStr = (String) supplierCombo.getSelectedItem();
            int idFournisseur = Integer.parseInt(
                supplierStr.substring(supplierStr.lastIndexOf("(") + 1, supplierStr.lastIndexOf(")"))
            );
            Supplier supplier = supplierDao.getSupplierById(idFournisseur);
            Medicine medChanged = new Medicine( selectedMedicine.getCodeBarre(), nom, prixAchat, prixVente, 
					   tauxTVA, dosage, quantite, seuil, forme, 
					   emplacement, prescription, supplier);
			
            // Update medicine
           
            boolean success = medicineDao.updateMedicine(medChanged);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Médicament modifié avec succès!", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                if (parentPanel != null) {
                    parentPanel.refreshAndShowAll();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la modification", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez entrer des valeurs valides pour les champs numériques", 
                "Erreur de Format", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        searchField.setText("");
        suggestionModel.clear();
        codeBarreField.setText("");
        nomField.setText("");
        prixAchatField.setText("");
        prixVenteField.setText("");
        tauxTVAField.setText("");
        dosageField.setText("");
        quantiteField.setText("");
        seuilField.setText("");
        formeField.setText("");
        emplacementField.setText("");
        prescriptionCheckBox.setSelected(false);
        supplierCombo.setSelectedIndex(0);
        selectedMedicine = null;
        setFormEnabled(false);
        searchField.requestFocus();
    }
    
    private void setFormEnabled(boolean enabled) {
        nomField.setEditable(enabled);
        prixAchatField.setEditable(enabled);
        prixVenteField.setEditable(enabled);
        tauxTVAField.setEditable(enabled);
        dosageField.setEditable(enabled);
        quantiteField.setEditable(enabled);
        seuilField.setEditable(enabled);
        formeField.setEditable(enabled);
        emplacementField.setEditable(enabled);
        prescriptionCheckBox.setEnabled(enabled);
        supplierCombo.setEnabled(enabled);
    }
}