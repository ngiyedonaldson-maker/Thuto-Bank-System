package bw.ac.thuto.bank.model;

public class SavingsAccount extends Account implements InterestBearing {
    private final double interestRate = 0.0005;

    public SavingsAccount(String accountNumber, String branch, Customer customer) {
        super(accountNumber, branch, customer);
    }

    @Override
    public void withdraw(double amount) {
        throw new UnsupportedOperationException("Savings accounts do not allow withdrawals");
    }

    @Override
    public double calculateInterest() { return balance * interestRate; }

    @Override
    public void applyInterest() {
        double interest = calculateInterest();
        balance += interest;
        transactions.add(new Transaction("INTEREST", interest, balance));
    }

    @Override
    public String getAccountType() { return "Savings Account"; }
}
