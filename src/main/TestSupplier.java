package main;

import ui.SupplierPanel;

import javax.swing.*;

/**
 * Classe pour lancer l'interface SupplierPanel
 */
public class TestSupplier {

    public static void main(String[] args) {
        // S'assurer que l'interface utilise le look & feel du système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Création du JFrame principal
        JFrame frame = new JFrame("Gestion des Fournisseurs");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ajouter le panel SupplierPanel
        SupplierPanel panel = new SupplierPanel();
        frame.setContentPane(panel);

        // Ajuster la taille et centrer
        frame.pack();
        frame.setLocationRelativeTo(null);

        // Rendre visible
        frame.setVisible(true);
    }
}
