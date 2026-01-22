package dao;

import util.DatabaseConnection;
import model.CommandItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.Medicine;
import model.Command;

public class CommandItemDao {

    // Récupère tous les items de commandes
    public List<CommandItem> getAllCommandItems() {
        List<CommandItem> list = new ArrayList<>();
        String sql = "SELECT * FROM commandeproduit";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                String codeBarre = rs.getString("code_barre");
                int quantite = rs.getInt("quantite");
                int idCommande = rs.getInt("id_commande");

                // Récupération des objets Medicine et Command associés
                Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                Command c = new CommandDao().getCommandeById(idCommande);

                list.add(new CommandItem(c, m, quantite));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Recherche des items par nom de fournisseur et nom de médicament
    public List<CommandItem> searchByFournisseurAndMedicament(String supplierName, String medicineName) {
        List<CommandItem> list = new ArrayList<>();
        String sql = "SELECT cp.* FROM commandeproduit cp "
                + "JOIN commande c ON cp.id_commande = c.id_commande "
                + "JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur "
                + "JOIN medicament m ON cp.code_barre = m.code_barre "
                + "WHERE f.societe LIKE ? AND m.nom LIKE ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "%" + supplierName + "%");
            pst.setString(2, "%" + medicineName + "%");

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                String codeBarre = rs.getString("code_barre");
                int quantite = rs.getInt("quantite");

                // Récupération des objets Medicine et Command associés
                Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                Command c = new CommandDao().getCommandeById(rs.getInt("id_commande"));

                list.add(new CommandItem(c, m, quantite));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Recherche des items par fournisseur uniquement
    public List<CommandItem> searchByFournisseur(String supplierName) {
        List<CommandItem> list = new ArrayList<>();
        String sql = "SELECT cp.* FROM commandeproduit cp "
                + "JOIN commande c ON cp.id_commande = c.id_commande "
                + "JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur "
                + "WHERE f.societe LIKE ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "%" + supplierName + "%");

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                String codeBarre = rs.getString("code_barre");
                int quantite = rs.getInt("quantite");

                // Récupération des objets Medicine et Command associés
                Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                Command c = new CommandDao().getCommandeById(rs.getInt("id_commande"));

                list.add(new CommandItem(c, m, quantite));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Recherche des items par médicament uniquement
    public List<CommandItem> searchByMedicine(String medicineName) {
        List<CommandItem> list = new ArrayList<>();
        String sql = "SELECT cp.* FROM commandeproduit cp "
                + "JOIN medicament m ON cp.code_barre = m.code_barre "
                + "WHERE m.nom LIKE ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "%" + medicineName + "%");

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                String codeBarre = rs.getString("code_barre");
                int quantite = rs.getInt("quantite");

                // Récupération des objets Medicine et Command associés
                Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                Command c = new CommandDao().getCommandeById(rs.getInt("id_commande"));

                list.add(new CommandItem(c, m, quantite));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Récupère tous les items pour une commande spécifique
    public List<CommandItem> getCommandItemByCommandId(int idCommande) {
        List<CommandItem> list = new ArrayList<>();
        String sql = "SELECT * FROM commandeproduit WHERE id_commande = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idCommande);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                String codeBarre = rs.getString("code_barre");
                int quantite = rs.getInt("quantite");

                // Récupération des objets Medicine et Command associés
                Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                Command c = new CommandDao().getCommandeById(rs.getInt("id_commande"));

                list.add(new CommandItem(c, m, quantite));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Ajoute un nouvel item de commande
    public void addCommandItem(CommandItem ci) {
        String sql = "INSERT INTO commandeproduit (id_commande, code_barre, quantite) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, ci.getC().getIdCommande());
            pst.setString(2, ci.getM().getCodeBarre());
            pst.setInt(3, ci.getQuantity());
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Récupère les produits d’une commande spécifique
    public List<CommandItem> getProduitsByCommande(int idCommande) {
        List<CommandItem> list = new ArrayList<>();
        String sql = "SELECT code_barre, quantite FROM commandeproduit WHERE id_commande = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                String codeBarre = rs.getString("code_barre");
                int quantite = rs.getInt("quantite");

                // Récupération des objets Medicine et Command associés
                Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                Command c = new CommandDao().getCommandeById(rs.getInt("id_commande"));

                list.add(new CommandItem(c, m, quantite));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erreur en chargeant produits de la commande: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    // Récupère tous les items de commandes dont le statut est "Reçue"
    public List<CommandItem> getAllCommandItemsReceived() {
        List<CommandItem> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM commandeproduit cp "
                    + "JOIN commande c ON cp.id_commande = c.id_commande "
                    + "WHERE c.statut = 'Reçue'";
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                String codeBarre = rs.getString("code_barre");
                int quantite = rs.getInt("quantite");

                // Récupération des objets Medicine et Command associés
                Medicine m = new MedicineDao().getMedecineByCode(codeBarre);
                Command c = new CommandDao().getCommandeById(rs.getInt("id_commande"));

                list.add(new CommandItem(c, m, quantite));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
