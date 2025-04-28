package com.mycompany.blackjack;

import java.util.HashMap;
import java.util.Scanner;

public class BlackJack
{
    public static void main(String[] args)
    {
        Bank playerBank = new Bank();
        Scanner scanner = new Scanner(System.in);
        boolean replay = true;

        while (replay)
        {
            HashMap<String, Integer> deck = SetDeck.createDeck();
            int balance = playerBank.getBalance();

            if (balance <= 0)
            {
                System.out.println("\n=== Game Over ===");
                System.out.println("You are out of money!");

                System.out.print("\n\nEnter your deposit amount here: (e to exit) ");
                try
                {
                    int depositAmount = scanner.nextInt();
                    playerBank.increaseBalance(depositAmount);
                }
                catch (Exception e)
                {
                    System.out.println("Exiting game.");
                    break;
                }
            }
            
            // reads balance again
            balance = playerBank.getBalance();
            
            System.out.println("\n===============================");
            System.out.println("Current Balance: $" + balance);
            System.out.print("Enter your bet amount: ");
            int bet = scanner.nextInt();
            scanner.nextLine();

            if (bet > balance || bet <= 0)
            {
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

            boolean hasPlayed = false;
            int originalBet = bet; // Store the original bet before doubling

            System.out.println("\n=== Your Turn ===");
            while (true)
            {
                System.out.print("Hit or Stand or Double? (h/s/d): ");
                String action = scanner.nextLine().toLowerCase();

                if (action.equals("h"))
                {
                    game.hit("player");
                    hasPlayed = true;
                    System.out.println("Your hand: " + game.getPlayerHand() + " (Score: " + game.getPlayerScore() + ")");
                    if (game.getPlayerScore() == 21)
                    {
                        System.out.println("You hit 21!");
                        break;
                    }
                    
                    if (game.getPlayerScore() > 21)
                    {
                        System.out.println("Bust! You lose your bet.");
                        playerBank.deductBalance(bet);
                        break;
                    }
                }
                else if (action.equals("s"))
                {
                    break;
                }
                else if (action.equals("d"))
                {
                    if (hasPlayed)
                    {
                        System.out.println("You can only double as your first move.");
                        continue;
                    }

                    if (playerBank.getBalance() >= bet)
                    {
                        bet = bet * 2; // Double the bet
                        game.hit("player");
                        hasPlayed = true;

                        System.out.println("Your hand: " + game.getPlayerHand() + " (Score: " + game.getPlayerScore() + ")");
                        if (game.getPlayerScore() > 21)
                        {
                            System.out.println("Bust! You lose your bet.");
                            playerBank.deductBalance(bet); // Deduct the full doubled bet
                            break;
                        }
                    }
                    else
                    {
                        System.out.println("Not enough balance to double your bet.");
                    }
                }
                else
                {
                    System.out.println("Invalid input. Please enter 'h', 's', or 'd'.");
                }
            }

            if (game.getPlayerScore() <= 21)
            {
                System.out.println("\n=== House Turn ===");
                System.out.println("House reveals: " + game.getHouseHand() + " (Score: " + game.getHouseScore() + ")");

                while (game.getHouseScore() <= 16 && game.getPlayerScore() != 21)
                {
                    game.hit("house");
                    System.out.println("House hits: " + game.getHouseHand().get(game.getHouseHand().size() - 1));
                    System.out.println("House hand: " + game.getHouseHand() + " (Score: " + game.getHouseScore() + ")");
                }

                System.out.println("\n=== Result ===");
                Boolean result = game.bustCheck();
                if (result == null)
                {
                    System.out.println("It's a draw!");
                    
                }
                else if (result)
                {
                    playerBank.increaseBalance(bet); // Full payout (doubled bet)
                }
                else
                {
                    System.out.println("House wins.");
                    playerBank.deductBalance(bet); // Deduct the full doubled bet here if the player loses
                }

            }
            
            System.out.println("New balance: $" + playerBank.getBalance()); // Show new balance after hand

            System.out.print("\nPlay again? (y/n): ");
            String again = scanner.nextLine().toLowerCase();
            replay = again.equals("y");

            System.out.println("\n\n=========================================\n");
        }

        System.out.println("Thanks for playing! Final balance: $" + playerBank.getBalance());
        scanner.close();
    }
}
