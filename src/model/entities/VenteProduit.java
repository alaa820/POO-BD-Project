package model.entities;



public class VenteProduit {
    private Vente vente;
    private Medicament med ;
    private int quantity ;
    public void setVente(Vente vente) {
        this.vente = vente;
        if (!(vente.isInclude(this)))

        {
            vente.addToHistorique(this);
        }

    }
    public void setMedicament(Medicament med) {
        this.med = med;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public Vente getVente() {
        return vente;

    }
    public Medicament getMedicament() {
        return med;
    }
    public int getQuantity() {
        return quantity;
    }




}
