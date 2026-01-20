package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import util.DatabaseConnection;
import dao.CommandDao;
import dao.MedicineDao;
import dao.SupplierDao;
import dao.CommandItemDao;
import model.Command;

import model.Medicine;
import model.CommandItem;
import model.Supplier;

public class ViewCommandPanel extends JPanel {
    private JTable commandsTable;
    private DefaultTableModel commandsTableModel;

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private boolean showingPending = true;
    private CommandDao commandDao;
    private MedicineDao medicineDao;
    private CommandItemDao commandItemDao;

    // Search UI
    private JComboBox<String> searchCombo;
    private JTextField searchField;
    private JButton searchButton;

    // Action buttons (promoted to fields so selection listener and handlers can access them)
    private JButton markReceivedBtn;
    private JButton cancelBtn;
    private JButton deleteBtn;

    // Command Products UI
    private JTable commandProductsTable;
    private DefaultTableModel commandProductsTableModel;
    private JTextField searchFournisseurField;
    private JTextField searchMedicamentField;
    private JButton searchProductsButton;

    public ViewCommandPanel() {
        commandDao = new CommandDao();
        medicineDao = new MedicineDao();
        commandItemDao = new CommandItemDao();

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Top buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.NORTH);

        // Content with card layout (commands and products)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel commandsPanel = createCommandsPanel();
        JPanel productsPanel = createCommandProductsPanel();
        contentPanel.add(commandsPanel, "COMMANDS");
        contentPanel.add(productsPanel, "PRODUCTS");

        add(contentPanel, BorderLayout.CENTER);

        // show default and load pending commandes
        cardLayout.show(contentPanel, "COMMANDS");
        loadCommands(true);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("View Options"));

        JButton btnPending = new JButton("Commandes en attente");
        btnPending.setPreferredSize(new Dimension(180, 35));
        btnPending.addActionListener(e -> {
            showingPending = true;
            loadCommands(true);
            cardLayout.show(contentPanel, "COMMANDS");
        });

        JButton btnReceived = new JButton("Commandes reçues");
        btnReceived.setPreferredSize(new Dimension(180, 35));
        btnReceived.addActionListener(e -> {
            showingPending = false;
            loadCommands(false);
            cardLayout.show(contentPanel, "COMMANDS");
        });

        JButton btnViewProducts = new JButton("Voir Produits Commandés");
        btnViewProducts.setPreferredSize(new Dimension(200, 35));
        btnViewProducts.addActionListener(e -> {
            cardLayout.show(contentPanel, "PRODUCTS");
            loadAllCommandProducts();
        });

        panel.add(btnPending);
        panel.add(btnReceived);
        panel.add(btnViewProducts);
        return panel;
    }

    private JPanel createCommandsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(BorderFactory.createTitledBorder("Commandes"));

        // Search toolbar (above the table)
        JPanel searchTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        searchCombo = new JComboBox<>(new String[] {"Societe Fournisseur"});
        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");
        searchTop.add(searchCombo);
        searchTop.add(searchField);
        searchTop.add(searchButton);
        panel.add(searchTop, BorderLayout.NORTH);

        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        String[] columns = {"ID Commande","Fournisseur","Date Commande","Date Réception","Statut","Prix"};
        commandsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        commandsTable = new JTable(commandsTableModel);
        // set some preferred widths
        if (commandsTable.getColumnModel().getColumnCount() >= 6) {
            commandsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            commandsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
            commandsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
            commandsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
            commandsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
            commandsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        panel.add(new JScrollPane(commandsTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Annuler Commande: only for statut = "en attente"
        cancelBtn = new JButton("Annuler Commande");
        cancelBtn.addActionListener(e -> cancelSelectedCommande());
        bottom.add(cancelBtn);

        // Supprimer Commande: only works if statut != 'reçue'
        deleteBtn = new JButton("Supprimer Commande");
        deleteBtn.addActionListener(e -> deleteSelectedCommande());
        bottom.add(deleteBtn);

        // Mark as received
        markReceivedBtn = new JButton("Marquer comme reçue");
        markReceivedBtn.addActionListener(e -> markSelectedAsReceived());
        bottom.add(markReceivedBtn);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadCommands(showingPending));
        bottom.add(refresh);
        panel.add(bottom, BorderLayout.SOUTH);

        // Enable/disable action buttons based on selection & statut
        commandsTable.getSelectionModel().addListSelectionListener(e -> {
            int sel = commandsTable.getSelectedRow();
            if (sel < 0) {
                markReceivedBtn.setEnabled(false);
                cancelBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                return;
            }
            int modelRow = commandsTable.convertRowIndexToModel(sel);
            Object statutObj = commandsTableModel.getValueAt(modelRow, 4);
            String statut = statutObj == null ? "" : statutObj.toString();
            boolean isEnAttente = "en attente".equalsIgnoreCase(statut);

            // Annuler works only when en attente
            cancelBtn.setEnabled(isEnAttente);
            // Supprimer: enable for any selected row (including received)
            deleteBtn.setEnabled(true);
            // Mark received works only when en attente
            markReceivedBtn.setEnabled(isEnAttente);
        });

        markReceivedBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        return panel;
    }

    private JPanel createCommandProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(BorderFactory.createTitledBorder("Produits Commandés"));

        // Search toolbar with two search fields
        JPanel searchTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        searchTop.add(new JLabel("Fournisseur:"));
        searchFournisseurField = new JTextField(15);
        searchTop.add(searchFournisseurField);
        
        searchTop.add(new JLabel("Médicament:"));
        searchMedicamentField = new JTextField(15);
        searchTop.add(searchMedicamentField);
        
        searchProductsButton = new JButton("Rechercher");
        searchProductsButton.addActionListener(e -> performProductSearch());
        searchTop.add(searchProductsButton);
        
        JButton clearButton = new JButton("Effacer");
        clearButton.addActionListener(e -> {
            searchFournisseurField.setText("");
            searchMedicamentField.setText("");
            loadAllCommandProducts();
        });
        searchTop.add(clearButton);
        
        panel.add(searchTop, BorderLayout.NORTH);

        // Table for command products
        String[] columns = {"ID Commande", "Code Barre", "Nom Médicament", "Quantité", "Prix Unitaire", "Fournisseur"};
        commandProductsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        commandProductsTable = new JTable(commandProductsTableModel);
        
        if (commandProductsTable.getColumnModel().getColumnCount() >= 6) {
            commandProductsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            commandProductsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            commandProductsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            commandProductsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
            commandProductsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
            commandProductsTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        }

        panel.add(new JScrollPane(commandProductsTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshProducts = new JButton("Refresh");
        refreshProducts.addActionListener(e -> loadAllCommandProducts());
        bottom.add(refreshProducts);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void loadAllCommandProducts() {
        commandProductsTableModel.setRowCount(0);
        try {
            List<CommandItem> items = commandItemDao.getAllCommandItems();
            populateCommandProductsTable(items);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des produits: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performProductSearch() {
        String fournisseur = searchFournisseurField.getText().trim();
        String medicament = searchMedicamentField.getText().trim();

        commandProductsTableModel.setRowCount(0);
        
        try {
            List<CommandItem> items = null;
            
            if (!fournisseur.isEmpty() && !medicament.isEmpty()) {
                // Search by both
                items = commandItemDao.searchByFournisseurAndMedicament(fournisseur, medicament);
            } else if (!fournisseur.isEmpty()) {
                // Search by fournisseur only
                items = commandItemDao.searchByFournisseur(fournisseur);
            } else if (!medicament.isEmpty()) {
                // Search by medicament only
                items = commandItemDao.searchByMedicine(medicament);
            } else {
                // No search criteria, load all
                loadAllCommandProducts();
                return;
            }
            
            populateCommandProductsTable(items);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateCommandProductsTable(List<CommandItem> items) {
        if (items == null) return;
        
        for (CommandItem item : items) {
            Command cmd = item.getC();
            int idCommande = cmd != null ? cmd.getIdCommande() : 0;
            String codeBarre = item.getM().getCodeBarre();
            int quantite = item.getQuantity();
            
            // Get medicine details
            Medicine med = item.getM();
            String nomMed = med != null ? med.getNom() : "N/A";
            double prixUnitaire = med != null ? med.getPrixAchat() : 0.0;
            
            // Get fournisseur from command
            Supplier s = item.getC().getSupplier();
			String fournisseur = s != null ? s.getSociete() : "N/A";
            
            commandProductsTableModel.addRow(new Object[] {
                idCommande,
                codeBarre,
                nomMed,
                quantite,
                String.format("%.2f", prixUnitaire),
                fournisseur
            });
        }
    }

    private void performSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) {
            // empty search -> load default view
            loadCommands(showingPending);
            return;
        }
        String selected = (String) searchCombo.getSelectedItem();
        List<Command> rows = null;
        try {
            if ("Societe Fournisseur".equals(selected)) {
                rows = commandDao.searchCommandesByFournisseurSociete(q);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear table and populate with filtered results according to showingPending
        commandsTableModel.setRowCount(0);
        if (rows == null) return;
        for (Command c : rows) {
            boolean isPending = (c.getDateReception() == null);
            if (showingPending && !isPending) continue;
            if (!showingPending && isPending) continue;

            

            commandsTableModel.addRow(new Object[] { c.getIdCommande(), c.getSupplier().getSociete(), c.getDateCommande(), c.getDateReception(), c.getStatut(), String.format("%.2f", c.getPrix()) });
        }
    }

    private void markSelectedAsReceived() {
        int sel = commandsTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner une commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int modelRow = commandsTable.convertRowIndexToModel(sel);
        Object idObj = commandsTableModel.getValueAt(modelRow, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "Impossible de lire l'identifiant de la commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idCommande;
        try { idCommande = Integer.parseInt(idObj.toString()); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Identifiant de commande invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object statutObj = commandsTableModel.getValueAt(modelRow, 4);
        String statut = statutObj == null ? "" : statutObj.toString();
        if (!"en attente".equalsIgnoreCase(statut)) {
            JOptionPane.showMessageDialog(this, "Commande déjà reçue", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Update statut in DB
        boolean ok = commandDao.updateStatut(idCommande, "reçue");
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du statut", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // For each produit, increment medicine quantity
        List<CommandItem> produits = commandItemDao.getCommandItemByCommandId(idCommande);
        for (CommandItem cp : produits) {
            try {
                Medicine med = cp.getM();
                if (med == null) {
                    System.out.println("Medicament not found for code: " + cp.getM().getCodeBarre());
                    continue;
                }
                int current = med.getQuantite();
                int updated = current + cp.getQuantity();
                medicineDao.updateMedicineQuantity(cp.getM().getCodeBarre(), updated);
              /*  boolean uok = medicineDao.updateMedicineQuantity(code, updated);
                if (!uok) {
                    System.out.println("Failed to update quantite for " + code);
                }*/
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        JOptionPane.showMessageDialog(this, "Commande marquée reçue et stock mis à jour", "Info", JOptionPane.INFORMATION_MESSAGE);
        loadCommands(showingPending);
    }

    private void cancelSelectedCommande() {
        int sel = commandsTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner une commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int modelRow = commandsTable.convertRowIndexToModel(sel);
        Object idObj = commandsTableModel.getValueAt(modelRow, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "Impossible de lire l'identifiant de la commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idCommande;
        try { idCommande = Integer.parseInt(idObj.toString()); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Identifiant de commande invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object statutObj = commandsTableModel.getValueAt(modelRow, 4);
        String statut = statutObj == null ? "" : statutObj.toString();
        if (!"en attente".equalsIgnoreCase(statut)) {
            JOptionPane.showMessageDialog(this, "Commande déjà reçue ou annulée", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Confirm before canceling
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir annuler cette commande?", "Confirmer annulation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Delete the commande
        boolean ok = commandDao.deleteCommande(idCommande);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'annulation de la commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Commande annulée avec succès", "Info", JOptionPane.INFORMATION_MESSAGE);
        loadCommands(showingPending);
    }

    private void deleteSelectedCommande() {
        int sel = commandsTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner une commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int modelRow = commandsTable.convertRowIndexToModel(sel);
        Object idObj = commandsTableModel.getValueAt(modelRow, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "Impossible de lire l'identifiant de la commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idCommande;
        try { idCommande = Integer.parseInt(idObj.toString()); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Identifiant de commande invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm before deleting (allow deletion for received orders as requested)
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cette commande? Cette action supprimera aussi les produits associés.", "Confirmer suppression", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Delete the commande
        boolean ok = commandDao.deleteCommande(idCommande);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la commande", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Commande supprimée avec succès", "Info", JOptionPane.INFORMATION_MESSAGE);
        loadCommands(showingPending);
    }

    private void loadCommands(boolean pending) {
        commandsTableModel.setRowCount(0);//in command dao :  getCommandREceived list<commadn>
        String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.id_fournisseur, "
                   + "COALESCE((SELECT SUM(cp.quantite * m.prix_achat) FROM commandeproduit cp JOIN medicament m ON cp.code_barre = m.code_barre WHERE cp.id_commande = c.id_commande), c.prix, 0) AS prix, "
                   + "f.societe AS fournisseur "
                   + "FROM commande c LEFT JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur "
                   + (pending ? "WHERE c.date_reception IS NULL" : "WHERE c.date_reception IS NOT NULL");
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object id = rs.getObject("id_commande");
                Object fournisseur = rs.getObject("fournisseur");
                Object dateCmd = rs.getObject("date_commande");
                Object dateRec = rs.getObject("date_reception");
                Object statut = rs.getObject("statut");
                Object prix = rs.getObject("prix");
                commandsTableModel.addRow(new Object[] { id, fournisseur, dateCmd, dateRec, statut, String.format("%.2f", prix == null ? 0.0 : ((Number)prix).doubleValue()) });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur en chargeant commandes: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * TODO:
     * button view commandProduit search (fourniseur, medicament) wala zouz
     * 
     * 
     * 
     * */
    
    
    
    // expose refresh
    public void refresh() { loadCommands(showingPending); }
}