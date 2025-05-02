/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.blackjack;

/**
 *
 * @author ay196
 */

public class HousePlayer extends Player
{
    public HousePlayer()
    {
        super("House");
    }
    
    
    @Override
    public boolean shouldHit()
    {
        //standard dealer rules for blackjack
        // Uses getter instead of direct field access
        return getScore() <= 16;
    }
}
