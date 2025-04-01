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
        int cardValue = deck.remove(card); //this will remove card from the deck
        
        if(playerOrhouse.equals("player"))
        {
              // adds the value of the card to player score;
            playerHand.add(card);
            playerScore += Ace(playerScore, cardValue);
             
        }
        
        else if(playerOrhouse.equals("House"))
        {
            houseHand.add(card);
            houseScore += Ace(houseScore, cardValue);
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
    
    private int Ace(int currentScore, int cardValue) {
        if (cardValue == 1 && currentScore + 11 <= 21) {
            return 11; // this would count the ace as 11 if it doesnt cause a bust
        }
        return cardValue;
    }
    
    

    private String drawCard() {
        return deck.keySet().iterator().next();
    }
    
    public int getPlayerScore() {
        return playerScore;
    }
    
    public int getHouseScore() {
        return houseScore;
    }
    
    public List<String> getPlayerHand() {
        return playerHand;
    }
    
    public List<String> getHouseHand() {
        return houseHand;
    }
}
