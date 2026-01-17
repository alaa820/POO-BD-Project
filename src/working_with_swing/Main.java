package working_with_swing;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(360, 180);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        // Login action: trigger event when button is pressed
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            // Simple example check. Replace with real auth logic as needed.
            if (true) { // always succeed for demo
                // On success: close login frame and open AppFrame
                SwingUtilities.invokeLater(() -> {
                    frame.dispose();
                    AppFrame app = new AppFrame(username);
                    app.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password", "Login failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Allow Enter key in password field to trigger login
        passField.addActionListener(e -> loginBtn.doClick());

        frame.getContentPane().add(panel);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}