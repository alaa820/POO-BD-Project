package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.MedicineDao;
import model.Medicine;

/**
 * Panel to search medicines by code_barre, name, or supplier
 */
public class SearchMedicinePanel extends JPanel {

    private JTextField codeBarreField, nameField, supplierField;
    private JButton searchBtn;
    private JTable table;
    private DefaultTableModel tableModel;
    private MedicineDao dao;

    public SearchMedicinePanel() {
        super(new BorderLayout());
        dao = new MedicineDao();

        // Search panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(new Color(240, 240, 240));
        
        codeBarreField = new JTextField(12);
        nameField = new JTextField(12);
        supplierField = new JTextField(12);
        searchBtn = new JButton("Rechercher");
        
        topPanel.add(new JLabel("Code Barre:"));
        topPanel.add(codeBarreField);
        topPanel.add(new JLabel("Nom:"));
        topPanel.add(nameField);
        topPanel.add(new JLabel("Fournisseur:"));
        topPanel.add(supplierField);
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);

        // Results table
        String[] columns = {"Code Barre", "Nom", "Fournisseur"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> searchMedicine());
    }

    private void searchMedicine() {
    	System.out.println("Searching medicine...");
        String codeBarre = codeBarreField.getText().trim();
        String name = nameField.getText().trim();
        String supplier = supplierField.getText().trim();
        
        tableModel.setRowCount(0);
        List<Medicine> results = dao.searchMedicines(
            codeBarre.isEmpty() ? null : codeBarre,
            name.isEmpty() ? null : name,
            supplier.isEmpty() ? null : supplier
        );
        
        if (results != null) {
            for (Medicine m : results) {
                String supplierName = (m.getSupplier() != null) ? m.getSupplier().getSociete() : "N/A";
                tableModel.addRow(new Object[]{
                        m.getCodeBarre(), m.getNom(), supplierName, 
                        m.getQuantite(), m.getPrixVente()
                });
            }
        }
        else {
			JOptionPane.showMessageDialog(this, "Aucun médicament trouvé.", "Résultat de la recherche", JOptionPane.INFORMATION_MESSAGE);
		}
        System.out.println("Search completed. Found " + results.size() + " results.");
    }
}
