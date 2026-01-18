package dao;

import model.Medicine;
import java.util.List;

public class MedicineDao {
    
    /**
     * Search medicines by name
     * @param name the medicine name to search for
     * @return List of matching medicines
     */
    List<Medicine> searchByName(String name){
    			// Implementation goes here
		return null;
    }
    
    /**
     * Get all medicines
     * @return List of all medicines
     */
    List<Medicine> getAllMedicines(){
    	return null; // Implementation goes here
    }
    public boolean isQuantityAvailable(int medicineId, int requiredQuantity) {
    	return true; // Placeholder implementation
    }
}