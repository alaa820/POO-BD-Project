package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.SaleDao;
import dao.SaleItemDao;
import model.Sale;
import model.SaleItem;

public class ViewSalePanel extends JPanel {
    private SaleDao saleDAO;
    private SaleItemDao saleItemDAO;
    
    // Sales panel components
    private JTable salesTable;
    private DefaultTableModel salesTableModel;
    
    // Sale products panel components
    private JTable saleProductsTable;
    private DefaultTableModel saleProductsTableModel;
    
    // Search components
    private JTextField searchClientField;
    private JTextField searchMedicineField;
    private JButton searchButton;
    private JButton resetButton;
    
    // Main panels
    private JPanel salesPanel;
    private JPanel saleProductsPanel;
    private JPanel buttonPanel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    public ViewSalePanel() {
        saleDAO = new SaleDao();
        saleItemDAO = new SaleItemDao();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create button panel at top
        buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.NORTH);

        // Create content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Create both panels
        salesPanel = createSalesPanel();
        saleProductsPanel = createSaleProductsPanel();

        // Add panels to card layout
        contentPanel.add(salesPanel, "SALES");
        contentPanel.add(saleProductsPanel, "SALE_PRODUCTS");

        add(contentPanel, BorderLayout.CENTER);

        // Show sales panel by default
        cardLayout.show(contentPanel, "SALES");
        loadSales();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("View Options"));

        JButton viewSalesButton = new JButton("View Sales");
        viewSalesButton.setPreferredSize(new Dimension(150, 35));
        viewSalesButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "SALES");
            loadSales();
        });

        JButton viewSaleProductsButton = new JButton("Search Sale Products");
        viewSaleProductsButton.setPreferredSize(new Dimension(180, 35));
        viewSaleProductsButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "SALE_PRODUCTS");
            loadSaleProducts();
        });

        panel.add(viewSalesButton);
        panel.add(viewSaleProductsButton);

        return panel;
    }

    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Sales (Ventes)"));

        // Create table
        String[] columns = {"ID Vente", "Client Name", "Date", "Total Price"};
        salesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesTable = new JTable(salesTableModel);
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(salesTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadSales());
        bottomPanel.add(refreshButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSaleProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Sale Products (Vente Produit)"));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Client Name:"));
        searchClientField = new JTextField(15);
        searchPanel.add(searchClientField);

        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Medicine Name:"));
        searchMedicineField = new JTextField(15);
        searchPanel.add(searchMedicineField);

        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchSaleProducts());
        searchPanel.add(searchButton);

        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            searchClientField.setText("");
            searchMedicineField.setText("");
            loadSaleProducts();
        });
        searchPanel.add(resetButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = {"ID Vente", "Client Name", "Medicine Name", "Quantity", "Date"};
        saleProductsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        saleProductsTable = new JTable(saleProductsTableModel);
        saleProductsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        saleProductsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        saleProductsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        saleProductsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        saleProductsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(saleProductsTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadSales() {
        salesTableModel.setRowCount(0);
        
        try {
            List<Sale> sales = saleDAO.getAllSales();
            
            for (Sale sale : sales) {
                salesTableModel.addRow(new Object[]{
                    sale.getIdVente(),
                    sale.getClient().getNom() + " " + sale.getClient().getPrenom(),
                    sale.getDateVente(),
                    String.format("%.2f", sale.getPrix())
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading sales: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSaleProducts() {
        saleProductsTableModel.setRowCount(0);
        
        try {
            List<SaleItem> saleItems = saleItemDAO.getAllSaleItem();
            
            for (SaleItem item : saleItems) {
                saleProductsTableModel.addRow(new Object[]{
                    item.getSale().getIdVente(),
                    item.getSale().getClient().getNom() + " " + item.getSale().getClient().getPrenom(),
                    item.getMedicine().getNom(),
                    item.getQuantity(),
                    item.getSale().getDateVente()
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading sale products: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchSaleProducts() {
        saleProductsTableModel.setRowCount(0);
        String clientName = searchClientField.getText().trim();
        String medicineName = searchMedicineField.getText().trim();

        try {
            List<SaleItem> saleItems;
            
            if (!clientName.isEmpty() && !medicineName.isEmpty()) {
                // Search by both client and medicine
                saleItems = saleItemDAO.searchByClientAndMedicine(clientName, medicineName);
            } else if (!clientName.isEmpty()) {
                // Search by client only
                saleItems = saleItemDAO.searchByClient(clientName);
            } else if (!medicineName.isEmpty()) {
                // Search by medicine only
                saleItems = saleItemDAO.searchByMedicine(medicineName);
            } else {
                // No search criteria, load all
                loadSaleProducts();
                return;
            }
            
            for (SaleItem item : saleItems) {
                saleProductsTableModel.addRow(new Object[]{
                    item.getSale().getIdVente(),
                    item.getSale().getClient().getNom() + " " + item.getSale().getClient().getPrenom(),
                    item.getMedicine().getNom(),
                    item.getQuantity(),
                    item.getSale().getDateVente()
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error searching sale products: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
        loadSales();
        loadSaleProducts();
    }
}