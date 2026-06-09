import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionRecord {
    private String type;
    private double amount;
    private double balanceAfter;
    private String dateTime;

    public TransactionRecord(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.dateTime = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"));
    }

    public TransactionRecord(String type, double amount, double balanceAfter, String dateTime) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.dateTime = dateTime;
    }

    public String getType()        { return type; }
    public double getAmount()      { return amount; }
    public double getBalanceAfter(){ return balanceAfter; }
    public String getDateTime()    { return dateTime; }

    public String toCSV() {
        return type + "," + amount + "," + balanceAfter + "," + dateTime;
    }
}
