package dao;

import util.DatabaseConnection;
import model.Customer;
import model.Employee;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

import exception.DataMissingException;
import exception.InvalideUsernameException;

import java.util.ArrayList;

public class EmployeeDao {

	public boolean authenticateUser(String username, String password) throws InvalideUsernameException {
		try {
			Connection con = DatabaseConnection.getConnection();
			String query = "SELECT * FROM utilisateur WHERE username = ? AND mdp = ?";
			PreparedStatement pst = con.prepareStatement(query);

			pst.setString(1, username);
			pst.setString(2, password);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				System.out.println("Login successful! Role: " + rs.getString("access"));
				return true;
			} else {
				System.out.println("Login failed!");
				throw new InvalideUsernameException("Invalid Username or Password");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new InvalideUsernameException("Database error");

		}

	}

	public List<Employee> getAllEmployees() {

		System.out.println("Fetching all employees");
		List<Employee> employees = new ArrayList<>();
		try {
			Connection con = DatabaseConnection.getConnection();
			String query = "SELECT * FROM utilisateur";
			PreparedStatement pst = con.prepareStatement(query);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				String username = rs.getString("username");
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				String adresse = rs.getString("adresse");
				String phone = rs.getString("phone");
				String mdp = rs.getString("mdp");
				String access = rs.getString("access");
				Employee employee = new Employee(username, nom, prenom, adresse, phone, mdp, access);
				employees.add(employee);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employees;
	}

	public List<Employee> searchEmployees(String nom, String prenom, String username) {

		List<Employee> employees = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder("SELECT * FROM utilisateur WHERE 1=1");

			List<Object> params = new ArrayList<>();

			if (nom != null) {
				sql.append(" AND nom LIKE ?");
				params.add("%" + nom + "%");
			}

			if (prenom != null) {
				sql.append(" AND prenom LIKE ?");
				params.add("%" + prenom + "%");
			}

			if (username != null) {
				sql.append(" AND telephone LIKE ?");
				params.add("%" + username + "%");
			}

			Connection con = DatabaseConnection.getConnection();
			PreparedStatement pst = con.prepareStatement(sql.toString());

			for (int i = 0; i < params.size(); i++) {
				pst.setObject(i + 1, params.get(i));
			}

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				// Create Customer object and add to list (pseudo-code)
				String usernamee = rs.getString("username");
				String nomm = rs.getString("nom");
				String prenomm = rs.getString("prenom");
				String adresse = rs.getString("adresse");
				String phone = rs.getString("phone");
				String mdp = rs.getString("mdp");
				String access = rs.getString("access");
				Employee employee = new Employee(usernamee, nomm, prenomm, adresse, phone, mdp, access);
				employees.add(employee);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employees;
	}

	public boolean addEmployee(String username, String nom, String prenom, String adresse, String phone, String mdp,
			String access) throws DataMissingException {
// Validate required fields
		if (nom.isEmpty() || prenom.isEmpty() || username.isEmpty() || mdp.isEmpty() || phone.isEmpty()
				|| adresse.isEmpty()) {
			throw new DataMissingException("Please fill in all required fields");
		}

		try {
			Connection con = DatabaseConnection.getConnection();
			String query = "INSERT INTO utilisateur (username, adresse, nom, prenom, mdp, phone, access) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pst = con.prepareStatement(query);

			pst.setString(1, username);
			pst.setString(2, adresse);
			pst.setString(3, nom);
			pst.setString(4, prenom);
			pst.setString(5, mdp);
			pst.setString(6, phone);
			pst.setString(7, access);

			int rowsAffected = pst.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) { // Catch SQLException specifically
			e.printStackTrace();
			throw new DataMissingException("Database error: " + e.getMessage());
		}
	}

	public Employee getEmployeeByUsername(String username) {
		Employee employee = null;
		try {
			Connection con = DatabaseConnection.getConnection();
			String query = "SELECT * FROM utilisateur WHERE username = ?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, username);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				String adresse = rs.getString("adresse");
				String phone = rs.getString("phone");
				String mdp = rs.getString("mdp");
				String access = rs.getString("access");
				employee = new Employee(username, nom, prenom, adresse, phone, mdp, access);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return employee;
	}
}