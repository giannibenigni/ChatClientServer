/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servergui.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gianni.benigni
 */
public class Connection extends Thread{
    
    
    private Socket Client = null;
    private BufferedReader In = null;
    private PrintStream Out = null;
    
    private Semaphore outSem = null;
    
    private String ClientName = "";
    
    private ArrayList<Connection> OpenConnection;

    
    public Connection(Socket client,ArrayList<Connection> array){
        
        OpenConnection = new ArrayList<Connection>();
        
        this.Client = client;
        OpenConnection = array;
        
        try {
            In = new BufferedReader(new InputStreamReader(Client.getInputStream()));
            Out = new PrintStream(Client.getOutputStream(),true);
        } catch (Exception e) {
            
            try {
                Client.close();
            } catch (Exception e1) {
                System.out.println(e.getMessage());
            }   
        }
        
        outSem = new Semaphore(1);
        
        this.start();
    }
    
    
    public void writeMessage(String message) throws InterruptedException{
        
        outSem.acquire();
        Out.print(message);
        outSem.release();
        
    }
    
    
    @Override
    public void run(){
        
        try {
            ClientName = In.readLine();
            
            for(Connection connection : OpenConnection ){
                
                connection.writeMessage("--BENVENUTO/A " + ClientName);
                
            }
            
            System.out.println("--BENVENUTO/A " + ClientName);
            
            while(true){
                String msg = In.readLine();
                
                for(Connection connection: OpenConnection){
                    connection.writeMessage(ClientName + " -> " + msg);
                }
                System.out.println(ClientName + " -> " + msg);
                
            }
            
            
        } catch (Exception ex) {
           System.err.println(ex);
        }
        
    }
    
    
    
}
