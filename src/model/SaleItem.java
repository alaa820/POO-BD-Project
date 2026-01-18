package model;

/**
 * Sale Item - represents a single item in the shopping cart
 */
public class SaleItem {
    private int idMedicine;
    private String medicineName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    
    public SaleItem(int idMedicine, String medicineName, int quantity, double unitPrice) {
        this.idMedicine = idMedicine;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }
    
    public int getIdMedicine() { return idMedicine; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }
    
    public void setQuantity(int quantity) { 
        this.quantity = quantity;
        this.totalPrice = quantity * unitPrice;
    }
    
    @Override
    public String toString() {
        return medicineName + " x " + quantity;
    }
}
