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
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.json.*;

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
    
    private ObservableList<Connection> OpenConnection;
    
    private StringProperty outputString;

    
    public Connection(Socket client, ObservableList<Connection> array, StringProperty outputS, BufferedReader in, PrintStream out){
        
        
        this.Client = client;
        this.OpenConnection = array;
        outputString = outputS;
        
        try {
            In = in;
            Out = out;
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
        Out.println(message);
        outSem.release();
        
    }
    
    
    @Override
    public void run(){
        
        try {
            
            //Legge i dati di login
            JSONObject logIn = new JSONObject(In.readLine());
            ClientName = logIn.getJSONObject("newUserData").getString("username");
            logIn.getJSONObject("newUserData").put("ip",Client.getInetAddress().toString());
            
            //ClientName = In.readLine();
            
            outputString.set(outputString.get() + "\n" + "<Server> BENVENUTO/A " + ClientName);
            
            
            for(Connection connection : OpenConnection ){
                
                connection.writeMessage("<Server> BENVENUTO/A " + ClientName);
            }
                        
            
            while(true){
                
                String msg = In.readLine();
                if(msg.equalsIgnoreCase("/exit")){
                    for(Connection connection: OpenConnection){
                        connection.writeMessage("<Server> "  + ClientName + " ha ABBANDONATO la chat");
                    }
                    outputString.set(outputString.get() + "\n" + "<Server> " + ClientName + " ha ABBANDONATO la chat");
                    
                    OpenConnection.remove(this);
                    Out.flush();
                    Out.close();
                    In.close();
                    Client.close();
                    return;
                }else{
                    outputString.set(outputString.get() + "\n<" + ClientName + "> " + msg);

                    for(Connection connection: OpenConnection){
                        connection.writeMessage("<" + ClientName + "> " + msg);
                    }
                }
                
                
                
            }
            
            
        } catch (Exception ex) {
           System.err.println(ex);
        }
        
    }
    
    
    /**
     * Metodo che serve per stampare la lista dei client
     * @return 
     */
    @Override
    public String toString(){
        return "Username: " + ClientName + "\nIP: " + Client.getInetAddress();
    }
    
    
    
}
