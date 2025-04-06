/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */


package com.mycompany.blackjack;

import java.util.HashMap;
import java.util.Scanner;


/**
 *
 * @author ay196
 */

public class BlackJack {
    public static void main(String[] args) {
        Bank playerBank = new Bank();
        Scanner scanner = new Scanner(System.in);
        boolean replay = true;

        while (replay) {
            HashMap<String, Integer> deck = SetDeck.createDeck();
            int balance = playerBank.getBalance();

            if (balance <= 0) {
                System.out.println("\n=== Game Over ===");
                System.out.println("You are out of money!");
                break;
            }

            System.out.println("\n===============================");
            System.out.println("Current Balance: $" + balance);
            System.out.print("Enter your bet amount: ");
            int bet = scanner.nextInt();
            scanner.nextLine();

            if (bet > balance || bet <= 0) {
                System.out.println("Invalid bet. Try again.");
                continue;
            }

            GameLogic game = new GameLogic(deck);

            game.hit("player");
            game.hit("house");
            game.hit("player");
            game.hit("house");

            System.out.println("\n=== Initial Deal ===");
            System.out.println("Your hand: " + game.getPlayerHand() + " (Score: " + game.getPlayerScore() + ")");
            System.out.println("House hand: [" + game.getHouseHand().get(0) + ", ?]");

            System.out.println("\n=== Your Turn ===");
            while (true) {
                System.out.print("Hit or Stand? (h/s): ");
                String action = scanner.nextLine().toLowerCase();

                if (action.equals("h")) {
                    game.hit("player");
                    System.out.println("Your hand: " + game.getPlayerHand() + " (Score: " + game.getPlayerScore() + ")");
                    if (game.getPlayerScore() > 21) {
                        System.out.println("Bust! You lose your bet.");
                        playerBank.deductBalance(bet);
                        break;
                    }
                } else if (action.equals("s")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'h' or 's'.");
                }
            }

            if (game.getPlayerScore() <= 21) {
                System.out.println("\n=== House Turn ===");
                System.out.println("House reveals: " + game.getHouseHand() + " (Score: " + game.getHouseScore() + ")");

                while (game.getHouseScore() <= 16) {
                    game.hit("house");
                    System.out.println("House hits: " + game.getHouseHand().get(game.getHouseHand().size() - 1));
                    System.out.println("House hand: " + game.getHouseHand() + " (Score: " + game.getHouseScore() + ")");
                }

                System.out.println("\n=== Result ===");
                Boolean result = game.bustCheck();
                if (result == null) {
                    System.out.println("It's a draw!");
                } else if (result) {
                    System.out.println("You win!");
                    playerBank.increaseBalance(bet);
                } else {
                    System.out.println("House wins.");
                    playerBank.deductBalance(bet);
                }

                System.out.println("New balance: $" + playerBank.getBalance());
            }

            System.out.print("\nPlay again? (y/n): ");
            String again = scanner.nextLine().toLowerCase();
            replay = again.equals("y");

            System.out.println("\n\n=========================================\n");
        }

        System.out.println("Thanks for playing! Final balance: $" + playerBank.getBalance());
        scanner.close();
    }
}
