
package servergui.classes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.json.*;

/**
 *
 * @author gianni.benigni
 */
public class Connection extends Thread{        
    private Socket Client = null;
    private BufferedReader in = null;
    private PrintStream out = null;
    
    private Semaphore outSem = null;
    private Semaphore listSem = null;
    private Semaphore messagesSem = null;        
    
    private ObservableList<Connection> OpenConnection;    
    private StringProperty outputString;
    
    private ClientData clientData = new ClientData();
    
    public Connection(Socket client, ObservableList<Connection> array, StringProperty outputS, Semaphore listSem, Semaphore msgSem){                
        this.Client = client;
        this.OpenConnection = array;
        outputString = outputS;
        this.listSem = listSem;
        this.messagesSem = msgSem;
        
        try {
            in = new BufferedReader(new InputStreamReader(Client.getInputStream()));
            out = new PrintStream(Client.getOutputStream(), true);
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
            out.println(message);
            outSem.release();
    }    
    
    @Override
    public void run(){        
        try {            
            
//Legge i dati di login
            JSONObject logIn = new JSONObject(in.readLine());
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
            
            messagesSem.acquire();
            
            outputString.set(outputString.get()+"\nBENVENUTO "+clientData.getUsername()+"["+clientData.getIp()+"]");
            
            messagesSem.release();
            
            while(true){
                
                JSONObject json = new JSONObject(in.readLine());

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

                messagesSem.acquire();
                outputString.set(outputString.get()+"\n<"+clientData.getUsername()+"["+clientData.getIp()+"]> "+json.getString("messageText")); 
                messagesSem.release();
                
            }                        
        } catch (Exception ex) {
           System.err.println(ex);
        }        
    }
    
    /**
     * Metodo per la disconnessione del client
     * @param json JSONObject che contiene le informazioni del logout
     */
    public void disconnect(JSONObject json){                
        try {
            json.getJSONObject("userData").put("ip",clientData.getIp());
            listSem.acquire();
            for(Connection connect: OpenConnection){
                connect.writeMessage(json.toString());
            }
                
            Platform.runLater(() -> { 
                OpenConnection.remove(this); 
            });
            listSem.release();
            
            messagesSem.acquire();
            outputString.set(outputString.get()+"\n<"+clientData.getUsername()+"["+clientData.getIp()+"]> HA ABBANDONATO LA CHAT!"); 
            messagesSem.release();

            out.flush();
            out.close();
            in.close();
            Client.close();  
        } catch (Exception ex) {
            System.err.println(ex);
        }               
    }
        
    /**
     * Metodo ToString 
     * @return String
     */
    @Override
    public String toString(){
        return "Username: " + clientData.getUsername() + "\nIP: " + clientData.getIp();
    }    
}
