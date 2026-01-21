package dao;

import model.Customer;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import exception.DataMissingException;

import java.util.ArrayList;

public class CustomerDao {
	public CustomerDao() {
	}

	public List<Customer> getAllCustomers() {
		// list
		List<Customer> listC = new ArrayList<>();
		
		try {
			Connection con = DatabaseConnection.getConnection();
			String query = "SELECT * FROM client";
			PreparedStatement pst = con.prepareStatement(query);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				int idClient = rs.getInt("id_client");
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				LocalDate dateNaissance = rs.getDate("date_naissance").toLocalDate();
				String sexe = rs.getString("sexe");
				String telephone = rs.getString("telephone");
				String email = rs.getString("email");
				String adresse = rs.getString("adresse");
				String description = rs.getString("description");
				Customer customer = new Customer(idClient, nom, prenom, dateNaissance, sexe, telephone, email, adresse,
						description);
				listC.add(customer);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listC;
	}

	public Customer getCustomerById(int idClient) {
		Customer customer = null;
		try {
			Connection con = DatabaseConnection.getConnection();
			String query = "SELECT * FROM client WHERE id_client = ?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idClient);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id_client");
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				LocalDate dateNaissance = rs.getDate("date_naissance").toLocalDate();
				String sexe = rs.getString("sexe");
				String telephone = rs.getString("telephone");
				String email = rs.getString("email");
				String adresse = rs.getString("adresse");
				String description = rs.getString("description");
				customer = new Customer(id, nom, prenom, dateNaissance, sexe, telephone, email, adresse, description);
				return customer;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Customer> searchCustomers(String nom, String prenom, String telephone) {
		List<Customer> listC = new ArrayList<>();
		 
		try {
			StringBuilder sql = new StringBuilder("SELECT * FROM Client WHERE 1=1");

			List<Object> params = new ArrayList<>();

			if (nom != null) {
				sql.append(" AND nom LIKE ?");
				params.add("%" + nom + "%");
			}

			if (prenom != null) {
				sql.append(" AND prenom LIKE ?");
				params.add("%" + prenom + "%");
			}

			if (telephone != null) {
				sql.append(" AND telephone LIKE ?");
				params.add("%" + telephone + "%");
			}

			Connection con = DatabaseConnection.getConnection();
			PreparedStatement pst = con.prepareStatement(sql.toString());

			for (int i = 0; i < params.size(); i++) {
				pst.setObject(i + 1, params.get(i));
			}

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				// Create Customer object and add to list (pseudo-code)
				int idClient = rs.getInt("id_client");
				String nomC = rs.getString("nom");
				String prenomC = rs.getString("prenom");
				LocalDate dateNaissance = rs.getDate("date_naissance").toLocalDate();
				String sexe = rs.getString("sexe");
				String telephoneC = rs.getString("telephone");
				String email = rs.getString("email");
				String adresse = rs.getString("adresse");
				String description = rs.getString("description");
				Customer customer = new Customer(idClient, nomC, prenomC, dateNaissance, sexe, telephoneC, email,
						adresse, description);
				listC.add(customer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listC;
	}

	public boolean addCustomer(String nom, String prenom, LocalDate dateNaissance, String sexe, String telephone,
			String email, String adresse, String description) throws DataMissingException {
		// Validate required fields
		if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || telephone.isEmpty() || email.isEmpty()
				|| adresse.isEmpty()) {
			throw new DataMissingException(
					"Please fill in all required fields (Nom, Prenom, Date Naissance, Telephone, Email, Adresse)");
		}

		try {
			Connection con = DatabaseConnection.getConnection();
			String query = "INSERT INTO client (nom, prenom, date_naissance, sexe, telephone, email, adresse, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pst = con.prepareStatement(query);

			pst.setString(1, nom);
			pst.setString(2, prenom);
			pst.setDate(3, java.sql.Date.valueOf(dateNaissance));
			pst.setString(4, sexe);
			pst.setString(5, telephone);
			pst.setString(6, email);
			pst.setString(7, adresse);
			pst.setString(8, description);

			int rowsAffected = pst.executeUpdate(); // EXECUTE THE QUERY!
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataMissingException("Database error: " + e.getMessage());
		}
	}
}