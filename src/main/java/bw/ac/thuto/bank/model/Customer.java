package bw.ac.thuto.bank.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends Person {
    private String customerId;
    private String dateRegistered;
    private String employerName;
    private String employerAddress;
    private List<Account> accounts = new ArrayList<>();

    public Customer(String customerId, String firstName, String surname, String address,
                    String phone, String email, String employerName, String employerAddress) {
        super(firstName, surname, address, phone, email);
        this.customerId = customerId;
        this.dateRegistered = java.time.LocalDate.now().toString();
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    public void addAccount(Account account) { accounts.add(account); }
    public void removeAccount(Account account) { accounts.remove(account); }
    public List<Account> getAccounts() { return accounts; }
    public String getCustomerId() { return customerId; }
    public String getDateRegistered() { return dateRegistered; }
    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }
}
