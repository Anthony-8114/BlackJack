/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.blackjack;

/**
 *
 * @author ay196
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GameLogic 
{
    private List<String> playerHand;
    private int playerScore;
    
    private List<String> houseHand;
    private int houseScore;
    
    private HashMap<String, Integer> deck;
    
    public GameLogic(HashMap<String, Integer> shuffledDeck)
    {
        this.deck = shuffledDeck;
        this.playerHand = new ArrayList<>();
        this.houseHand = new ArrayList<>();
        
        this.playerScore = 0;
        this.houseScore = 0;
    }
    
    
    public void hit(String playerOrhouse)
    {   
        String card = drawCard();
        
        if(playerOrhouse.equals("player"))
        {
            playerHand.add(card);
            
            // adds the value of the card to player score;
            int cardValue = deck.get(card);
            playerScore += cardValue;
             
        }
        
        else if(playerOrhouse.equals("House"))
        {
            houseHand.add(card);
            
            int cardValue = deck.get(card);
            houseScore += cardValue;
        }
    }
    
    public void stand()
    {
        
        if(houseScore <= 16)
        {
            hit("house");
        }
        
    }
    
    // using Boolean instead of boolean so i can return null incase of a draw.
    public Boolean bustCheck()
    {
        if(playerScore > 21 || playerScore < houseScore)
        {
            System.out.println("You lose");
            return false;
        }
        
        else if(playerScore == 21 || playerScore > houseScore)
        {
            System.out.println("You win");
            return true;
        }
        
        else if(playerScore == houseScore)
        {
            System.out.println("Draw");
            return null;
        }
        
        else
        {
            System.out.println("IDK");
            return null;
        }
        
    }
    

    private String drawCard() {
        return deck.keySet().iterator().next();
    }
}
