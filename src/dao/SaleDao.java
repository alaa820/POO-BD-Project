package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import util.DatabaseConnection;
import model.Customer;
import java.time.LocalDateTime;
import util.Session;
import model.Employee;
import model.Customer;
import model.Sale;
import java.util.List;
import java.util.ArrayList;
import dao.CustomerDao;

public class SaleDao {

	public int addSale(double totalAmount, Customer c) {
		// TODO Auto-generated method stub
		try {
			Connection con = util.DatabaseConnection.getConnection();
			String query = "INSERT INTO vente (date_vente, prix, id_client, username_name) VALUES (?, ?, ?,?)";
			PreparedStatement pst = con.prepareStatement(query);
			LocalDateTime dateTime = LocalDateTime.now();
			pst.setObject(1, dateTime);
			pst.setDouble(2, totalAmount);
			pst.setInt(3, c.getIdClient());
			pst.setString(4, Session.getCurrentUser().getUsername());
			pst.executeUpdate();
			String query2 = "SELECT id_vente FROM vente ORDER BY id_vente DESC LIMIT 1";
			PreparedStatement pst2 = con.prepareStatement(query2);
			var rs = pst2.executeQuery();
			if (rs.next()) {
				return rs.getInt("id_vente");
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return -1;
	}

	public List<Sale> getAllSales() {

		List<Sale> listS = new ArrayList<>();

		try {
			Connection con = DatabaseConnection.getConnection();
			// Sample query execution (pseudo-code)
			String query = "SELECT * FROM vente";
			PreparedStatement pst = con.prepareStatement(query);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				int idVente = rs.getInt("id_vente");
				LocalDateTime dateVente = rs.getObject("date_vente", LocalDateTime.class);
				double prix = rs.getDouble("prix");
				int idClient = rs.getInt("id_client");
				String username = rs.getString("username_name");

				Customer c = new CustomerDao().getCustomerById(idClient);
				Employee e = new EmployeeDao().getEmployeeByUsername(username);
				Sale sale = new Sale(idVente, dateVente, prix, c, e);

				listS.add(sale);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listS;
	}

	public Sale getSaleById(int idVente) {
		Sale sale = null;
		try {
			Connection con = DatabaseConnection.getConnection();

			String query = "SELECT * FROM vente WHERE id_vente = ?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idVente);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id_vente");
				LocalDateTime dateVente = rs.getObject("date_vente", LocalDateTime.class);
				double prix = rs.getDouble("prix");
				int idClient = rs.getInt("id_client");
				String username = rs.getString("username_name");

				Customer c = new CustomerDao().getCustomerById(idClient);
				Employee e = new EmployeeDao().getEmployeeByUsername(username);
				sale = new Sale(id, dateVente, prix, c, e);
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return sale;
	}

	public double getTotalRevenue() {
		double totalRevenue = 0.0;
		String sql = "SELECT SUM(prix) AS chiffre_affaires FROM vente";

		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				totalRevenue = rs.getDouble("chiffre_affaires");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return totalRevenue;
	}

	public List<Sale> getSaleByEmployee(String username) {
		List<Sale> listS = new ArrayList<>();

		try {
			Connection con = DatabaseConnection.getConnection();

			String query = "SELECT * FROM vente WHERE username_name = ?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, username);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				int idVente = rs.getInt("id_vente");
				LocalDateTime dateVente = rs.getObject("date_vente", LocalDateTime.class);
				double prix = rs.getDouble("prix");
				int idClient = rs.getInt("id_client");
				String usern = rs.getString("username_name");

				Customer c = new CustomerDao().getCustomerById(idClient);
				Employee e = new EmployeeDao().getEmployeeByUsername(usern);
				Sale sale = new Sale(idVente, dateVente, prix, c, e);

				listS.add(sale);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listS;
	}

}