package bw.ac.thuto.bank.controller;

import bw.ac.thuto.bank.dao.UserAccountDAO;
import bw.ac.thuto.bank.model.UserAccount;
import java.sql.SQLException;

public class LoginController {

    private final UserAccountDAO userAccountDAO = new UserAccountDAO();

    public UserAccount login(String username, String password) {
        if (username == null || username.trim().isEmpty()) return null;
        if (password == null || password.isEmpty()) return null;

        try {
            UserAccount user = userAccountDAO.findByUsername(username.trim());
            if (user == null) return null;

            return user.authenticate(password) ? user : null;

        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
            return null;
        }
    }
}
