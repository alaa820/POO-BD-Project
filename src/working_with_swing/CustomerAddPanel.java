package working_with_swing;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CustomerAddPanel extends JPanel {
    private final JTextField nameField = new JTextField(14);
    private final JTextField surnameField = new JTextField(14);
    private final JTextField phoneField = new JTextField(14);
    private final List<Customer> data;
    private final CustomerListPanel listPanel;

    public CustomerAddPanel(List<Customer> data, CustomerListPanel listPanel) {
        super(new BorderLayout(8, 8));
        this.data = data;
        this.listPanel = listPanel;

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; form.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Surname:"), gbc);
        gbc.gridx = 1; form.add(surnameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; form.add(phoneField, gbc);

        JButton addBtn = new JButton("Add Customer");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        form.add(addBtn, gbc);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(listPanel), BorderLayout.CENTER);

        addBtn.addActionListener(e -> doAdd());
    }

    private void doAdd() {
        String n = nameField.getText().trim();
        String s = surnameField.getText().trim();
        String p = phoneField.getText().trim();
        if (n.isEmpty() && s.isEmpty() && p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one field", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Customer c = new Customer(n, s, p);
        data.add(c);
        listPanel.populate(data);
        nameField.setText(""); surnameField.setText(""); phoneField.setText("");
    }
}
