package model;

/**
 * Sale Item - represents a single item in the shopping cart
 */
public class SaleItem {
    private Medicine medicine;
   private Sale sale;
    private int quantity;
   
    public SaleItem(Medicine medicine, int quantity) {
		this.medicine = medicine;
		this.quantity = quantity;
	}
    	public Medicine getMedicine() {
		return medicine;
	}
    	public SaleItem(Medicine medicine, Sale sale, int quantity) {
    				this.medicine = medicine;
					this.sale = sale;
					this.quantity = quantity;
    	}

	

	public int getQuantity() {return quantity;}
	public void setQuantity(int quantity) {this.quantity = quantity;}
	public Sale getSale() {return sale;}
	
}