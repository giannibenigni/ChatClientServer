
package clientgui;

import clientgui.classes.ClientData;
import clientgui.classes.PrivateChat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.json.*;

/**
 * 
 * @author Eugenio
 */
public class Writer extends Thread{
    private boolean attivo;
    private final BufferedReader in;    
    private final StringProperty globalChat;
    private final ObjectProperty<ObservableList<ClientData>> listClient;
    private final ObjectProperty<ObservableList<PrivateChat>> chats;
    private final BooleanProperty showIp;
    private final String myUsername;    
    private final BooleanProperty newPrivMessage;    
    private BooleanProperty bannato;

    private PrintStream out = null;
    private Socket socket = null;
    
    private final Semaphore disconnectSem;
    
    /**
     * Metodo Costruttore
     * @param buffer Input Stream del Server
     * @param globalMessages Stringa di output chat globale     
     * @param clients ObservableList of connected clients
     * @param showIp BooleanProperty showIp
     * @param sem Semaphore 
     * @param chats Observable List of PrivateChat
     * @param myUser String myUsername
     * @param newPrivMes     
     */
    public Writer(BufferedReader buffer, StringProperty globalMessages, ObjectProperty<ObservableList<ClientData>> clients, BooleanProperty showIp, Semaphore sem, ObjectProperty<ObservableList<PrivateChat>> chats, String myUser, BooleanProperty newPrivMes, Socket s, PrintStream p){
        this.in = buffer;
        this.globalChat = globalMessages;
        this.attivo = true;
        this.listClient = clients;
        this.showIp = showIp;
        this.disconnectSem = sem;
        this.chats = chats;
        this.myUsername = myUser;
        this.newPrivMessage = newPrivMes;        
        this.socket = s;
        this.out = p;
    }
    
    /**
     * Metodo per fermare il thread
     */
    public void ferma(){
        this.attivo = false;
    }
    
    /**
     * Metodo che aggiunge del testo alla stringa della chat globale
     * @param str String 
     */
    public void writeGlobal(String str){
        globalChat.set(globalChat.get()+"\n"+str);
    }
    
    /**
     * Metodo che aggiunge un messaggio ad una chat privata
     * @param message String messaggio da aggiungere
     * @param from PrivateChat dove aggiungere il messaggio
     */
    public void writePrivate(String message, PrivateChat from){
        boolean trovato = false;
        int i = 0; 
        while(!trovato && i<chats.get().size()){
            if(chats.get().get(i).getContact().equals(from.getContact())){
                trovato = true;
                chats.get().get(i).addMessages(message);
            }
            i++;
        }
        if(!trovato){
            addChat(from);
            chats.get().get(i).addMessages(message); 
        }
    }
    
    /**
     * Metodo che aggiunge una chat alla lista delle chatprivate attive
     * @param chat Private chat chat da aggiungere
     */
    public void addChat(PrivateChat chat){
        if(chats.get().stream().noneMatch((elem) -> elem.getContact().equals(chat.getContact()))) 
            this.chats.get().add(chat);
    }
    
    /**
     * Metodo run del Thread
     */
    @Override
    public void run() {        
        while(attivo){
            try { 
                JSONObject jsonMessage = new JSONObject(in.readLine());
                final JSONObject clientData;
                String usernameToDisplay;
                
                switch (jsonMessage.getInt("messageType")) {
                    case 0: // normalMessage      
                        clientData = jsonMessage.getJSONObject("from");                               
                        usernameToDisplay = showIp.get() ? clientData.getString("username")+"["+clientData.getString("ip")+"]" : clientData.getString("username");                        
                        writeGlobal(usernameToDisplay+" -> "+jsonMessage.getString("messageText"));
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
                        writeGlobal("BENVENUTO "+usernameToDisplay);
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

                        writeGlobal("-- "+usernameToDisplay+" HA ABBANDONATO LA CHAT");
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
                    case 5: // private message                         
                        final JSONObject whoWrite;
                        if(jsonMessage.getJSONObject("from").getString("username").equals(myUsername)){
                            whoWrite = jsonMessage.getJSONObject("to");                            
                        }else{
                            whoWrite = jsonMessage.getJSONObject("from");
                            Platform.runLater(()->this.newPrivMessage.set(true)); 
                        }
                        usernameToDisplay = showIp.get() ? jsonMessage.getJSONObject("from").getString("username")+"["+jsonMessage.getJSONObject("from").getString("ip")+"]" : jsonMessage.getJSONObject("from").getString("username");
                        Platform.runLater(()-> {
                            try {
                                writePrivate(usernameToDisplay+" -> "+jsonMessage.getString("messageText"), new PrivateChat(new ClientData(whoWrite.getString("username"), whoWrite.getString("ip"))));
                            } catch (JSONException ex) {
                                System.err.println(ex.getMessage());
                            }
                        });
                        break;
                    case 6:
                        Platform.runLater(()->{
                            Alert a = new Alert(Alert.AlertType.ERROR, "HAI GIA KIKKATO QUESTO UTENTE", ButtonType.OK);
                            a.show();
                        });
                        break;
                    case 9:                                 
                        clientData = jsonMessage.getJSONObject("clientToBan");
                        ClientData clToBan = new ClientData(clientData.getString("username"), clientData.getString("ip"));
                        
                        if(clToBan.getUsername().equals(myUsername)){
                            out.flush();
                            out.close();
                            in.close();
                            socket.close();
                            Platform.exit();
                            return;
                        }
                        
                        int ind = 0;
                        boolean tro = false;
                        while(ind<listClient.get().size() && !tro) {
                            if (listClient.get().get(ind).equals(clToBan)){   
                                //Per rimuovere l'elemento devo fare cosi perchè se no da errore                                 
                                final int indexRemove = ind;
                                Platform.runLater(() -> {listClient.get().remove(indexRemove);});
                                tro = true;
                            }
                            ind++;
                        }
                        writeGlobal("-- "+clToBan.getUsername()+" E' STATO BANNATO");
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
