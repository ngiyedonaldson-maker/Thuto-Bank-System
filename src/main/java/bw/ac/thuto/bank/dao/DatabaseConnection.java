package bw.ac.thuto.bank.dao;

import bw.ac.thuto.bank.model.UserAccount;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:bank.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            createTables(connection);
        }
        return connection;
    }

    private static void createTables(Connection conn) {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS user_accounts (" +
                    "username TEXT PRIMARY KEY, " +
                    "password_hash TEXT, " +
                    "role TEXT, " +
                    "active INTEGER DEFAULT 1, " +
                    "customer_id TEXT)");

            st.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "customer_id TEXT PRIMARY KEY, " +
                    "firstname TEXT, surname TEXT, " +
                    "address TEXT, phone TEXT, email TEXT, " +
                    "employer_name TEXT, employer_address TEXT)");

            st.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "account_number TEXT PRIMARY KEY, " +
                    "account_type TEXT, balance REAL, " +
                    "branch TEXT, customer_id TEXT)");

            st.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id TEXT PRIMARY KEY, " +
                    "account_number TEXT, " +
                    "type TEXT, amount REAL, balance_after REAL)");

            // Seed Users with HASHED passwords
            st.execute("INSERT OR REPLACE INTO user_accounts (username, password_hash, role, active, customer_id) " +
                    "VALUES ('admin', '" + UserAccount.hashPassword("admin123") + "', 'ADMIN', 1, NULL)");
            st.execute("INSERT OR REPLACE INTO user_accounts (username, password_hash, role, active, customer_id) " +
                    "VALUES ('staff1', '" + UserAccount.hashPassword("staff123") + "', 'BANK_STAFF', 1, NULL)");
            st.execute("INSERT OR REPLACE INTO user_accounts (username, password_hash, role, active, customer_id) " +
                    "VALUES ('C100', '" + UserAccount.hashPassword("cust123") + "', 'CUSTOMER', 1, 'C100')");

            seedSampleData(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedSampleData(Connection conn) throws SQLException {
        String[] rows = {
                "C100|Donaldson|Ngiye|Gaborone|71234567|d@email.com|BAC|Gaborone",
                "C101|Rebaone|Mlalazi|Francistown|72345678|r@email.com|Stanbic|Francistown",
                "C102|Thabo|Molefe|Maun|73456789|t@email.com|Debswana|Maun",
                "C103|Neo|Kgosi|Gaborone|74567890|n@email.com|Barclays|Gaborone",
                "C104|Mpho|Ramosweu|Kasane|75678901|m@email.com|Chobe|Kasane",
                "C105|Kabelo|Seitei|Palapye|76789012|k@email.com|Morupule|Palapye",
                "C106|Boitumelo|Molefe|Serowe|77890123|b@email.com|BPC|Serowe",
                "C107|Tshepo|Mogorosi|Mahalapye|78901234|ts@email.com|FNB|Mahalapye",
                "C108|Lorato|Kebonang|Lobatse|79012345|l@email.com|BNYC|Lobatse",
                "C109|Tebogo|Mokgweetsi|Gaborone|70123456|tb@email.com|Absa|Gaborone"
        };

        String sql = "INSERT OR REPLACE INTO customers (customer_id, firstname, surname, " +
                "address, phone, email, employer_name, employer_address) " +
                "VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String row : rows) {
                String[] p = row.split("\\|");
                ps.setString(1, p[0]);
                ps.setString(2, p[1]);
                ps.setString(3, p[2]);
                ps.setString(4, p[3]);
                ps.setString(5, p[4]);
                ps.setString(6, p[5]);
                ps.setString(7, p[6]);
                ps.setString(8, p[7]);
                ps.executeUpdate();
            }
        }
    }
}