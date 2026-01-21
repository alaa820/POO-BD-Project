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

        String[] columns = {"Last Name", "First Name", "Company", "Email", "Phone", "Address", "Description"};
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
        
        // Set column widths for better visibility
        table.getColumnModel().getColumn(0).setPreferredWidth(100);  // Last Name
        table.getColumnModel().getColumn(1).setPreferredWidth(100);  // First Name
        table.getColumnModel().getColumn(2).setPreferredWidth(150);  // Company
        table.getColumnModel().getColumn(3).setPreferredWidth(150);  // Email
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Phone
        table.getColumnModel().getColumn(5).setPreferredWidth(150);  // Address
        table.getColumnModel().getColumn(6).setPreferredWidth(200);  // Description

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

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