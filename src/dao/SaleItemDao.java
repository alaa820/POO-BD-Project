package dao;
import model.Customer;
import model.Medicine;

import model.Supplier;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.SaleItem;

public class SaleItemDao {

	
	public void saveSaleItem(int idVente, SaleItem saleItem) {
	    String sql = "INSERT INTO venteproduit (id_vente, code_barre, quantite) VALUES (?,?, ?)";
	    
	    try (java.sql.Connection conn = DatabaseConnection.getConnection();
	         java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, idVente);
	        pstmt.setString(2, saleItem.getMedicine().getCodeBarre());
	        
	        pstmt.setInt(3, saleItem.getQuantity());
	        
	        pstmt.executeUpdate();
	    } catch (java.sql.SQLException e) {
	        e.printStackTrace();
	    }
}
	


}
