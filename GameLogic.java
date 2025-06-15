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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameLogic
{
    private final PlayerInterface player;
    private final PlayerInterface house;
    private final Map<String, Integer> deck; // current deck
    private final Map<String, Integer> allCardValues; // all possible card values
    
    public GameLogic(Map<String, Integer> shuffledDeck) {
        this.deck = new LinkedHashMap<>(shuffledDeck);
        this.allCardValues = new HashMap<>(shuffledDeck);
        this.player = new HumanPlayer();
        this.house = new HousePlayer();
    }
    
    public void hit(String who) {
        String card = drawCard();
        // Remove from playable deck
        int cardValue = deck.remove(card);
        
        PlayerInterface target = who.equals("player") ? player : house;
        target.addCard(card, calculateCardValue(target, card, cardValue));
    }
    
    
    private int calculateCardValue(PlayerInterface target, String newCard, int cardValue) {
        if (cardValue == 1) { // For Ace
            List<String> hand = target.getHand();
            if (!hand.isEmpty()) {
                String previousCard = hand.get(hand.size() - 1);
                int previousValue = allCardValues.get(previousCard); // Use preserved values
                if (previousValue == 10) {
                    return 11;
                }
            }
            return 1;
        }
        return cardValue;
    }
    
    // checks if house should hit, if house value is 16 or under it will hit
    public void stand() {
        while (house.shouldHit()) {
            hit("house");
        }
        bustCheck();
    }
    
    // checks who won
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
    
    // draws card from deck
    private String drawCard()
    {
        return deck.keySet().iterator().next();
    }
    
    
    // getters
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
