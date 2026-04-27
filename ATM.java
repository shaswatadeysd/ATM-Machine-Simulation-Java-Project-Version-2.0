import java.util.Scanner;

public class ATM {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AuthService.loadUsers();

        while (true) {
            System.out.println("\n===== WELCOME =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            int choice = Integer.parseInt(sc.nextLine());

            if (choice == 1) {
                AuthService.register(sc);
            }

            else if (choice == 2) {
                if (AuthService.login(sc)) {

                    Account userAccount = new Account(4321, 5000);

                    int attempts = 3;
                    boolean login = false;

                    while (attempts > 0) {
                        System.out.print("Enter ATM PIN: ");
                        int pin = Integer.parseInt(sc.nextLine());

                        if (userAccount.validatePin(pin)) {
                            login = true;
                            break;
                        } else {
                            attempts--;
                            System.out.println("Wrong PIN! Left: " + attempts);
                        }
                    }

                    if (login) {
                        ATMOperations atm = new ATMOperations(userAccount);
                        atm.showMenu();
                    } else {
                        System.out.println("Card Blocked!");
                    }
                }
            }

            else if (choice == 3) {
                System.out.println("Thank you!");
                break;
            }

            else {
                System.out.println("Invalid choice!");
            }
        }
    }
}