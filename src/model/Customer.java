package model;


import java.time.LocalDate;

public class Customer {
	private int idClient;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String sexe;
    private String telephone;
    private String email;
    private String adresse;
    private String description;
    public Customer(int id, String nom, String prenom, LocalDate dateNaissance, String sexe,String telephone, String email, String adresse, String description) {
        this.idClient = id;
    	this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.description = description;
    }
    public int getIdClient() { return idClient; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getSexe() { return sexe; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public String getAdresse() { return adresse; }
    public String getDescription() { return description; }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String toString() {
        return nom + " " + prenom + " (" + telephone + ")";
    }
}