package model;
import java.time.LocalDate;

public class Command {
    private int idCommande;
    private LocalDate dateCommande;
    private LocalDate dateReception;
    private String statut;
    private double prix;
    private int idFournisseur;

    // Constructeur sans id (car id auto-incrémenté en DB)
    public Command(LocalDate dateCommande, LocalDate dateReception, String statut, double prix, int idFournisseur) {
        this.dateCommande = dateCommande;
        this.dateReception = dateReception;
        this.statut = statut;
        this.prix = prix;
        this.idFournisseur = idFournisseur;
    }

    // Getters
    public int getIdCommande() { return idCommande; }
    public LocalDate getDateCommande() { return dateCommande; }
    public LocalDate getDateReception() { return dateReception; }
    public String getStatut() { return statut; }
    public double getPrix() { return prix; }
    public int getIdFournisseur() { return idFournisseur; }

    // Setters
    public void setDateCommande(LocalDate dateCommande) { this.dateCommande = dateCommande; }
    public void setDateReception(LocalDate dateReception) { this.dateReception = dateReception; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setPrix(double prix) { this.prix = prix; }
    public void setIdFournisseur(int idFournisseur) { this.idFournisseur = idFournisseur; }

    // Setter for id (used when loading from DB)
    public void setIdCommande(int idCommande) { this.idCommande = idCommande; }

    // toString
    @Override
    public String toString() {
        return "Commande{" +
                "idCommande=" + idCommande +
                ", dateCommande=" + dateCommande +
                ", dateReception=" + dateReception +
                ", statut='" + statut + '\'' +
                ", prix=" + prix +
                ", idFournisseur=" + idFournisseur +
                '}';
    }

}