package bw.ac.thuto.bank.dao;

import bw.ac.thuto.bank.model.Account;
import bw.ac.thuto.bank.model.ChequeAccount;
import bw.ac.thuto.bank.model.Customer;
import bw.ac.thuto.bank.model.InvestmentAccount;
import bw.ac.thuto.bank.model.SavingsAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public void save(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, account_type, balance, " +
                "branch, customer_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getAccountNumber());
            ps.setString(2, account.getAccountType());
            ps.setDouble(3, account.getBalance());
            ps.setString(4, account.getBranch());
            ps.setString(5, account.getCustomer().getCustomerId());
            ps.executeUpdate();
        }
    }

    public Account findByNumber(String accountNumber, Customer customer) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs, customer);
            }
        }
        return null;
    }

    public List<Account> findByCustomer(String customerId, Customer customer) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accounts.add(mapRow(rs, customer));
            }
        }
        return accounts;
    }

    public void updateBalance(Account account) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, account.getBalance());
            ps.setString(2, account.getAccountNumber());
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                String insertSql = "INSERT INTO accounts (account_number, account_type, balance, branch, customer_id) " +
                        "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setString(1, account.getAccountNumber());
                    insertPs.setString(2, account.getAccountType());
                    insertPs.setDouble(3, account.getBalance());
                    insertPs.setString(4, account.getBranch());
                    insertPs.setString(5, account.getCustomer().getCustomerId());
                    insertPs.executeUpdate();
                }
            }
        }
    }

    public void delete(String accountNumber) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        }
    }

    private Account mapRow(ResultSet rs, Customer customer) throws SQLException {
        String type = rs.getString("account_type");
        String accountNumber = rs.getString("account_number");
        String branch = rs.getString("branch");
        double balance = rs.getDouble("balance");

        Account account;

        switch (type) {
            case "Savings Account":
                account = new SavingsAccount(accountNumber, branch, customer);
                break;
            case "Investment Account":
                account = new InvestmentAccount(accountNumber, branch, customer);
                break;
            case "Cheque Account":
                account = new ChequeAccount(accountNumber, branch, customer);
                break;
            default:
                throw new SQLException("Unknown account type: " + type);
        }

        account.setBalance(balance);
        return account;
    }
}

