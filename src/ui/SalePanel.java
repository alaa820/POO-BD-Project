package ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import dao.CustomerDao;
import dao.MedicineDao;
import dao.StockDao;
import model.Customer;
import model.Medicine;
import model.SaleItem;


/**
 * Sale Panel for processing customer purchases
 * Features:
 * 1. Search and select customer by name
 * 2. Search and select medicine by name
 * 3. Enter quantity
 * 4. Add items to shopping cart
 * 5. View cart and checkout
 */
public class SalePanel extends JPanel {
    
    // Customer selection
    private JTextField customerField;
    private JComboBox<Customer> customerCombo;
    private Customer selectedCustomer;
    
    // Medicine selection
    private JTextField medicineField;
    private JComboBox<Medicine> medicineCombo;
    private Medicine selectedMedicine;
    private JLabel medicineIdLabel;
    
    // Quantity
    private JSpinner quantitySpinner;
    
    // Cart
    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    private JLabel totalLabel;
    private List<SaleItem> cartItems;
    
    // DAOs
    private CustomerDao customerDao;
    private StockDao stockDao;
    private MedicineDao medicineDao;

    public SalePanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        this.cartItems = new ArrayList<>();
        this.customerDao = new CustomerDao();
        this.stockDao = new StockDao();
        this.medicineDao = new MedicineDao();
        
        // Create top panel for inputs
        JPanel inputPanel = createInputPanel();
        
        // Create bottom panel for cart
        JPanel cartPanel = createCartPanel();
        
        add(inputPanel, BorderLayout.NORTH);
        add(cartPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the input panel for customer, medicine, and quantity selection
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("Sale Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // === CUSTOMER SELECTION ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel customerLabel = new JLabel("Select Customer:");
        customerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(customerLabel, gbc);
        
        gbc.gridx = 1;
        customerField = new JTextField(15);
        customerField.setFont(new Font("Arial", Font.PLAIN, 11));
        customerField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterCustomers(); }
            public void removeUpdate(DocumentEvent e) { filterCustomers(); }
            public void changedUpdate(DocumentEvent e) { filterCustomers(); }
        });
        panel.add(customerField, gbc);
        
        gbc.gridx = 2;
        customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Arial", Font.PLAIN, 11));
        customerCombo.addActionListener(e -> {
            selectedCustomer = (Customer) customerCombo.getSelectedItem();
            if (selectedCustomer != null) {
                customerField.setText(selectedCustomer.getNom() + " " + selectedCustomer.getPrenom());
            }
        });
        panel.add(customerCombo, gbc);
        
        row++;
        
        // === MEDICINE SELECTION ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel medicineLabel = new JLabel("Select Medicine:");
        medicineLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(medicineLabel, gbc);
        
        gbc.gridx = 1;
        medicineField = new JTextField(15);
        medicineField.setFont(new Font("Arial", Font.PLAIN, 11));
        medicineField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterMedicines(); }
            public void removeUpdate(DocumentEvent e) { filterMedicines(); }
            public void changedUpdate(DocumentEvent e) { filterMedicines(); }
        });
        panel.add(medicineField, gbc);
        
        gbc.gridx = 2;
        medicineCombo = new JComboBox<>();
        medicineCombo.setFont(new Font("Arial", Font.PLAIN, 11));
        medicineCombo.addActionListener(e -> {
            selectedMedicine = (Medicine) medicineCombo.getSelectedItem();
            if (selectedMedicine != null) {
                medicineField.setText(selectedMedicine.getName());
                medicineIdLabel.setText("ID: " + selectedMedicine.getIdMedicine());
            }
        });
        panel.add(medicineCombo, gbc);
        
        gbc.gridx = 3;
        medicineIdLabel = new JLabel("ID: -");
        medicineIdLabel.setFont(new Font("Arial", Font.BOLD, 11));
        medicineIdLabel.setForeground(new Color(0, 100, 200));
        panel.add(medicineIdLabel, gbc);
        
        row++;
        
        // === QUANTITY ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(quantityLabel, gbc);
        
        gbc.gridx = 1;
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        quantitySpinner.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(quantitySpinner, gbc);
        
        // === ADD TO CART BUTTON ===
        gbc.gridx = 2;
        gbc.gridy = row;
        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addToCartBtn.setPreferredSize(new Dimension(120, 35));
        addToCartBtn.addActionListener(e -> handleAddToCart());
        panel.add(addToCartBtn, gbc);
        
        return panel;
    }
    
    /**
     * Creates the cart panel showing selected items and checkout
     */
    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        
        // Cart table
        String[] columnNames = {"Medicine", "Quantity", "Unit Price", "Total"};
        cartTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        cartTable = new JTable(cartTableModel);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 11));
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        cartTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with total and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Total and summary
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        totalLabel = new JLabel("Total: 0.00 DZD");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(new Color(0, 100, 0));
        totalPanel.add(totalLabel);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setFont(new Font("Arial", Font.BOLD, 11));
        removeBtn.addActionListener(e -> handleRemoveFromCart());
        
        JButton clearBtn = new JButton("Clear Cart");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 11));
        clearBtn.addActionListener(e -> handleClearCart());
        
        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        checkoutBtn.setBackground(new Color(0, 150, 0));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setPreferredSize(new Dimension(100, 35));
        checkoutBtn.addActionListener(e -> handleCheckout());
        
        buttonsPanel.add(removeBtn);
        buttonsPanel.add(clearBtn);
        buttonsPanel.add(checkoutBtn);
        
        bottomPanel.add(totalPanel, BorderLayout.WEST);
        bottomPanel.add(buttonsPanel, BorderLayout.EAST);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Filter customers based on text input
     */
    private void filterCustomers() {
        String searchText = customerField.getText().trim().toLowerCase();
        customerCombo.removeAllItems();
        
        if (searchText.isEmpty()) {
            return;
        }
        
        // TODO: Call customerDao.searchCustomers(nom, prenom, telephone) 
        // List<Customer> customers = customerDao.searchCustomers(searchText, null, null);
        // for (Customer c : customers) {
        //     customerCombo.addItem(c);
        // }
    }
    
    /**
     * Filter medicines based on text input
     */
    private void filterMedicines() {
        String searchText = medicineField.getText().trim().toLowerCase();
        medicineCombo.removeAllItems();
        
        if (searchText.isEmpty()) {
            return;
        }
        
        // TODO: Call medicineDao.searchByName(searchText) to get matching medicines
        // List<Medicine> medicines = medicineDao.searchByName(searchText);
        // for (Medicine m : medicines) {
        //     medicineCombo.addItem(m);
        // }
    }
    
    /**
     * Handle adding item to cart
     */
    private void handleAddToCart() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (selectedMedicine == null) {
            JOptionPane.showMessageDialog(this, "Please select a medicine", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int quantity = (Integer) quantitySpinner.getValue();
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // TODO: Uncomment the line below to check stock availability
        // boolean isAvailable = stockDao.isQuantityAvailable(selectedMedicine.getIdMedicine(), quantity);
        // if (!isAvailable) {
        //     JOptionPane.showMessageDialog(this, "Insufficient quantity in stock", "Error", JOptionPane.ERROR_MESSAGE);
        //     return;
        // }
        
        // Create sale item
        SaleItem item = new SaleItem(
            selectedMedicine.getIdMedicine(),
            selectedMedicine.getName(),
            quantity,
            selectedMedicine.getPrice()
        );
        
        // Add to cart
        cartItems.add(item);
        cartTableModel.addRow(new Object[]{
            item.getMedicineName(),
            item.getQuantity(),
            String.format("%.2f", item.getUnitPrice()),
            String.format("%.2f", item.getTotalPrice())
        });
        
        // Update total
        updateCartTotal();
        
        // Reset fields
        medicineField.setText("");
        medicineIdLabel.setText("ID: -");
        selectedMedicine = null;
        quantitySpinner.setValue(1);
        medicineCombo.removeAllItems();
        
        JOptionPane.showMessageDialog(this, "Item added to cart", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Handle removing item from cart
     */
    private void handleRemoveFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        cartItems.remove(selectedRow);
        cartTableModel.removeRow(selectedRow);
        updateCartTotal();
    }
    
    /**
     * Handle clearing entire cart
     */
    private void handleClearCart() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is already empty", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Clear entire cart?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            cartTableModel.setRowCount(0);
            updateCartTotal();
        }
    }
    
    /**
     * Handle checkout
     */
    private void handleCheckout() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // TODO: Call saleDao.createSale(customer, cartItems) to save the sale
        // TODO: Update stock by calling stockDao.reduceQuantity(medicineId, quantity) for each item
        
        // For now, show success message
        double total = cartItems.stream().mapToDouble(SaleItem::getTotalPrice).sum();
        JOptionPane.showMessageDialog(this, 
            "Sale completed successfully!\n\n" +
            "Customer: " + selectedCustomer.getNom() + " " + selectedCustomer.getPrenom() + "\n" +
            "Total Amount: " + String.format("%.2f", total) + " DZD\n" +
            "Items: " + cartItems.size(),
            "Checkout Success", JOptionPane.INFORMATION_MESSAGE);
        
        // Reset for next sale
        resetSalePanel();
    }
    
    /**
     * Update cart total
     */
    private void updateCartTotal() {
        double total = cartItems.stream().mapToDouble(SaleItem::getTotalPrice).sum();
        totalLabel.setText("Total: " + String.format("%.2f", total) + " DZD");
    }
    
    /**
     * Reset the entire sale panel
     */
    private void resetSalePanel() {
        customerField.setText("");
        customerCombo.removeAllItems();
        selectedCustomer = null;
        medicineField.setText("");
        medicineCombo.removeAllItems();
        medicineIdLabel.setText("ID: -");
        selectedMedicine = null;
        quantitySpinner.setValue(1);
        cartItems.clear();
        cartTableModel.setRowCount(0);
        updateCartTotal();
    }
}

