package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import dao.MedicineDao;
import dao.SaleDao;
import dao.SupplierDao;
import dao.SaleItemDao;
import model.Medicine;
import model.SaleItem;
import model.Supplier;

public class DashboardPanel extends JPanel {

    private final MedicineDao medicineDao = new MedicineDao();
    private final SaleDao saleDao = new SaleDao();
    private final SupplierDao supplierDao = new SupplierDao();
    private final SaleItemDao saleItemDao = new SaleItemDao();

    private JTable alertTable;
    private JPanel statsPanel;
    private JPanel alertPanel;

    public DashboardPanel() {
        setLayout(new BorderLayout(10,10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Top panel with refresh button
        add(createTopPanel(), BorderLayout.NORTH);

        // Center container for stats and alerts
        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setBackground(Color.WHITE);
        
        // Stats panels
        statsPanel = createStatsPanel();
        centerContainer.add(statsPanel, BorderLayout.NORTH);
        
        // Alert table panel
        alertPanel = createAlertTablePanelWithMessage();
        centerContainer.add(alertPanel, BorderLayout.CENTER);
        
        add(centerContainer, BorderLayout.CENTER);

        // Boutons rapports
        add(createReportsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("📊 Dashboard Pharmacie", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.WEST);

        JButton refreshButton = new JButton("🔄 Actualiser");
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.addActionListener(e -> refreshDashboard());
        panel.add(refreshButton, BorderLayout.EAST);

        return panel;
    }

    private void refreshDashboard() {
        // Get the center container
        Component centerComponent = ((BorderLayout)getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent instanceof JPanel) {
            JPanel centerContainer = (JPanel) centerComponent;
            centerContainer.removeAll();
            
            // Recreate stats panel with fresh data
            statsPanel = createStatsPanel();
            centerContainer.add(statsPanel, BorderLayout.NORTH);
            
            // Recreate alert panel with fresh data
            alertPanel = createAlertTablePanelWithMessage();
            centerContainer.add(alertPanel, BorderLayout.CENTER);
            
            // Refresh the display
            centerContainer.revalidate();
            centerContainer.repaint();
        }
        
        JOptionPane.showMessageDialog(this, 
            "Dashboard actualisé avec succès!", 
            "Actualisation", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // 4 panels en haut
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBackground(Color.WHITE);

        int totalMedicines = medicineDao.countMedicines();
        int lowStock = medicineDao.countLowStockMedicines();
        double totalRevenue = saleDao.getTotalRevenue();
        int totalSuppliers = supplierDao.countSuppliers();

        panel.add(createStatCard("Total produits", totalMedicines));
        panel.add(createStatCard("Stock faible", lowStock));
        panel.add(createStatCard("Chiffre d'affaires (DT)", totalRevenue));
        panel.add(createStatCard("Fournisseurs", totalSuppliers));

        return panel;
    }

    private JLabel createStatCard(String title, double value) {
        JLabel label = new JLabel("<html><center>" + title + "<br><h2>" + value + "</h2></center></html>", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        label.setOpaque(true);
        label.setBackground(new Color(230, 240, 255));
        return label;
    }

    // Panel central = phrase + tableau alertes
    private JPanel createAlertTablePanelWithMessage() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBackground(Color.WHITE);

        if(medicineDao.countLowStockMedicines() == 0) {
            JLabel noAlertLabel = new JLabel("✅ Aucun médicament n'est sous le seuil minimal", SwingConstants.CENTER);
            noAlertLabel.setFont(new Font("Arial", Font.BOLD, 14));
            noAlertLabel.setForeground(new Color(0, 128, 0));
            noAlertLabel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            panel.add(noAlertLabel, BorderLayout.CENTER);
            return panel;
        } else {
            // Phrase d'alerte
            JLabel alertLabel = new JLabel("⚠️ Alerte : certains médicaments sont sous le seuil minimal", SwingConstants.CENTER);
            alertLabel.setFont(new Font("Arial", Font.BOLD, 14));
            alertLabel.setForeground(Color.RED);
            alertLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            panel.add(alertLabel, BorderLayout.NORTH);
        }
        
        // Tableau
        String[] columns = {"Médicament", "Quantité", "Seuil"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        alertTable = new JTable(model);
        alertTable.setFillsViewportHeight(true);

        List<Medicine> lowStockMedicines = medicineDao.getLowStockMedicines();
        for (Medicine m : lowStockMedicines) {
            Object[] row = {m.getNom(), m.getQuantite(), m.getSeuil()};
            model.addRow(row);
        }

        panel.add(new JScrollPane(alertTable), BorderLayout.CENTER);

        return panel;
    }

    // Boutons rapports
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);

        JButton stockReportBtn = new JButton("📋 Etat du stock");
        stockReportBtn.addActionListener(e -> showStockReport());

        JButton revenueReportBtn = new JButton("💰 Chiffre d'affaires");
        revenueReportBtn.addActionListener(e -> showRevenueReport());

        JButton supplierReportBtn = new JButton("👥 Performance fournisseurs");
        supplierReportBtn.addActionListener(e -> showSupplierReport());

        panel.add(stockReportBtn);
        panel.add(revenueReportBtn);
        panel.add(supplierReportBtn);

        return panel;
    }

    // Rapport stock = liste complète des produits
    private void showStockReport() {
        List<Medicine> allMedicines = medicineDao.getAllMedicines();

        String[] columns = {"Nom", "Quantité", "Seuil", "Emplacement"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Medicine m : allMedicines) {
            Object[] row = {m.getNom(), m.getQuantite(), m.getSeuil(), m.getEmplacement()};
            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Etat complet du stock", true);
        dialog.getContentPane().add(scrollPane);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Rapport chiffre d'affaires
    private void showRevenueReport() {
        double totalRevenue = saleDao.getTotalRevenue();
        JOptionPane.showMessageDialog(this, "Chiffre d'affaires total : " + totalRevenue + " DT",
                "Chiffre d'affaires", JOptionPane.INFORMATION_MESSAGE);
    }

    // Rapport Performance fournisseurs
    private void showSupplierReport() {
        JPanel supplierPanel = createSupplierPerformancePanel();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Performance Fournisseurs", true);
        dialog.getContentPane().add(supplierPanel);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Panel Performance fournisseurs
    private JPanel createSupplierPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Performance Fournisseurs"));

        String[] columns = {"Fournisseur", "Nombre de commandes", "Chiffre d'affaires (DT)"};
        List<Supplier> suppliers = supplierDao.getAllSuppliers();

        Object[][] data = new Object[suppliers.size()][3];

        List<SaleItem> saleItems = saleItemDao.getAllSaleItem();

        for (int i = 0; i < suppliers.size(); i++) {
            Supplier s = suppliers.get(i);
            String fullName = s.getNom() + " " + s.getPrenom();

            int numOrders = 0;
            double totalRevenue = 0;

            for (SaleItem si : saleItems) {
                if (si.getMedicine().getSupplier().getIdFournisseur() == s.getIdFournisseur()) {
                    numOrders++;
                    totalRevenue += si.getMedicine().getPrixVente() * si.getQuantity();
                }
            }

            data[i][0] = fullName;
            data[i][1] = numOrders;
            data[i][2] = String.format("%.2f", totalRevenue);
        }

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    // Main pour tester la dashboard
    
}