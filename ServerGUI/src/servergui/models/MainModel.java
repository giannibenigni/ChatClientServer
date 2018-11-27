/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servergui.models;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import servergui.classes.Connection;



/**
 *
 * @author gianni.benigni
 */
public class MainModel extends Thread{
    
    private ServerSocket Server;
    private ArrayList<Connection> OpenConnection;
    
    
   
    public MainModel() throws Exception{
        OpenConnection = new ArrayList<Connection>();
        Server = new ServerSocket(4000);
        this.start();
        
    }
    
    @Override
    public void run(){
        
        while(true){
            
            try {
                
                Socket client = Server.accept();
                OpenConnection.add(new Connection(client,OpenConnection));
                
            } catch (Exception e) {
            }
            
        }
        
    }
    
    
}
