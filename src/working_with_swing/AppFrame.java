package working_with_swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AppFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel rightPanel = new JPanel(cardLayout);

    public AppFrame(String username) {
        super("Main App - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Left navigation panel with buttons
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] btnNames = {"Customer", "Supplier", "Stock", "Purchase", "Command"};
        for (String name : btnNames) {
            JButton b = new JButton(name);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            b.addActionListener(this::onNavClicked);
            left.add(b);
            left.add(Box.createVerticalStrut(8));
        }

        // Right panel with CardLayout to swap views
        rightPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        // Use dedicated CustomerPanel for the Customer view
        rightPanel.add(new CustomerPanel(), "Customer");
        rightPanel.add(makePlaceholderPanel("Supplier View"), "Supplier");
        rightPanel.add(makePlaceholderPanel("Stock View"), "Stock");
        rightPanel.add(makePlaceholderPanel("Purchase View"), "Purchase");
        rightPanel.add(makePlaceholderPanel("Command View"), "Command");

        // Layout: split left and right
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, rightPanel);
        split.setDividerLocation(160);
        split.setResizeWeight(0);

        getContentPane().add(split, BorderLayout.CENTER);
        setResizable(true);
    }

    private void onNavClicked(ActionEvent e) {
        String cmd = ((JButton) e.getSource()).getText();
        cardLayout.show(rightPanel, cmd);
    }

    private JPanel makePlaceholderPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 18f));
        p.add(l, BorderLayout.CENTER);
        return p;
    }
}