CREATE DATABASE pharmacie_1;
USE pharmacie_1;

CREATE TABLE IF NOT EXISTS Utilisateur (
    id_utilisateur INT PRIMARY KEY AUTO_INCREMENT,
    adresse VARCHAR(200),
    nom VARCHAR(50),
    prenom VARCHAR(50),
    password VARCHAR(50),
    phone VARCHAR(8),
    role ENUM('admin', 'employee') NOT NULL DEFAULT 'employee'
    
);
CREATE TABLE IF NOT EXISTS Fournisseur (
    id_fournisseur INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50),
    prenom VARCHAR(50),
    societe VARCHAR(100),
    email VARCHAR(100),
    telephone VARCHAR(20),
    adresse VARCHAR(100),
    description TEXT
);
CREATE TABLE IF NOT EXISTS Medicament (
    code_barre VARCHAR(50) PRIMARY KEY,
    nom VARCHAR(100),
    prix_achat DECIMAL(10,2),
    prix_vente DECIMAL(10,2),
    taux_TVA DECIMAL(5,2),
    dosage VARCHAR(50),
    quantite INT,
    seuil INT,
    forme_pharmaceutique VARCHAR(50),
    emplacement VARCHAR(50),
    necessite_prescription BOOLEAN,
    id_fournisseur INT,
    FOREIGN KEY (id_fournisseur) REFERENCES Fournisseur(id_fournisseur)
);
CREATE TABLE IF NOT EXISTS Client (
    id_client INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50),
    prenom VARCHAR(50),
    date_naissance DATE,
    sexe VARCHAR(10),
    telephone VARCHAR(20),
    email VARCHAR(100),
    adresse VARCHAR(100),
    description TEXT
);
CREATE TABLE IF NOT EXISTS Commande (
    id_commande INT PRIMARY KEY AUTO_INCREMENT,
    date_commande DATE,
    date_reception DATE,
    statut VARCHAR(100),
    prix DECIMAL(10,2),
    id_fournisseur INT,
    FOREIGN KEY (id_fournisseur) REFERENCES Fournisseur(id_fournisseur)
);
CREATE TABLE IF NOT EXISTS Vente (
    id_vente INT PRIMARY KEY AUTO_INCREMENT,
    date_vente DATE,
    prix DECIMAL(10,2),
    id_client INT,
    id_utilisateur INT,
    FOREIGN KEY (id_client) REFERENCES Client(id_client),
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateur(id_utilisateur)
);
CREATE TABLE IF NOT EXISTS CommandeProduit (
    id_commande INT,
    code_barre VARCHAR(50),
    quantite INT,
    PRIMARY KEY (id_commande, code_barre),
    FOREIGN KEY (id_commande) REFERENCES Commande(id_commande),
    FOREIGN KEY (code_barre) REFERENCES Medicament(code_barre)
);
CREATE TABLE IF NOT EXISTS venteProduit (
    id_vente INT,
    code_barre VARCHAR(50),
    quantite INT,
    PRIMARY KEY (id_vente, code_barre),
    FOREIGN KEY (id_vente) REFERENCES Vente(id_vente),
    FOREIGN KEY (code_barre) REFERENCES Medicament(code_barre)
);


