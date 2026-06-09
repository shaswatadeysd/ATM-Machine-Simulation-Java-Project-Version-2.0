import java.util.ArrayList;
import java.util.List;

public class Account {
    private User user;
    private List<TransactionRecord> history;

    public Account(User user) {
        this.user = user;
        this.history = new ArrayList<>();
    }

    public boolean validatePin(int inputPin) {
        return user.getPin() == inputPin;
    }

    public double getBalance() {
        return user.getBalance();
    }

    public TransactionRecord deposit(double amount) {
        if (amount <= 0) return null;
        user.setBalance(user.getBalance() + amount);
        TransactionRecord rec = new TransactionRecord("Deposit", amount, user.getBalance());
        history.add(rec);
        return rec;
    }

    public TransactionRecord withdraw(double amount) {
        if (amount <= 0) return null;
        if (amount > user.getBalance()) return null;
        user.setBalance(user.getBalance() - amount);
        TransactionRecord rec = new TransactionRecord("Withdrawal", amount, user.getBalance());
        history.add(rec);
        return rec;
    }

    public void addHistoryRecord(TransactionRecord record) {
        history.add(record);
    }

    public List<TransactionRecord> getHistory() { return history; }
    public User getUser() { return user; }
}