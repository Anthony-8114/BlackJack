/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.blackjack;

import java.util.HashMap;


/**
 *
 * @author ay196
 */


public class BlackJack
{

    public static void main(String[] args) 
    {   
        Bank player = new Bank();
        int bal = player.getBalance();
        
        // creates the deck
        HashMap<String, Integer> deck = SetDeck.createDeck();
        
        
        for (String card : deck.keySet()) {
            System.out.println(deck.get(card) + " -> " + card);
        }
        
        
        System.out.println("You balance:");
        System.out.println(bal);
        
        player.deductBalance(100);
        bal = player.getBalance();
        System.out.println(bal);
    }
}
