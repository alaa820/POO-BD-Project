package main;

import util.DatabaseConnection;

public class TestConnection {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello, welcome to the Pharmacy Management System!");
		try {
			DatabaseConnection.getConnection();
			}catch(Exception e) {
				e.printStackTrace();
			}
			}


}