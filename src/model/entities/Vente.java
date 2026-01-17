package model.entities;

import java.util.ArrayList;
import java.util.Date;

public class Vente {
    private int idVente ;
    private Date dateVente ;
    private float prix;
    private ArrayList<VenteProduit> histoVente = new ArrayList<VenteProduit>();
    public Vente(int idVente, Date dateVente, float prix) {
        this.idVente = idVente;
        this.dateVente = dateVente;
        this.prix = prix;

    }
    public int getIdVente() {
        return idVente;
    }

    public Date getDateVente() {
        return dateVente;
    }
    public float getPrix() {
        return prix;
    }
    public void addToHistorique(VenteProduit medicament) {
        histoVente.add(medicament);
        medicament.setVente(this);


    }
    public boolean isInclude(VenteProduit medicament) {
        return histoVente.contains(medicament);
    }

}
