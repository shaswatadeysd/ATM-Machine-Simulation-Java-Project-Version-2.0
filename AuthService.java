import java.util.*;
import java.io.*;

public class AuthService {

    private static final String USERS_FILE = "users.txt";
    private static final String TRANS_FILE = "transactions.txt";
    private static List<User> users = new ArrayList<>();

    public static void loadUsers() {
        users.clear();
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) return;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",", -1);
                if (p.length >= 5) {
                    users.add(new User(p[0], p[1], p[2],
                        Integer.parseInt(p[3].trim()),
                        Double.parseDouble(p[4].trim())));
                } else if (p.length == 3) {
                   
                    users.add(new User(p[0], p[1], p[2], 1234, 1000.00));
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    public static void saveUsers() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE));
            for (User u : users)
                pw.printf("%s,%s,%s,%d,%.2f%n",
                    u.getEmail(), u.getMobile(), u.getPassword(),
                    u.getPin(), u.getBalance());
            pw.close();
        } catch (Exception e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static User authenticate(String input, String password) {
        for (User u : users) {
            if (u.getEmail().equals(input) || u.getMobile().equals(input)) {
                return u.getPassword().equals(password) ? u : null;
            }
        }
        return null;
    }

    public static boolean userExists(String input) {
        for (User u : users)
            if (u.getEmail().equals(input) || u.getMobile().equals(input)) return true;
        return false;
    }


    public static boolean register(String emailOrPhone, String password, int pin) {
        String email  = emailOrPhone.contains("@") ? emailOrPhone : "";
        String mobile = emailOrPhone.contains("@") ? "" : emailOrPhone;
        for (User u : users)
            if ((!email.isEmpty()  && email.equals(u.getEmail())) ||
                (!mobile.isEmpty() && mobile.equals(u.getMobile()))) return false;
        users.add(new User(email, mobile, password, pin, 1000.00));
        saveUsers();
        return true;
    }

    public static void saveTransaction(String userKey, TransactionRecord rec) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(TRANS_FILE, true));
            pw.println(userKey + "|" + rec.toCSV());
            pw.close();
        } catch (Exception e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    public static List<TransactionRecord> loadTransactions(String userKey) {
        List<TransactionRecord> list = new ArrayList<>();
        try {
            File file = new File(TRANS_FILE);
            if (!file.exists()) return list;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                int sep = line.indexOf('|');
                if (sep < 0 || !line.substring(0, sep).equals(userKey)) continue;
                // format: type,amount,balanceAfter,dateTime
                String[] p = line.substring(sep + 1).split(",", 4);
                if (p.length == 4)
                    list.add(new TransactionRecord(p[0],
                        Double.parseDouble(p[1]),
                        Double.parseDouble(p[2]), p[3]));
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
        return list;
    }

    public static List<User> getUsers() { return users; }
}