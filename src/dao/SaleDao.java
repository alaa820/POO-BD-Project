package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import util.DatabaseConnection;
import util.Session;
import model.Customer;
import model.Employee;
import model.Sale;
import exception.DataMissingException;

public class SaleDao {

    // Ajoute une vente et retourne son ID
    public int addSale(double totalAmount, Customer c) {
        String insertQuery = "INSERT INTO vente (date_vente, prix, id_client, username_name) VALUES (?, ?, ?, ?)";
        String getLastIdQuery = "SELECT id_vente FROM vente ORDER BY id_vente DESC LIMIT 1";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(insertQuery);
             PreparedStatement pst2 = con.prepareStatement(getLastIdQuery)) {

            LocalDateTime dateTime = LocalDateTime.now();
            pst.setObject(1, dateTime);
            pst.setDouble(2, totalAmount);
            pst.setInt(3, c.getIdClient());
            pst.setString(4, Session.getCurrentUser().getUsername());

            pst.executeUpdate();

            try (ResultSet rs = pst2.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_vente");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Récupère toutes les ventes
    public List<Sale> getAllSales() {
        List<Sale> listS = new ArrayList<>();
        String query = "SELECT * FROM vente";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int idVente = rs.getInt("id_vente");
                LocalDateTime dateVente = rs.getObject("date_vente", LocalDateTime.class);
                double prix = rs.getDouble("prix");
                int idClient = rs.getInt("id_client");
                String username = rs.getString("username_name");

                Customer c = new CustomerDao().getCustomerById(idClient);
                Employee e = new EmployeeDao().getEmployeeByUsername(username);
                listS.add(new Sale(idVente, dateVente, prix, c, e));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listS;
    }

    // Récupère une vente par son ID
    public Sale getSaleById(int idVente) {
        Sale sale = null;
        String query = "SELECT * FROM vente WHERE id_vente = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, idVente);

            try (ResultSet rs = pst.executeQuery()) {
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
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sale;
    }

    // Calcule le chiffre d'affaires total
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

    // Récupère toutes les ventes d'un employé
    public List<Sale> getSaleByEmployee(String username) {
        List<Sale> listS = new ArrayList<>();
        String query = "SELECT * FROM vente WHERE username_name = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int idVente = rs.getInt("id_vente");
                    LocalDateTime dateVente = rs.getObject("date_vente", LocalDateTime.class);
                    double prix = rs.getDouble("prix");
                    int idClient = rs.getInt("id_client");
                    String usern = rs.getString("username_name");

                    Customer c = new CustomerDao().getCustomerById(idClient);
                    Employee e = new EmployeeDao().getEmployeeByUsername(usern);
                    listS.add(new Sale(idVente, dateVente, prix, c, e));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listS;
    }

}
