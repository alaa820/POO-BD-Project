package model;
import java.util.Date;

public class Commande {
    private int idCommande;
    private Date dateCommande;
    private Date dateReception;
    private String statut;
    private double prix;
    public Commande(Date dateCommande, String statut, double prix) {
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.prix = prix;
    }
    public int getIdCommande() { return idCommande; }
    public Date getDateCommande() { return dateCommande; }
    public Date getDateReception() { return dateReception; }
    public String getStatut() { return statut; }
    public double getPrix() { return prix; }
    public void setDateReception(Date dateReception) {
        this.dateReception = dateReception;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }
}
