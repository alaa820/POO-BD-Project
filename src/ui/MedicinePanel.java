package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main panel containing all Medicine sub-panels with CardLayout
 */
public class MedicinePanel extends JPanel {

    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);

    public MedicinePanel() {
        super(new BorderLayout(8, 8));

        // Toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton allBtn = new JButton("Tous les Médicaments");
        JButton searchBtn = new JButton("Chercher Médicament");
        JButton addBtn = new JButton("Ajouter Médicament");

        allBtn.addActionListener(e -> cardLayout.show(cardPanel, "ALL"));
        searchBtn.addActionListener(e -> cardLayout.show(cardPanel, "SEARCH"));
        addBtn.addActionListener(e -> cardLayout.show(cardPanel, "ADD"));

        toolbar.add(allBtn);
        toolbar.addSeparator();
        toolbar.add(searchBtn);
        toolbar.addSeparator();
        toolbar.add(addBtn);

        // Panels
        cardPanel.add(new AllMedicinePanel(), "ALL");
        cardPanel.add(new SearchMedicinePanel(), "SEARCH");
        cardPanel.add(new AddMedicinePanel(), "ADD");

        add(toolbar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        cardLayout.show(cardPanel, "ALL");
    }
}
