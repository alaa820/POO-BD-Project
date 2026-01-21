package dao;
import javax.swing.JOptionPane;

import exception.DataMissingException;
import model.Supplier;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDao {

   
    public void addSupplier(Supplier s) throws SQLException,DataMissingException {
        String sql = "INSERT INTO Fournisseur " +
                     "(nom, prenom, societe, email, telephone, adresse, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        if(s.getNom().isEmpty()|| s.getPrenom().isEmpty() || s.getSociete().isEmpty() ||s.getEmail().isEmpty() || s.getTelephone().isEmpty() || s.getAdresse().isEmpty())
        	throw new DataMissingException("Plead fill in all required fields(nom, prenom, societe, email, telephone, adresse)" );
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

    public Supplier getSupplierById(int id) {
		Supplier s = null;
		String sql = "SELECT * FROM Fournisseur WHERE id_fournisseur = ?";
		try (Connection c = DatabaseConnection.getConnection();
			 PreparedStatement ps = c.prepareStatement(sql)) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
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