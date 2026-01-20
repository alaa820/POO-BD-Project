package model;

public class CommandItem {
	private Command c;
	private Medicine m;
	private int quantity;
	
	public CommandItem(Command c, Medicine m, int quantity) {
		this.c = c;
		this.m = m;
		this.quantity = quantity;
	}
	public CommandItem(Medicine m, int quantity) {
		this.m = m;
		this.quantity = quantity;
		
	}
	public Command getC() {
		return c;
	}
	public Medicine getM() {
		return m;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setC(Command c) {
		this.c = c;
	}
	public void setM(Medicine m) {
		this.m = m;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
