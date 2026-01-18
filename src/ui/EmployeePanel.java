package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main Employee Panel containing three sub-panels:
 * 1. All Employees - Display all employees in a table
 * 2. Search Employee - Search employees by nom, prenom, username
 * 3. Add Employee - Add a new employee
 */
public class EmployeePanel extends JPanel {
    
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    public EmployeePanel() {
        super(new BorderLayout(8, 8));
        
        // Create toolbar with buttons
        JToolBar toolbar = createToolbar();
        
        // Create sub-panels
        cardPanel.add(new AllEmployeesPanel(), "ALL");
        cardPanel.add(new SearchEmployeePanel(), "SEARCH");
        cardPanel.add(new AddEmployeePanel(), "ADD");
        
        add(toolbar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        
        // Show All Employees by default
        cardLayout.show(cardPanel, "ALL");
    }
    
    /**
     * Creates the toolbar with navigation buttons
     */
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton allBtn = new JButton("All Employees");
        allBtn.setFont(new Font("Arial", Font.BOLD, 12));
        allBtn.addActionListener(e -> cardLayout.show(cardPanel, "ALL"));
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.addActionListener(e -> cardLayout.show(cardPanel, "SEARCH"));
        
        JButton addBtn = new JButton("Add Employee");
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
