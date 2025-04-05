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


public class BlackJack
{

    public static void main(String[] args) 
    {   
        Bank player = new Bank();
        Scanner scanner = new Scanner(System.in);
        
        // creates the deck
        HashMap<String, Integer> deck = SetDeck.createDeck();
        
        
        //for (String card : deck.keySet()) {
        //    System.out.println(deck.get(card) + " -> " + card);
        //}
        
        GameLogic game = new GameLogic(deck);

        System.out.println("Welcome to Blackjack!");
        System.out.println("Current balance: $" + player.getBalance());
        
        while(true)
        {
            if (player.getBalance() <= 0) 
            {
                System.out.println("You're out of money. Game over!");
                break;
            }
            
            System.out.print("Enter your bet amount: ");
            int bet = scanner.nextInt();
            scanner.nextLine();
            
            if (bet > player.getBalance() || bet <= 0) 
            {
                System.out.println("Invalid bet amount. Try again.");
            }
            
            else
            {
                game.hit("player");
                game.hit("house");
                game.hit("player");
                game.hit("house");
            }
            
            
            System.out.println("\nYour hand: " + game.getPlayerHand() + " | Score: " + game.getPlayerScore()); 
            System.out.println("\nHouse hand: " + game.getHouseHand() + " | Score: " + game.getHouseScore()); 
            
            
        }
    }
}
