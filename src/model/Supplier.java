package model;

public class Supplier {
    private int idFournisseur;
    private String nom;
    private String prenom;
    private String societe;
    private String email;
    private String telephone;
    private String adresse;
    private String description;
	public Supplier() {}
    public Supplier(String nom, String prenom, String societe,
            String email, String telephone,
            String adresse, String description) {
    	this.nom = nom;
    	this.prenom = prenom;
    	this.societe = societe;
    	this.email = email;
    	this.telephone = telephone;
    	this.adresse = adresse;
    	this.description = description;
    }
    public int getIdFournisseur() {return idFournisseur;}
    public void setIdFournisseur(int idFournisseur) {this.idFournisseur = idFournisseur;}
    public String getNom() {return nom;}      
    public String getPrenom() {return prenom;}       
    public String getSociete() {return societe;}        
    public String getEmail() {return email;}       
    public String getTelephone() {return telephone;}        
    public String getAdresse() {return adresse;}
    public String getDescription() {return description;}
}
