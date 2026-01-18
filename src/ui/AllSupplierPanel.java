package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.SupplierDao;
import model.Supplier;

/**
 * Panel to display all suppliers
 */
public class AllSupplierPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private SupplierDao dao;

    public AllSupplierPanel() {
        super(new BorderLayout());
        dao = new SupplierDao();

        String[] columns = {"Nom", "Prénom", "Société", "Email", "Téléphone", "Adresse", "Description"};
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
        refreshBtn.addActionListener(e -> loadSuppliers());
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.NORTH);

        loadSuppliers();
    }

    private void loadSuppliers() {
        tableModel.setRowCount(0);
        List<Supplier> suppliers = dao.getAllSuppliers();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{
                    s.getNom(), s.getPrenom(), s.getSociete(), s.getEmail(),
                    s.getTelephone(), s.getAdresse(), s.getDescription()
            });
        }
    }
}
