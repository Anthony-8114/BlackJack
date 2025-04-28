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
import java.util.List;

public class Player
{
    protected List<String> hand;
    protected int score;
    
    public Player()
    {
        hand = new ArrayList<>();
        score = 0;
    }
    
    public void addCard(String card, int value)
    {
        hand.add(card);
        score += value;
    }
    
    public int getScore()
    {
        return score;
    }
    
    public List<String> getHand()
    {
        return hand;
    }
}
