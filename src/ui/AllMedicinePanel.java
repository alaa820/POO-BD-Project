package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.MedicineDao;
import model.Medicine;

public class AllMedicinePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private MedicineDao dao;

    public AllMedicinePanel() {
        super(new BorderLayout());
        dao = new MedicineDao();

        String[] columns = { "Barcode", "Name", "Purchase Price", "Sale Price", "VAT Rate (%)", 
                           "Dosage", "Quantity", "Threshold", "Pharmaceutical Form", "Location", 
                           "Requires Prescription", "Supplier"};
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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        
        
        table.getColumnModel().getColumn(0).setPreferredWidth(120);   // Barcode
        table.getColumnModel().getColumn(1).setPreferredWidth(150);   // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(100);   // Purchase Price
        table.getColumnModel().getColumn(3).setPreferredWidth(100);   // Sale Price
        table.getColumnModel().getColumn(4).setPreferredWidth(100);   // VAT Rate
        table.getColumnModel().getColumn(5).setPreferredWidth(100);   // Dosage
        table.getColumnModel().getColumn(6).setPreferredWidth(80);    // Quantity
        table.getColumnModel().getColumn(7).setPreferredWidth(80);    // Threshold
        table.getColumnModel().getColumn(8).setPreferredWidth(150);   // Pharmaceutical Form
        table.getColumnModel().getColumn(9).setPreferredWidth(120);  // Location
        table.getColumnModel().getColumn(10).setPreferredWidth(150);  // Requires Prescription
        table.getColumnModel().getColumn(11).setPreferredWidth(150);  // Supplier

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadMedicines());
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.NORTH);

        loadMedicines();
    }
    
    public void refresh() {
        loadMedicines();
    }
    
    private void loadMedicines() {
        tableModel.setRowCount(0);
        List<Medicine> medicines = dao.getAllMedicines();
        if (medicines != null) {
            for (Medicine m : medicines) {
                String supplierName = (m.getSupplier() != null) ? m.getSupplier().getSociete() : "N/A";
                String prescription = m.isNecessitePrescription() ? "Yes" : "No";
                
                tableModel.addRow(new Object[]{
                    m.getCodeBarre(),
                    m.getNom(),
                    m.getPrixAchat(),
                    m.getPrixVente(),
                    m.getTauxTVA(),
                    m.getDosage(),
                    m.getQuantite(),
                    m.getSeuil(),
                    m.getFormePharmaceutique(),
                    m.getEmplacement(),
                    prescription,
                    supplierName
                });
            }
        }
    }
}