package working_with_swing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerSearchPanel extends JPanel {
    private final JTextField nameField = new JTextField(12);
    private final JTextField surnameField = new JTextField(12);
    private final JTextField phoneField = new JTextField(12);
    private final CustomerListPanel resultsPanel;
    private final List<Customer> source;

    public CustomerSearchPanel(List<Customer> customers) {
        super(new BorderLayout(8, 8));
        this.source = customers == null ? new ArrayList<>() : customers;

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Surname:"));
        form.add(surnameField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        JButton searchBtn = new JButton("Search");
        form.add(searchBtn);

        resultsPanel = new CustomerListPanel(new ArrayList<>());

        add(form, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> doSearch());
    }

    private void doSearch() {
        String n = nameField.getText().trim().toLowerCase();
        String s = surnameField.getText().trim().toLowerCase();
        String p = phoneField.getText().trim().toLowerCase();

        List<Customer> found = source.stream().filter(c -> {
            boolean ok = true;
            if (!n.isEmpty()) ok &= c.getName().toLowerCase().contains(n);
            if (!s.isEmpty()) ok &= c.getSurname().toLowerCase().contains(s);
            if (!p.isEmpty()) ok &= c.getPhone().toLowerCase().contains(p);
            return ok;
        }).collect(Collectors.toList());

        resultsPanel.populate(found);
    }
}
