package model;

/**
 * Employee model class
 */
public class Employee {
    private String username; // PRIMARY KEY
    private String nom;
    private String prenom;
    private String adresse;
    private String phone;
    private String mdp; // password
    private String access; // role/access level
    
    public Employee(String username, String nom, String prenom, String adresse, String phone, String mdp, String access) {
        this.username = username;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.phone = phone;
        this.mdp = mdp;
        this.access = access;
    }
    
    public String getUsername() { return username; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getAdresse() { return adresse; }
    public String getPhone() { return phone; }
    public String getMdp() { return mdp; }
    public String getAccess() { return access; }
    
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setMdp(String mdp) { this.mdp = mdp; }
    public void setAccess(String access) { this.access = access; }
    
    @Override
    public String toString() {
        return nom + " " + prenom + " (" + username + ")";
    }
}
