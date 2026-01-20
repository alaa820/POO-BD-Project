package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main panel containing all Medicine sub-panels with CardLayout
 */
public class MedicinePanel extends JPanel {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    private AllMedicinePanel allMedicinePanel;
    
    public MedicinePanel() {
        super(new BorderLayout(8, 8));
        
        // Toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton allBtn = new JButton("Tous les Médicaments");
        JButton searchBtn = new JButton("Chercher Médicament");
        JButton addBtn = new JButton("Ajouter Médicament");
        JButton modifyBtn = new JButton("Modifier Médicament");
        
        allBtn.addActionListener(e -> {
            allMedicinePanel.refresh();
            cardLayout.show(cardPanel, "ALL");
        });
        searchBtn.addActionListener(e -> cardLayout.show(cardPanel, "SEARCH"));
        addBtn.addActionListener(e -> cardLayout.show(cardPanel, "ADD"));
        modifyBtn.addActionListener(e -> cardLayout.show(cardPanel, "MODIFY"));
        
        toolbar.add(allBtn);
        toolbar.addSeparator();
        toolbar.add(searchBtn);
        toolbar.addSeparator();
        toolbar.add(addBtn);
        toolbar.addSeparator();
        toolbar.add(modifyBtn);
        
        // Panels
        allMedicinePanel = new AllMedicinePanel();
        cardPanel.add(allMedicinePanel, "ALL");
        cardPanel.add(new SearchMedicinePanel(), "SEARCH");
        cardPanel.add(new AddMedicinePanel(), "ADD");
        cardPanel.add(new ModifyMedicinePanel(this), "MODIFY");
        
        add(toolbar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        
        cardLayout.show(cardPanel, "ALL");
    }
    
    /**
     * Refresh and show the All Medicines panel
     */
    public void refreshAndShowAll() {
        allMedicinePanel.refresh();
        cardLayout.show(cardPanel, "ALL");
    }
}