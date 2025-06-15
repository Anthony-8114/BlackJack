package com.mycompany.blackjack;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;

public class BlackJack {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PlayerAccount playerAccount = handleLogin(scanner);
        
        if (playerAccount == null) {
            System.out.println("Exiting game.");
            scanner.close();
            return;
        }

        Bank playerBank = playerAccount.getBank();
        boolean replay = true;

        while (replay) {
            HashMap<String, Integer> deck = SetDeck.createDeck();
            int balance = playerBank.getBalance();

            if (balance <= 0) {
                handleEmptyBalance(playerBank, scanner);
                if (playerBank.getBalance() <= 0) break;
            }
            
            balance = playerBank.getBalance();
            int bet = getValidBet(scanner, balance);
            if (bet == -1) continue;

            GameLogic game = new GameLogic(deck);
            dealInitialCards(game);
            displayInitialHands(game);
   
            boolean hasPlayed = false;
            int originalBet = bet;

            playerTurn(scanner, game, playerBank, bet, hasPlayed);
            
            if (game.getPlayerScore() <= 21) {
                houseTurn(game);
                handleGameResult(game, playerBank, bet);
            }
            
            System.out.println("New balance: $" + playerBank.getBalance());
            replay = askToPlayAgain(scanner);
        }

        System.out.println("Thanks for playing! Final balance: $" + playerBank.getBalance());
        scanner.close();
    }

    private static PlayerAccount handleLogin(Scanner scanner) {
        System.out.println("=== Blackjack Console Version ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Play as Guest");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                return handleUserLogin(scanner);
            case "2":
                return handleUserRegistration(scanner);
            case "3":
                return new PlayerAccount("guest", "");
            case "4":
                return null;
            default:
                System.out.println("Invalid option. Please try again.");
                return handleLogin(scanner);
        }
    }

    private static PlayerAccount handleUserLogin(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        PlayerAccount account = new PlayerAccount(username, password);
        if (account.login()) {
            System.out.println("Login successful!");
            return account;
        } else {
            System.out.println("Invalid username or password.");
            return handleLogin(scanner);
        }
    }

    private static PlayerAccount handleUserRegistration(Scanner scanner) {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();

        PlayerAccount account = new PlayerAccount(username, password);
        if (account.register()) {
            System.out.println("Registration successful! Please login.");
            return handleUserLogin(scanner);
        } else {
            System.out.println("Username already exists. Please try again.");
            return handleLogin(scanner);
        }
    }

    private static void handleEmptyBalance(Bank playerBank, Scanner scanner) {
        System.out.println("\n=== Game Over ===");
        System.out.println("You are out of money!");
        System.out.print("Enter your deposit amount (or 'e' to exit): ");
        String input = scanner.nextLine();
        
        if (input.equalsIgnoreCase("e")) {
            return;
        }

        try {
            int depositAmount = Integer.parseInt(input);
            if (depositAmount <= 0) {
                System.out.println("Deposit must be positive.");
            } else {
                playerBank.increaseBalance(depositAmount);
                System.out.println("Deposit successful. New balance: $" + playerBank.getBalance());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private static int getValidBet(Scanner scanner, int balance) {
        System.out.println("\n===============================");
        System.out.println("Current Balance: $" + balance);
        System.out.print("Enter your bet amount: ");
        try {
            String input = scanner.nextLine();
            int bet = Integer.parseInt(input);
            if (bet > balance || bet <= 0) {
                System.out.println("Invalid bet. Try again.");
                return -1;
            }
            return bet;
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

    private static void dealInitialCards(GameLogic game) {
        game.hit("player");
        game.hit("house");
        game.hit("player");
        game.hit("house");
    }

    private static void displayInitialHands(GameLogic game) {
        System.out.println("\n=== Initial Deal ===");
        System.out.println("Your hand: " + game.getPlayerHand() + " (Score: " + game.getPlayerScore() + ")");
        System.out.println("House hand: [" + game.getHouseHand().get(0) + ", ?]");
    }

    private static void playerTurn(Scanner scanner, GameLogic game, Bank playerBank, int bet, boolean hasPlayed) {
        System.out.println("\n=== Your Turn ===");
        while (true) {
            System.out.print("Hit or Stand or Double? (h/s/d): ");
            String action = scanner.nextLine().toLowerCase();

            if (action.equals("h")) {
                game.hit("player");
                hasPlayed = true;
                System.out.println("Your hand: " + game.getPlayerHand() + " (Score: " + game.getPlayerScore() + ")");
                if (game.getPlayerScore() >= 21) break;
            } else if (action.equals("s")) {
                break;
            } else if (action.equals("d")) {
                handleDoubleDown(scanner, game, playerBank, bet, hasPlayed);
                break;
            } else {
                System.out.println("Invalid input. Please enter 'h', 's', or 'd'.");
            }
        }
    }

    private static void handleDoubleDown(Scanner scanner, GameLogic game, Bank playerBank, int bet, boolean hasPlayed) {
        if (hasPlayed) {
            System.out.println("You can only double as your first move.");
            return;
        }

        if (playerBank.getBalance() >= bet) {
            bet *= 2;
            playerBank.recordWager(bet);
            game.hit("player");
            System.out.println("Your hand: " + game.getPlayerHand() + " (Score: " + game.getPlayerScore() + ")");
            if (game.getPlayerScore() > 21) {
                System.out.println("Bust! You lose your bet.");
                playerBank.deductBalance(bet);
                playerBank.recordLoss(bet);
            }
        } else {
            System.out.println("Not enough balance to double your bet.");
        }
    }

    private static void houseTurn(GameLogic game) {
        System.out.println("\n=== House Turn ===");
        System.out.println("House reveals: " + game.getHouseHand() + " (Score: " + game.getHouseScore() + ")");

        while (game.getHouseScore() <= 16 && game.getPlayerScore() != 21) {
            game.hit("house");
            System.out.println("House hits: " + game.getHouseHand().get(game.getHouseHand().size() - 1));
            System.out.println("House hand: " + game.getHouseHand() + " (Score: " + game.getHouseScore() + ")");
        }
    }

    private static void handleGameResult(GameLogic game, Bank playerBank, int bet) {
        System.out.println("\n=== Result ===");
        Boolean result = game.bustCheck();
        if (result == null) {
            System.out.println("It's a draw!");
        } else if (result) {
            System.out.println("You win!");
            playerBank.increaseBalance(bet);
            playerBank.recordWin(bet);
        } else {
            System.out.println("You lose!");
            playerBank.deductBalance(bet);
            playerBank.recordLoss(bet);
        }
    }

    private static boolean askToPlayAgain(Scanner scanner) {
        System.out.print("\nPlay again? (y/n): ");
        String again = scanner.nextLine().toLowerCase();
        System.out.println("\n\n=========================================\n");
        return again.equals("y");
    }
}
