import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // DATABASE INITIALIZATION
        String dbUrl = "jdbc:mysql://localhost:3306/simple_bank?useSSL=false&serverTimezone=UTC";
        String dbUsername = "root";
        String dbUserPassword = "";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbUserPassword)) {
            System.out.println("Database connection initialized !");
            bankInterface(conn);

        } catch (SQLException dbE) {
            System.out.println("SQL Error: "+ dbE.getMessage());
            // dbE.printStackTrace();
        }

        System.out.println("DATABASE UPDATED\n");
        System.out.println("HAVE A NICE DAY !!!");
        scanner.close();
    }

    static void bankInterface(Connection conn) {
        double balance = 0;
        boolean isRunning = true;
        int choice;
        String whoAmI;

        System.out.print("Si quheni ju ?: ");
        whoAmI = capitalizeFirstLetter(scanner.nextLine());

        String getNameInfoSQL = "SELECT bilanci FROM perdoruesi WHERE emri = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(getNameInfoSQL)) {
            pstmt.setString(1, whoAmI);
            ResultSet balResult = pstmt.executeQuery();
            if (balResult.next()) {
                balance = balResult.getDouble("bilanci");
                System.out.println("Bilanci: " + balResult.getDouble("bilanci"));
            } else {
                String insertNameSQL = "INSERT INTO perdoruesi (emri, bilanci) VALUES (?, 0)";
                PreparedStatement insertStatement = conn.prepareStatement(insertNameSQL);
                insertStatement.setString(1, whoAmI);
                insertStatement.executeUpdate();
                System.out.println("New user registered with balance 0!");
            }

        } catch (SQLException insE) {
            System.out.println("SQL error: " + insE.getMessage());
            // insE.printStackTrace();
        }

        while (isRunning) {
            System.out.printf("%n..::%s jeni i kyqur ne sistem !::..%n..::Zgjedhni nga alternativat me poshte !::..%n", whoAmI);
            try {
                System.out.println("*****************");
                System.out.println("MY BANKING SYSTEM");
                System.out.println("*****************");
                System.out.println("1. Show balance");
                System.out.println("2. Make deposit");
                System.out.println("3. Make withdrawal");
                System.out.println("4. Exit");
                System.out.println("*****************");

                System.out.print("Please make a choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> showBalance(balance);
                    case 2 -> balance += deposit();
                    case 3 -> balance -= withdraw(balance);
                    case 4 -> isRunning = false;
                    default -> System.out.println("Invalid input!");
                }
            }
            catch (InputMismatchException e) {
                System.out.printf("Invalid input. Your input gave %s.", e.getMessage());
                // e.printStackTrace();
                scanner.next();
            }
        }
        String finalBalanceSQL = "UPDATE perdoruesi SET bilanci = ? WHERE emri = ?";
        try (PreparedStatement updateBal = conn.prepareStatement(finalBalanceSQL)) {
            updateBal.setDouble(1, balance);
            updateBal.setString(2, whoAmI);
            updateBal.executeUpdate();
        } catch (SQLException updtE) {
            System.out.println("Balance Update Error: " + updtE.getMessage());
            // updtE.printStackTrace();
        }

    }

    static void showBalance(double balance) {
        System.out.println("________________________");
        System.out.printf("You have $%.2f%n", balance);
        System.out.println("________________________");
    }

    static double deposit() {
        double amount;
        System.out.print("Enter deposit amount: ");
        amount = scanner.nextDouble();

        if (amount > 0) {
            System.out.println("____________________________________");
            System.out.printf("You have deposited $%.2f successfully !%n", amount);
            System.out.println("____________________________________");
            return amount;
        }
        else {
            System.out.println("_____________________________");
            System.out.println("Entered amount is incorrect !");
            System.out.println("_____________________________");
        }
        return 0;
    }

    static double withdraw(double balance) {
        double amount;
        System.out.print("Enter withdraw ammount: ");
        amount = scanner.nextDouble();

        if (amount > 0 && amount <= balance) {
            System.out.println("____________________________________");
            System.out.printf("You have withdrawn $%.2f successfully from balance: $%.2f !%n", amount, balance);
            System.out.println("____________________________________");
            return amount;
        }
        else {
            System.out.println("Entered ammount is incorrect or there are insufficient funds");
        }
        return 0;
    }

    static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

}
