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
    private Semaphore listSem = null;
    
    private String ClientName = "";
    
    private ObservableList<Connection> OpenConnection;
    
    private StringProperty outputString;
    
    private ClientData clientData = new ClientData();
    private ClientData clientsData = new ClientData();

    
    public Connection(Socket client, ObservableList<Connection> array, StringProperty outputS, BufferedReader in, PrintStream out, Semaphore list){
        
        
        this.Client = client;
        this.OpenConnection = array;
        outputString = outputS;
        listSem = list;
        
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
            clientData.setUsername(logIn.getJSONObject("newUserData").getString("username"));
            clientData.setIp(Client.getInetAddress().toString());
            logIn.getJSONObject("newUserData").put("ip",Client.getInetAddress().toString());            
                     
            // mi creo un array con i dati di tutti i client connessi eccetto me stesso
            ArrayList<ClientData> clientsData = new ArrayList<>();
            listSem.acquire();                
            for (Connection connection: OpenConnection) {
                if(connection != this)
                    clientsData.add(connection.clientData);
            }
            listSem.release();
            
            //Invio ai client la lista dei client connessi
            writeMessage(JSONParser.getClientListJSON(clientsData).toString());
            
            //Trasmetto il login a tutti i client eccetto questo
            listSem.acquire();
            for(Connection connect : OpenConnection){
                if(connect != this){
                    connect.writeMessage(logIn.toString());
                }
            }
            listSem.release();
            
            while(true){
                
                JSONObject json = new JSONObject(In.readLine());
                
                if(json.getInt("messageType") == 2){
                    disconnect(json);
                    return;
                }
                
                json.getJSONObject("from").put("ip", clientData.getIp());
                
                listSem.acquire();
                
                for(Connection connect: OpenConnection){
                    connect.writeMessage(json.toString());
                }
                listSem.release();
                                        
            }
            
            
        } catch (Exception ex) {
           System.err.println(ex);
        }
        
    }
    
    public void disconnect(JSONObject json){
        
        
        try {
            json.getJSONObject("userData").put("ip",clientData.getIp());
            listSem.acquire();
            for(Connection connect: OpenConnection){
                    connect.writeMessage(json.toString());
                }
                OpenConnection.remove(this);
                listSem.release();
                System.out.println("-- "+clientData.getUsername() + " ha ABBANDONATO la chat.");

                Out.flush();
                Out.close();
                In.close();
                Client.close();  
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
