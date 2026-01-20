package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Medicine;
import model.Supplier;
import model.SaleItem;
import model.Command;
import model.CommandItem;
import util.DatabaseConnection;
import dao.SupplierDao;
import dao.MedicineDao;
import dao.CommandDao;
import dao.CommandItemDao;

public class CommandPanel extends JPanel {

    // Create order UI
    private JComboBox<String> fournisseurCombo;
    private Map<String, Integer> fournisseurMap;

    private JComboBox<String> medicineCombo;
    private Map<String, String> medicineCodeMap;

    private JSpinner quantitySpinner;
    private JButton btnClearSelection;

    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    private JLabel totalLabel;
    private List<CommandItem> cartItems;

    private SupplierDao supplierDao;
    private MedicineDao medicineDao;
    private CommandDao commandDao;
    private CommandItemDao commandItemDao  = new CommandItemDao();;
    

    public CommandPanel() {
        super(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        supplierDao = new SupplierDao();
        medicineDao = new MedicineDao();
        commandDao = new CommandDao();
        fournisseurMap = new HashMap<>();
        medicineCodeMap = new HashMap<>();
        cartItems = new ArrayList<>();

        // Build UI: input panel on top, cart panel center
        add(createInputPanel(), BorderLayout.NORTH);
        add(createCartPanel(), BorderLayout.CENTER);

        loadFournisseurs();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Créer Commande"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Fournisseur:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        fournisseurCombo = new JComboBox<>();
        fournisseurCombo.setPreferredSize(new Dimension(300,24));
        panel.add(fournisseurCombo, gbc);
        fournisseurCombo.addActionListener(e -> onFournisseurSelected());

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Medicament:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        medicineCombo = new JComboBox<>();
        medicineCombo.setPreferredSize(new Dimension(300,24));
        panel.add(medicineCombo, gbc);

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Quantite:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1,1,Integer.MAX_VALUE,1));
        panel.add(quantitySpinner, gbc);

        gbc.gridx = 2; gbc.gridy = row;
        JButton addBtn = new JButton("Ajouter au Panier");
        addBtn.addActionListener(e -> handleAddToCart());
        panel.add(addBtn, gbc);

        gbc.gridx = 3; gbc.gridy = row++;
        btnClearSelection = new JButton("Annuler");
        btnClearSelection.addActionListener(e -> clearSelection());
        panel.add(btnClearSelection, gbc);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(6,6));
        panel.setBorder(BorderFactory.createTitledBorder("Panier"));

        String[] cols = {"Medicament","Quantite","Prix Unitaire","Total"};
        cartTableModel = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;} };
        cartTable = new JTable(cartTableModel);
        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: 0.00 DZD");
        bottom.add(totalLabel, BorderLayout.WEST);

        JPanel right = new JPanel();
        JButton validate = new JButton("Valider Commande");
        validate.addActionListener(e -> handleValidateCommande());
        right.add(validate);
        bottom.add(right, BorderLayout.EAST);

        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void loadFournisseurs() {
        fournisseurCombo.removeAllItems();
        fournisseurMap.clear();
        List<Supplier> suppliers = supplierDao.getAllSuppliers();
        for (Supplier s : suppliers) {
            String societe = s.getSociete();
            if (societe == null) continue;
            fournisseurMap.put(societe, s.getIdFournisseur());
            fournisseurCombo.addItem(societe);
        }
        // start with no selection
        fournisseurCombo.setSelectedIndex(-1);
    }

    private void onFournisseurSelected() {
        String societe = (String) fournisseurCombo.getSelectedItem();
        medicineCombo.removeAllItems();
        medicineCodeMap.clear();
        if (societe == null) return;
        Integer idFournisseur = fournisseurMap.get(societe);
        if (idFournisseur == null) return;

        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT nom, code_barre FROM medicament WHERE id_fournisseur = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idFournisseur);
            ResultSet rs = ps.executeQuery();
            int found = 0;
            while (rs.next()) {
                String nom = rs.getString("nom");
                String codeBarre = rs.getString("code_barre");
                medicineCombo.addItem(nom);
                medicineCodeMap.put(nom, codeBarre);
                found++;
            }
            if (found > 0) medicineCombo.setSelectedIndex(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur en chargeant medicaments: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSelection() {
        try { fournisseurCombo.setSelectedIndex(-1); } catch (Exception ignored) {}
        medicineCombo.removeAllItems();
        medicineCodeMap.clear();
        quantitySpinner.setValue(1);
        cartItems.clear();
        refreshCartTable();
    }

    private void handleAddToCart() {
        String medName = (String) medicineCombo.getSelectedItem();
        if (medName == null) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner un medicament", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String codeBarre = medicineCodeMap.get(medName);
        if (codeBarre == null) {
            JOptionPane.showMessageDialog(this, "Code barre introuvable pour le medicament", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Medicine med = medicineDao.getMedecineByCode(codeBarre);
        if (med == null) {
            JOptionPane.showMessageDialog(this, "Medicament introuvable en base", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity = (Integer) quantitySpinner.getValue();
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantite doit etre > 0", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean already = false;
        for (CommandItem it : cartItems) {
            if (it.getM().getCodeBarre().equals(med.getCodeBarre())) {
                it.setQuantity(it.getQuantity() + quantity);
                already = true;
                break;
            }
        }
        if (!already) cartItems.add(new CommandItem(med, quantity));
        refreshCartTable();
        quantitySpinner.setValue(1);
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0.0;
        for (CommandItem it : cartItems) {
        	double line = it.getQuantity() * it.getM().getPrixAchat();
            cartTableModel.addRow(new Object[] { it.getM().getNom(), it.getQuantity(), String.format("%.2f", it.getM().getPrixAchat()), String.format("%.2f", line) });
            total += line;
        }
        totalLabel.setText("Total: " + String.format("%.2f", total) + " DZD");
    }

    private void handleValidateCommande() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le panier est vide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String societe = (String) fournisseurCombo.getSelectedItem();
        if (societe == null) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner un fournisseur", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer idFournisseur = fournisseurMap.get(societe);
        if (idFournisseur == null) {
            JOptionPane.showMessageDialog(this, "Fournisseur invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalPrix = 0.0;
        for (CommandItem it : cartItems) {
            totalPrix += it.getQuantity() * it.getM().getPrixAchat();
        }

        // build Command model and create via DAO
        Command cmd = new Command(null, null, "en attente", totalPrix, new SupplierDao().getSupplierById(idFournisseur));
        int idCommande = commandDao.createCommande(cmd);
        if (idCommande <= 0) {
            JOptionPane.showMessageDialog(this, "Impossible de creer la commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
		cmd.setIdCommande(idCommande);
        // insert each produit individually
        boolean failed = false;
        for (CommandItem it : cartItems) {
            it.setC(cmd);
            try {
                commandItemDao.addCommandItem(it);
            } catch (Exception ex) {
                ex.printStackTrace();
                failed = true;
                break;
            }
        }
        if (failed) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'insertion des produits de la commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Commande enregistre avec succes", "Succes", JOptionPane.INFORMATION_MESSAGE);
        cartItems.clear();
        refreshCartTable();
    }

    // Getter for tests
    public List<CommandItem> getCartItems() { return cartItems; }
}