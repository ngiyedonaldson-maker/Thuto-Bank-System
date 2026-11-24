package bw.ac.thuto.bank.model;

public class InvestmentAccount extends Account implements InterestBearing {
    private final double interestRate = 0.05;
    private final double minimumOpeningDeposit = 500.00;

    public InvestmentAccount(String accountNumber, String branch, Customer customer) {
        super(accountNumber, branch, customer);
    }

    @Override
    public void deposit(double amount) {
        if (balance == 0 && amount < minimumOpeningDeposit) {
            throw new IllegalArgumentException("Minimum opening deposit is BWP 500.00");
        }
        super.deposit(amount);
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
    public String getAccountType() { return "Investment Account"; }
}
