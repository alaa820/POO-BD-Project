package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main Customer Panel containing three sub-panels:
 * 1. All Customers - Display all customers in a table
 * 2. Search Customer - Search customers by name
 * 3. Add Customer - Add a new customer
 */
public class CustomerPanel extends JPanel {
    
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    public CustomerPanel() {
        super(new BorderLayout(8, 8));
        
        // Create toolbar with buttons
        JToolBar toolbar = createToolbar();
        
        // Create sub-panels
        cardPanel.add(new AllCustomersPanel(), "ALL");
        cardPanel.add(new SearchCustomerPanel(), "SEARCH");
        cardPanel.add(new AddCustomerPanel(), "ADD");
        
        add(toolbar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        
        // Show All Customers by default
        cardLayout.show(cardPanel, "ALL");
    }
    
    /**
     * Creates the toolbar with navigation buttons
     */
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton allBtn = new JButton("All Customers");
        allBtn.setFont(new Font("Arial", Font.BOLD, 12));
        allBtn.addActionListener(e -> cardLayout.show(cardPanel, "ALL"));
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.addActionListener(e -> cardLayout.show(cardPanel, "SEARCH"));
        
        JButton addBtn = new JButton("Add Customer");
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.addActionListener(e -> cardLayout.show(cardPanel, "ADD"));
        
        toolbar.add(allBtn);
        toolbar.addSeparator();
        toolbar.add(searchBtn);
        toolbar.addSeparator();
        toolbar.add(addBtn);
        
        return toolbar;
    }
}
