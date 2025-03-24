/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author ay196
 */
public class SetDeck 
{
    
    public static HashMap<String, Integer> createDeck() {
        HashMap<String, Integer> deck = new HashMap<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                int cardValue;

                if (rank.matches("[2-9]"))
                {
                    cardValue = Integer.parseInt(rank);
                } 
                else if (rank.equals("10") || rank.equals("J") || rank.equals("Q") || rank.equals("K")) 
                {
                    cardValue = 10;
                }
                
                // Ace is not always 1 it can be 11 if matched with a face card before hand
                // a check is needed when drawing cards.
                else if(rank.equals("A"))
                {
                    cardValue = 1;
                }
                else 
                {
                    cardValue = 11;
                }

                String cardName = rank + " of " + suit;
                deck.put(cardName, cardValue);
            }
        }
        
        
        return suffleDeck(deck);
    }
    
    
    public static HashMap<String, Integer> suffleDeck(HashMap<String, Integer> deck)
    {
        
        // entry is the vaule example a face cards "entry" is 10
        // converts it to list so it can be suffled with collections
        List<Map.Entry<String, Integer>> deckList = new ArrayList<>(deck.entrySet());
        Collections.shuffle(deckList);
        
        // linked hashmap because it keeps the order
        HashMap<String, Integer> shuffledDeck = new LinkedHashMap<>();
        
        for(Map.Entry<String, Integer> entry : deckList)
        {
            shuffledDeck.put(entry.getKey(), entry.getValue());
        }
        
        return shuffledDeck;
       
        
    }
    
}
