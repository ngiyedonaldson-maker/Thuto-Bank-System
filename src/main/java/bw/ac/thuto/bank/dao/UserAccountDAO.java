package bw.ac.thuto.bank.dao;

import bw.ac.thuto.bank.model.UserAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAccountDAO {

    public UserAccount findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM user_accounts WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new UserAccount(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("customer_id")
                );
            }
        }
        return null;
    }

    public void save(UserAccount user) throws SQLException {
        String sql = "INSERT INTO user_accounts (username, password_hash, role, active, customer_id) " +
                "VALUES (?, ?, ?, 1, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, UserAccount.hashPassword(user.getPasswordHash())); // Hash it
            ps.setString(3, user.getRole());
            ps.setString(4, user.getCustomerId());
            ps.executeUpdate();
        }
    }

    public void updatePassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE user_accounts SET password_hash = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, UserAccount.hashPassword(newPassword)); // Hash it
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    public void deactivate(String username) throws SQLException {
        String sql = "UPDATE user_accounts SET active = 0 WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }
}
