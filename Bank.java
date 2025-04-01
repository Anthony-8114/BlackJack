/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.blackjack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 *
 * @author ay196
 */
public class Bank 
{
    private int balance;
    
    private static final String FILE_PATH = "src\\main\\java\\com\\mycompany\\blackjack\\resources/balance.txt";
    
    public Bank() 
    {
        this.balance = readBalance();
    }
    
    private int readBalance()
    {   
        File file = new File(FILE_PATH);
        
        if (!file.exists()) 
        {
            try 
            {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                
                file.createNewFile();
                
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) 
                {
                    writer.write("100");
                }
                
            } 
            catch (IOException e) 
            {
                System.out.println("Error creating file: " + e.getMessage());
            }
            
        }

        // Read from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
        {
            String line = reader.readLine();

            System.out.println("Read from file: " + line);

            if (line != null && !line.trim().isEmpty()) 
            {   
                
                balance = Integer.parseInt(line.trim());
                return Integer.parseInt(line.trim());
            }
        } 
        catch (IOException | NumberFormatException e) 
        {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return 0;
    }
        
    
    public int getBalance()
    {
        File file = new File(FILE_PATH);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
        {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) 
            {
                return Integer.parseInt(line.trim());
            }
        }
        
        catch (IOException | NumberFormatException e) 
        {
            System.out.println("Error reading balance: " + e.getMessage());
        }
        
        return 0;
        
    }
    
    // deducts the amount bet if player loses
    public void deductBalance(int betSize)
    {   
        // ensures that player's balance cant go under 0
        // there is also another check in the main file that players cant bet if balance is 0
        if(getBalance() > 0)
        {
            updateBalance(this.balance -= betSize);
        }
        
    }
    
    // increase the balance if player wins
    public void increaseBalance(int betSize)
    {   
        updateBalance(this.balance += betSize);
        
    }
    
    private void updateBalance(int newBalance) 
    {
        File file = new File(FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false)))
        {
            writer.write(String.valueOf(newBalance));
        } 
        catch (IOException e) 
        {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    
    
}
