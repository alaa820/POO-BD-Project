package ui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import dao.CustomerDao;
import dao.MedicineDao;
import model.Customer;
import model.Medicine;
import model.SaleItem;

import dao.SaleItemDao;
import dao.SaleDao;

public class SalePanel extends JPanel {

	// Customer
	private JTextField customerField;
	private JList<String> customerSuggestionList;
	private DefaultListModel<String> customerSuggestionModel;
	private Customer selectedCustomer;

	// Medicine
	private JTextField medicineField;
	private JList<String> medicineSuggestionList;
	private DefaultListModel<String> medicineSuggestionModel;
	private Medicine selectedMedicine;
	private JLabel medicineIdLabel;

	// Quantity & Cart
	private JSpinner quantitySpinner;
	private DefaultTableModel cartTableModel;
	private JTable cartTable;
	private JLabel totalLabel;
	private List<SaleItem> cartItems;

	// DAOs
	private CustomerDao customerDao;
	private MedicineDao medicineDao;
	private SaleItemDao saleItemDao;
	private SaleDao saleDao;

	/*
	 * creating SalePanel 
	 * at the top there is customer and medicine suggestion fields with quantity spinner and add to cart button
	 * in the center there is the cart table with total and checkout button
	 * */
	public SalePanel() {
		super(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		cartItems = new ArrayList<>();
		customerDao = new CustomerDao();
		medicineDao = new MedicineDao();

		JPanel inputPanel = createInputPanel();
		JPanel cartPanel = createCartPanel();

		add(inputPanel, BorderLayout.NORTH);
		add(cartPanel, BorderLayout.CENTER);
	}

	private JPanel createInputPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(new Color(245, 245, 245));
		panel.setBorder(BorderFactory.createTitledBorder("Sale Information"));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		int row = 0;
		row = addCustomerSuggestionField(panel, gbc, row);//suggestion field for customer
		row = addMedicineSuggestionField(panel, gbc, row);//suggestion field for medicine

		// Quantity
		gbc.gridx = 0;
		gbc.gridy = row;
		JLabel quantityLabel = new JLabel("Quantity:");
		quantityLabel.setFont(new Font("Arial", Font.BOLD, 12));
		panel.add(quantityLabel, gbc);
		gbc.gridx = 1;
		quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		panel.add(quantitySpinner, gbc);

		//add to cart button
		gbc.gridx = 2;
		JButton addToCartBtn = new JButton("Add to Cart");
		addToCartBtn.addActionListener(e -> {handleAddToCart();customerField.setEnabled(false);
		
		});
		panel.add(addToCartBtn, gbc);

		return panel;
	}

	/*
	 * a suggestion bar that shows all customers having the same name
	 * the user then selects the desired customer from the list
	 * the selected user is what we will be working with in the entirety of the cart
	 * */
	private int addCustomerSuggestionField(JPanel panel, GridBagConstraints gbc, int row) {
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel customerLabel = new JLabel("Customer:");
		customerLabel.setFont(new Font("Arial", Font.BOLD, 12));
		panel.add(customerLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = row++;
		customerField = new JTextField(20);
		customerField.setFont(new Font("Arial", Font.PLAIN, 11));
		panel.add(customerField, gbc);

		customerSuggestionModel = new DefaultListModel<>();
		customerSuggestionList = new JList<>(customerSuggestionModel);
		customerSuggestionList.setFont(new Font("Arial", Font.PLAIN, 10));
		customerSuggestionList.setVisibleRowCount(4);
		customerSuggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane suggestionScroll = new JScrollPane(customerSuggestionList);
		suggestionScroll.setPreferredSize(new Dimension(250, 80));

		gbc.gridx = 1;
		gbc.gridy = row++;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 0.1;
		panel.add(suggestionScroll, gbc);
		gbc.weighty = 0;

		// Document listener for any typing
		customerField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {updateSuggestions();}
			public void removeUpdate(DocumentEvent e) {updateSuggestions();}
			public void changedUpdate(DocumentEvent e) {updateSuggestions();}

			private void updateSuggestions() {
				String text = customerField.getText().trim();
				customerSuggestionModel.clear();
				if (!text.isEmpty()) {
					List<Customer> list = customerDao.searchCustomers(text, null, null);//search by name
					for (Customer c : list) {
						customerSuggestionModel.addElement(c.getIdClient() + " " + c.getNom() + " " + c.getPrenom());
					}
					suggestionScroll.setVisible(!customerSuggestionModel.isEmpty());
				} else {
					suggestionScroll.setVisible(false);
					selectedCustomer = null;
				}
				panel.revalidate();
				panel.repaint();
			}
		});

		customerSuggestionList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				String selected = customerSuggestionList.getSelectedValue();
				if (selected != null) {
					customerField.setText(selected);
					int id = Integer.parseInt(selected.split(" ")[0]);
					Customer c = customerDao.getCustomerById(id);
					if (c != null) {
						selectedCustomer = c;//set selected customer
					}
					suggestionScroll.setVisible(false);
					panel.revalidate();
				}
			}
		});

		suggestionScroll.setVisible(false);
		return row;
	}

	
	
	/*
	 * a suggestion bar that shows all medicines having the same name
	 * the user then selects the desired medicine from the list
	 * */
	private int addMedicineSuggestionField(JPanel panel, GridBagConstraints gbc, int row) {
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel medicineLabel = new JLabel("Medicine:");
		medicineLabel.setFont(new Font("Arial", Font.BOLD, 12));
		panel.add(medicineLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = row++;
		medicineField = new JTextField(20);
		medicineField.setFont(new Font("Arial", Font.PLAIN, 11));
		panel.add(medicineField, gbc);

		medicineSuggestionModel = new DefaultListModel<>();
		medicineSuggestionList = new JList<>(medicineSuggestionModel);
		medicineSuggestionList.setFont(new Font("Arial", Font.PLAIN, 10));
		medicineSuggestionList.setVisibleRowCount(4);
		medicineSuggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane suggestionScroll = new JScrollPane(medicineSuggestionList);
		suggestionScroll.setPreferredSize(new Dimension(250, 80));

		gbc.gridx = 1;
		gbc.gridy = row++;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 0.1;
		panel.add(suggestionScroll, gbc);
		gbc.weighty = 0;

		gbc.gridx = 2;
		gbc.gridy = row - 1;
		medicineIdLabel = new JLabel("ID: -");
		medicineIdLabel.setFont(new Font("Arial", Font.BOLD, 11));
		medicineIdLabel.setForeground(new Color(0, 100, 200));
		panel.add(medicineIdLabel, gbc);

		// Document listener
		medicineField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				updateSuggestions();
			}

			public void removeUpdate(DocumentEvent e) {
				updateSuggestions();
			}

			public void changedUpdate(DocumentEvent e) {
				updateSuggestions();
			}

			private void updateSuggestions() {
				String text = medicineField.getText().trim();
				medicineSuggestionModel.clear();
				if (!text.isEmpty()) {
					List<Medicine> list = medicineDao.searchMedicines("", text, "");
					System.out.println("Found " + list.size() + " medicines for query: " + text);
					for (Medicine m : list) {
						medicineSuggestionModel
								.addElement(m.getCodeBarre() + " " + m.getNom() + " - " + m.getSupplier().getSociete());
					}
					suggestionScroll.setVisible(!medicineSuggestionModel.isEmpty());
				} else {
					suggestionScroll.setVisible(false);
					selectedMedicine = null;
					medicineIdLabel.setText("ID: -");
				}
				panel.revalidate();
				panel.repaint();
			}
		});

		medicineSuggestionList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				String selected = medicineSuggestionList.getSelectedValue();
				if (selected != null) {
					medicineField.setText(selected);

					Medicine m = medicineDao.getMedecineByCode(selected.split(" ")[0]);
					if (m != null) {
						selectedMedicine = m;
					}
					suggestionScroll.setVisible(false);
					panel.revalidate();
				}
			}
		});

		suggestionScroll.setVisible(false);
		return row;
	}

	private JPanel createCartPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

		String[] columns = { "Medicine", "Quantity", "Unit Price", "Total" };
		cartTableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};

		cartTable = new JTable(cartTableModel);
		panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		totalLabel = new JLabel("Total: 0.00 DT");
		bottomPanel.add(totalLabel, BorderLayout.WEST);

		JPanel btnPanel = new JPanel();
		JButton checkout = new JButton("Checkout");
		checkout.addActionListener(e -> handleCheckout());
		btnPanel.add(checkout);
		bottomPanel.add(btnPanel, BorderLayout.EAST);

		panel.add(bottomPanel, BorderLayout.SOUTH);
		return panel;
	}
/*
 * handling add to cart should be checked for:
 * filling all text fields
 * the quantity of selected medicines
 * */
	private void handleAddToCart() {
		if (selectedCustomer == null) {
			JOptionPane.showMessageDialog(this, "Please select a customer", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (selectedMedicine == null) {
			JOptionPane.showMessageDialog(this, "Please select a medicine", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int quantity = (Integer) quantitySpinner.getValue();
		if (quantity <= 0) {
			JOptionPane.showMessageDialog(this, "Quantity must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (quantity > selectedMedicine.getQuantite()) {
			JOptionPane.showMessageDialog(this, "Insufficient stock for the selected medicine", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		boolean alreadyInCart = false;
		for (SaleItem item : cartItems) {
			if (item.getMedicine().getCodeBarre().equals(selectedMedicine.getCodeBarre())) {
				if (item.getQuantity() + quantity > selectedMedicine.getQuantite()) {
					JOptionPane.showMessageDialog(this, "Insufficient stock to add more of this medicine", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				item.setQuantity(item.getQuantity() + quantity);
				System.out.println("Updated quantity for " + item.getMedicine().getNom() + " to " + item.getQuantity());
				alreadyInCart = true;
				break;
			}
		}

		if (alreadyInCart) {
			// refresh cart
			cartTableModel.setRowCount(0);
			for (SaleItem it : cartItems) {
				cartTableModel.addRow(new Object[] { it.getMedicine().getNom(), it.getQuantity(),
						String.format("%.2f", it.getMedicine().getPrixVente()),
						String.format("%.2f", it.getMedicine().getPrixVente() * it.getQuantity()) });
			}
		} else {
			SaleItem item = new SaleItem(selectedMedicine, quantity);
			cartItems.add(item);
			cartTableModel.addRow(new Object[] { item.getMedicine().getNom(), item.getQuantity(),
					String.format("%.2f", item.getMedicine().getPrixVente()),
					String.format("%.2f", item.getMedicine().getPrixVente() * item.getQuantity())

			});
		}

		updateCartTotal();

		// Reset medicine field
		medicineField.setText("");
		medicineIdLabel.setText("ID: -");
		selectedMedicine = null;
		quantitySpinner.setValue(1);
		medicineSuggestionModel.clear();
	}

	private void handleCheckout() {
		if (selectedCustomer == null) {
			JOptionPane.showMessageDialog(this, "Please select a customer", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (cartItems.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Cart is empty", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		double total = 0;
		for (SaleItem item : cartItems) {
			total += item.getQuantity() * item.getMedicine().getPrixVente();
			// Update medicine stock
			Medicine m = item.getMedicine();
			m.setQuantite(m.getQuantite() - item.getQuantity());
			medicineDao.updateMedicineQuantity(m.getCodeBarre(), m.getQuantite());
		}
		
		saleDao = new SaleDao();
		int id = saleDao.addSale(total, selectedCustomer);

		for (SaleItem item : cartItems) {
			
			saleItemDao = new SaleItemDao();
			saleItemDao.saveSaleItem(id, item);
			if (item.getMedicine().getQuantite() < item.getMedicine().getSeuil()) {
				JOptionPane.showMessageDialog(this,
						"Warning: Stock for medicine " + item.getMedicine().getNom() + " is below the threshold!",
						"Stock Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		JOptionPane.showMessageDialog(this,
				"Sale completed successfully!\n\n" + "Customer: " + selectedCustomer.getNom() + " "
						+ selectedCustomer.getPrenom() + "\n" + "Total Amount: " + String.format("%.2f", total)
						+ " DT\n" + "Items: " + cartItems.size(),
				"Checkout Success", JOptionPane.INFORMATION_MESSAGE);

		// Reset
		resetSalePanel();
		customerField.setEnabled(true
				);
	}

	private void updateCartTotal() {
		double total = 0.0;
		for (SaleItem item : cartItems) {
			total += item.getQuantity() * item.getMedicine().getPrixVente();
		}
		totalLabel.setText("Total: " + String.format("%.2f", total) + " DT");
	}

	private void resetSalePanel() {
		customerField.setText("");
		selectedCustomer = null;
		customerSuggestionModel.clear();

		medicineField.setText("");
		selectedMedicine = null;
		medicineIdLabel.setText("ID: -");
		medicineSuggestionModel.clear();

		quantitySpinner.setValue(1);
		cartItems.clear();
		cartTableModel.setRowCount(0);
		updateCartTotal();
		cartItems = null;
	}
}