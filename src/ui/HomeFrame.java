package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import util.Session;

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
        
        // Create top panel with user greeting
        JPanel topPanel = createTopPanel();
        
        // Create left navigation panel
        JPanel leftPanel = createNavigationPanel();
        
        // Create right content panel with CardLayout
        createContentPanels();
        
        // Create main layout with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, contentPanel);
        splitPane.setDividerLocation(180);
        splitPane.setResizeWeight(0);
        
        // Add components to frame
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        setResizable(true);
    }
    
    /**
     * Creates the top panel with user greeting
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xAC1754)); // Dark pink from palette
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Get user name from session
        String userName = Session.getCurrentUser() != null ? 
                         Session.getCurrentUser().getNom() : "User";
        String userRole = Session.getUserRole();
        
        // Create greeting label
        JLabel greetingLabel = new JLabel("Hello, " + userName);
        greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        greetingLabel.setForeground(new Color(0xF7A8C4)); // Light pink from palette
        
        // Create role label
        JLabel roleLabel = new JLabel("Role: " + userRole);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(0xF37199)); // Medium pink from palette
        
        // Left side with greeting and role
        JPanel leftSide = new JPanel();
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));
        leftSide.setOpaque(false);
        leftSide.add(greetingLabel);
        leftSide.add(Box.createVerticalStrut(2));
        leftSide.add(roleLabel);
        
        // Right side with logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(0xE53888)); // Bright pink from palette
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> handleLogout());
        
        // Add hover effect to logout button
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(new Color(0xE53888).darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(new Color(0xE53888));
            }
        });
        
        panel.add(leftSide, BorderLayout.WEST);
        panel.add(logoutBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the left navigation panel with buttons for all modules
     */
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.setBackground(new Color(0xAC1754)); // Dark pink from palette
        
        boolean isAdmin = "ADMIN".equalsIgnoreCase(Session.getUserRole());
        
        // Button configurations: name, prefix, color
        // Using palette: F7A8C4 (light), F37199 (medium-light), E53888 (medium), AC1754 (dark - background)
        Object[][] buttonConfigs = {
            {"Dashboard", "■", new Color(0xF7A8C4)},          // Light pink - Dashboard
            {"Customer", "►", new Color(0xF37199)},           // Medium-light pink - Customer
            {"Supplier", "►", new Color(0xE88CB3)},           // Lighter variant - Supplier
            {"Medicine", "►", new Color(0xE53888)},           // Medium pink - Medicine
            {"Sale", "►", new Color(0xF7A8C4)},               // Light pink - Sale
            {"ViewSale", "►", new Color(0xF37199)},           // Medium-light pink - ViewSale (same family)
            {"Command", "►", new Color(0xE53888)},            // Medium pink - Command
            {"CommandView", "►", new Color(0xDB5B94)},        // Darker variant - CommandView (same family)
            {"Employee", "►", new Color(0xF7A8C4)}            // Light pink - Employee
        };
        
        for (Object[] config : buttonConfigs) {
            String buttonName = (String) config[0];
            String prefix = (String) config[1];
            Color color = (Color) config[2];
            
            // 🔒 hide admin-only buttons
            if (!isAdmin && (buttonName.equals("Dashboard") || buttonName.equals("Employee"))) {
                continue;
            }
            
            JButton btn = createStyledButton(buttonName, prefix, color);
            btn.addActionListener(e -> switchPanel(buttonName));
            panel.add(btn);
            panel.add(Box.createVerticalStrut(5));
        }
        
        panel.add(Box.createVerticalGlue());
        return panel;
    }
    
    /**
     * Creates a styled button with prefix symbol and color
     */
    private JButton createStyledButton(String text, String prefix, Color baseColor) {
        JButton btn = new JButton(prefix + "  " + text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(baseColor);
        
        // Determine text color based on background brightness
        int brightness = (baseColor.getRed() + baseColor.getGreen() + baseColor.getBlue()) / 3;
        btn.setForeground(brightness > 150 ? new Color(0xAC1754) : Color.WHITE);
        
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        
        // Add hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(baseColor.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(baseColor);
            }
        });
        
        return btn;
    }
    
    /**
     * Creates all content panels and adds them to the CardLayout
     */
    private void createContentPanels() {
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        boolean isAdmin = "ADMIN".equalsIgnoreCase(Session.getUserRole());
        
        if (isAdmin) {
            contentPanel.add(new DashboardPanel(), "Dashboard");
            contentPanel.add(new EmployeePanel(), "Employee");
        }
        
        contentPanel.add(new CustomerPanel(), "Customer");
        contentPanel.add(new SupplierPanel(), "Supplier");
        contentPanel.add(new MedicinePanel(), "Medicine");
        contentPanel.add(new SalePanel(), "Sale");
        contentPanel.add(new ViewSalePanel(), "ViewSale");
        contentPanel.add(new ViewCommandPanel(), "CommandView");
        contentPanel.add(new CommandPanel(), "Command");
        
        cardLayout.show(contentPanel, isAdmin ? "Dashboard" : "Customer");
    }
    
    /**
     * Switches to the selected panel
     */
    private void switchPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }
    
    /**
     * Handle logout action
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            Session.clearSession();
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}