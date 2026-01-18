package dao;
import javax.swing.JOptionPane;

import model.Supplier;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDao {

    // Ajouter un fournisseur
    public void addSupplier(Supplier s) throws SQLException {
        String sql = "INSERT INTO Fournisseur " +
                     "(nom, prenom, societe, email, telephone, adresse, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getNom());
            ps.setString(2, s.getPrenom());
            ps.setString(3, s.getSociete());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getTelephone());
            ps.setString(6, s.getAdresse());
            ps.setString(7, s.getDescription());

            ps.executeUpdate();
        }
    }

    // Récupérer tous les fournisseurs
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

    // Chercher fournisseur par nom (like %keyword%)
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
}
