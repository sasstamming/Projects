package service;

import model.User;
import model.Fund;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

        public class BankingService {

        private enum AppState {
        LOGIN, MENU, EXIT
         }

        private AppState state = AppState.LOGIN;
        private User currentUser;
        private final Map<String, User> users = new HashMap<>();

        public void start(Scanner scanner) {
        seedUsers();

     while (state != AppState.EXIT) {
     if (state == AppState.LOGIN) {
                handleLogin(scanner);
            } 
     else if (state == AppState.MENU) {
                runMenu(scanner);
                }
              }
            }

        private void seedUsers() {
        users.put("Alice", new User("Alice"));
        users.put("Bob", new User("Bob"));
        users.put("Charlie", new User("Charlie"));
        users.put("Diana", new User("Diana"));
        }

        private void handleLogin(Scanner scanner) {
        System.out.print("Enter your name to login: ");
     if (!scanner.hasNextLine()) {
            state = AppState.EXIT;
            return;
             }

        String name = scanner.nextLine();
     if (users.containsKey(name)) {
            currentUser = users.get(name);
            System.out.println("Welcome, " + name + "!");
            System.out.println();
            state = AppState.MENU;
            } 
            else {
            System.out.println("User not found. Please try again.");
             }
            }

         private void runMenu(Scanner scanner) {
            while (state == AppState.MENU) {
            printMenu();

            if (!scanner.hasNextLine()) {
                state = AppState.EXIT;
                return;
              }

            String input = scanner.nextLine();
            handleMenuChoice(input, scanner);
            }
         }

     private void printMenu() {
        System.out.println("--- Banking App Menu ---");
        System.out.println("1. Show balance");
        System.out.println("2. Deposit money");
        System.out.println("3. Withdraw money");
        System.out.println("4. Send money to a person");
        System.out.println("5. Invest in funds");
        System.out.println("6. Transfer between accounts");
        System.out.println("7. Withdraw all investments");
        System.out.println("8. Logout");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
             }

        private void handleMenuChoice(String input, Scanner scanner) {
         try {
            int choice = Integer.parseInt(input);
            switch (choice) {
            case 1 -> showBalance();
            case 2 -> deposit(scanner);
            case 3 -> withdraw(scanner);
            case 4 -> sendMoney(scanner);
            case 5 -> invest(scanner);
            case 6 -> transfer(scanner);
            case 7 -> liquidateInvestments();
            case 8 -> logout();
            case 9 -> exit();
            default -> invalidChoice();
                }
            } 
        catch (NumberFormatException e) {
            invalidChoice();
            }
                 }

            private void invalidChoice() {

             System.out.println("Invalid choice. Please try again.");
             System.out.println();
             }

            private void showBalance() {
                 currentUser.applyGrowth();

             System.out.println("Savings account balance: $" +
                 currentUser.getSavingsAccount().getBalance());

             System.out.println("Investment account balance:");
             System.out.println("* Not Invested: $" +
                currentUser.getInvestmentAccount().getBalance().setScale(2));

          for (Fund fund : Fund.values()) {
                BigDecimal amount =
                 currentUser.getInvestmentAccount()
                                 .getInvestedAmounts()
                                    .get(fund);

             if (amount.compareTo(BigDecimal.ZERO) > 0) {
             System.out.println("* " + fund + ": $" + amount.setScale(2));
            }
               }
               System.out.println();
            }

            private void deposit(Scanner scanner) {
             System.out.print("Enter amount to deposit to savings account: $");
          if (!scanner.hasNextLine()) return;

            try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
         if (!isPositive(amount)) {
            System.out.println("Deposit failed: amount must be positive");
            System.out.println();
                return;
            }

          if (currentUser.getCash().compareTo(amount) < 0) {
             System.out.println("Deposit failed: Insufficient cash on hand");
             System.out.println();
                return;
            }

            currentUser.setCash(currentUser.getCash().subtract(amount));
            currentUser.getSavingsAccount().add(amount);
          System.out.println("Deposit successful.");
            System.out.println();

             }
              catch (NumberFormatException ignored) {
             System.out.println();
             }
            }

             private void withdraw(Scanner scanner) {
                System.out.print("Enter amount to withdraw from savings account: $");
        if (!scanner.hasNextLine()) return;

             try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
        if (!isPositive(amount)) {
             System.out.println("Withdrawal failed: amount must be positive");
             System.out.println();
                return;
            }

        if (currentUser.getSavingsAccount().getBalance().compareTo(amount) < 0) {
                System.out.println("Withdrawal failed: Insufficient funds");
                System.out.println();
                return;
            }

            currentUser.getSavingsAccount().subtract(amount);
            currentUser.setCash(currentUser.getCash().add(amount));
            System.out.println("Withdrawal successful.");
            System.out.println();

            } 
            catch (NumberFormatException ignored) {
            System.out.println();
             }
         }

             private void sendMoney(Scanner scanner) {
                 System.out.println("Available recipients:");
                for (String name : new String[]{"Alice","Bob","Charlie","Diana"}) {
          if (!name.equals(currentUser.getName())) {
                 System.out.println(name);
                 }  
            }

                System.out.print("Enter recipient's name: ");
         if (!scanner.hasNextLine()) return;

                String recipientName = scanner.nextLine();
        if (!users.containsKey(recipientName)
                || recipientName.equals(currentUser.getName())) {
            System.out.println("Invalid recipient.");
            System.out.println();
            return;
             }

                System.out.print("Enter amount to send: $");
         if (!scanner.hasNextLine()) return;

             try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
        if (!isPositive(amount)) {
                System.out.println("Failed to send money: amount must be positive");
                System.out.println();
                return;
            }

        if (currentUser.getSavingsAccount().getBalance().compareTo(amount) < 0) {
                System.out.println("Failed to send money: Insufficient funds");
                System.out.println();
                return;
            }

            currentUser.getSavingsAccount().subtract(amount);
            users.get(recipientName).getSavingsAccount().add(amount);

            System.out.println("Sent $" + amount + " to " + recipientName);
            System.out.println();

             } 
         catch (NumberFormatException ignored) {
            System.out.println();
             }
                 }

             private void invest(Scanner scanner) {

             System.out.println("Available funds:");
             System.out.println("LOW_RISK");
             System.out.println("MEDIUM_RISK");
             System.out.println("HIGH_RISK");
             System.out.print("Enter fund to invest in: ");

        if (!scanner.hasNextLine()) {
             System.out.println();
            return;
            }
            String fundInput = scanner.nextLine().trim().toUpperCase();

                Fund fund;
                try {
            fund = Fund.valueOf(fundInput);
             }
             catch (IllegalArgumentException e) {
            System.out.println("Invalid fund.");
            System.out.println();

            return;
            }

             System.out.print("Enter amount to invest: $");
          if (!scanner.hasNextLine()) return;

              try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
          if (!isPositive(amount)) {
                System.out.println("Failed to invest: amount must be positive");
                System.out.println();
                return;
            }

         if (currentUser.getInvestmentAccount().getBalance().compareTo(amount) < 0) {
                System.out.println("Failed to invest: Insufficient funds");
                System.out.println();
                return;
            }

            currentUser.getInvestmentAccount().invest(fund, amount);
                System.out.println("Successfully invested $" + amount + " in " + fund + " fund");
                System.out.println();

            }  
             catch (NumberFormatException ignored) {
                 System.out.println();
                }
             }

             private void transfer(Scanner scanner) {
                 System.out.println("1. Transfer from savings to investment");
                 System.out.println("2. Transfer from investment to savings");
                System.out.print("Enter your choice: ");

         if (!scanner.hasNextLine()) return;

         try {
            int choice = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter amount to transfer: $");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

        if (!isPositive(amount)) {
                System.out.println("Transfer failed: amount must be positive");
                System.out.println();
                return;
            }

         if (choice == 1) {
         if (currentUser.getSavingsAccount().getBalance().compareTo(amount) >= 0) {
                currentUser.getSavingsAccount().subtract(amount);
                 currentUser.getInvestmentAccount().add(amount);
                    System.out.println("Successfully transferred $" + amount + " to investment account.");
            } 
        else {
                    System.out.println("Transfer failed: Insufficient funds");
                 }
             } 
        else if (choice == 2) {
         if (currentUser.getInvestmentAccount().getBalance().compareTo(amount) >= 0) {
                currentUser.getInvestmentAccount().subtract(amount);
                 currentUser.getSavingsAccount().add(amount);
                 System.out.println("Successfully transferred $" + amount + " to savings account.");
                   } 
         else {
                System.out.println("Transfer failed: Insufficient funds");
                    }
                } 
         else {
                System.out.println("Invalid choice.");
            }
                System.out.println();

             } 
             catch (NumberFormatException e) {
                System.out.println("Error: Please enter valid numerical values.");
             }
         }

         private void liquidateInvestments() {
        currentUser.getInvestmentAccount().withdrawAll();
        System.out.println("All investments have been withdrawn and added to your investment account balance.");
        System.out.println();
        }

         private void logout() {
        System.out.println("You have been logged out.");
        currentUser = null;
        state = AppState.LOGIN;
        }

        private void exit() {
        System.out.println("Thank you for using our banking app. Goodbye!");
        state = AppState.EXIT;
        }

        private boolean isPositive(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0;
     }
}