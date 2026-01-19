package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Main application frame for the Pharmacy Management System.
 * Contains left navigation panel with buttons for all modules and right content area
 * that switches between different panels based on button clicks.
 */
public class HomeFrame extends JFrame {
    
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public HomeFrame() {
        super("Pharmacy Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        // Create left navigation panel
        JPanel leftPanel = createNavigationPanel();
        
        // Create right content panel with CardLayout
        createContentPanels();
        
        // Create main layout with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, contentPanel);
        splitPane.setDividerLocation(180);
        splitPane.setResizeWeight(0);
        
        getContentPane().add(splitPane, BorderLayout.CENTER);
        setResizable(true);
    }
    
    /**
     * Creates the left navigation panel with buttons for all modules
     */
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.setBackground(new Color(240, 240, 240));
        
        // Button definitions
        String[] buttonNames = {
            "Dashboard",
            "Customer",
            "Supplier",
            "Medicine",
            "Sale",
            "Command",
            "Employee"
        };
        
        // Create buttons
        for (String buttonName : buttonNames) {
            JButton btn = new JButton(buttonName);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.addActionListener(e -> switchPanel(buttonName));
            
            panel.add(btn);
            panel.add(Box.createVerticalStrut(8));
        }
        
        // Add spacer at the bottom
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    /**
     * Creates all content panels and adds them to the CardLayout
     */
    private void createContentPanels() {
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Add all panels
        contentPanel.add(new DashboardPanel(), "Dashboard");
        contentPanel.add(new CustomerPanel(), "Customer");
        contentPanel.add(new SupplierPanel(), "Supplier");
        contentPanel.add(new MedicinePanel(), "Medicine");
        contentPanel.add(new SalePanel(), "Sale");
        contentPanel.add(new CommandPanel(), "Command");
        contentPanel.add(new EmployeePanel(), "Employee");
        
        // Show Dashboard by default
        cardLayout.show(contentPanel, "Dashboard");
    }
    
    /**
     * Switches to the selected panel
     */
    private void switchPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }
    
    /**
     * Main method to launch the application
     */
    
}