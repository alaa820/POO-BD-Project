package working_with_swing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerPanel extends JPanel {
    private final CardLayout internalCard = new CardLayout();
    private final JPanel cards = new JPanel(internalCard);
    private final List<Customer> customers = new ArrayList<>();

    public CustomerPanel() {
        super(new BorderLayout(8,8));

        // Top: small toolbar to switch between sub-views
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        JButton listBtn = new JButton("All Customers");
        JButton searchBtn = new JButton("Search");
        JButton addBtn = new JButton("Add");
        tb.add(listBtn); tb.add(searchBtn); tb.add(addBtn);

        // Prepare panels
        CustomerListPanel listPanel = new CustomerListPanel(customers);
        CustomerSearchPanel searchPanel = new CustomerSearchPanel(customers);
        // For add panel, we want a list subview updated when add occurs
        CustomerListPanel addListView = new CustomerListPanel(customers);
        CustomerAddPanel addPanel = new CustomerAddPanel(customers, addListView);

        cards.add(listPanel, "list");
        cards.add(searchPanel, "search");
        cards.add(addPanel, "add");

        add(tb, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);

        listBtn.addActionListener(e -> internalCard.show(cards, "list"));
        searchBtn.addActionListener(e -> internalCard.show(cards, "search"));
        addBtn.addActionListener(e -> internalCard.show(cards, "add"));

        // default
        internalCard.show(cards, "list");
    }
}
