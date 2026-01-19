package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
	
	
	
	private static final String URL = "jdbc:mysql://localhost:3306/pharmacie_3?useSSL=false&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASSWORD = "houdasql4896";
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
