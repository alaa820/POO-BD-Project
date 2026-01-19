package model;

/**
 * Medicine model class
 */
public class Medicine {
    private int idMedicine;
    private String codeBarre;
    private String nom;
    private double prixAchat;
    private double prixVente;
    private double tauxTVA;
    private String dosage;
    private int quantite;
    private int seuil;
    private String formePharmaceutique;
    private String emplacement;
    private boolean necessitePrescription;
    private Supplier supplier;
    
    public Medicine() {}
    
    public Medicine(int idMedicine, String codeBarre, String nom, double prixAchat, 
                    double prixVente, double tauxTVA, String dosage, int quantite, 
                    int seuil, String formePharmaceutique, String emplacement, 
                    boolean necessitePrescription, Supplier supplier) {
        this.idMedicine = idMedicine;
        this.codeBarre = codeBarre;
        this.nom = nom;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.tauxTVA = tauxTVA;
        this.dosage = dosage;
        this.quantite = quantite;
        this.seuil = seuil;
        this.formePharmaceutique = formePharmaceutique;
        this.emplacement = emplacement;
        this.necessitePrescription = necessitePrescription;
        this.supplier = supplier;
    }
    
    public Medicine(String codeBarre, String nom, double prixAchat, 
                    double prixVente, double tauxTVA, String dosage, int quantite, 
                    int seuil, String formePharmaceutique, String emplacement, 
                    boolean necessitePrescription, Supplier supplier) {
        this.codeBarre = codeBarre;
        this.nom = nom;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.tauxTVA = tauxTVA;
        this.dosage = dosage;
        this.quantite = quantite;
        this.seuil = seuil;
        this.formePharmaceutique = formePharmaceutique;
        this.emplacement = emplacement;
        this.necessitePrescription = necessitePrescription;
        this.supplier = supplier;
    }
    
    // Getters
    public int getIdMedicine() { return idMedicine; }
    public String getCodeBarre() { return codeBarre; }
    public String getNom() { return nom; }
    public double getPrixAchat() { return prixAchat; }
    public double getPrixVente() { return prixVente; }
    public double getTauxTVA() { return tauxTVA; }
    public String getDosage() { return dosage; }
    public int getQuantite() { return quantite; }
    public int getSeuil() { return seuil; }
    public String getFormePharmaceutique() { return formePharmaceutique; }
    public String getEmplacement() { return emplacement; }
    public boolean isNecessitePrescription() { return necessitePrescription; }
    public Supplier getSupplier() { return supplier; }
    
    // Setters
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrixAchat(double prixAchat) { this.prixAchat = prixAchat; }
    public void setPrixVente(double prixVente) { this.prixVente = prixVente; }
    public void setTauxTVA(double tauxTVA) { this.tauxTVA = tauxTVA; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public void setSeuil(int seuil) { this.seuil = seuil; }
    public void setFormePharmaceutique(String formePharmaceutique) { this.formePharmaceutique = formePharmaceutique; }
    public void setEmplacement(String emplacement) { this.emplacement = emplacement; }
    public void setNecessitePrescription(boolean necessitePrescription) { this.necessitePrescription = necessitePrescription; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    
    @Override
    public String toString() {
        return nom;
    }
}