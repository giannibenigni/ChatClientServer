/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jclient;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author Eugenio
 */
public class Writer extends Thread{

    private BufferedReader in = null; 
    private boolean fine = false;
    
    public Writer(BufferedReader i){
        this.in = i;
    }
    
    public void fine(){
        fine = true;
    }
    
    @Override
    public void run() {
        while(!fine){                            
            try {
                System.out.println(in.readLine());
            } catch (IOException ex) {
                return;
            }
        }
    }
    
}
