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

public abstract class Player implements PlayerInterface
{
    protected List<String> hand;
    protected int score;
    protected String name;
    
    public Player(String name)
    {
        this.hand = new ArrayList<>();
        this.score = 0;
        this.name = name;
    }
    
    @Override
    public void addCard(String card, int value) {
        hand.add(card);
        score += value;
    }
    
    @Override
    public int getScore() {
        return score;
    }
    
    @Override
    public List<String> getHand() {
        return hand;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    
    @Override
    public abstract boolean shouldHit();
}
