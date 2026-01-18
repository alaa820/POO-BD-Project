package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.SupplierDao;
import model.Supplier;

/**
 * Panel to search suppliers by name
 */
public class SearchSupplierPanel extends JPanel {

    private JTextField searchField;
    private JButton searchBtn;
    private JTable table;
    private DefaultTableModel tableModel;
    private SupplierDao dao;

    public SearchSupplierPanel() {
        super(new BorderLayout());
        dao = new SupplierDao();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchBtn = new JButton("Search");
        topPanel.add(new JLabel("Nom du fournisseur:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Nom", "Prénom", "Société", "Email", "Téléphone", "Adresse", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> searchSupplier());
    }

    private void searchSupplier() {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0);
        List<Supplier> results = dao.searchSupplierByName(keyword);
        for (Supplier s : results) {
            tableModel.addRow(new Object[]{
                    s.getNom(), s.getPrenom(), s.getSociete(), s.getEmail(),
                    s.getTelephone(), s.getAdresse(), s.getDescription()
            });
        }
    }
}
