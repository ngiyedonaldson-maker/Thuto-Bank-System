package bw.ac.thuto.bank.model;

public class ChequeAccount extends Account {
    public ChequeAccount(String accountNumber, String branch, Customer customer) {
        super(accountNumber, branch, customer);
        if (customer.getEmployerName() == null || customer.getEmployerName().isEmpty()) {
            throw new IllegalArgumentException("Cheque accounts require employer information.");
        }
    }

    @Override
    public String getAccountType() { return "Cheque Account"; }
}
