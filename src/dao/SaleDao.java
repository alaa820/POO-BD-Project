package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.Customer;
import java.time.LocalDateTime;
import util.Session;
public class SaleDao {

	public int addSale(double totalAmount, Customer c) {
		// TODO Auto-generated method stub
		try {
			Connection con  = util.DatabaseConnection.getConnection();
			String query = "INSERT INTO vente (date_vente, prix, id_client, username_name) VALUES (?, ?, ?,?)";
			PreparedStatement pst = con.prepareStatement(query);
			LocalDateTime dateTime = LocalDateTime.now();
			pst.setObject(1, dateTime);
			pst.setDouble(2, totalAmount);
			pst.setInt(3, c.getIdClient());
			pst.setString(4, Session.getCurrentUser().getUsername());
			pst.executeUpdate();
			//search the id of the last inserted sale
			String query2  = "SELECT id_vente FROM vente ORDER BY id_vente DESC LIMIT 1";
			PreparedStatement pst2 = con.prepareStatement(query2);
			var rs = pst2.executeQuery();
			if(rs.next()) {
				return rs.getInt("id_vente");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}