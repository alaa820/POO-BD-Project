package dao;

import util.DatabaseConnection;
import model.SaleItem;
import model.Medicine;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDao {

    // Create a new commande and return generated id, or -1 on failure
    public int createCommande(int idFournisseur, double prix) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // Insert the prix into commande so the table's prix column is populated
            String insertCommande = "INSERT INTO commande (id_fournisseur, date_commande, statut, prix) VALUES (?, NOW(), ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertCommande, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idFournisseur);
            ps.setString(2, "en attente");
            ps.setDouble(3, prix);
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la creation de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    // Add commandeproduit rows in batch
    public boolean addCommandeProduits(int idCommande, List<SaleItem> items) {
        if (items == null || items.isEmpty()) return false;
        try (Connection con = DatabaseConnection.getConnection()) {
            // Insert only id_commande, code_barre and quantite. Prices are derived from medicament at read time.
            String insertProduit = "INSERT INTO commandeproduit (id_commande, code_barre, quantite) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertProduit);
            for (SaleItem it : items) {
                Medicine m = it.getMedicine();
                ps.setInt(1, idCommande);
                ps.setString(2, m.getCodeBarre());
                ps.setInt(3, it.getQuantity());
                ps.addBatch();
            }
            int[] res = ps.executeBatch();
            return res.length == items.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de l'insertion des produits de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Return list of produits for a commande, joined with medicament to get name and price
    public List<Map<String, Object>> getCommandeProduits(int idCommande) {
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT cp.id_commande, cp.code_barre, cp.quantite, m.nom AS medicament_nom, m.prix_achat AS prix_unitaire, (cp.quantite * m.prix_achat) AS total "
                    + "FROM commandeproduit cp JOIN medicament m ON cp.code_barre = m.code_barre WHERE cp.id_commande = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("id_commande", rs.getInt("id_commande"));
                m.put("code_barre", rs.getString("code_barre"));
                m.put("quantite", rs.getInt("quantite"));
                m.put("medicament_nom", rs.getString("medicament_nom"));
                m.put("prix_unitaire", rs.getDouble("prix_unitaire"));
                m.put("total", rs.getDouble("total"));
                list.add(m);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur en chargeant les produits de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    // Return list of commandes (as maps) where date_reception IS NULL
    public List<Map<String, Object>> getPendingCommandes() {
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.id_fournisseur, "
                    + "COALESCE((SELECT SUM(cp.quantite * m.prix_achat) FROM commandeproduit cp "
                    + "JOIN medicament m ON cp.code_barre = m.code_barre "
                    + "WHERE cp.id_commande = c.id_commande), c.prix, 0) AS prix "
                    + "FROM commande c WHERE c.date_reception IS NULL";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("id_commande", rs.getInt("id_commande"));
                m.put("date_commande", rs.getString("date_commande"));
                m.put("date_reception", rs.getString("date_reception"));
                m.put("statut", rs.getString("statut"));
                m.put("prix", rs.getDouble("prix"));
                m.put("id_fournisseur", rs.getInt("id_fournisseur"));
                list.add(m);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur en chargeant commandes en attente: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    // Search pending commandes by id, supplier societe, or statut
    public List<Map<String, Object>> searchPendingCommandes(String q) {
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.id_fournisseur, "
                    + "COALESCE((SELECT SUM(cp.quantite * m.prix_achat) FROM commandeproduit cp "
                    + "JOIN medicament m ON cp.code_barre = m.code_barre "
                    + "WHERE cp.id_commande = c.id_commande), c.prix, 0) AS prix "
                    + "FROM commande c LEFT JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur "
                    + "WHERE c.date_reception IS NULL AND (c.id_commande = ? OR f.societe LIKE ? OR c.statut LIKE ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            int id = -1;
            try { id = Integer.parseInt(q); } catch (Exception ignored) {}
            ps.setInt(1, id);
            ps.setString(2, "%" + q + "%");
            ps.setString(3, "%" + q + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("id_commande", rs.getInt("id_commande"));
                m.put("date_commande", rs.getString("date_commande"));
                m.put("date_reception", rs.getString("date_reception"));
                m.put("statut", rs.getString("statut"));
                m.put("prix", rs.getDouble("prix"));
                m.put("id_fournisseur", rs.getInt("id_fournisseur"));
                list.add(m);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur en recherchant commandes en attente: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    // Return list of commandes where date_reception IS NOT NULL
    public List<Map<String, Object>> getReceivedCommandes() {
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.id_fournisseur, "
                    + "COALESCE((SELECT SUM(cp.quantite * m.prix_achat) FROM commandeproduit cp "
                    + "JOIN medicament m ON cp.code_barre = m.code_barre "
                    + "WHERE cp.id_commande = c.id_commande), c.prix, 0) AS prix "
                    + "FROM commande c WHERE c.date_reception IS NOT NULL";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("id_commande", rs.getInt("id_commande"));
                m.put("date_commande", rs.getString("date_commande"));
                m.put("date_reception", rs.getString("date_reception"));
                m.put("statut", rs.getString("statut"));
                m.put("prix", rs.getDouble("prix"));
                m.put("id_fournisseur", rs.getInt("id_fournisseur"));
                list.add(m);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur en chargeant commandes reçues: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    // Search received commandes by id, supplier societe, or statut
    public List<Map<String, Object>> searchReceivedCommandes(String q) {
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.id_fournisseur, "
                    + "COALESCE((SELECT SUM(cp.quantite * m.prix_achat) FROM commandeproduit cp "
                    + "JOIN medicament m ON cp.code_barre = m.code_barre "
                    + "WHERE cp.id_commande = c.id_commande), c.prix, 0) AS prix "
                    + "FROM commande c LEFT JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur "
                    + "WHERE c.date_reception IS NOT NULL AND (c.id_commande = ? OR f.societe LIKE ? OR c.statut LIKE ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            int id = -1;
            try { id = Integer.parseInt(q); } catch (Exception ignored) {}
            ps.setInt(1, id);
            ps.setString(2, "%" + q + "%");
            ps.setString(3, "%" + q + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("id_commande", rs.getInt("id_commande"));
                m.put("date_commande", rs.getString("date_commande"));
                m.put("date_reception", rs.getString("date_reception"));
                m.put("statut", rs.getString("statut"));
                m.put("prix", rs.getDouble("prix"));
                m.put("id_fournisseur", rs.getInt("id_fournisseur"));
                list.add(m);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur en recherchant commandes reçues: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    // Reception logic: set date_reception = CURDATE(), statut = 'reçue' and update medicament quantities
    public boolean receptionnerCommande(int idCommande) {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            String updCommande = "UPDATE commande SET date_reception = CURDATE(), statut = ? WHERE id_commande = ?";
            PreparedStatement psUpd = con.prepareStatement(updCommande);
            psUpd.setString(1, "reçue");
            psUpd.setInt(2, idCommande);
            psUpd.executeUpdate();

            String selProduits = "SELECT code_barre, quantite FROM commandeproduit WHERE id_commande = ?";
            PreparedStatement psSel = con.prepareStatement(selProduits);
            psSel.setInt(1, idCommande);
            ResultSet rs = psSel.executeQuery();

            String updMed = "UPDATE medicament SET quantite = quantite + ? WHERE code_barre = ?";
            PreparedStatement psUpdMed = con.prepareStatement(updMed);
            while (rs.next()) {
                int q = rs.getInt("quantite");
                String code = rs.getString("code_barre");
                psUpdMed.setInt(1, q);
                psUpdMed.setString(2, code);
                psUpdMed.addBatch();
            }
            psUpdMed.executeBatch();
            con.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la reception: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Cancel a commande (mark as 'annulée') only if it has not been received yet
    public boolean cancelCommande(int idCommande) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "UPDATE commande SET statut = ? WHERE id_commande = ? AND date_reception IS NULL";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "annulée");
            ps.setInt(2, idCommande);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de l'annulation de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Delete a commande and its produits only if it has not been received yet
    public boolean deleteCommande(int idCommande) {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            // Ensure not received
            String check = "SELECT date_reception FROM commande WHERE id_commande = ?";
            PreparedStatement psCheck = con.prepareStatement(check);
            psCheck.setInt(1, idCommande);
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next()) {
                java.sql.Timestamp dr = rsCheck.getTimestamp("date_reception");
                if (dr != null) {
                    // already received, don't delete
                    return false;
                }
            } else {
                // no such commande
                return false;
            }

            String delProduits = "DELETE FROM commandeproduit WHERE id_commande = ?";
            PreparedStatement psDelProd = con.prepareStatement(delProduits);
            psDelProd.setInt(1, idCommande);
            psDelProd.executeUpdate();

            String delCommande = "DELETE FROM commande WHERE id_commande = ?";
            PreparedStatement psDelCmd = con.prepareStatement(delCommande);
            psDelCmd.setInt(1, idCommande);
            psDelCmd.executeUpdate();

            con.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Delete a received commande: revert stock by subtracting the ordered quantities, then delete commandeproduit and commande
    public boolean deleteReceivedCommande(int idCommande) {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            // Ensure it is received
            String check = "SELECT date_reception FROM commande WHERE id_commande = ?";
            PreparedStatement psCheck = con.prepareStatement(check);
            psCheck.setInt(1, idCommande);
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next()) {
                java.sql.Timestamp dr = rsCheck.getTimestamp("date_reception");
                if (dr == null) {
                    // not received yet -- this method is for received commandes
                    return false;
                }
            } else {
                // no such commande
                return false;
            }

            // For each produit, subtract quantity from medicament.quantite (use GREATEST to avoid negative)
            String selProduits = "SELECT code_barre, quantite FROM commandeproduit WHERE id_commande = ?";
            PreparedStatement psSel = con.prepareStatement(selProduits);
            psSel.setInt(1, idCommande);
            ResultSet rs = psSel.executeQuery();

            String updMed = "UPDATE medicament SET quantite = GREATEST(quantite - ?, 0) WHERE code_barre = ?";
            PreparedStatement psUpdMed = con.prepareStatement(updMed);
            while (rs.next()) {
                int q = rs.getInt("quantite");
                String code = rs.getString("code_barre");
                psUpdMed.setInt(1, q);
                psUpdMed.setString(2, code);
                psUpdMed.addBatch();
            }
            psUpdMed.executeBatch();

            // Delete produits then commande
            String delProduits = "DELETE FROM commandeproduit WHERE id_commande = ?";
            PreparedStatement psDelProd = con.prepareStatement(delProduits);
            psDelProd.setInt(1, idCommande);
            psDelProd.executeUpdate();

            String delCommande = "DELETE FROM commande WHERE id_commande = ?";
            PreparedStatement psDelCmd = con.prepareStatement(delCommande);
            psDelCmd.setInt(1, idCommande);
            psDelCmd.executeUpdate();

            con.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de la commande reçue: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Recompute and update commande.prix from commandeproduit join medicament
    public boolean updateCommandePrix(int idCommande) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String upd = "UPDATE commande c SET c.prix = COALESCE((SELECT SUM(cp.quantite * m.prix_achat) FROM commandeproduit cp JOIN medicament m ON cp.code_barre = m.code_barre WHERE cp.id_commande = ?), 0) WHERE c.id_commande = ?";
            PreparedStatement ps = con.prepareStatement(upd);
            ps.setInt(1, idCommande);
            ps.setInt(2, idCommande);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la mise à jour du prix de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}