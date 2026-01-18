package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main panel containing all Supplier sub-panels with CardLayout
 */
public class SupplierPanel extends JPanel {

    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);

    public SupplierPanel() {
        super(new BorderLayout(8, 8));

        // Toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton allBtn = new JButton("Tous Fournisseurs");
        JButton searchBtn = new JButton("Chercher Fournisseur");
        JButton addBtn = new JButton("Ajouter Fournisseur");

        allBtn.addActionListener(e -> cardLayout.show(cardPanel, "ALL"));
        searchBtn.addActionListener(e -> cardLayout.show(cardPanel, "SEARCH"));
        addBtn.addActionListener(e -> cardLayout.show(cardPanel, "ADD"));

        toolbar.add(allBtn);
        toolbar.addSeparator();
        toolbar.add(searchBtn);
        toolbar.addSeparator();
        toolbar.add(addBtn);

        // Panels
        cardPanel.add(new AllSupplierPanel(), "ALL");
        cardPanel.add(new SearchSupplierPanel(), "SEARCH");
        cardPanel.add(new AddSupplierPanel(), "ADD");

        add(toolbar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        cardLayout.show(cardPanel, "ALL");
    }
}
