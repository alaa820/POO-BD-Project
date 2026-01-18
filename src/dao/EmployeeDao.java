package dao;

import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
public class EmployeeDao {
    
    /**
     * Authenticates an employee with username and password
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticateUser(String username, String password) {
    	// Replace this with actual database query to validate credentials
        //connection to database and query execution logic goes here
    	try {
    		Connection con  = DatabaseConnection.getConnection();
    		// Sample query execution (pseudo-code)
    		String query = "SELECT * FROM utilisateur WHERE username = ? AND mdp = ?";
    		PreparedStatement pst = con.prepareStatement(query);

    		pst.setString(1, username); // first '?'
    		pst.setString(2, password); // second '?'

    		ResultSet rs = pst.executeQuery();
    		
    		if(rs.next()) {
    			System.out.println("Login successful! Role: " + rs.getString("access"));
				return true;
			}
    		else {
    			System.out.println("Login failed!");
				return false;
    		}
    	}catch(Exception e) {
			e.printStackTrace();
		}
		return "admin".equals(username) && "password".equals(password);
    }
    
    
}
