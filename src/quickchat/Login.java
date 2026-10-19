package quickchat;

import java.util.regex.Pattern;

public class Login {

    private static final Pattern CELL = Pattern.compile("^\\+27[0-9]{9}$");

    private String username;
    private String password;
    private String firstName;
    private String lastName;

    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) return false;
        boolean upper = false, digit = false, special = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) upper = true;
            else if (Character.isDigit(c)) digit = true;
            else if (!Character.isLetterOrDigit(c)) special = true;
        }
        return upper && digit && special;
    }

    public boolean checkCellPhoneNumber(String cell) {
        return cell != null && CELL.matcher(cell).matches();
    }

    public String registerUser(String firstName, String lastName, String username,
                               String password, String cell) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted, please ensure that your username "
                    + "contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted, please ensure that the password contains "
                    + "at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(cell)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        return "Username successfully captured. Password successfully captured. "
                + "Cell phone number successfully added.";
    }

    public boolean loginUser(String username, String password) {
        return this.username != null && this.username.equals(username)
                && this.password != null && this.password.equals(password);
    }

    public String returnLoginStatus(boolean loggedIn) {
        if (loggedIn) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}