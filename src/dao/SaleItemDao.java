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
import model.Medicine;
import model.Sale;

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
	public List<SaleItem> getAllSaleItem(){
		//list 
		 List<SaleItem> listSI= new ArrayList<>();; // Initialize your list here
	 	try {
	 		Connection con  = DatabaseConnection.getConnection();
	 		// Sample query execution (pseudo-code)
	 		String query = "SELECT * FROM venteproduit";
	 		PreparedStatement pst = con.prepareStatement(query);

	 		

	 		ResultSet rs = pst.executeQuery();
	 		

	 		
	 		while(rs.next()) {
				int idVente = rs.getInt("id_vente");
				String codeBarre = rs.getString("code_barre");
				int quantite = rs.getInt("quantite");
				
				Sale s = new SaleDao().getSaleById(idVente);
				
				Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
				SaleItem saleItem = new SaleItem(m, s,quantite);
				
				listSI.add(saleItem);
				// SaleItem saleItem = new SaleItem(...);
				// saleItems.add(saleItem);
			}
	 	}
	 	catch(Exception e) {
	 					e.printStackTrace();
	 	}
	 	return listSI;
	}
	public List<SaleItem> searchByClientAndMedicine(String nomClient, String nomMedicine){
		List<SaleItem> saleItems = new ArrayList<>();
		try {
			Connection con  = DatabaseConnection.getConnection();
			String query = "SELECT vp.* FROM venteproduit vp " +
					"JOIN vente v ON vp.id_vente = v.id_vente " +
					"JOIN client c ON v.id_client = c.id_client " +
					"JOIN medicament p ON vp.code_barre = p.code_barre " +
					"WHERE c.nom LIKE ? AND p.nom LIKE ?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, "%" + nomClient + "%");
			pst.setString(2, "%" + nomMedicine + "%");
			ResultSet rs = pst.executeQuery();
			
			while (rs.next()) {
				int idVente = rs.getInt("id_vente");
				String codeBarre = rs.getString("code_barre");
				int quantite = rs.getInt("quantite");

				Sale s = new SaleDao().getSaleById(idVente);
				Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
				SaleItem saleItem = new SaleItem(m, s, quantite);
				saleItems.add(saleItem);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return saleItems;
	}
	
	public List<SaleItem> searchByClient(String nomClient){
		List<SaleItem> saleItems = new ArrayList<>();
	
		try {
			Connection con  = DatabaseConnection.getConnection();
			String query = "SELECT vp.* FROM venteproduit vp " +
					"JOIN vente v ON vp.id_vente = v.id_vente " +
					"JOIN client c ON v.id_client = c.id_client " +
					"WHERE c.nom LIKE ? ";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, "%" + nomClient + "%");
			
			ResultSet rs = pst.executeQuery();
			
			while (rs.next()) {
				int idVente = rs.getInt("id_vente");
				String codeBarre = rs.getString("code_barre");
				int quantite = rs.getInt("quantite");

				Sale s = new SaleDao().getSaleById(idVente);
				Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
				SaleItem saleItem = new SaleItem(m, s, quantite);
				saleItems.add(saleItem);
		}}catch(Exception e) {
			e.printStackTrace();
		}
		return saleItems;
	}
	
	public List<SaleItem> searchByMedicine(String nomMedicine){
		List<SaleItem> saleItems = new ArrayList<>();
		try {
			Connection con  = DatabaseConnection.getConnection();
			String query = "SELECT vp.* FROM venteproduit vp " +
					"JOIN vente v ON vp.id_vente = v.id_vente " +				
					"JOIN medicament p ON vp.code_barre = p.code_barre " +
					"WHERE  p.nom LIKE  ?";
			PreparedStatement pst = con.prepareStatement(query);
			
			pst.setString(1,"%"+ nomMedicine+"%");
			ResultSet rs = pst.executeQuery();
			
			while (rs.next()) {
				int idVente = rs.getInt("id_vente");
				String codeBarre = rs.getString("code_barre");
				int quantite = rs.getInt("quantite");

				Sale s = new SaleDao().getSaleById(idVente);
				Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
				SaleItem saleItem = new SaleItem(m, s, quantite);
				saleItems.add(saleItem);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return saleItems;
	}
}
