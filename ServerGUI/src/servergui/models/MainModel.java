/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servergui.models;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import servergui.classes.Connection;



/**
 *
 * @author gianni.benigni
 */
public class MainModel extends Thread{
    
    private ServerSocket Server;
    private StringProperty messageToSend;
    
    public ArrayList<Connection> OpenConnection; 
   
    public MainModel() throws Exception{
        OpenConnection = new ArrayList<Connection>();
        Server = new ServerSocket(4000);
        
        messageToSend = new SimpleStringProperty("");
        
        this.start();
        
    }
    
    @Override
    public void run(){
        
        while(true){
            
            try {
                System.out.println("*****  In attesa di connessione.\n");
                Socket client = Server.accept();
                System.out.println("*****  Connessione accettata da: "+client.getInetAddress()+"\n");
                OpenConnection.add(new Connection(client,OpenConnection));
                
                OpenConnection.toString();
                
            } catch (Exception ex) {
                System.out.println(ex);
            }
            
        }
        
    }
    
    
    /**
     * MessageToSend Property Getter
     * @return StringProperty
     */
    public StringProperty messageToSendProperty(){
        
        return messageToSend;
    }
    
    
}
