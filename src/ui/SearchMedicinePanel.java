package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.MedicineDao;
import model.Medicine;

public class SearchMedicinePanel extends JPanel {

	private JTextField codeBarreField, nameField, supplierField;
	private JButton searchBtn;
	private JTable table;
	private DefaultTableModel tableModel;
	private MedicineDao dao;

	public SearchMedicinePanel() {
		setLayout(new BorderLayout());
		dao = new MedicineDao();

		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

		codeBarreField = new JTextField(12);
		nameField = new JTextField(12);
		supplierField = new JTextField(12);
		searchBtn = new JButton("Rechercher");

		topPanel.add(new JLabel("Code Barre:"));
		topPanel.add(codeBarreField);
		topPanel.add(new JLabel("Nom:"));
		topPanel.add(nameField);
		topPanel.add(new JLabel("Fournisseur:"));
		topPanel.add(supplierField);
		topPanel.add(searchBtn);

		add(topPanel, BorderLayout.NORTH);

		/* ---------- TABLE ---------- */
		String[] columns = { "BarCode", "Name", "Supplier", "Delete" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 3; // only delete column clickable
			}
		};

		table = new JTable(tableModel);
		table.setRowHeight(30);

		// Renderer (shows button)
		table.getColumn("Delete").setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
			JButton btn = new JButton("Delete");
			btn.setBackground(Color.RED);
			btn.setForeground(Color.WHITE);
			return btn;
		});

		// Editor (handles click)
		table.getColumn("Delete").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
			private final JButton btn = new JButton("Delete");
			private int row;

			{
				btn.setBackground(Color.RED);
				btn.setForeground(Color.WHITE);

				btn.addActionListener(e -> {
					String codeBarre = table.getValueAt(row, 0).toString();

					int confirm = JOptionPane.showConfirmDialog(SearchMedicinePanel.this,
							"Delete medicine " + codeBarre + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);

					if (confirm == JOptionPane.YES_OPTION) {
						boolean success = dao.deleteByCodeBarre(codeBarre);

						if (success) {
							tableModel.removeRow(row);
							System.out.println("Delete succeeded for barCode: " + codeBarre);
						} else {
							System.out.println("Delete failed for barCode: " + codeBarre);
							JOptionPane.showMessageDialog(SearchMedicinePanel.this, "Deletion failed.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}

					}
					fireEditingStopped();
				});
			}

			@Override
			public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
					int column) {
				this.row = row;
				return btn;
			}
		});

		add(new JScrollPane(table), BorderLayout.CENTER);

		searchBtn.addActionListener(e -> searchMedicine());
	}

	
	private void searchMedicine() {
		tableModel.setRowCount(0);

		List<Medicine> results = dao.searchMedicines(emptyToNull(codeBarreField.getText()),
				emptyToNull(nameField.getText()), emptyToNull(supplierField.getText()));

		if (results == null || results.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No medicine found");
			return;
		}

		for (Medicine m : results) {
			String supplierName = (m.getSupplier() != null) ? m.getSupplier().getSociete() : "N/A";

			tableModel.addRow(new Object[] { m.getCodeBarre(), m.getNom(), supplierName, "Supprimer" });
		}
	}

	private String emptyToNull(String s) {
		return s == null || s.trim().isEmpty() ? null : s.trim();
	}
}
