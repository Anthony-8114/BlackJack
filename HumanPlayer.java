/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.mycompany.blackjack;

public class HumanPlayer extends Player {
    public HumanPlayer()
    {
        super("Player");
    }
    
    @Override
    public boolean shouldHit() {
        return false;
    }
    
}
