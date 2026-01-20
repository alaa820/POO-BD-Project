package dao;

import util.DatabaseConnection;
import model.Command;
import model.CommandItem;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Supplier;

public class CommandDao {

    /**
     * Insert a new Command into commande table and return generated id_commande.
     * Returns -1 on failure.
     */
    public int createCommande(Command commande) {
        if (commande == null) return -1;
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO commande (id_fournisseur, date_commande, statut, prix) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, commande.getSupplier().getIdFournisseur());
            // store date_commande as NOW() if null, otherwise use the provided date
            if (commande.getDateCommande() == null) {
                ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            } else {
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(commande.getDateCommande().atStartOfDay()));
            }
            ps.setString(3, commande.getStatut());
            ps.setDouble(4, commande.getPrix());
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

    /**
     * Update the statut of a commande. If statut equals 'reçue' (case-insensitive),
     * also set date_reception = CURDATE(). Returns true when an update occurred.
     */
    public boolean updateStatut(int idCommande, String statut) {
        if (statut == null) return false;
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql;
            if ("reçue".equalsIgnoreCase(statut) || "recue".equalsIgnoreCase(statut)) {
                sql = "UPDATE commande SET statut = ?, date_reception = CURDATE() WHERE id_commande = ?";
            } else {
                sql = "UPDATE commande SET statut = ? WHERE id_commande = ?";
            }
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, statut);
            ps.setInt(2, idCommande);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la mise à jour du statut de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Delete a commande and its produits. This method will delete related rows in
     * commandeproduit and then delete the commande row in a transaction.
     * Returns true on success, false otherwise.
     */
    public boolean deleteCommande(int idCommande) {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            // Delete produits first
            String delProduits = "DELETE FROM commandeproduit WHERE id_commande = ?";
            PreparedStatement psDelProd = con.prepareStatement(delProduits);
            psDelProd.setInt(1, idCommande);
            psDelProd.executeUpdate();

            // Delete commande
            String delCommande = "DELETE FROM commande WHERE id_commande = ?";
            PreparedStatement psDelCmd = con.prepareStatement(delCommande);
            psDelCmd.setInt(1, idCommande);
            int affected = psDelCmd.executeUpdate();

            con.commit();

            // If affected == 0 then no commande was deleted (id not found)
            return affected > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                // best effort rollback
                Connection con = DatabaseConnection.getConnection();
                if (con != null) con.rollback();
            } catch (Exception ignored) {
            }
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Search commandes by fournisseur societe and return List<Command>
     * Optimized with JOIN to avoid N+1 queries
     */
    public List<Command> searchCommandesByFournisseurSociete(String societe) {
        List<Command> list = new ArrayList<>();
        String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.prix, " +
                     "f.id_fournisseur, f.nom, f.prenom, f.societe, f.email, f.telephone, f.adresse, f.description " +
                     "FROM commande c " +
                     "LEFT JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur " +
                     "WHERE f.societe LIKE ?";
        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + (societe == null ? "" : societe) + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int idCommande = rs.getInt("id_commande");
                String statut = rs.getString("statut");
                double prix = rs.getDouble("prix");
                java.sql.Timestamp tsDateCmd = rs.getTimestamp("date_commande");
                java.sql.Timestamp tsDateRec = rs.getTimestamp("date_reception");
                
                // Build Supplier from joined data
                int idFournisseur = rs.getInt("id_fournisseur");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String societeF = rs.getString("societe");
                String email = rs.getString("email");
                String telephone = rs.getString("telephone");
                String adresse = rs.getString("adresse");
                String description = rs.getString("description");
                
                Supplier supplier = new Supplier(nom, prenom, societeF, email, telephone, adresse, description);
                supplier.setIdFournisseur(idFournisseur);
                
                Command cmd = new Command(
                    tsDateCmd != null ? tsDateCmd.toLocalDateTime().toLocalDate() : null,
                    tsDateRec != null ? tsDateRec.toLocalDateTime().toLocalDate() : null,
                    statut,
                    prix,
                    supplier
                );
                cmd.setIdCommande(idCommande);
                list.add(cmd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur en recherchant commandes par fournisseur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    /**
     * Search commandes that include a produit whose medicament.nom matches the provided name
     * Optimized with JOIN to avoid N+1 queries
     */
    public List<Command> searchCommandesByMedicamentNom(String nom) {
        List<Command> list = new ArrayList<>();
        String sql = "SELECT DISTINCT c.id_commande, c.date_commande, c.date_reception, c.statut, c.prix, " +
                     "f.id_fournisseur, f.nom, f.prenom, f.societe, f.email, f.telephone, f.adresse, f.description " +
                     "FROM commande c " +
                     "JOIN commandeproduit cp ON cp.id_commande = c.id_commande " +
                     "JOIN medicament m ON cp.code_barre = m.code_barre " +
                     "LEFT JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur " +
                     "WHERE m.nom LIKE ?";
        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + (nom == null ? "" : nom) + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int idCommande = rs.getInt("id_commande");
                String statut = rs.getString("statut");
                double prix = rs.getDouble("prix");
                java.sql.Timestamp tsDateCmd = rs.getTimestamp("date_commande");
                java.sql.Timestamp tsDateRec = rs.getTimestamp("date_reception");
                
                // Build Supplier from joined data
                int idFournisseur = rs.getInt("id_fournisseur");
                String nomF = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String societe = rs.getString("societe");
                String email = rs.getString("email");
                String telephone = rs.getString("telephone");
                String adresse = rs.getString("adresse");
                String description = rs.getString("description");
                
                Supplier supplier = new Supplier(nomF, prenom, societe, email, telephone, adresse, description);
                supplier.setIdFournisseur(idFournisseur);
                
                Command cmd = new Command(
                    tsDateCmd != null ? tsDateCmd.toLocalDateTime().toLocalDate() : null,
                    tsDateRec != null ? tsDateRec.toLocalDateTime().toLocalDate() : null,
                    statut,
                    prix,
                    supplier
                );
                cmd.setIdCommande(idCommande);
                list.add(cmd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur en recherchant commandes par medicament: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    /**
     * Get a command by ID with JOIN to avoid additional query for supplier
     * Optimized to fetch supplier data in the same query
     */
    public Command getCommandeById(int idCommande) {
        Command cmd = null;
        String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.prix, " +
                     "f.id_fournisseur, f.nom, f.prenom, f.societe, f.email, f.telephone, f.adresse, f.description " +
                     "FROM commande c " +
                     "LEFT JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur " +
                     "WHERE c.id_commande = ?";
        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id_commande");
                String statut = rs.getString("statut");
                double prix = rs.getDouble("prix");
                java.sql.Timestamp tsDateCmd = rs.getTimestamp("date_commande");
                java.sql.Timestamp tsDateRec = rs.getTimestamp("date_reception");
                
                // Build Supplier from joined data
                int idFournisseur = rs.getInt("id_fournisseur");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String societe = rs.getString("societe");
                String email = rs.getString("email");
                String telephone = rs.getString("telephone");
                String adresse = rs.getString("adresse");
                String description = rs.getString("description");
                
                Supplier supplier = new Supplier(nom, prenom, societe, email, telephone, adresse, description);
                supplier.setIdFournisseur(idFournisseur);
                
                cmd = new Command(
                    tsDateCmd != null ? tsDateCmd.toLocalDateTime().toLocalDate() : null,
                    tsDateRec != null ? tsDateRec.toLocalDateTime().toLocalDate() : null,
                    statut,
                    prix,
                    supplier
                );
                cmd.setIdCommande(id);
            }
            return cmd;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur en chargeant la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return cmd;
    }
}