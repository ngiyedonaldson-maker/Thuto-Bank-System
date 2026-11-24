package bw.ac.thuto.bank.dao;

import bw.ac.thuto.bank.model.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void saveTransaction(Transaction transaction, String accountNumber) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id, account_number, type, " +
                "amount, balance_after) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transaction.getTransactionId());
            ps.setString(2, accountNumber);
            ps.setString(3, transaction.getType());
            ps.setDouble(4, transaction.getAmount());
            ps.setDouble(5, transaction.getBalanceAfter());
            ps.executeUpdate();
        }
    }

    public List<Transaction> findByAccount(String accountNumber) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getDouble("balance_after")));
            }
        }
        return transactions;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}