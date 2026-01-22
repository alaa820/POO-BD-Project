package dao;

import model.Customer;
import model.Medicine;
import model.Supplier;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import exception.CodeBarreExistsException;
import exception.DataMissingException;

public class MedicineDao {

    // Récupère un médicament par son code-barre
    public Medicine getMedecineByCode(String codeBarre) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM medicament WHERE code_barre = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, codeBarre);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Extraction des données du médicament
                String nomMed = rs.getString("nom");
                double prixAchat = rs.getDouble("prix_achat");
                double prixVente = rs.getDouble("prix_vente");
                double tauxTVA = rs.getDouble("taux_TVA");
                String dosage = rs.getString("dosage");
                int quantite = rs.getInt("quantite");
                int seuil = rs.getInt("seuil");
                String formePharmaceutique = rs.getString("forme_pharmaceutique");
                String emplacement = rs.getString("emplacement");
                boolean necessitePrescription = rs.getBoolean("necessite_prescription");

                // Récupération du fournisseur
                Supplier supp = null;
                String querySupplier = "SELECT * FROM fournisseur WHERE id_fournisseur = ?";
                try (PreparedStatement pstSupplier = con.prepareStatement(querySupplier)) {
                    pstSupplier.setInt(1, rs.getInt("id_fournisseur"));
                    ResultSet rsSupplier = pstSupplier.executeQuery();
                    if (rsSupplier.next()) {
                        supp = new Supplier(rsSupplier.getString("nom"), rsSupplier.getString("prenom"),
                                rsSupplier.getString("societe"), rsSupplier.getString("email"),
                                rsSupplier.getString("telephone"), rsSupplier.getString("adresse"),
                                rsSupplier.getString("description"));
                        supp.setIdFournisseur(rsSupplier.getInt("id_fournisseur"));
                    }
                }

                return new Medicine(codeBarre, nomMed, prixAchat, prixVente, tauxTVA, dosage, quantite, seuil,
                        formePharmaceutique, emplacement, necessitePrescription, supp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Récupère tous les médicaments
    public List<Medicine> getAllMedicines() {
        List<Medicine> medecines = new ArrayList<>();
        String query = "SELECT * FROM medicament";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String codeBarre = rs.getString("code_barre");
                String nom = rs.getString("nom");
                double prixAchat = rs.getDouble("prix_achat");
                double prixVente = rs.getDouble("prix_vente");
                double tauxTVA = rs.getDouble("taux_TVA");
                String dosage = rs.getString("dosage");
                int quantite = rs.getInt("quantite");
                int seuil = rs.getInt("seuil");
                String formePharmaceutique = rs.getString("forme_pharmaceutique");
                String emplacement = rs.getString("emplacement");
                boolean necessitePrescription = rs.getBoolean("necessite_prescription");

                Supplier supplier = null;
                String querySupplier = "SELECT * FROM fournisseur WHERE id_fournisseur = ?";
                try (PreparedStatement pstSupplier = con.prepareStatement(querySupplier)) {
                    pstSupplier.setInt(1, rs.getInt("id_fournisseur"));
                    ResultSet rsSupplier = pstSupplier.executeQuery();
                    if (rsSupplier.next()) {
                        supplier = new Supplier(rsSupplier.getString("nom"), rsSupplier.getString("prenom"),
                                rsSupplier.getString("societe"), rsSupplier.getString("email"),
                                rsSupplier.getString("telephone"), rsSupplier.getString("adresse"),
                                rsSupplier.getString("description"));
                        supplier.setIdFournisseur(rsSupplier.getInt("id_fournisseur"));
                    }
                }

                medecines.add(new Medicine(codeBarre, nom, prixAchat, prixVente, tauxTVA, dosage, quantite, seuil,
                        formePharmaceutique, emplacement, necessitePrescription, supplier));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return medecines;
    }

    // Recherche des médicaments par code-barre, nom ou fournisseur
    public List<Medicine> searchMedicines(String codeBarre, String nom, String supplier) {
        List<Medicine> medecines = new ArrayList<>();

        try {
            StringBuilder sql = new StringBuilder(
                    "SELECT m.* FROM medicament m JOIN fournisseur f ON m.id_fournisseur = f.id_fournisseur WHERE 1=1");
            List<Object> params = new ArrayList<>();

            if (codeBarre != null) {
                sql.append(" AND m.code_barre LIKE ?");
                params.add("%" + codeBarre + "%");
            }
            if (nom != null) {
                sql.append(" AND m.nom LIKE ?");
                params.add("%" + nom + "%");
            }
            if (supplier != null) {
                sql.append(" AND f.nom LIKE ?");
                params.add("%" + supplier + "%");
            }

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement pst = con.prepareStatement(sql.toString())) {

                for (int i = 0; i < params.size(); i++) {
                    pst.setObject(i + 1, params.get(i));
                }

                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    medecines.add(getMedecineByCode(rs.getString("code_barre"))); // Réutilisation méthode existante
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return medecines;
    }

    // Ajoute un médicament en vérifiant la présence du code-barre
    public boolean addMedicine(String codeBarre, String nom, double prixAchat, double prixVente, double tauxTVA,
                               String dosage, int quantite, int seuil, String formePharmaceutique, String emplacement,
                               boolean necessitePrescription, Supplier supplier)
            throws CodeBarreExistsException, DataMissingException {
        if (codeBarre.isEmpty() || nom.isEmpty() || prixAchat <= 0 || tauxTVA <= 0 || dosage.isEmpty()
                || quantite <= 0 || seuil <= 0 || formePharmaceutique.isEmpty() || emplacement.isEmpty()
                || supplier == null) {
            throw new DataMissingException("Veuillez remplir tous les champs");
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            // Vérification code-barre existant
            String checkQuery = "SELECT COUNT(*) FROM medicament WHERE code_barre = ?";
            try (PreparedStatement checkPst = con.prepareStatement(checkQuery)) {
                checkPst.setString(1, codeBarre);
                ResultSet rs = checkPst.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new CodeBarreExistsException("Medicine with barcode " + codeBarre + " already exists");
                }
            }

            String query = "INSERT INTO medicament (code_barre, nom, prix_achat, prix_vente, taux_TVA, dosage, quantite, seuil, forme_pharmaceutique, emplacement, necessite_prescription, id_fournisseur) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, codeBarre);
                pst.setString(2, nom);
                pst.setDouble(3, prixAchat);
                pst.setDouble(4, prixVente);
                pst.setDouble(5, tauxTVA);
                pst.setString(6, dosage);
                pst.setInt(7, quantite);
                pst.setInt(8, seuil);
                pst.setString(9, formePharmaceutique);
                pst.setString(10, emplacement);
                pst.setBoolean(11, necessitePrescription);
                pst.setInt(12, supplier.getIdFournisseur());

                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Met à jour un médicament existant
    public boolean updateMedicine(Medicine m) {
        String query = "UPDATE medicament SET nom = ?, prix_achat = ?, prix_vente = ?, taux_tva = ?, dosage = ?, quantite = ?, seuil = ?, forme_pharmaceutique = ?, emplacement = ?, necessite_prescription = ?, id_fournisseur = ? WHERE code_barre = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, m.getNom());
            pst.setDouble(2, m.getPrixAchat());
            pst.setDouble(3, m.getPrixVente());
            pst.setDouble(4, m.getTauxTVA());
            pst.setString(5, m.getDosage());
            pst.setInt(6, m.getQuantite());
            pst.setInt(7, m.getSeuil());
            pst.setString(8, m.getFormePharmaceutique());
            pst.setString(9, m.getEmplacement());
            pst.setBoolean(10, m.isNecessitePrescription());
            pst.setInt(11, m.getSupplier().getIdFournisseur());
            pst.setString(12, m.getCodeBarre());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprime un médicament par son code-barre
    public boolean deleteByCodeBarre(String codeBarre) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("DELETE FROM medicament WHERE code_barre = ?")) {

            pst.setString(1, codeBarre);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Met à jour la quantité d'un médicament
    public void updateMedicineQuantity(String codeBarre, int newQuantity) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("UPDATE medicament SET quantite = ? WHERE code_barre = ?")) {

            pst.setInt(1, newQuantity);
            pst.setString(2, codeBarre);
            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Compte les médicaments en rupture ou au seuil
    public int countLowStockMedicines() {
        String sql = "SELECT COUNT(*) FROM medicament WHERE quantite <= seuil";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Compte tous les médicaments
    public int countMedicines() {
        String sql = "SELECT COUNT(*) FROM medicament";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Récupère tous les médicaments au seuil
    public List<Medicine> getLowStockMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE quantite <= seuil";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new Medicine(rs.getString("code_barre"), rs.getString("nom"),
                        rs.getDouble("prix_achat"), rs.getDouble("prix_vente"),
                        rs.getDouble("taux_TVA"), rs.getString("dosage"),
                        rs.getInt("quantite"), rs.getInt("seuil"),
                        rs.getString("forme_pharmaceutique"), rs.getString("emplacement"),
                        rs.getBoolean("necessite_prescription"), null));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Récupère tous les médicaments d’un fournisseur
    public List<Medicine> getMedicineBySupplier(int supplierID) {
        List<Medicine> medecines = new ArrayList<>();
        String query = "SELECT * FROM medicament WHERE id_fournisseur = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, supplierID);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Supplier supplier = new SupplierDao().getSupplierById(supplierID);
                medecines.add(new Medicine(rs.getString("code_barre"), rs.getString("nom"),
                        rs.getDouble("prix_achat"), rs.getDouble("prix_vente"), rs.getDouble("taux_TVA"),
                        rs.getString("dosage"), rs.getInt("quantite"), rs.getInt("seuil"),
                        rs.getString("forme_pharmaceutique"), rs.getString("emplacement"),
                        rs.getBoolean("necessite_prescription"), supplier));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return medecines;
    }

}
