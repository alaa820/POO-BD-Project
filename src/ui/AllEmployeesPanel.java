package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.EmployeeDao;
import model.Employee;

/**
 * Panel to display all employees in a table
 */
public class AllEmployeesPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private EmployeeDao employeeDao;

    public AllEmployeesPanel() {
        super(new BorderLayout());
        this.employeeDao = new EmployeeDao();

        // Create table
        String[] columnNames = {"Username", "Last Name", "First Name", "Phone", "Address", "Access"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadAllEmployees());
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.NORTH);

        // Load data on startup
        loadAllEmployees();
    }

    /**
     * Load all employees from database and display in table
     */
    private void loadAllEmployees() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDao.getAllEmployees();

        for (Employee e : employees) {
            tableModel.addRow(new Object[]{
                e.getUsername(),
                e.getNom(),
                e.getPrenom(),
                e.getPhone(),
                e.getAdresse(),
                e.getAccess()
            });
        }
    }
}