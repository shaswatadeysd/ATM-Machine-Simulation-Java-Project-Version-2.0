import java.util.*;
import java.io.*;

public class AuthService {

    private static final String FILE_NAME = "users.txt";
    private static List<User> users = new ArrayList<>();

    public static void loadUsers() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) return;

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(",");
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error loading users!");
        }
    }

    public static void saveUsers() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME));

            for (User u : users) {
                pw.println(u.getEmail() + "," + u.getMobile() + "," + u.getPassword());
            }
            pw.close();
        } catch (Exception e) {
            System.out.println("Error saving users!");
        }
    }

    public static void register(Scanner sc) {
        System.out.print("Enter Email or Phone: ");
        String input = sc.nextLine();

        String email = "";
        String mobile = "";

        if (input.contains("@")) {
            email = input;
        } else {
            mobile = input;
        }

        for (User u : users) {
            if ((!email.isEmpty() && email.equals(u.getEmail())) ||
                (!mobile.isEmpty() && mobile.equals(u.getMobile()))) {

                System.out.println("Already registered!");
                return;
            }
        }

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        users.add(new User(email, mobile, password));
        saveUsers();

        System.out.println("Registration successful!");
    }

    public static boolean login(Scanner sc) {
        System.out.print("Enter Email or Phone: ");
        String input = sc.nextLine();
        return login(input, sc);
    }

    public static boolean login(String input, Scanner sc) {

        for (User u : users) {
            if (u.getEmail().equals(input) || u.getMobile().equals(input)) {

                while (true) {
                    System.out.print("Enter Password: ");
                    String pass = sc.nextLine();

                    if (pass.equals(u.getPassword())) {
                        System.out.println("Login success!");
                        return true;
                    } else {
                        System.out.println("Wrong password!");
                    }
                }
            }
        }

        System.out.println("User not found!");
        return false;
    }
}