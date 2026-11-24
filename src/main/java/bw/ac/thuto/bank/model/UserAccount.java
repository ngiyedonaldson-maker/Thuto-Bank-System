package bw.ac.thuto.bank.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserAccount {
    private String username;
    private String passwordHash;
    private String role;
    private boolean active;
    private String lastLogin;
    private String customerId;

    public UserAccount(String username, String passwordHash, String role, String customerId) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = true;
        this.customerId = customerId;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean authenticate(String password) {
        String hashedInput = hashPassword(password);
        return this.passwordHash != null && this.passwordHash.equals(hashedInput) && this.active;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public String getCustomerId() { return customerId; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
}