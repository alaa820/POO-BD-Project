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

public class MedicineDao {
    
    /**
     * Get all medicines
     * @return List of all medicines
     */
    public List<Medicine> getAllMedicines() {
    	List<Medicine> medecines= new ArrayList<>();; // Initialize your list here
    	try {
    		Connection con  = DatabaseConnection.getConnection();
    		// Sample query execution (pseudo-code)
    		String query = "SELECT * FROM medicament";
    		PreparedStatement pst = con.prepareStatement(query);

    		

    		ResultSet rs = pst.executeQuery();
    		
    		
    		while(rs.next()) {
				String codeBarre = rs.getString("code_barre");
				String nom = rs.getString("nom");
				double prixAchat = rs.getDouble("prix_achat");
				double prixVente = rs.getDouble("prix_vente");
				double tauxTVA = rs.getDouble("taux_TVA");
				String dosage = rs.getString("dosage");
				int quantite = rs.getInt("quantite");
				int seuil = rs.getInt("seuil");
				String formePharmaceutique = rs.getString("forme_pharmaceutique");
				String emplacement = rs.getString("emplacement");
				boolean necessitePrescription = rs.getBoolean("necessite_prescription");
				String querySupplier = "SELECT * FROM fournisseur WHERE id_fournisseur = ?";
				PreparedStatement pstSupplier = con.prepareStatement(querySupplier);
				pstSupplier.setInt(1, rs.getInt("id_fournisseur"));
				ResultSet rsSupplier = pstSupplier.executeQuery();
				Supplier supplier = null;
				if(rsSupplier.next()) {
					supplier = new Supplier(
							rsSupplier.getString("nom"),
							rsSupplier.getString("prenom"),
							rsSupplier.getString("societe"),
							rsSupplier.getString("email"),
							rsSupplier.getString("telephone"),
							rsSupplier.getString("adresse"),
							rsSupplier.getString("description")
					);
					supplier.setIdFournisseur(rsSupplier.getInt("id_fournisseur"));
				Medicine medicine = new Medicine(codeBarre, nom, prixAchat, prixVente, tauxTVA, dosage, quantite, seuil, formePharmaceutique, emplacement, necessitePrescription, supplier);
				medecines.add(medicine);
				}
				
			}
    	}
    		catch(Exception e) {
    						e.printStackTrace();
    		}
    	return medecines;
        
      
    }
    
    /**
     * Search medicines by code_barre, name, or supplier
     * @param codeBarre the code barre to search for (can be null)
     * @param nom the medicine name to search for (can be null)
     * @param supplier the supplier name to search for (can be null)
     * @return List of matching medicines
     */
    public List<Medicine> searchMedicines(String codeBarre, String nom, String supplier) {
        		List<Medicine> medecines= new ArrayList<>();; // Initialize your list here
		try {
			StringBuilder sql = new StringBuilder(
				    "SELECT m.* FROM medicament m JOIN fournisseur f ON m.id_fournisseur = f.id_fournisseur WHERE 1=1"
				);

				List<Object> params = new ArrayList<>();

				if (codeBarre != null) {
				    sql.append(" AND m.code_barre LIKE ?");
				    params.add("%" + codeBarre + "%");
				}

				if (nom !=null) {
				    sql.append(" AND m.nom LIKE ?");
				    params.add("%" + nom + "%");
				}

				if (supplier != null) {
				    sql.append(" AND f.nom LIKE ?");
				    params.add("%" + supplier + "%");
				}

				Connection con  = DatabaseConnection.getConnection();
				PreparedStatement pst = con.prepareStatement(sql.toString());

				for (int i = 0; i < params.size(); i++) {
				    pst.setObject(i + 1, params.get(i));
				}

				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
					String code_barre = rs.getString("code_barre");
					String nomMed = rs.getString("nom");
					double prixAchat = rs.getDouble("prix_achat");
					double prixVente = rs.getDouble("prix_vente");
					double tauxTVA = rs.getDouble("taux_TVA");
					String dosage = rs.getString("dosage");
					int quantite = rs.getInt("quantite");
					int seuil = rs.getInt("seuil");
					String formePharmaceutique = rs.getString("forme_pharmaceutique");
					String emplacement = rs.getString("emplacement");
					boolean necessitePrescription = rs.getBoolean("necessite_prescription");
					String querySupplier = "SELECT * FROM fournisseur WHERE id_fournisseur = ?";
					PreparedStatement pstSupplier = con.prepareStatement(querySupplier);
					pstSupplier.setInt(1, rs.getInt("id_fournisseur"));
					ResultSet rsSupplier = pstSupplier.executeQuery();
					Supplier supp = null;
					if(rsSupplier.next()) {
						supp = new Supplier(
								rsSupplier.getString("nom"),
								rsSupplier.getString("prenom"),
								rsSupplier.getString("societe"),
								rsSupplier.getString("email"),
								rsSupplier.getString("telephone"),
								rsSupplier.getString("adresse"),
								rsSupplier.getString("description")
						);
						supp.setIdFournisseur(rsSupplier.getInt("id_fournisseur"));
					Medicine medicine = new Medicine(code_barre, nomMed, prixAchat, prixVente, tauxTVA, dosage, quantite, seuil, formePharmaceutique, emplacement, necessitePrescription, supp);
					medecines.add(medicine);
					}
				}

				// Process ResultSet to build medecines list (similar to getAllMedicines)
				
		}
			catch(Exception e) {
							e.printStackTrace();
			}
		return medecines;
    }
    
    /**
     * Add a new medicine
     * @param codeBarre the medicine code barre
     * @param nom the medicine name
     * @param prixAchat the purchase price
     * @param prixVente the selling price
     * @param tauxTVA the VAT rate
     * @param dosage the dosage
     * @param quantite the quantity
     * @param seuil the threshold
     * @param formePharmaceutique the pharmaceutical form
     * @param emplacement the location/placement
     * @param necessitePrescription whether prescription is required
     * @param supplier the supplier object
     */
    public boolean addMedicine(String codeBarre, String nom, double prixAchat, double prixVente, 
                           double tauxTVA, String dosage, int quantite, int seuil, 
                           String formePharmaceutique, String emplacement, 
                           boolean necessitePrescription, Supplier supplier) {
    	try {
    		Connection con  = DatabaseConnection.getConnection();
			// Sample query execution (pseudo-code)`medicament`
			String query = "INSERT INTO medicament (code_barre, nom,prix_achat,prix_vente,taux_TVA,dosage,quantite,seuil,forme_pharmaceutique,emplacement,necessite_prescription,id_fournisseur)  VALUES (?,?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, codeBarre);
			pst.setString(2, nom);
			pst.setDouble(3, prixAchat);
			pst.setDouble(4, prixVente);
			pst.setDouble(5, tauxTVA);
			pst.setString(6, dosage);
			pst.setInt(7, quantite);
			pst.setInt(8, seuil);
			pst.setString(9, formePharmaceutique);
			pst.setString(10, emplacement);
			pst.setBoolean(11, necessitePrescription);
			pst.setInt(12, supplier.getIdFournisseur());
			
			

			int rowsAffected = pst.executeUpdate();
			return rowsAffected > 0;
			
		}
			catch(Exception e) {
							e.printStackTrace();
    	}
    	return false;
    	
    }
    
    
    /**
     * Check if medicine quantity is available
     * @param medicineId the medicine id
     * @param requiredQuantity the required quantity
     * @return true if quantity is available
     */
    public boolean isQuantityAvailable(int medicineId, int requiredQuantity) {
        return false;
    }
}