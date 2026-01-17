package model.entities;

public class Medicament {
    private String codeABarre;
    private String nom;
    private double prixAchat;
    private double prixVente;
    private double tauxTva;
    private String dosage;
    private int quantite;
    private int seuil;
    private String formePharmaceutique;
    private String emplacement;
    private boolean necessitePrescription;

    public Medicament() {}
    public Medicament(String codeABarre,String nom,double prixAchat,double prixVente,double tauxTva,String dosage,int quantite,int seuil,String formePharmaceutique,String emplacement,boolean necessitePrescription){
        this.codeABarre=codeABarre;
        this.nom=nom;
        this.prixAchat=prixAchat;
        this.prixVente=prixVente;
        this.tauxTva=tauxTva;
        this.dosage=dosage;
        this.quantite=quantite;
        this.seuil=seuil;
        this.formePharmaceutique=formePharmaceutique;
        this.emplacement=emplacement;
        this.necessitePrescription=necessitePrescription;
    }

    // Méthode pour l'alerte de stock
    public boolean estEnAlerte() {
        return this.quantite <= this.seuil;
    }
    public int getQuantite() { return quantite; }
    public void setQuantite(int q) { this.quantite = q; }
}
