package util;


import model.Employee;

public class Session {

    private static Employee currentUser;

    private Session() {
        // prevent instantiation
    }

    public static void setCurrentUser(Employee user) {
        currentUser = user;
    }

    public static Employee getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    public static String getUserRole() {
		return isLoggedIn() ? currentUser.getAccess() : null;
	}
}
