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
import java.util.List;
import java.util.Map;

public class GameLogic
{
    private final PlayerInterface player;
    private final PlayerInterface house;
    private final Map<String, Integer> deck;
    
    public GameLogic(Map<String, Integer> shuffledDeck)
    {
        this.deck = shuffledDeck;
        this.player = new HumanPlayer();
        this.house = new HousePlayer();
    }
    
    public void hit(String who)
    {
        String card = drawCard();
        int cardValue = deck.remove(card);
        
        PlayerInterface target = who.equals("player") ? player : house;
        target.addCard(card, calculateCardValue(target.getScore(), cardValue));
    }
    
    
    private int calculateCardValue(int currentScore, int cardValue) {
        // method to handle ace as 11 if it wont bust
        return (cardValue == 1 && currentScore + 11 <= 21) ? 11: cardValue;
    }
    
    public void stand()
    {
        if (house.shouldHit())
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
    
    public List<String> getPlayerHand()
    {
        return player.getHand();
    }
    
    public List<String> getHouseHand()
    {
        return house.getHand();
    }
    
    public String getPlayerName() {
        return player.getName();
    }
    
    public String getHouseName() {
        return house.getName();
    }
}
