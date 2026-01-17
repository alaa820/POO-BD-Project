package working_with_swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class AddItemFrame extends JFrame {
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public AddItemFrame(String username) {
        super("Add Items - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Top: input and add button
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JTextField itemField = new JTextField(20);
        JButton addBtn = new JButton("Add");
        top.add(new JLabel("Item:"));
        top.add(itemField);
        top.add(addBtn);

        // Center: list of items
        JList<String> itemList = new JList<>(listModel);
        JScrollPane scroll = new JScrollPane(itemList);

        // Bottom: remove and clear buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton removeBtn = new JButton("Remove Selected");
        JButton clearBtn = new JButton("Clear All");
        bottom.add(removeBtn);
        bottom.add(clearBtn);

        main.add(top, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        getContentPane().add(main);

        // Actions
        addBtn.addActionListener(e -> {
            String text = itemField.getText().trim();
            if (!text.isEmpty()) {
                listModel.addElement(text);
                itemField.setText("");
                itemField.requestFocusInWindow();
            }
        });

        itemField.addActionListener(e -> addBtn.doClick());

        removeBtn.addActionListener(e -> {
            int idx = itemList.getSelectedIndex();
            if (idx >= 0) {
                listModel.remove(idx);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to remove.", "No selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        clearBtn.addActionListener(e -> {
            if (!listModel.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Clear all items?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    listModel.clear();
                }
            }
        });

        // Small convenience: double-click to remove
        itemList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = itemList.locationToIndex(e.getPoint());
                    if (idx >= 0) listModel.remove(idx);
                }
            }
        });

        setResizable(true);
    }
}
