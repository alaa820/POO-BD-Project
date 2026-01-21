package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import dao.EmployeeDao;
import exception.InvalideUsernameException;
import dao.EmployeeDao;
import util.Session;
import model.Employee;

/**
 * Login frame for the Pharmacy Management System.
 * Handles user authentication and navigation to the main application.
 */
public class LoginFrame extends JFrame {
    
    // Color Palette
    private static final Color COLOR_BACKGROUND = new Color(0xEDAFB8);  // Light pink
    private static final Color COLOR_PANEL = new Color(0xF7E1D7);       // Cream
    private static final Color COLOR_ACCENT = new Color(0xDEDBD2);      // Light gray
    private static final Color COLOR_PRIMARY = new Color(0xB0C4B1);     // Sage green
    private static final Color COLOR_DARK = new Color(0x4A5759);        // Dark blue-gray
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginFrame() {
        super("Pharmacy Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create main panel with gradient effect
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BACKGROUND);
        
        // Create login panel
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        
        getContentPane().add(mainPanel);
    }
    
    /**
     * Creates the login form panel
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Pharmacy Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(COLOR_DARK.brighter());
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 15, 10);
        panel.add(subtitleLabel, gbc);
        
        // Username label and field
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(COLOR_DARK);
        panel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(18);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        usernameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
        panel.add(usernameField, gbc);
        
        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(COLOR_DARK);
        panel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
        panel.add(passwordField, gbc);
        
        // Message label (for error messages)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(200, 50, 50));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(messageLabel, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(COLOR_PANEL);
        
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setPreferredSize(new Dimension(110, 40));
        loginButton.setBackground(COLOR_PRIMARY);
        loginButton.setForeground(COLOR_DARK);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        
        // Add hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(COLOR_PRIMARY.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(COLOR_PRIMARY);
            }
        });
        
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exitButton.setPreferredSize(new Dimension(110, 40));
        exitButton.setBackground(COLOR_ACCENT);
        exitButton.setForeground(COLOR_DARK);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(e -> System.exit(0));
        
        // Add hover effect
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(COLOR_ACCENT.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(COLOR_ACCENT);
            }
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    /**
     * Handles login button click and validation
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }
        
        EmployeeDao employeeDao = new EmployeeDao();
        try {
            // authenticateUser will either return true or throw exception
            employeeDao.authenticateUser(username, password);
            
            // If we reach here, authentication was successful
            Employee emp = employeeDao.getEmployeeByUsername(username);
            
            System.out.println("Logged in user: " + emp);
            Session.setCurrentUser(emp);
            
            SwingUtilities.invokeLater(() -> {
                HomeFrame homeFrame = new HomeFrame();
                homeFrame.setVisible(true);
                LoginFrame.this.dispose();
            });
        } catch(InvalideUsernameException e) {
            messageLabel.setText("Invalid username or password");
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}