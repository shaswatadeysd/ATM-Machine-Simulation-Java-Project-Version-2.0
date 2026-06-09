public class User {
    private String email;
    private String mobile;
    private String password;
    private int pin;
    private double balance;

    public User(String email, String mobile, String password, int pin, double balance) {
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.pin = pin;
        this.balance = balance;
    }

    public String getEmail()    { return email; }
    public String getMobile()   { return mobile; }
    public String getPassword() { return password; }
    public int    getPin()      { return pin; }
    public double getBalance()  { return balance; }

    public void setBalance(double balance) { this.balance = balance; }
    public void setPin(int pin)            { this.pin = pin; }

    public String getIdentifier() {
        return (email != null && !email.isEmpty()) ? email : mobile;
    }
}
