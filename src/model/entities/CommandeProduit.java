package model.entities;


import java.lang.constant.ModuleDesc;

public class CommandeProduit {
    private Commande commande ;
    private Medicament produit ;
    private int quantite ;
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    public void setMedicament(Medicament medicament) {
        produit = medicament;

    }
    public void setCommande(Commande commande) {
        this.commande = commande;
        if(!(commande.isInclude(this)))
        {
            commande.addToHistorique(this);
        }

    }
    public int getQuantite() {
        return  quantite;
    }
    public Medicament getMedicament() {
        return  produit ;
    }
    public Commande getCommande() {
        return  commande ;
    }


}

