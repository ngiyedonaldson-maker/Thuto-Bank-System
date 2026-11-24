package bw.ac.thuto.bank.dao;

import bw.ac.thuto.bank.model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    public Customer findByCustomerId(String customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getString("customer_id"),
                        rs.getString("firstname"),
                        rs.getString("surname"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("employer_name"),
                        rs.getString("employer_address")
                );
            }
        }
        return null;
    }

    public void updateContactInfo(String customerId, String phone, String email) throws SQLException {
        String sql = "UPDATE customers SET phone = ?, email = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, email);
            ps.setString(3, customerId);
            ps.executeUpdate();
        }
    }
}
