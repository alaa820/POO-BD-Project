package working_with_swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerListPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;

    public CustomerListPanel(List<Customer> customers) {
        super(new BorderLayout());
        String[] cols = {"Name", "Surname", "Phone"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        

        table = new JTable(tableModel);
        populate(customers);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void populate(List<Customer> customers) {
        tableModel.setRowCount(0);
        if (customers == null) return;
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{c.getName(), c.getSurname(), c.getPhone()});
        }
    }
}
