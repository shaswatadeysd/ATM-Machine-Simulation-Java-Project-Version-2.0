public class Account {
    private int pin;
    private double balance;


    public Account(int pin, double balance) {
        this.pin = pin;
        this.balance = balance;
    }


    public boolean validatePin(int inputPin) {
        return this.pin == inputPin;
    }


    public double getBalance() {
        return balance;
    }

    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposit Successful!");
        } else {
            System.out.println("Invalid amount!");
        }
    }


    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return false;
        }

        if (amount <= balance) {
            balance -= amount;
            return true;
        } else {
            return false;
        }
    }
}