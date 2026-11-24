package bw.ac.thuto.bank.model;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String dateTime;
    private String type;
    private double amount;
    private double balanceAfter;

    public Transaction(String type, double amount, double balanceAfter) {
        this.transactionId = java.util.UUID.randomUUID().toString().substring(0, 8);
        this.dateTime = LocalDateTime.now().toString();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public String getTransactionId() { return transactionId; }
    public String getDateTime() { return dateTime; }
    public String getDetails() { return type + " of BWP " + amount + " on " + dateTime; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
}
