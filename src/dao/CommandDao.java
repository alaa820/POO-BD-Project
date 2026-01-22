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
	

	public int createCommande(Command commande) {

    if (commande == null)
        return -1;

    try (Connection con = DatabaseConnection.getConnection()) {

        String sql = "INSERT INTO commande (id_fournisseur, date_commande, statut, prix) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Gestion de la clé étrangère : récupération de l'identifiant du fournisseur
        ps.setInt(1, commande.getSupplier().getIdFournisseur());

        // Si la date de commande est nulle, on utilise la date et l'heure actuelles
        // Sinon, conversion de LocalDate en Timestamp pour compatibilité JDBC
        if (commande.getDateCommande() == null) {
            ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
        } else {
            ps.setTimestamp(2,
                    java.sql.Timestamp.valueOf(commande.getDateCommande().atStartOfDay()));
        }

        ps.setString(3, commande.getStatut());
        ps.setDouble(4, commande.getPrix());

        int affected = ps.executeUpdate();

        // Vérifie qu'au moins une ligne a été insérée dans la base de données
        if (affected == 0)
            return -1;

        // Récupération de la clé primaire auto-générée (id_commande)
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next())
                return keys.getInt(1);
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                null,
                "Erreur lors de la creation de la commande: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    return -1;
}

	public boolean updateStatut(int idCommande, String statut) {

    if (statut == null)
        return false;

    try (Connection con = DatabaseConnection.getConnection()) {

        String sql;

        // Si le statut devient "reçue / recue", on met aussi à jour la date de réception
        // CURDATE() permet d'utiliser la date courante directement depuis la base de données
        if ("reçue".equalsIgnoreCase(statut) || "recue".equalsIgnoreCase(statut)) {
            sql = "UPDATE commande SET statut = ?, date_reception = CURDATE() WHERE id_commande = ?";
        } else {
            // Sinon, seule la colonne statut est modifiée
            sql = "UPDATE commande SET statut = ? WHERE id_commande = ?";
        }

        // Préparation de la requête SQL pour éviter les injections SQL
        PreparedStatement ps = con.prepareStatement(sql);

        // Attribution du nouveau statut à la commande
        ps.setString(1, statut);

        // Identification de la commande à mettre à jour via son identifiant
        ps.setInt(2, idCommande);

        // Exécution de la requête et récupération du nombre de lignes affectées
        int affected = ps.executeUpdate();

        // Retourne true si au moins une ligne a été mise à jour
        return affected > 0;

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                null,
                "Erreur lors de la mise à jour du statut de la commande: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    return false;
}

	public boolean deleteCommande(int idCommande) {

    try (Connection con = DatabaseConnection.getConnection()) {

        // Désactivation de l’auto-commit pour gérer la suppression comme une transaction
        // Cela garantit que toutes les suppressions sont effectuées ensemble
        con.setAutoCommit(false);

        // Suppression d’abord des produits liés à la commande
        // Nécessaire pour respecter les contraintes de clé étrangère
        String delProduits = "DELETE FROM commandeproduit WHERE id_commande = ?";
        PreparedStatement psDelProd = con.prepareStatement(delProduits);
        psDelProd.setInt(1, idCommande);
        psDelProd.executeUpdate();

        // Suppression de la commande principale
        String delCommande = "DELETE FROM commande WHERE id_commande = ?";
        PreparedStatement psDelCmd = con.prepareStatement(delCommande);
        psDelCmd.setInt(1, idCommande);
        int affected = psDelCmd.executeUpdate();

        // Validation de la transaction si toutes les opérations ont réussi
        con.commit();

        // Retourne true si une commande a réellement été supprimée
        return affected > 0;

    } catch (Exception ex) {
        ex.printStackTrace();
        try {
            // Tentative de rollback pour annuler les changements en cas d’erreur
            // Cela permet de préserver l’intégrité des données
            Connection con = DatabaseConnection.getConnection();
            if (con != null)
                con.rollback();
        } catch (Exception ignored) {
        }

        JOptionPane.showMessageDialog(
                null,
                "Erreur lors de la suppression de la commande: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    return false;
}


	public List<Command> searchCommandesByFournisseurSociete(String societe) {

    List<Command> list = new ArrayList<>();

    // Requête SQL avec jointure pour récupérer les commandes
    // ainsi que les informations du fournisseur associé
    String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.prix, "
            + "f.id_fournisseur, f.nom, f.prenom, f.societe, f.email, f.telephone, f.adresse, f.description "
            + "FROM commande c "
            + "LEFT JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur "
            + "WHERE f.societe LIKE ?";

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        // Utilisation de LIKE avec des jokers pour permettre une recherche partielle
        // Si le paramètre est null, on recherche toutes les sociétés
        ps.setString(1, "%" + (societe == null ? "" : societe) + "%");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            int idCommande = rs.getInt("id_commande");
            String statut = rs.getString("statut");
            double prix = rs.getDouble("prix");

            // Conversion des Timestamp SQL en LocalDate (API java.time)
            java.sql.Timestamp tsDateCmd = rs.getTimestamp("date_commande");
            java.sql.Timestamp tsDateRec = rs.getTimestamp("date_reception");

            // Construction de l’objet Supplier à partir des données issues de la jointure
            int idFournisseur = rs.getInt("id_fournisseur");
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String societeF = rs.getString("societe");
            String email = rs.getString("email");
            String telephone = rs.getString("telephone");
            String adresse = rs.getString("adresse");
            String description = rs.getString("description");

            Supplier supplier = new Supplier(
                    nom, prenom, societeF, email, telephone, adresse, description
            );
            supplier.setIdFournisseur(idFournisseur);

            // Création de l’objet Command à partir des données récupérées
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
        JOptionPane.showMessageDialog(
                null,
                "Erreur en recherchant commandes par fournisseur: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    return list;
}

	public List<Command> searchCommandesByMedicamentNom(String nom) {

    List<Command> list = new ArrayList<>();

    // Requête SQL avec plusieurs jointures pour retrouver les commandes
    // contenant un médicament donné par son nom
    String sql = "SELECT DISTINCT c.id_commande, c.date_commande, c.date_reception, c.statut, c.prix, "
            + "f.id_fournisseur, f.nom, f.prenom, f.societe, f.email, f.telephone, f.adresse, f.description "
            + "FROM commande c "
            + "JOIN commandeproduit cp ON cp.id_commande = c.id_commande "
            + "JOIN medicament m ON cp.code_barre = m.code_barre "
            + "LEFT JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur "
            + "WHERE m.nom LIKE ?";

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        // Utilisation de LIKE pour permettre une recherche partielle sur le nom du médicament
        // Si le paramètre est null, la recherche retourne toutes les commandes
        ps.setString(1, "%" + (nom == null ? "" : nom) + "%");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            int idCommande = rs.getInt("id_commande");
            String statut = rs.getString("statut");
            double prix = rs.getDouble("prix");

            // Conversion des dates SQL en LocalDate
            java.sql.Timestamp tsDateCmd = rs.getTimestamp("date_commande");
            java.sql.Timestamp tsDateRec = rs.getTimestamp("date_reception");

            // Construction de l’objet Supplier à partir des données récupérées via les jointures
            int idFournisseur = rs.getInt("id_fournisseur");
            String nomF = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String societe = rs.getString("societe");
            String email = rs.getString("email");
            String telephone = rs.getString("telephone");
            String adresse = rs.getString("adresse");
            String description = rs.getString("description");

            Supplier supplier = new Supplier(
                    nomF, prenom, societe, email, telephone, adresse, description
            );
            supplier.setIdFournisseur(idFournisseur);

            // Création de l’objet Command à partir des données récupérées
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
        JOptionPane.showMessageDialog(
                null,
                "Erreur en recherchant commandes par medicament: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    return list;
}

	/**
	 * Get a command by ID with JOIN to avoid additional query for supplier
	 * Optimized to fetch supplier data in the same query
	 */
	public Command getCommandeById(int idCommande) {

    Command cmd = null;

    // Requête SQL avec jointure pour récupérer une commande précise
    // ainsi que les informations du fournisseur associé
    String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.prix, "
            + "f.id_fournisseur, f.nom, f.prenom, f.societe, f.email, f.telephone, f.adresse, f.description "
            + "FROM commande c "
            + "LEFT JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur "
            + "WHERE c.id_commande = ?";

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        // Passage de l’identifiant de la commande en paramètre sécurisé
        ps.setInt(1, idCommande);

        ResultSet rs = ps.executeQuery();

        // Une seule ligne attendue car l’identifiant de la commande est unique
        if (rs.next()) {

            int id = rs.getInt("id_commande");
            String statut = rs.getString("statut");
            double prix = rs.getDouble("prix");

            // Conversion des dates SQL en LocalDate
            java.sql.Timestamp tsDateCmd = rs.getTimestamp("date_commande");
            java.sql.Timestamp tsDateRec = rs.getTimestamp("date_reception");

            // Construction de l’objet Supplier à partir des données issues de la jointure
            int idFournisseur = rs.getInt("id_fournisseur");
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String societe = rs.getString("societe");
            String email = rs.getString("email");
            String telephone = rs.getString("telephone");
            String adresse = rs.getString("adresse");
            String description = rs.getString("description");

            Supplier supplier = new Supplier(
                    nom, prenom, societe, email, telephone, adresse, description
            );
            supplier.setIdFournisseur(idFournisseur);

            // Création de l’objet Command avec les données récupérées
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
        JOptionPane.showMessageDialog(
                null,
                "Erreur en chargeant la commande: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    return cmd;
}


	public List<Command> getAllCommands(boolean pending) {

    List<Command> list = new ArrayList<>();

    // Requête SQL permettant de récupérer toutes les commandes
    // Le prix est calculé dynamiquement à partir des produits de la commande
    // COALESCE permet de gérer les cas où aucune ligne n’est trouvée
    String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.id_fournisseur, "
            + "COALESCE((SELECT SUM(cp.quantite * m.prix_achat) "
            + "FROM commandeproduit cp "
            + "JOIN medicament m ON cp.code_barre = m.code_barre "
            + "WHERE cp.id_commande = c.id_commande), c.prix, 0) AS prix, "
            + "f.id_fournisseur, f.nom, f.prenom, f.societe, f.email, f.telephone, f.adresse, f.description "
            + "FROM commande c "
            + "LEFT JOIN fournisseur f ON c.id_fournisseur = f.id_fournisseur "
            // Filtrage dynamique : commandes en attente (date_reception NULL) ou reçues
            + (pending ? "WHERE c.date_reception IS NULL" : "WHERE c.date_reception IS NOT NULL");

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            int idCommande = rs.getInt("id_commande");

            // Conversion des dates SQL en LocalDate
            java.sql.Timestamp tsDateCmd = rs.getTimestamp("date_commande");
            java.sql.Timestamp tsDateRec = rs.getTimestamp("date_reception");

            String statut = rs.getString("statut");
            double prix = rs.getDouble("prix");

            // Construction de l’objet Supplier à partir des données issues de la jointure
            int idFournisseur = rs.getInt("id_fournisseur");
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String societe = rs.getString("societe");
            String email = rs.getString("email");
            String telephone = rs.getString("telephone");
            String adresse = rs.getString("adresse");
            String description = rs.getString("description");

            Supplier supplier = new Supplier(
                    nom, prenom, societe, email, telephone, adresse, description
            );
            supplier.setIdFournisseur(idFournisseur);

            // Création de l’objet Command avec les données récupérées
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
        JOptionPane.showMessageDialog(
                null,
                "Erreur en chargeant commandes: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    return list;
}


	public List<Command> getReceivedCommandsReceivedBySupplier(Supplier supplier) {

    List<Command> list = new ArrayList<>();

    // Requête SQL permettant de récupérer uniquement les commandes reçues
    // d’un fournisseur donné (date_reception non nulle)
    String sql = "SELECT c.id_commande, c.date_commande, c.date_reception, c.statut, c.prix "
            + "FROM commande c "
            + "WHERE c.id_fournisseur = ? AND c.date_reception IS NOT NULL";

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        // Passage de l’identifiant du fournisseur en paramètre sécurisé
        ps.setInt(1, supplier.getIdFournisseur());

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            int idCommande = rs.getInt("id_commande");

            // Conversion des dates SQL en LocalDate
            java.sql.Timestamp tsDateCmd = rs.getTimestamp("date_commande");
            java.sql.Timestamp tsDateRec = rs.getTimestamp("date_reception");

            String statut = rs.getString("statut");
            double prix = rs.getDouble("prix");

            // Création de l’objet Command en réutilisant l’objet Supplier fourni
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
        JOptionPane.showMessageDialog(
                null,
                "Erreur en chargeant commandes reçues par fournisseur: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    return list;
}}
