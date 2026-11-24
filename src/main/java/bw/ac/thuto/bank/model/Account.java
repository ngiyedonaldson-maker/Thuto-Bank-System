package bw.ac.thuto.bank.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Account implements Withdrawable {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected String dateOpened;
    protected String status;
    protected Customer customer;
    protected List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = 0.0;
        this.branch = branch;
        this.customer = customer;
        this.dateOpened = java.time.LocalDate.now().toString();
        this.status = "Active";
    }

    public void deposit(double amount) {
        validateAmount(amount);
        balance += amount;
        transactions.add(new Transaction("DEPOSIT", amount, balance));
    }

    @Override
    public void withdraw(double amount) {
        validateAmount(amount);
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds");
        balance -= amount;
        transactions.add(new Transaction("WITHDRAWAL", amount, balance));
    }

    public void validateAmount(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
    }

    public abstract String getAccountType();

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }

    public void setBalance(double balance) { this.balance = balance; }

    public String getBranch() { return branch; }
    public Customer getCustomer() { return customer; }
    public List<Transaction> getTransactions() { return transactions; }
}

