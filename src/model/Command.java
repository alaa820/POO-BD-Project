package model;
import java.time.LocalDate;

public class Command {
    private int idCommande;
    private LocalDate dateCommande;
    private LocalDate dateReception;
    private String statut;
    private double prix;
    private Supplier s;
	
	public Command() {}
    public Command(LocalDate dateCommande, LocalDate dateReception, String statut, double prix, Supplier s) {
        this.dateCommande = dateCommande;
        this.dateReception = dateReception;
        this.statut = statut;
        this.prix = prix;
        this.s = s;
    }

    // Getters
    public int getIdCommande() { return idCommande; }
    public LocalDate getDateCommande() { return dateCommande; }
    public LocalDate getDateReception() { return dateReception; }
    public String getStatut() { return statut; }
    public double getPrix() { return prix; }
    public Supplier getSupplier() { return s; }

    // Setters
    public void setDateCommande(LocalDate dateCommande) { this.dateCommande = dateCommande; }
    public void setDateReception(LocalDate dateReception) { this.dateReception = dateReception; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setPrix(double prix) { this.prix = prix; }
    public void setSupplier(Supplier s) { this.s = s; }
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
                ", idFournisseur=" + s.getIdFournisseur()+
                '}';
    }

}
