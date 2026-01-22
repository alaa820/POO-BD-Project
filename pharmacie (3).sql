CREATE DATABASE pharmacie;
USE pharmacie;

CREATE TABLE IF NOT EXISTS Utilisateur (
    username VARCHAR(50) PRIMARY KEY ,
    adresse VARCHAR(200),
    nom VARCHAR(50),
    prenom VARCHAR(50),
    mdp VARCHAR(50),
    phone VARCHAR(8),
    access ENUM('admin', 'employee') NOT NULL DEFAULT 'employee'
    
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
    sexe ENUM('M', 'F') NOT NULL,
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
    username_name VARCHAR(50),
    FOREIGN KEY (id_client) REFERENCES Client(id_client),
    FOREIGN KEY (username_name) REFERENCES Utilisateur(username)
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
-- ============================================
-- UTILISATEUR (Users/Employees)
-- ============================================
INSERT INTO Utilisateur (username, adresse, nom, prenom, mdp, phone, access) VALUES
('admin', '123 Main St, Tunis', 'Ben Ali', 'Ahmed', 'admin123', '12345678', 'admin'),
('emp001', '45 Avenue Habib, Tunis', 'Trabelsi', 'Fatma', 'emp123', '98765432', 'employee'),
('emp002', '78 Rue de la Liberté, Ariana', 'Khelifi', 'Mohamed', 'emp123', '55667788', 'employee');

-- ============================================
-- FOURNISSEUR (Suppliers)
-- ============================================
INSERT INTO Fournisseur (nom, prenom, societe, email, telephone, adresse, description) VALUES
('Gharbi', 'Karim', 'PharmaTech Tunisia', 'contact@pharmatech.tn', '71123456', '15 Zone Industrielle, Ben Arous', 'Leading pharmaceutical distributor in Tunisia'),
('Saidi', 'Leila', 'MediSupply International', 'info@medisupply.com', '71234567', '92 Avenue Mohamed V, Tunis', 'International medical supplies and equipment'),
('Jemli', 'Nabil', 'BioHealth Solutions', 'sales@biohealth.tn', '71345678', '23 Rue des Entrepreneurs, Sfax', 'Specialized in antibiotics and chronic disease medications'),
('Mansouri', 'Samia', 'GlobalPharma', 'global@pharma.tn', '71456789', '67 Boulevard 7 Novembre, Sousse', 'Wide range of generic and branded medications'),
('Bouazizi', 'Hichem', 'VitalMed Distribution', 'vital@med.tn', '71567890', '34 Rue de Marseille, La Marsa', 'Emergency and critical care medications');

-- ============================================
-- MEDICAMENT (Medicines)
-- ============================================
INSERT INTO Medicament (code_barre, nom, prix_achat, prix_vente, taux_TVA, dosage, quantite, seuil, forme_pharmaceutique, emplacement, necessite_prescription, id_fournisseur) VALUES
-- Painkillers & Fever
('8712345000011', 'Paracetamol', 0.50, 1.20, 7.00, '500mg', 500, 100, 'Tablet', 'A1-01', FALSE, 1),
('8712345000028', 'Ibuprofen', 0.80, 1.80, 7.00, '400mg', 300, 80, 'Tablet', 'A1-02', FALSE, 1),
('8712345000035', 'Aspirin', 0.60, 1.40, 7.00, '100mg', 250, 60, 'Tablet', 'A1-03', FALSE, 2),
('8712345000042', 'Doliprane', 1.20, 2.50, 7.00, '1000mg', 200, 50, 'Tablet', 'A1-04', FALSE, 1),

-- Antibiotics (Prescription Required)
('8712345000059', 'Amoxicillin', 3.50, 8.00, 7.00, '500mg', 150, 40, 'Capsule', 'B2-01', TRUE, 3),
('8712345000066', 'Azithromycin', 5.00, 12.00, 7.00, '250mg', 120, 30, 'Tablet', 'B2-02', TRUE, 3),
('8712345000073', 'Ciprofloxacin', 4.20, 10.00, 7.00, '500mg', 100, 25, 'Tablet', 'B2-03', TRUE, 3),
('8712345000080', 'Cephalexin', 3.80, 9.50, 7.00, '500mg', 90, 25, 'Capsule', 'B2-04', TRUE, 3),

-- Antihistamines & Allergies
('8712345000097', 'Cetirizine', 1.50, 3.50, 7.00, '10mg', 200, 50, 'Tablet', 'C3-01', FALSE, 2),
('8712345000103', 'Loratadine', 1.80, 4.00, 7.00, '10mg', 180, 45, 'Tablet', 'C3-02', FALSE, 2),
('8712345000110', 'Fexofenadine', 2.50, 5.50, 7.00, '120mg', 150, 40, 'Tablet', 'C3-03', FALSE, 4),

-- Digestive System
('8712345000127', 'Omeprazole', 2.00, 5.00, 7.00, '20mg', 250, 60, 'Capsule', 'D4-01', FALSE, 4),
('8712345000134', 'Ranitidine', 1.50, 3.80, 7.00, '150mg', 200, 50, 'Tablet', 'D4-02', FALSE, 4),
('8712345000141', 'Metoclopramide', 1.20, 3.00, 7.00, '10mg', 180, 45, 'Tablet', 'D4-03', TRUE, 4),
('8712345000158', 'Loperamide', 1.80, 4.20, 7.00, '2mg', 150, 40, 'Capsule', 'D4-04', FALSE, 2),

-- Cardiovascular (Prescription Required)
('8712345000165', 'Amlodipine', 2.50, 6.00, 7.00, '5mg', 200, 50, 'Tablet', 'E5-01', TRUE, 5),
('8712345000172', 'Atenolol', 2.20, 5.50, 7.00, '50mg', 180, 45, 'Tablet', 'E5-02', TRUE, 5),
('8712345000189', 'Enalapril', 2.80, 6.80, 7.00, '10mg', 150, 40, 'Tablet', 'E5-03', TRUE, 5),
('8712345000196', 'Simvastatin', 3.50, 8.50, 7.00, '20mg', 120, 30, 'Tablet', 'E5-04', TRUE, 5),

-- Diabetes (Prescription Required)
('8712345000202', 'Metformin', 3.00, 7.50, 7.00, '850mg', 300, 70, 'Tablet', 'F6-01', TRUE, 4),
('8712345000219', 'Glibenclamide', 2.50, 6.50, 7.00, '5mg', 150, 40, 'Tablet', 'F6-02', TRUE, 4),
('8712345000226', 'Insulin Glargine', 25.00, 55.00, 7.00, '100UI/ml', 50, 15, 'Injectable', 'F6-03', TRUE, 5),

-- Vitamins & Supplements
('8712345000233', 'Vitamin C', 1.20, 2.80, 7.00, '1000mg', 400, 80, 'Tablet', 'G7-01', FALSE, 1),
('8712345000240', 'Vitamin D3', 2.00, 4.50, 7.00, '1000IU', 350, 70, 'Capsule', 'G7-02', FALSE, 1),
('8712345000257', 'Multivitamin Complex', 3.50, 8.00, 7.00, 'Daily', 250, 60, 'Tablet', 'G7-03', FALSE, 2),
('8712345000264', 'Omega-3', 4.00, 9.50, 7.00, '1000mg', 200, 50, 'Capsule', 'G7-04', FALSE, 2),
('8712345000271', 'Calcium + Vit D', 2.50, 5.80, 7.00, '600mg+400IU', 180, 45, 'Tablet', 'G7-05', FALSE, 1),

-- Cough & Cold
('8712345000288', 'Cough Syrup', 2.50, 5.50, 7.00, '120ml', 150, 35, 'Syrup', 'H8-01', FALSE, 2),
('8712345000295', 'Nasal Spray', 3.00, 6.50, 7.00, '15ml', 100, 25, 'Spray', 'H8-02', FALSE, 2),
('8712345000301', 'Throat Lozenges', 1.50, 3.20, 7.00, '24 pieces', 200, 50, 'Lozenge', 'H8-03', FALSE, 1),

-- Topical & Dermatology
('8712345000318', 'Hydrocortisone Cream', 3.50, 7.80, 7.00, '1%', 120, 30, 'Cream', 'I9-01', FALSE, 3),
('8712345000325', 'Clotrimazole Cream', 2.80, 6.50, 7.00, '1%', 100, 25, 'Cream', 'I9-02', FALSE, 3),
('8712345000332', 'Betamethasone', 4.00, 9.00, 7.00, '0.1%', 80, 20, 'Cream', 'I9-03', TRUE, 3),

-- Low Stock Items (for testing alerts)
('8712345000349', 'Emergency Epinephrine', 15.00, 35.00, 7.00, '0.3mg', 8, 10, 'Injectable', 'J10-01', TRUE, 5),
('8712345000356', 'Salbutamol Inhaler', 8.00, 18.00, 7.00, '100mcg', 12, 15, 'Inhaler', 'J10-02', TRUE, 5);

-- ============================================
-- CLIENT (Customers)
-- ============================================
INSERT INTO Client (nom, prenom, date_naissance, sexe, telephone, email, adresse, description) VALUES
('Amari', 'Sami', '1985-03-15', 'M', '20123456', 'sami.amari@email.tn', '12 Rue Ibn Khaldoun, Tunis', 'Regular customer, diabetic patient'),
('Touati', 'Amel', '1990-07-22', 'F', '22234567', 'amel.touati@email.tn', '45 Avenue de Carthage, La Marsa', 'Prefers generic medications'),
('Hamdi', 'Karim', '1978-11-08', 'M', '23345678', 'karim.hamdi@email.tn', '89 Rue de la République, Ariana', 'Hypertension medication monthly'),
('Belhadj', 'Salma', '1995-05-30', 'F', '24456789', 'salma.belhadj@email.tn', '34 Boulevard Habib Bourguiba, Sfax', 'Occasional customer'),
('Ferchichi', 'Nizar', '1982-09-12', 'M', '25567890', 'nizar.ferchichi@email.tn', '67 Avenue Mohamed Ali, Sousse', 'Allergies - antihistamines'),
('Mbarek', 'Leila', '1988-12-25', 'F', '26678901', 'leila.mbarek@email.tn', '23 Rue de Palestine, Bizerte', 'Children medications'),
('Triki', 'Ahmed', '1975-04-18', 'M', '27789012', 'ahmed.triki@email.tn', '56 Avenue de la Liberté, Nabeul', 'Cardiovascular patient - regular'),
('Sassi', 'Rim', '1992-08-07', 'F', '28890123', 'rim.sassi@email.tn', '78 Rue Farhat Hached, Monastir', 'Vitamin supplements customer'),
('Dridi', 'Hichem', '1980-01-20', 'M', '29901234', 'hichem.dridi@email.tn', '90 Boulevard 14 Janvier, Kairouan', 'Diabetic supplies'),
('Zouari', 'Sonia', '1987-06-14', 'F', '21012345', 'sonia.zouari@email.tn', '12 Avenue Taieb Mehiri, Tunis', 'Skin care products');

-- ============================================
-- COMMANDE (Orders to Suppliers)
-- ============================================
INSERT INTO Commande (date_commande, date_reception, statut, prix, id_fournisseur) VALUES
-- Received orders
('2025-01-05', '2025-01-10', 'reçue', 2500.00, 1),
('2025-01-08', '2025-01-15', 'reçue', 3200.00, 3),
('2025-01-12', '2025-01-18', 'reçue', 1800.00, 2),
('2025-01-15', '2025-01-20', 'reçue', 4500.00, 5),

-- Pending orders
('2025-01-18', NULL, 'en attente', 2800.00, 1),
('2025-01-19', NULL, 'en attente', 3500.00, 4),
('2025-01-20', NULL, 'en attente', 1500.00, 2),

-- Cancelled order
('2025-01-10', NULL, 'annulée', 2200.00, 3);

-- ============================================
-- COMMANDEPRODUIT (Order Items)
-- ============================================
INSERT INTO CommandeProduit (id_commande, code_barre, quantite) VALUES
-- Order 1 (Received)
(1, '8712345000011', 200),
(1, '8712345000028', 150),
(1, '8712345000233', 100),

-- Order 2 (Received)
(2, '8712345000059', 100),
(2, '8712345000073', 50),
(2, '8712345000318', 80),

-- Order 3 (Received)
(3, '8712345000097', 120),
(3, '8712345000288', 100),

-- Order 4 (Received)
(4, '8712345000165', 150),
(4, '8712345000202', 200),

-- Order 5 (Pending)
(5, '8712345000011', 300),
(5, '8712345000028', 200),

-- Order 6 (Pending)
(6, '8712345000127', 150),
(6, '8712345000202', 100),

-- Order 7 (Pending)
(7, '8712345000233', 200),
(7, '8712345000240', 150);

-- ============================================
-- VENTE (Sales)
-- ============================================
INSERT INTO Vente (date_vente, prix, id_client, username_name) VALUES
('2025-01-15', 45.50, 1, 'emp001'),
('2025-01-15', 28.30, 2, 'emp002'),
('2025-01-16', 67.80, 3, 'emp001'),
('2025-01-16', 22.50, 4, 'emp002'),
('2025-01-17', 95.20, 5, 'emp001'),
('2025-01-17', 38.70, 6, 'emp002'),
('2025-01-18', 124.50, 7, 'emp001'),
('2025-01-18', 15.60, 8, 'emp002'),
('2025-01-19', 82.40, 9, 'emp001'),
('2025-01-19', 56.30, 10, 'emp002'),
('2025-01-20', 73.90, 1, 'emp001'),
('2025-01-20', 41.20, 3, 'emp002'),
('2025-01-21', 88.60, 5, 'emp001'),
('2025-01-21', 34.80, 7, 'emp002');

-- ============================================
-- VENTEPRODUIT (Sale Items)
-- ============================================
INSERT INTO venteProduit (id_vente, code_barre, quantite) VALUES
-- Sale 1 - Diabetic customer
(1, '8712345000202', 2),
(1, '8712345000233', 3),

-- Sale 2 - General customer
(2, '8712345000011', 2),
(2, '8712345000097', 1),

-- Sale 3 - Cardiovascular patient
(3, '8712345000165', 2),
(3, '8712345000189', 2),

-- Sale 4 - Cold symptoms
(4, '8712345000288', 1),
(4, '8712345000301', 2),

-- Sale 5 - Allergy patient
(5, '8712345000097', 3),
(5, '8712345000110', 2),

-- Sale 6 - Children medications
(6, '8712345000011', 4),
(6, '8712345000288', 1),

-- Sale 7 - Regular cardiovascular patient
(7, '8712345000165', 3),
(7, '8712345000196', 2),

-- Sale 8 - Vitamins
(8, '8712345000233', 2),
(8, '8712345000240', 1),

-- Sale 9 - Antibiotic prescription
(9, '8712345000059', 2),
(9, '8712345000011', 3),

-- Sale 10 - Skin care
(10, '8712345000318', 2),
(10, '8712345000325', 1),

-- Sale 11 - Diabetic supplies
(11, '8712345000202', 3),
(11, '8712345000219', 1),

-- Sale 12 - Pain relief
(12, '8712345000028', 3),
(12, '8712345000042', 2),

-- Sale 13 - Multiple items
(13, '8712345000127', 2),
(13, '8712345000141', 1),
(13, '8712345000011', 2),

-- Sale 14 - Vitamins and supplements
(14, '8712345000257', 1),
(14, '8712345000271', 2);

