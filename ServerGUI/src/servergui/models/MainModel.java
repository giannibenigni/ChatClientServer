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
    private StringProperty messages;
    
    public ArrayList<Connection> OpenConnection; 
   
    public MainModel() throws Exception{
        OpenConnection = new ArrayList<Connection>();
        Server = new ServerSocket(4000);
        
        messageToSend = new SimpleStringProperty("");
        messages = new SimpleStringProperty("") {};
        
        this.start();
        
    }
    
    @Override
    public void run(){
        
        while(true){
            
            try {
                System.out.println("*****  In attesa di connessione.\n");
                Socket client = Server.accept();
                System.out.println("*****  Connessione accettata da: " + client.getInetAddress()+"\n");
                OpenConnection.add(new Connection(client,OpenConnection));
                
                
            } catch (Exception ex) {
                System.out.println(ex);
            }
            
        }
        
    }
    
    
    /***
     * Getter messages per textArea
     * @return String
     */
    public String getMessages(){
        return this.messages.get();
    }
    
    /***
     * Setter del testo per textArea
     * @param value String
     */
    public void setMessages(String value){
        messages.set(value);
    }
    
    /**
     * Messages Property Getter
     * @return StringProperty
     */
    public StringProperty messagesProperty(){
        return messages;
    }
    
    /**
     * MessageToSend Property Getter
     * @return StringProperty
     */
    public StringProperty messageToSendProperty(){
        
        return messageToSend;
    }
    
    
}
