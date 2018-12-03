
package clientgui;

import clientgui.classes.ClientData;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
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
    private ObjectProperty<ObservableList<ClientData>> listClient;
    private BooleanProperty showIp;
    
    private Semaphore disconnectSem;
    
    /**
     * Metodo Costruttore
     * @param buffer Input Stream del Server
     * @param s Stringa di output
     * @param clients ObservableList of connected clients
     * @param showIp BooleanProperty showIp
     * @param sem Semaphore 
     */
    public Writer(BufferedReader buffer, StringProperty s, ObjectProperty<ObservableList<ClientData>> clients, BooleanProperty showIp, Semaphore sem){
        this.in = buffer;
        this.outputString = s;
        this.attivo = true;
        this.listClient = clients;
        this.showIp = showIp;
        this.disconnectSem = sem;
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
                final JSONObject clientData;
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
                        Platform.runLater(()->{
                            try{
                                listClient.get().add(new ClientData(clientData.getString("username"), clientData.getString("ip")));
                            }catch(JSONException jsonEx){
                                System.err.println(jsonEx.getMessage());
                            }
                        });
                        scrivi("BENVENUTO "+usernameToDisplay);
                        break;
                    case 2: // logOut
                        clientData = jsonMessage.getJSONObject("userData");
                        usernameToDisplay = showIp.get() ? clientData.getString("username")+"["+clientData.getString("ip")+"]" : clientData.getString("username");
                        
                        ClientData clientToRemove = new ClientData(clientData.getString("username"), clientData.getString("ip"));
                        
                        int index = 0;
                        boolean trovato = false;
                        while(index<listClient.get().size() && !trovato) {
                            if (listClient.get().get(index).equals(clientToRemove)){   
                                //Per rimuovere l'elemento devo fare cosi perchè se no da errore                                 
                                final int indexRemove = index;
                                Platform.runLater(() -> {listClient.get().remove(indexRemove);});
                                trovato = true;
                            }
                            index++;
                        }

                        scrivi("-- "+usernameToDisplay+" HA ABBANDONATO LA CHAT");
                        break;
                    case 3: // listClients                               
                        JSONArray clients = jsonMessage.getJSONArray("users");
                        for (int i = 0; i < clients.length(); i++) { 
                            final JSONObject user = clients.getJSONObject(i);
                            Platform.runLater(()->{
                                try{
                                    listClient.get().add(new ClientData(user.getString("username"), user.getString("ip")));
                                }catch(JSONException jsonEx){
                                    System.err.println(jsonEx.getMessage());
                                }
                            });
                        }
                        break;
                    default: break;
                }                
            } catch (IOException | JSONException ex) {                
                System.err.println(ex.getMessage());
            }
        }
        disconnectSem.release();
    }
}
