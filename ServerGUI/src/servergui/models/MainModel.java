/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servergui.models;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import servergui.classes.Connection;



/**
 *
 * @author gianni.benigni
 */
public class MainModel extends Thread{
    
    private ServerSocket Server;
    private StringProperty messageToSend;
    private StringProperty messages;
    private BufferedReader In;
    private PrintStream Out;
    private ObservableList<Connection> clientsConnected = FXCollections.observableArrayList();
    
    public ObservableList<Connection> OpenConnection; 
   
    public MainModel() throws Exception{
        //OpenConnection = new ArrayList<Connection>();
        OpenConnection = FXCollections.observableArrayList();
        Server = new ServerSocket(4000);
        
        messageToSend = new SimpleStringProperty("");
        messages = new SimpleStringProperty("");
        
        this.start();
        
        
    }
    
    @Override
    public void run(){
        
        while(true){
            
            try {
                System.out.println("*****  In attesa di connessione.\n");
                
                Socket Client = Server.accept();
                
                In = new BufferedReader(new InputStreamReader(Client.getInputStream()));
                Out = new PrintStream(Client.getOutputStream(),true);
                
                System.out.println("*****  Connessione accettata da: " + Client.getInetAddress()+"\n");
                
                OpenConnection.add(new Connection(Client,OpenConnection,messages,In,Out));
                
                
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
     * Getter del texsto da inviare
     * @return String
     */
    public String getMesssageToSend(){
        return this.messageToSend.get();
    }
    
    /**
     * Setter del testo da inviare
     * @param value 
     */
    public void setMessageTosend(String value){
        this.messageToSend.set(value);
    }
    
    /**
     * MessageToSend Property Getter
     * @return StringProperty
     */
    public StringProperty messageToSendProperty(){
        
        return messageToSend;
    }
    
    /**
     * Getter dei client Connessi
     * @return  ObserveList of Connection
     */
    public ObservableList<Connection> getOpenConnection(){
        return OpenConnection;
    }
    
    
    
    //EVENTS ----------------------------------------------------------
    
    /**
     * SEND MESSAGE Handler
     */
    public EventHandler<ActionEvent> sendMessageHandler = e -> {
        
        if(OpenConnection.size() != 0){
            Out.println("<Server> " + getMesssageToSend());
            messages.set(messages.get() + "\n"+ "<Server> " + getMesssageToSend());
            setMessageTosend("");
        }
    };
    
    /**
     * SendMessage Handler Getter
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getSendMessageHandler(){
        return this.sendMessageHandler;
    }
    
    
}
