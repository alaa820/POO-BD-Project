package service;

import dao.SupplierDao;
import model.Supplier;

import java.sql.SQLException;
import java.util.List;

public class SupplierService {

    private SupplierDao dao = new SupplierDao();

    public void ajouterFournisseur(Supplier s) throws SQLException {
        if (s.getNom() == null || s.getNom().isEmpty()) {
            throw new IllegalArgumentException("Nom obligatoire");
        }
        dao.addSupplier(s);
    }

    public List<Supplier> listerFournisseurs() throws SQLException {
        return dao.getAllSuppliers();
    }
}
