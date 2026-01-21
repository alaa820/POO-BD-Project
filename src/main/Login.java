package main;

import javax.swing.SwingUtilities;

import ui.LoginFrame;

public class Login {

	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}