package model;

import java.time.LocalDateTime;

public class Sale {

    private int idVente;
    private LocalDateTime dateVente;
    private double prix;

    private Customer client;      // references the customer who made the purchase
    private Employee employee; // references the employee who handled the sale

    // Constructor with all fields
    
    // Empty constructor
    public Sale() {}

    public Sale(int idVente,LocalDateTime dateVente, double prix, Customer client, Employee employee) {
    	this.idVente = idVente;
		this.dateVente = dateVente;
		this.prix = prix;
		this.client = client;
		this.employee = employee;
	}
    // Getters and setters
    public int getIdVente() {return idVente;}
    public LocalDateTime getDateVente() {return dateVente;}
    public double getPrix() {return prix;}
    public Customer getClient() {return client;}
    public Employee getEmployee() {return employee;}
	
    public void setEmployee(Employee employee) {this.employee = employee;}
	public void setIdVente(int idVente) {this.idVente = idVente;}
	public void setDateVente(LocalDateTime dateVente) {this.dateVente = dateVente;}
	public void setPrix(double prix) {this.prix = prix;}
	public void setClient(Customer client) {this.client = client;}
    
	@Override
    public String toString() {
        return "Sale{" +
                "idVente=" + idVente +
                ", dateVente=" + dateVente +
                ", prix=" + prix +
                ", client=" + (client != null ? client.getNom() + " " + client.getPrenom() : "null") +
                ", employee=" + (employee != null ? employee.getUsername() : "null") +
                '}';
    }
}
