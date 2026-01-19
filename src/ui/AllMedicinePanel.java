package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.MedicineDao;
import model.Medicine;

/**
 * Panel to display all medicines
 */
public class AllMedicinePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private MedicineDao dao;

    public AllMedicinePanel() {
        super(new BorderLayout());
        dao = new MedicineDao();

        String[] columns = {"Code Barre", "Nom", "Quantité", "Prix Vente", "Fournisseur"};
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadMedicines());
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.NORTH);

        loadMedicines();
    }

    private void loadMedicines() {
        tableModel.setRowCount(0);
        List<Medicine> medicines = dao.getAllMedicines();
        if (medicines != null) {
            for (Medicine m : medicines) {
                String supplierName = (m.getSupplier() != null) ? m.getSupplier().getSociete() : "N/A";
                tableModel.addRow(new Object[]{
                        m.getCodeBarre(), m.getNom(), m.getQuantite(), m.getPrixVente(), supplierName
                });
            }
        }
    }
}
