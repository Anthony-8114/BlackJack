/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.blackjack;

/**
 *
 * @author ciara
 */

import java.util.List;


public interface PlayerInterface {
    void addCard(String card, int value);
    int getScore();
    List<String> getHand();
    boolean shouldHit();
    String getName();
}
