package model;

/**
 * Sale Item - represents a single item in the shopping cart
 */
public class SaleItem {
    private Medicine medicine;
    private Customer customer;
    private int quantity;
   
    public SaleItem(Medicine medicine, Customer customer, int quantity) {
		this.medicine = medicine;
		this.customer = customer;
		this.quantity = quantity;
	}
    	public Medicine getMedicine() {
		return medicine;
	}

	public Customer getCustomer() {
		return customer;
	}

	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
}