package bw.ac.thuto.bank.controller;

import bw.ac.thuto.bank.dao.AccountDAO;
import bw.ac.thuto.bank.dao.TransactionDAO;
import bw.ac.thuto.bank.model.Account;
import bw.ac.thuto.bank.model.Customer;
import bw.ac.thuto.bank.model.InterestBearing;
import bw.ac.thuto.bank.model.PayInterest;
import bw.ac.thuto.bank.model.Transaction;

import java.sql.SQLException;

public class AccountController {

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public void deposit(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        try {
            account.deposit(amount);
            accountDAO.updateBalance(account);

            Transaction latest = account.getTransactions()
                    .get(account.getTransactions().size() - 1);
            transactionDAO.saveTransaction(latest, account.getAccountNumber());
        } catch (SQLException e) {
            throw new RuntimeException("Database error during deposit", e);
        }
    }

    public void withdraw(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        try {
            account.withdraw(amount);
            accountDAO.updateBalance(account);

            Transaction latest = account.getTransactions()
                    .get(account.getTransactions().size() - 1);
            transactionDAO.saveTransaction(latest, account.getAccountNumber());
        } catch (SQLException e) {
            throw new RuntimeException("Database error during withdrawal", e);
        }
    }

    public void payInterest(Account account) {
        if (!(account instanceof InterestBearing)) {
            throw new IllegalArgumentException("This account does not earn interest");
        }
        PayInterest process = new PayInterest();
        try {
            process.initiate();
            ((InterestBearing) account).applyInterest();
            process.calculateSuccess();
            process.validateEligibility();

            accountDAO.updateBalance(account);
            Transaction latest = account.getTransactions()
                    .get(account.getTransactions().size() - 1);
            transactionDAO.saveTransaction(latest, account.getAccountNumber());

            process.postInterest();
            process.confirmPosting();
            System.out.println("Interest paid. Final status: " + process.getStatus());
        } catch (SQLException e) {
            process.calculationError();
            throw new RuntimeException("Database error during interest payment", e);
        }
    }

    public Account findAccount(String accountNumber, Customer customer) {
        try {
            return accountDAO.findByNumber(accountNumber, customer);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while looking up account", e);
        }
    }
}
