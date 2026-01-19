package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Medicine;
import model.Supplier;
import model.SaleItem;
import util.DatabaseConnection;
import dao.SupplierDao;
import dao.MedicineDao;
import dao.CommandDao;

public class CommandPanel extends JPanel {

    // Top navigation
    private JButton btnCreateCommande;
    private JButton btnCommandesEnAttente;
    private JButton btnCommandesRecues;

    private JPanel mainCardPanel;
    private CardLayout mainCardLayout;

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
    private List<SaleItem> cartItems;

    private SupplierDao supplierDao;
    private MedicineDao medicineDao;
    private CommandDao commandDao;

    // Pending/Received commandes
    private DefaultTableModel pendingTableModel;
    private JTable pendingTable;
    private JButton btnReceptionner;

    private DefaultTableModel receivedTableModel;
    private JTable receivedTable;

    public CommandPanel() {
        super(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        supplierDao = new SupplierDao();
        medicineDao = new MedicineDao();
        commandDao = new CommandDao();
        fournisseurMap = new HashMap<>();
        medicineCodeMap = new HashMap<>();
        cartItems = new ArrayList<>();

        createTopNav();
        createMainCards();

        add(createTopPanel(), BorderLayout.NORTH);
        add(mainCardPanel, BorderLayout.CENTER);

        loadFournisseurs();
        loadPendingCommandes();
        loadReceivedCommandes();
    }

    private void createTopNav() {
        btnCreateCommande = new JButton("Créer Commande");
        btnCommandesEnAttente = new JButton("Commandes en attente");
        btnCommandesRecues = new JButton("Commandes reçues");

        btnCreateCommande.addActionListener(e -> showCard("create"));
        btnCommandesEnAttente.addActionListener(e -> { showCard("pending"); loadPendingCommandes(); });
        btnCommandesRecues.addActionListener(e -> { showCard("received"); loadReceivedCommandes(); });
    }

    private JPanel createTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT,6,6));
        nav.add(btnCreateCommande);
        nav.add(btnCommandesEnAttente);
        nav.add(btnCommandesRecues);
        top.add(nav, BorderLayout.WEST);
        return top;
    }

    private void createMainCards() {
        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);

        JPanel createPanel = new JPanel(new BorderLayout());
        createPanel.add(createInputPanel(), BorderLayout.NORTH);
        createPanel.add(createCartPanel(), BorderLayout.CENTER);

        JPanel pendingPanel = createPendingPanel();
        JPanel receivedPanel = createReceivedPanel();

        mainCardPanel.add(createPanel, "create");
        mainCardPanel.add(pendingPanel, "pending");
        mainCardPanel.add(receivedPanel, "received");

        mainCardLayout.show(mainCardPanel, "create");
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

    private JPanel createPendingPanel() {
        JPanel panel = new JPanel(new BorderLayout(6,6));
        panel.setBorder(BorderFactory.createTitledBorder("Commandes en attente"));

        // Search toolbar for pending commandes
        JPanel searchTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        JLabel lblSearchPending = new JLabel("Rechercher:");
        JTextField txtSearchPending = new JTextField(20);
        JButton btnSearchPending = new JButton("Rechercher");
        JButton btnClearSearchPending = new JButton("Effacer");
        searchTop.add(lblSearchPending);
        searchTop.add(txtSearchPending);
        searchTop.add(btnSearchPending);
        searchTop.add(btnClearSearchPending);
        panel.add(searchTop, BorderLayout.NORTH);

        // Search actions
        btnSearchPending.addActionListener(e -> searchPending(txtSearchPending.getText().trim()));
        btnClearSearchPending.addActionListener(e -> { txtSearchPending.setText(""); loadPendingCommandes(); });
        txtSearchPending.addActionListener(e -> searchPending(txtSearchPending.getText().trim()));

         String[] cols = {"id_commande","date_commande","date_reception","statut","prix","id_fournisseur"};
         pendingTableModel = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;} };
         pendingTable = new JTable(pendingTableModel);
         panel.add(new JScrollPane(pendingTable), BorderLayout.CENTER);

         JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
         btnReceptionner = new JButton("Réceptionner");
         btnReceptionner.addActionListener(e -> receptionnerSelectedCommande());
         bottom.add(btnReceptionner);
         
         JButton btnAnnulerCommande = new JButton("Annuler");
         btnAnnulerCommande.addActionListener(e -> {
             int sel = pendingTable.getSelectedRow();
             if (sel < 0) { JOptionPane.showMessageDialog(this, "Veuillez selectionner une commande a annuler", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
             int modelRow = pendingTable.convertRowIndexToModel(sel);
             int idCommande = (Integer) pendingTableModel.getValueAt(modelRow, 0);
             int confirm = JOptionPane.showConfirmDialog(this, "Confirmer l'annulation de la commande #" + idCommande + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
             if (confirm != JOptionPane.YES_OPTION) return;
             boolean res = commandDao.cancelCommande(idCommande);
             if (res) {
                 JOptionPane.showMessageDialog(this, "Commande annulée", "Info", JOptionPane.INFORMATION_MESSAGE);
                 loadPendingCommandes();
                 loadReceivedCommandes();
             } else {
                 JOptionPane.showMessageDialog(this, "Impossible d'annuler la commande (elle peut etre deja reçue)", "Erreur", JOptionPane.ERROR_MESSAGE);
             }
         });
         bottom.add(btnAnnulerCommande);
         
         JButton btnSupprimerCommande = new JButton("Supprimer");
         btnSupprimerCommande.addActionListener(e -> {
             int sel = pendingTable.getSelectedRow();
             if (sel < 0) { JOptionPane.showMessageDialog(this, "Veuillez selectionner une commande a supprimer", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
             int modelRow = pendingTable.convertRowIndexToModel(sel);
             int idCommande = (Integer) pendingTableModel.getValueAt(modelRow, 0);
             int confirm = JOptionPane.showConfirmDialog(this, "Confirmer la suppression de la commande #" + idCommande + " ?\nCeci supprimera aussi les produits associés.", "Confirmation", JOptionPane.YES_NO_OPTION);
             if (confirm != JOptionPane.YES_OPTION) return;
             boolean res = commandDao.deleteCommande(idCommande);
             if (res) {
                 JOptionPane.showMessageDialog(this, "Commande supprimée", "Info", JOptionPane.INFORMATION_MESSAGE);
                 loadPendingCommandes();
                 loadReceivedCommandes();
             } else {
                 JOptionPane.showMessageDialog(this, "Impossible de supprimer la commande (elle peut etre deja reçue)", "Erreur", JOptionPane.ERROR_MESSAGE);
             }
         });
         bottom.add(btnSupprimerCommande);
         panel.add(bottom, BorderLayout.SOUTH);
         return panel;
    }

    private JPanel createReceivedPanel() {
        JPanel panel = new JPanel(new BorderLayout(6,6));
        panel.setBorder(BorderFactory.createTitledBorder("Commandes reçues"));

        // Search toolbar for received commandes
        JPanel searchTopR = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        JLabel lblSearchReceived = new JLabel("Rechercher:");
        JTextField txtSearchReceived = new JTextField(20);
        JButton btnSearchReceived = new JButton("Rechercher");
        JButton btnClearSearchReceived = new JButton("Effacer");
        searchTopR.add(lblSearchReceived);
        searchTopR.add(txtSearchReceived);
        searchTopR.add(btnSearchReceived);
        searchTopR.add(btnClearSearchReceived);
        panel.add(searchTopR, BorderLayout.NORTH);

        // Search actions
        btnSearchReceived.addActionListener(e -> searchReceived(txtSearchReceived.getText().trim()));
        btnClearSearchReceived.addActionListener(e -> { txtSearchReceived.setText(""); loadReceivedCommandes(); });
        txtSearchReceived.addActionListener(e -> searchReceived(txtSearchReceived.getText().trim()));

         String[] cols = {"id_commande","date_commande","date_reception","statut","prix","id_fournisseur"};
         receivedTableModel = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;} };
         receivedTable = new JTable(receivedTableModel);
         panel.add(new JScrollPane(receivedTable), BorderLayout.CENTER);

         JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
         JButton btnSupprimerRecue = new JButton("Supprimer");
         btnSupprimerRecue.addActionListener(e -> {
             int sel = receivedTable.getSelectedRow();
             if (sel < 0) { JOptionPane.showMessageDialog(this, "Veuillez selectionner une commande à supprimer", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
             int modelRow = receivedTable.convertRowIndexToModel(sel);
             int idCommande = (Integer) receivedTableModel.getValueAt(modelRow, 0);
             int confirm = JOptionPane.showConfirmDialog(this, "Confirmer la suppression de la commande reçue #" + idCommande + " ?\nCela retournera le stock et supprimera la commande.", "Confirmation", JOptionPane.YES_NO_OPTION);
             if (confirm != JOptionPane.YES_OPTION) return;
             boolean res = commandDao.deleteReceivedCommande(idCommande);
             if (res) {
                 JOptionPane.showMessageDialog(this, "Commande reçue supprimée", "Info", JOptionPane.INFORMATION_MESSAGE);
                 loadPendingCommandes();
                 loadReceivedCommandes();
             } else {
                 JOptionPane.showMessageDialog(this, "Impossible de supprimer la commande reçue", "Erreur", JOptionPane.ERROR_MESSAGE);
             }
         });
         bottom.add(btnSupprimerRecue);
         panel.add(bottom, BorderLayout.SOUTH);
         return panel;
     }

    // Helper to search pending commandes and populate the table
    private void searchPending(String q) {
        pendingTableModel.setRowCount(0);
        if (q == null) q = "";
        List<Map<String,Object>> rows = commandDao.searchPendingCommandes(q);
        for (Map<String,Object> m : rows) {
            pendingTableModel.addRow(new Object[]{m.get("id_commande"), m.get("date_commande"), m.get("date_reception"), m.get("statut"), m.get("prix"), m.get("id_fournisseur")});
        }
    }

    // Helper to search received commandes
    private void searchReceived(String q) {
        receivedTableModel.setRowCount(0);
        if (q == null) q = "";
        List<Map<String,Object>> rows = commandDao.searchReceivedCommandes(q);
        for (Map<String,Object> m : rows) {
            receivedTableModel.addRow(new Object[]{m.get("id_commande"), m.get("date_commande"), m.get("date_reception"), m.get("statut"), m.get("prix"), m.get("id_fournisseur")});
        }
    }

    private void showCard(String name) {
        mainCardLayout.show(mainCardPanel, name);
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
        fournisseurCombo.setSelectedIndex(-1);
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
        for (SaleItem it : cartItems) {
            if (it.getMedicine().getCodeBarre().equals(med.getCodeBarre())) {
                it.setQuantity(it.getQuantity() + quantity);
                already = true;
                break;
            }
        }
        if (!already) cartItems.add(new SaleItem(med, quantity));
        refreshCartTable();
        quantitySpinner.setValue(1);
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0.0;
        for (SaleItem it : cartItems) {
            double unit = it.getMedicine().getPrixAchat();
            double line = unit * it.getQuantity();
            cartTableModel.addRow(new Object[] { it.getMedicine().getNom(), it.getQuantity(), String.format("%.2f", unit), String.format("%.2f", line) });
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

        // Compute total price from cart items (sum of prix_achat * quantity)
        double totalPrix = 0.0;
        for (SaleItem it : cartItems) {
            totalPrix += it.getQuantity() * it.getMedicine().getPrixAchat();
        }

        // Create commande via DAO (store prix in commande), then add produits via DAO
        int idCommande = commandDao.createCommande(idFournisseur, totalPrix);
        if (idCommande <= 0) {
            JOptionPane.showMessageDialog(this, "Impossible de creer la commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean ok = commandDao.addCommandeProduits(idCommande, cartItems);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'insertion des produits de la commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ensure the stored commande.prix is consistent with the inserted products
        boolean updatedPrix = commandDao.updateCommandePrix(idCommande);
        if (!updatedPrix) {
            // Not fatal, but warn the user
            System.out.println("Warning: failed to update commande.prix for id " + idCommande);
            JOptionPane.showMessageDialog(this, "Attention: impossible de mettre a jour le prix de la commande dans la base.", "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
        
        JOptionPane.showMessageDialog(this, "Commande enregistre avec succes", "Succes", JOptionPane.INFORMATION_MESSAGE);
        cartItems.clear();
        refreshCartTable();
        loadPendingCommandes();
        loadReceivedCommandes();
    }

    private void loadPendingCommandes() {
        pendingTableModel.setRowCount(0);
        List<Map<String,Object>> rows = commandDao.getPendingCommandes();
        for (Map<String,Object> m : rows) {
            pendingTableModel.addRow(new Object[]{m.get("id_commande"), m.get("date_commande"), m.get("date_reception"), m.get("statut"), m.get("prix"), m.get("id_fournisseur")});
        }
    }

    private void loadReceivedCommandes() {
        receivedTableModel.setRowCount(0);
        List<Map<String,Object>> rows = commandDao.getReceivedCommandes();
        for (Map<String,Object> m : rows) {
            receivedTableModel.addRow(new Object[]{m.get("id_commande"), m.get("date_commande"), m.get("date_reception"), m.get("statut"), m.get("prix"), m.get("id_fournisseur")});
        }
    }

    private void receptionnerSelectedCommande() {
        int sel = pendingTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner une commande a receptionner", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int modelRow = pendingTable.convertRowIndexToModel(sel);
        int idCommande = (Integer) pendingTableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Confirmer la reception de la commande #" + idCommande + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = commandDao.receptionnerCommande(idCommande);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Commande receptionnee et stock mis a jour", "Succes", JOptionPane.INFORMATION_MESSAGE);
            loadPendingCommandes();
            loadReceivedCommandes();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de la reception", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Getter for tests
    public List<SaleItem> getCartItems() { return cartItems; }}