package bw.ac.thuto.bank.model;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    private String bankName;
    private String branchName;
    private String branchCode;
    private String swiftCode;
    private String headOfficeAddress;
    private String telephoneNumber;
    private String emailAddress;
    private String country;
    private String city;
    private List<Customer> customers;
    private List<Account> accounts;

    public Bank(String bankName, String branchName, String branchCode, String swiftCode,
                String headOfficeAddress, String telephoneNumber, String emailAddress,
                String country, String city) {
        this.bankName = bankName;
        this.branchName = branchName;
        this.branchCode = branchCode;
        this.swiftCode = swiftCode;
        this.headOfficeAddress = headOfficeAddress;
        this.telephoneNumber = telephoneNumber;
        this.emailAddress = emailAddress;
        this.country = country;
        this.city = city;
        this.customers = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    public void registerCustomer(Customer customer) {
        if (customer != null && !customers.contains(customer)) {
            customers.add(customer);
        }
    }

    public void openAccount(Customer customer, Account account) {
        if (customer != null && account != null) {
            customer.addAccount(account);
            accounts.add(account);
        }
    }

    public Account findAccount(String accountNumber) {
        if (accountNumber == null) return null;
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) return account;
        }
        return null;
    }

    public String getBankName() { return bankName; }
    public String getBranchName() { return branchName; }
    public String getBranchCode() { return branchCode; }
    public String getSwiftCode() { return swiftCode; }
    public String getHeadOfficeAddress() { return headOfficeAddress; }
    public String getTelephoneNumber() { return telephoneNumber; }
    public String getEmailAddress() { return emailAddress; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public List<Customer> getCustomers() { return customers; }
    public List<Account> getAccounts() { return accounts; }
}