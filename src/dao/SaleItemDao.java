package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Medicine;
import model.Sale;
import model.SaleItem;
import util.DatabaseConnection;

public class SaleItemDao {

    // Enregistre un article de vente pour une vente spécifique
    public void saveSaleItem(int idVente, SaleItem saleItem) {
        String sql = "INSERT INTO venteproduit (id_vente, code_barre, quantite) VALUES (?,?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idVente);
            pstmt.setString(2, saleItem.getMedicine().getCodeBarre());
            pstmt.setInt(3, saleItem.getQuantity());
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Récupère tous les articles de vente
    public List<SaleItem> getAllSaleItem() {
        List<SaleItem> listSI = new ArrayList<>();
        String query = "SELECT * FROM venteproduit";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int idVente = rs.getInt("id_vente");
                String codeBarre = rs.getString("code_barre");
                int quantite = rs.getInt("quantite");

                Sale s = new SaleDao().getSaleById(idVente);
                Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                listSI.add(new SaleItem(m, s, quantite));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listSI;
    }

    // Recherche les articles par nom de client et nom de médicament
    public List<SaleItem> searchByClientAndMedicine(String nomClient, String nomMedicine) {
        List<SaleItem> saleItems = new ArrayList<>();
        String query = "SELECT vp.* FROM venteproduit vp "
                     + "JOIN vente v ON vp.id_vente = v.id_vente "
                     + "JOIN client c ON v.id_client = c.id_client "
                     + "JOIN medicament p ON vp.code_barre = p.code_barre "
                     + "WHERE c.nom LIKE ? AND p.nom LIKE ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, "%" + nomClient + "%");
            pst.setString(2, "%" + nomMedicine + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int idVente = rs.getInt("id_vente");
                    String codeBarre = rs.getString("code_barre");
                    int quantite = rs.getInt("quantite");

                    Sale s = new SaleDao().getSaleById(idVente);
                    Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                    saleItems.add(new SaleItem(m, s, quantite));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return saleItems;
    }

    // Recherche les articles par nom de client
    public List<SaleItem> searchByClient(String nomClient) {
        List<SaleItem> saleItems = new ArrayList<>();
        String query = "SELECT vp.* FROM venteproduit vp "
                     + "JOIN vente v ON vp.id_vente = v.id_vente "
                     + "JOIN client c ON v.id_client = c.id_client "
                     + "WHERE c.nom LIKE ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, "%" + nomClient + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int idVente = rs.getInt("id_vente");
                    String codeBarre = rs.getString("code_barre");
                    int quantite = rs.getInt("quantite");

                    Sale s = new SaleDao().getSaleById(idVente);
                    Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                    saleItems.add(new SaleItem(m, s, quantite));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return saleItems;
    }

    // Recherche les articles par nom de médicament
    public List<SaleItem> searchByMedicine(String nomMedicine) {
        List<SaleItem> saleItems = new ArrayList<>();
        String query = "SELECT vp.* FROM venteproduit vp "
                     + "JOIN vente v ON vp.id_vente = v.id_vente "
                     + "JOIN medicament p ON vp.code_barre = p.code_barre "
                     + "WHERE p.nom LIKE ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, "%" + nomMedicine + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int idVente = rs.getInt("id_vente");
                    String codeBarre = rs.getString("code_barre");
                    int quantite = rs.getInt("quantite");

                    Sale s = new SaleDao().getSaleById(idVente);
                    Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                    saleItems.add(new SaleItem(m, s, quantite));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return saleItems;
    }
}
