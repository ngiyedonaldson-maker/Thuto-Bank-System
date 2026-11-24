package bw.ac.thuto.bank.model;

public abstract class Person {
    protected String firstName;
    protected String surname;
    protected String address;
    protected String phone;
    protected String email;

    public Person(String firstName, String surname, String address, String phone, String email) {
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public String getFullName() { return firstName + " " + surname; }
    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}