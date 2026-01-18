package dao;

import model.Supplier;
import util.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDao {

    public void addSupplier(Supplier s) throws SQLException {
        String sql = "INSERT INTO Fournisseur " +
                     "(nom, prenom, societe, email, telephone, adresse, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DataBaseConnection.getConnection();
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

    public List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Fournisseur";
        try (Connection c = DataBaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Supplier s = new Supplier();
                s.setIdFournisseur(rs.getInt("id_fournisseur"));
                list.add(s);
            }
        }
        return list;
    }
}
