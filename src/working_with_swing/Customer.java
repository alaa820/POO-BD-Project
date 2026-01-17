package working_with_swing;

public class Customer {
    private final String name;
    private final String surname;
    private final String phone;

    public Customer(String name, String surname, String phone) {
        this.name = name == null ? "" : name;
        this.surname = surname == null ? "" : surname;
        this.phone = phone == null ? "" : phone;
    }

    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPhone() { return phone; }
}
