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

public class GameLogic
{
    private HumanPlayer player;
    private HousePlayer house;
    
    private HashMap<String, Integer> deck;
    
    public GameLogic(HashMap<String, Integer> shuffledDeck)
    {
        this.deck = shuffledDeck;
        this.player = new HumanPlayer();
        this.house = new HousePlayer();
    }
    
    public void hit(String who)
    {
        String card = drawCard();
        int cardValue = deck.remove(card);
        
        if (who.equals("player"))
        {
            player.addCard(card, Ace(player.getScore(), cardValue));
        }
        else if (who.equals("house"))
        {
            house.addCard(card, Ace(house.getScore(), cardValue));
        }
    }
    
    public void stand()
    {
        if (house.getScore() <= 16)
        {
            hit("house");
        }
    }
    
    public Boolean bustCheck()
    {
        if (house.getScore() > 21)
        {
            System.out.println("House busts! You win.");
            return true;
        }
        else if (player.getScore() > 21)
        {
            System.out.println("You bust! House wins.");
            return false;
        }
        else if (player.getScore() > house.getScore())
        {
            System.out.println("You win!");
            return true;
        }
        else if (player.getScore() < house.getScore())
        {
            System.out.println("You lose.");
            return false;
        }
        else
        {
            System.out.println("It's a draw.");
            return null;
        }
    }
    
    private int Ace(int currentScore, int cardValue)
    {
        if (cardValue == 1 && currentScore + 11 <= 21)
        {
            return 11;
        }
        return cardValue;
    }
    
    private String drawCard()
    {
        return deck.keySet().iterator().next();
    }
    
    public int getPlayerScore()
    {
        return player.getScore();
    }
    
    public int getHouseScore()
    {
        return house.getScore();
    }
    
    public java.util.List<String> getPlayerHand()
    {
        return player.getHand();
    }
    
    public java.util.List<String> getHouseHand()
    {
        return house.getHand();
    }
}
