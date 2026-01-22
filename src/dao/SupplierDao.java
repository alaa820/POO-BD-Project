package dao;

import javax.swing.JOptionPane;
import exception.DataMissingException;
import model.Supplier;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDao {

    /**
     * Ajoute un fournisseur à la base de données
     * @param s Le fournisseur à ajouter
     * @throws SQLException En cas d'erreur SQL
     * @throws DataMissingException Si un champ obligatoire est manquant
     */
    public void addSupplier(Supplier s) throws SQLException, DataMissingException {
        // Vérification des champs obligatoires
        if(s.getNom().isEmpty() || s.getPrenom().isEmpty() || s.getSociete().isEmpty() ||
           s.getEmail().isEmpty() || s.getTelephone().isEmpty() || s.getAdresse().isEmpty()) {
            throw new DataMissingException("Veuillez remplir tous les champs obligatoires (nom, prenom, societe, email, telephone, adresse)");
        }

        String sql = "INSERT INTO Fournisseur " +
                     "(nom, prenom, societe, email, telephone, adresse, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // Remplissage des paramètres SQL
            ps.setString(1, s.getNom());
            ps.setString(2, s.getPrenom());
            ps.setString(3, s.getSociete());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getTelephone());
            ps.setString(6, s.getAdresse());
            ps.setString(7, s.getDescription());

            // Exécution de l'insertion
            ps.executeUpdate();
        }
    }

    /**
     * Récupère un fournisseur selon son ID
     * @param id ID du fournisseur
     * @return Le fournisseur correspondant ou null si non trouvé
     */
    public Supplier getSupplierById(int id) {
        Supplier s = null;
        String sql = "SELECT * FROM Fournisseur WHERE id_fournisseur = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Création de l'objet Supplier à partir du résultat SQL
                    s = new Supplier(
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("societe"),
                            rs.getString("email"),
                            rs.getString("telephone"),
                            rs.getString("adresse"),
                            rs.getString("description")
                    );
                    s.setIdFournisseur(rs.getInt("id_fournisseur"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur SQL : " + e.getMessage());
        }
        return s;
    }

    /**
     * Récupère tous les fournisseurs
     * @return Liste des fournisseurs
     */
    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Fournisseur";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Supplier s = new Supplier(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("societe"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("adresse"),
                        rs.getString("description")
                );
                s.setIdFournisseur(rs.getInt("id_fournisseur"));
                list.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur SQL : " + e.getMessage());
        }
        return list;
    }

    /**
     * Recherche des fournisseurs par nom
     * @param keyword Mot-clé à rechercher
     * @return Liste des fournisseurs correspondant au mot-clé
     */
    public List<Supplier> searchSupplierByName(String keyword) {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Fournisseur WHERE nom LIKE ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Supplier s = new Supplier(
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("societe"),
                            rs.getString("email"),
                            rs.getString("telephone"),
                            rs.getString("adresse"),
                            rs.getString("description")
                    );
                    s.setIdFournisseur(rs.getInt("id_fournisseur"));
                    list.add(s);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur SQL : " + e.getMessage());
        }
        return list;
    }

    /**
     * Compte le nombre total de fournisseurs
     * @return Nombre total de fournisseurs
     */
    public int countSuppliers() {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM fournisseur";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * Recherche des fournisseurs selon nom, prenom et société
     * @param nom Nom du fournisseur
     * @param prenom Prénom du fournisseur
     * @param societe Société du fournisseur
     * @return Liste des fournisseurs correspondants
     */
    public List<Supplier> searchSuppliers(String nom, String prenom, String societe) {
        List<Supplier> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Fournisseur WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (nom != null) {
            sql.append(" AND nom LIKE ?");
            params.add("%" + nom + "%");
        }
        if (prenom != null) {
            sql.append(" AND prenom LIKE ?");
            params.add("%" + prenom + "%");
        }
        if (societe != null) {
            sql.append(" AND societe LIKE ?");
            params.add("%" + societe + "%");
        }

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Supplier s = new Supplier(
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("societe"),
                            rs.getString("email"),
                            rs.getString("telephone"),
                            rs.getString("adresse"),
                            rs.getString("description")
                    );
                    s.setIdFournisseur(rs.getInt("id_fournisseur"));
                    list.add(s);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur SQL : " + e.getMessage());
        }
        return list;
    }
}
