/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.blackjack;

/**
 *
 * @author ay196
 */
import java.util.HashMap;
import java.util.Scanner;



//main game loop for blackjack game flow
public class BlackJack {
    public static void main(String[] args) {
        Bank playerBank = new Bank();
        Scanner scanner = new Scanner(System.in);
        boolean replay = true;

        while (replay) {
            
            //creates deck and shuffles for each new round of play
            HashMap<String, Integer> deck = SetDeck.createDeck();
            int balance = playerBank.getBalance();

            
            //if statement to check if player has run out of money
            if (balance <= 0) {
                handleEmptyBalance(playerBank, scanner);
                if (playerBank.getBalance() <= 0) break;
            }
            
            
            //gets valid bet from player
            balance = playerBank.getBalance();
            int bet = getValidBet(scanner, balance);
            if (bet == -1) continue;

            
            
            //game logic and deals cards
            GameLogic game = new GameLogic(deck);
            dealInitialCards(game);
            displayInitialHands(game);

            
   
            boolean hasPlayed = false;
            int originalBet = bet;

            
            //players chance to play allows user to hit stand or double
            playerTurn(scanner, game, playerBank, bet, hasPlayed);
            
            
            
            //house now plays if the player did not bust

            if (game.getPlayerScore() <= 21) {
                houseTurn(game);
                handleGameResult(game, playerBank, bet);
            }
            
            
            //this will now display new balance once game is done and asks user if they would like to play again
            System.out.println("New balance: $" + playerBank.getBalance());
            replay = askToPlayAgain(scanner);
        }

        
        
        // if the user chooses no or "n" game closes with this 
        System.out.println("Thanks for playing! Final balance: $" + playerBank.getBalance());
        scanner.close();
    }

    
    
    //This handles the player balance if the user ends up broke
    private static void handleEmptyBalance(Bank playerBank, Scanner scanner) {
        System.out.println("\n=== Game Over ===");
        System.out.println("You are out of money!");
        System.out.print("\n\nEnter your deposit amount here: (e to exit) ");
        try {
            int depositAmount = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            playerBank.increaseBalance(depositAmount);
        } catch (Exception e) {
            System.out.println("Exiting game.");
        }
    }

    
    
    //this gets the player bet amount validates also 
    private static int getValidBet(Scanner scanner, int balance) {
        System.out.println("\n===============================");
        System.out.println("Current Balance: $" + balance);
        System.out.print("Enter your bet amount: ");
        try {
            int bet = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (bet > balance || bet <= 0) {
                System.out.println("Invalid bet. Try again.");
                return -1;
            }
            return bet;
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
            return -1;
        }
    }

    
    //this deals two cards to both the player and the house 
    private static void dealInitialCards(GameLogic game) {
        game.hit("player");
        game.hit("house");
        game.hit("player");
        game.hit("house");
    }

    
    // this displays intial card for house with the second card for the house remaining hidden
    private static void displayInitialHands(GameLogic game) {
        System.out.println("\n=== Initial Deal ===");
        System.out.println("Your hand: " + game.getPlayerHand() + " (Score: " + game.getPlayerScore() + ")");
        System.out.println("House hand: [" + game.getHouseHand().get(0) + ", ?]");
    }

    
    
    //this manages the players turn allowing the user to hit stand or double
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

    
    //handles double down if the player chooses to double in their play
    private static void handleDoubleDown(Scanner scanner, GameLogic game, Bank playerBank, int bet, boolean hasPlayed) {
        if (hasPlayed) {
            System.out.println("You can only double as your first move.");
            return;
        }

        if (playerBank.getBalance() >= bet) {
            bet *= 2;
            game.hit("player");
            System.out.println("Your hand: " + game.getPlayerHand() + " (Score: " + game.getPlayerScore() + ")");
            if (game.getPlayerScore() > 21) {
                System.out.println("Bust! You lose your bet.");
                playerBank.deductBalance(bet);
            }
        } else {
            System.out.println("Not enough balance to double your bet.");
        }
    }

    
    //uses gamelogic for the house. this manages the houses turn in order to meet standard blackjack rules
    private static void houseTurn(GameLogic game) {
        System.out.println("\n=== House Turn ===");
        System.out.println("House reveals: " + game.getHouseHand() + " (Score: " + game.getHouseScore() + ")");

        
        //house must hit on 16 or less
        while (game.getHouseScore() <= 16 && game.getPlayerScore() != 21) {
            game.hit("house");
            System.out.println("House hits: " + game.getHouseHand().get(game.getHouseHand().size() - 1));
            System.out.println("House hand: " + game.getHouseHand() + " (Score: " + game.getHouseScore() + ")");
        }
    }

    
    //gamelogic to determine if the player or house wins or loses or draws
    private static void handleGameResult(GameLogic game, Bank playerBank, int bet) {
        System.out.println("\n=== Result ===");
        Boolean result = game.bustCheck();
        if (result == null) {
            // Draw - no change in balance
        }
        else if (result)
        {
            playerBank.increaseBalance(bet);
        } 
        else
        {
            playerBank.deductBalance(bet);
        }
    }

    
    //method to ask user if they wish to play again
    private static boolean askToPlayAgain(Scanner scanner) {
        System.out.print("\nPlay again? (y/n): ");
        String again = scanner.nextLine().toLowerCase();
        System.out.println("\n\n=========================================\n");
        return again.equals("y");
    }
}
