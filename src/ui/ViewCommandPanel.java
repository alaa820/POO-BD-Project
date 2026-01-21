package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;


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
    private JTextField searchField;
    private JButton searchButton;

    // Action buttons
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

        JButton btnPending = new JButton("Pending Orders");
        btnPending.setPreferredSize(new Dimension(180, 35));
        btnPending.addActionListener(e -> {
            showingPending = true;
            loadCommands(true);
            cardLayout.show(contentPanel, "COMMANDS");
        });

        JButton btnReceived = new JButton("Received Orders");
        btnReceived.setPreferredSize(new Dimension(180, 35));
        btnReceived.addActionListener(e -> {
            showingPending = false;
            loadCommands(false);
            cardLayout.show(contentPanel, "COMMANDS");
        });

        JButton btnViewProducts = new JButton("View Ordered Products");
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
        panel.setBorder(BorderFactory.createTitledBorder("Orders"));

        // Search toolbar (above the table)
        JPanel searchTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        
        JLabel searchLabel = new JLabel("Supplier:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchTop.add(searchLabel);
        
        searchField = new JTextField(20);
        searchTop.add(searchField);
        
        searchButton = new JButton("Search");
        searchTop.add(searchButton);
        
        panel.add(searchTop, BorderLayout.NORTH);

        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        String[] columns = {"Order ID","Supplier","Order Date","Receipt Date","Status","Price"};
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

        // Cancel Order: only for status = "pending"
        cancelBtn = new JButton("Cancel Order");
        cancelBtn.addActionListener(e -> cancelSelectedCommande());
        bottom.add(cancelBtn);

        // Delete Order
        deleteBtn = new JButton("Delete Order");
        deleteBtn.addActionListener(e -> deleteSelectedCommande());
        bottom.add(deleteBtn);

        // Mark as received
        markReceivedBtn = new JButton("Mark as Received");
        markReceivedBtn.addActionListener(e -> markSelectedAsReceived());
        bottom.add(markReceivedBtn);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadCommands(showingPending));
        bottom.add(refresh);
        panel.add(bottom, BorderLayout.SOUTH);

        // Enable/disable action buttons based on selection & status
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
            boolean isPending = "pending".equalsIgnoreCase(statut);

            // Cancel works only when pending
            cancelBtn.setEnabled(isPending);
            // Delete: enable for any selected row
            deleteBtn.setEnabled(true);
            // Mark received works only when pending
            markReceivedBtn.setEnabled(isPending);
        });

        markReceivedBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        return panel;
    }

    private JPanel createCommandProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(BorderFactory.createTitledBorder("Ordered Products"));

        // Search toolbar with two search fields
        JPanel searchTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        searchTop.add(new JLabel("Supplier:"));
        searchFournisseurField = new JTextField(15);
        searchTop.add(searchFournisseurField);
        
        searchTop.add(new JLabel("Medicine:"));
        searchMedicamentField = new JTextField(15);
        searchTop.add(searchMedicamentField);
        
        searchProductsButton = new JButton("Search");
        searchProductsButton.addActionListener(e -> performProductSearch());
        searchTop.add(searchProductsButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            searchFournisseurField.setText("");
            searchMedicamentField.setText("");
            loadAllCommandProducts();
        });
        searchTop.add(clearButton);
        
        panel.add(searchTop, BorderLayout.NORTH);

        // Table for command products
        String[] columns = {"Order ID", "Barcode", "Medicine Name", "Quantity", "Unit Price", "Supplier"};
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
            List<CommandItem> items = commandItemDao.getAllCommandItemsReceived();
            populateCommandProductsTable(items);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                // Search by supplier only
                items = commandItemDao.searchByFournisseur(fournisseur);
            } else if (!medicament.isEmpty()) {
                // Search by medicine only
                items = commandItemDao.searchByMedicine(medicament);
            } else {
                // No search criteria, load all
                loadAllCommandProducts();
                return;
            }
            
            populateCommandProductsTable(items);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            
            // Get supplier from command
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
        
        List<Command> rows = null;
        try {
            rows = commandDao.searchCommandesByFournisseurSociete(q);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear table and populate with filtered results according to showingPending
        commandsTableModel.setRowCount(0);
        if (rows == null) return;
        for (Command c : rows) {
            boolean isPending = (c.getDateReception() == null);
            if (showingPending && !isPending) continue;
            if (!showingPending && isPending) continue;

            commandsTableModel.addRow(new Object[] { 
                c.getIdCommande(), 
                c.getSupplier().getSociete(), 
                c.getDateCommande(), 
                c.getDateReception(), 
                c.getStatut(), 
                String.format("%.2f", c.getPrix()) 
            });
        }
    }

    private void markSelectedAsReceived() {
        int sel = commandsTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int modelRow = commandsTable.convertRowIndexToModel(sel);
        Object idObj = commandsTableModel.getValueAt(modelRow, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "Unable to read order ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idCommande;
        try { idCommande = Integer.parseInt(idObj.toString()); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid order ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object statutObj = commandsTableModel.getValueAt(modelRow, 4);
        String statut = statutObj == null ? "" : statutObj.toString();
        if (!"pending".equalsIgnoreCase(statut)) {
            JOptionPane.showMessageDialog(this, "Order already received", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Update status in DB
        boolean ok = commandDao.updateStatut(idCommande, "received");
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Error updating status", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // For each product, increment medicine quantity
        List<CommandItem> produits = commandItemDao.getCommandItemByCommandId(idCommande);
        for (CommandItem cp : produits) {
            try {
                Medicine med = cp.getM();
                if (med == null) {
                    System.out.println("Medicine not found for code: " + cp.getM().getCodeBarre());
                    continue;
                }
                int current = med.getQuantite();
                int updated = current + cp.getQuantity();
                medicineDao.updateMedicineQuantity(cp.getM().getCodeBarre(), updated);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        JOptionPane.showMessageDialog(this, "Order marked as received and stock updated", "Info", JOptionPane.INFORMATION_MESSAGE);
        loadCommands(showingPending);
    }

    private void cancelSelectedCommande() {
        int sel = commandsTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int modelRow = commandsTable.convertRowIndexToModel(sel);
        Object idObj = commandsTableModel.getValueAt(modelRow, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "Unable to read order ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idCommande;
        try { idCommande = Integer.parseInt(idObj.toString()); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid order ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object statutObj = commandsTableModel.getValueAt(modelRow, 4);
        String statut = statutObj == null ? "" : statutObj.toString();
        if (!"pending".equalsIgnoreCase(statut)) {
            JOptionPane.showMessageDialog(this, "Order already received or cancelled", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Confirm before canceling
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this order?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Update status to cancelled
        boolean ok = commandDao.updateStatut(idCommande, "cancelled");
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Error cancelling order", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Order cancelled successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
        loadCommands(showingPending);
    }

    private void deleteSelectedCommande() {
        int sel = commandsTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int modelRow = commandsTable.convertRowIndexToModel(sel);
        Object idObj = commandsTableModel.getValueAt(modelRow, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "Unable to read order ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idCommande;
        try { idCommande = Integer.parseInt(idObj.toString()); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid order ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm before deleting
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this order? This action will also delete associated products.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Delete the order
        boolean ok = commandDao.deleteCommande(idCommande);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Error deleting order", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Order deleted successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
        loadCommands(showingPending);
    }

    private void loadCommands(boolean pending) {
        commandsTableModel.setRowCount(0);
        
        try {
            List<Command> commands = commandDao.getAllCommands(pending);
            
            for (Command c : commands) {
                double prix = c.getPrix();
                String fournisseur = c.getSupplier() != null ? c.getSupplier().getSociete() : "N/A";
                
                commandsTableModel.addRow(new Object[] { 
                    c.getIdCommande(), 
                    fournisseur, 
                    c.getDateCommande(), 
                    c.getDateReception(), 
                    c.getStatut(), 
                    String.format("%.2f", prix) 
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // expose refresh
    public void refresh() { loadCommands(showingPending); }
}