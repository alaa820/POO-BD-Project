package model;

public class Utilisateur {
    private int idUtilisateur;
    private String login; // Dans votre MCD, c'est le champ "adresse"
    private String motDePasse;
    private String role; // "Administrateur" ou "Employé"
    public Utilisateur(String login, String motDePasse, String role) {
        this.login = login;
        this.motDePasse = motDePasse;
        // On force le rôle en minuscule pour faciliter les tests plus tard
        this.role = role;
    }

    // GETTERS : Indispensables pour le Login et la vérification des droits
    public String getLogin() { return login; }
    public String getMotDePasse() { return motDePasse; }
    public String getRole() { return role; }

    // SETTERS UTILES
    // Seul le mot de passe est vraiment utile à modifier (en cas d'oubli)
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    // METHODE LOGIQUE : Pour vérifier facilement les droits dans l'application
    public boolean estAdmin() {
        return "Administrateur".equalsIgnoreCase(this.role);
    }
}
