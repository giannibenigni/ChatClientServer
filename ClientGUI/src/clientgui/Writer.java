
package clientgui;

import clientgui.classes.ClientData;
import java.io.BufferedReader;
import java.io.IOException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.json.*;

/**
 * 
 * @author Eugenio
 */
public class Writer extends Thread{
    private boolean attivo;
    private BufferedReader in;    
    private StringProperty outputString;
    private ObservableList<ClientData> listClient;
    private BooleanProperty showIp;
    
    /**
     * Metodo Costruttore
     * @param buffer Input Stream del Server
     * @param s Stringa di output
     * @param clients ObservableList of connected clients
     * @param showIp BooleanProperty showIp
     */
    public Writer(BufferedReader buffer, StringProperty s, ObservableList<ClientData> clients, BooleanProperty showIp){
        this.in = buffer;
        this.outputString = s;
        this.attivo = true;
        this.listClient = clients;
        this.showIp = showIp;
    }
    
    /**
     * Metodo per fermare il thread
     */
    public void ferma(){
        this.attivo = false;
    }
    
    /**
     * Metodo che aggiunge del testo all'outputString
     * @param str String 
     */
    public void scrivi(String str){
        outputString.set(outputString.get()+"\n"+str);
    }
    
    /**
     * Metodo run del Thread
     */
    @Override
    public void run() {
        while(attivo){  //TODO ottimizzare 
            try { 
                JSONObject jsonMessage = new JSONObject(in.readLine());
                JSONObject clientData;
                String usernameToDisplay;
                
                switch (jsonMessage.getInt("messageType")) {
                    case 0: // normalMessage      
                        clientData = jsonMessage.getJSONObject("from");                               
                        usernameToDisplay = showIp.get() ? clientData.getString("username")+"["+clientData.getString("ip")+"]" : clientData.getString("username");                        
                        scrivi(usernameToDisplay+" -> "+jsonMessage.getString("messageText"));
                        break;
                    case 1: // logIn
                        clientData = jsonMessage.getJSONObject("newUserData"); 
                        usernameToDisplay = showIp.get() ? clientData.getString("username")+"["+clientData.getString("ip")+"]" : clientData.getString("username");
                        listClient.add(new ClientData(clientData.getString("username"), clientData.getString("ip")));
                        scrivi("BENVENUTO "+usernameToDisplay);
                        break;
                    case 2: // logOut
                        clientData = jsonMessage.getJSONObject("userData");
                        usernameToDisplay = showIp.get() ? clientData.getString("username")+"["+clientData.getString("ip")+"]" : clientData.getString("username");
                        listClient.remove(new ClientData(clientData.getString("username"), clientData.getString("ip")));
                        scrivi("-- "+usernameToDisplay+" HA ABBANDONATO LA CHAT");
                        break;
                    case 3: // listClients       
                        listClient.clear();
                        JSONArray clients = jsonMessage.getJSONArray("users");
                        for (int i = 0; i < clients.length(); i++) { 
                            JSONObject user = clients.getJSONObject(i);
                            listClient.add(new ClientData(user.getString("username"), user.getString("username")));
                        }
                        break;
                    default: break;
                }
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }
}
