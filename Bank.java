/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.blackjack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 *
 * @author ay196
 */
public class Bank 
{
    private int balance;
    
    public Bank() 
    {
        this.balance = readBalance();
    }
    
    private int readBalance()
    {
        try
        {   
            // Make this so it does not use the absolute file as that changes with different useres.
            File balanceFile = new File("C:\\Users\\ay196\\Documents\\NetBeansProjects\\BlackJack\\src\\main\\java\\com\\mycompany\\blackjack\\files\\balance.txt");
            Scanner fileReader = new Scanner(balanceFile);
            
            // Checks that the file has a something then reads it.
            while(fileReader.hasNextInt())
            {   
                // returns whatever number is in the fie "balance.txt"
                return fileReader.nextInt();
            }
        } 
        
        // Gives user a message that file can not be found if the file could not be found.
        catch (FileNotFoundException ex) 
        {
            System.out.println("File could not be found");
            
            // Maybe something here to create the file if its not found
        }
        
        return 0;
    }
        
    
    // reads the balance from the constructor
    public int getBalance()
    {
        return balance;
    }
    
    // deducts the amount bet if player loses
    public void deductBalance(int betSize)
    {   
        // ensures that player's balance cant go under 0
        // there is also another check in the main file that players cant bet if balance is 0
        if(getBalance() > 0)
        {
            this.balance -= betSize;
        }
        
    }
    
    // increase the balance if player wins
    public void increaseBalance(int betSize)
    {   
        
        this.balance += betSize;
    }
    
    
    
}
