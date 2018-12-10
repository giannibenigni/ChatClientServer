
package clientgui.models;

import java.io.*;
import java.net.*;
import clientgui.Writer;
import clientgui.classes.ClientData;
import clientgui.classes.PrivateChat;
import clientgui.classes.ServerData;
import clientgui.parser.JSONParser;
import java.util.concurrent.Semaphore;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Eugenio
 */
public class MainModel {
    private Writer writerThread;
    
    private final BooleanProperty userLogged;
    private final StringProperty messages;
    private final StringProperty messageToSend;
    private final StringProperty privateMessageToSend;
    private final BooleanProperty showIp;    
    private final StringProperty username;
    private final BooleanProperty newPrivMessages;
    
    private final ObjectProperty<ObservableList<ClientData>> clientsConnected = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<ObservableList<PrivateChat>> chats = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    
    private BufferedReader in = null;
    private PrintStream out = null;
    
    private Socket socket = null;
    
    private Semaphore disconnectSem;
    
    /**
     * Metodo Costruttore
     */
    public MainModel(){
        disconnectSem = new Semaphore(0);
        userLogged = new SimpleBooleanProperty(false);
        messages = new SimpleStringProperty("");     
        messageToSend = new SimpleStringProperty("");
        showIp = new SimpleBooleanProperty(true); 
        privateMessageToSend = new SimpleStringProperty(""); 
        username = new SimpleStringProperty("");
        newPrivMessages = new SimpleBooleanProperty(false);
    }
    
    /**
     * Username Getter
     * @return String username
     */
    public String getUsername(){
        return this.username.get();
    }
   
    /**
     * Username Setter
     * @param value String value
     */
    public void setUsername(String value){
        this.username.set(value);
    }
    
    /**
     * UserNameProperty Getter
     * @return StringProperty
     */
    public StringProperty usernameProperty(){
        return this.username;
    }
    
    /**
     * NewPrivMessages Getter
     * @return Boolean
     */
    public boolean getNewPrivMessages(){
        return this.newPrivMessages.get();
    }
    
    /**
     * NewPrivMessages Setter
     * @param value Boolean value
     */
    public void setNewPrivMessages(boolean value){
        this.newPrivMessages.set(value);
    }
    
    /**
     * NewPrivMessages Property Getter
     * @return Boolean Property
     */
    public BooleanProperty newPrivMessagesProperty(){
        return this.newPrivMessages;
    }
    
    /**
     * Messages Getter
     * @return String
     */
    public String getMessages(){
        return this.messages.get();
    }
    
    /**
     * Messages Setter
     * @param value String
     */
    public void setMessages(String value){
        messages.set(value);
    }
    
    /**
     * UserLogged Getter
     * @return Boolean
     */
    public boolean getUserLogged(){
        return this.userLogged.get();
    }
    
    /**
     * UserLogged Setter
     * @param value Boolean 
     */
    public void setUserLogged(boolean value){
        this.userLogged.set(value);
    }
    
    /**
     * MessageToSend Getter
     * @return String 
     */
    public String getMessageToSend(){
        return this.messageToSend.get();
    }
    
    /**
     * MessageToSend Setter
     * @param value String 
     */
    public void setMessageToSend(String value){
        this.messageToSend.set(value);
    }
    
    /**
     * PrivateMessageToSend Getter
     * @return String 
     */
    public String getPrivateMessageToSend(){
        return this.privateMessageToSend.get();
    }
    
    /**
     * PrivateMessageToSend Setter
     * @param value String 
     */
    public void setPrivateMessageToSend(String value){
        this.privateMessageToSend.set(value);
    }
    
    /**
     * ShowIp Getter
     * @return Boolean
     */
    public boolean getShowIp(){
        return showIp.get();
    }
    
    /**
     * ShowIp Setter
     * @param value Boolean 
     */
    public void setShowIp(boolean value){
        this.showIp.set(value);
    }
    
    /**
     * UserLogged Propety Getter
     * @return BooleanProperty
     */
    public BooleanProperty userLoggedProperty(){
        return userLogged;
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
    
    /**
     * PrivateMessageToSend Property Getter
     * @return StringProperty
     */
    public StringProperty privateMessageToSendProperty(){
        return privateMessageToSend;
    }
    
    /**
     * ShowIp Property Getter
     * @return BooleanProperty
     */
    public BooleanProperty showIpProperty(){
        return this.showIp;
    }
    
    /**
     * ClientsConnected Getter
     * @return ObservableList of ClientData
     */
    public ObservableList<ClientData> getClientsConnected(){
        return clientsConnected.get();
    }
    
    /**
     * ClientsConnected Setter
     * @param value ObservableList of ClientData
     */
    public void setClientsConnected(ObservableList<ClientData> value){
        this.clientsConnected.set(value);
    }
    
    /**
     * ClientsConnected Property Getter
     * @return ObjectProperty
     */
    public ObjectProperty<ObservableList<ClientData>> clientsConnectedProperty(){
        return clientsConnected;
    }
    
    /**
     * Chats Getter
     * @return ObservableList of PrivateChat
     */
    public ObservableList<PrivateChat> getChats(){
        return this.chats.get();
    }
    
    /**
     * Chats Setter
     * @param value ObservableList of PrivateChat
     */
    public void setChats(ObservableList<PrivateChat> value){
        this.chats.set(value);
    }
    
    /**
     * Chats Property Getter
     * @return ObjectProperty
     */
    public ObjectProperty<ObservableList<PrivateChat>> chatsProperty(){
        return chats;
    }

    /**
     * Metodo che effettua la connessione al socket, setta lo username del client
     * e fa partire il thread per la scrittura dei messaggi del server
     * @param serverData ServerData serverData (ip, port)
     * @param username String username
     * @param password String password
     * @return true se si è riuscito a connettere. altrimenti false
     */
    public boolean connectToSocket(ServerData serverData, String username, String password){
        try{        
            socket = new Socket(InetAddress.getByName(serverData.getIp()),  serverData.getPort()); 
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(), true);
        }catch(IOException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);            
            alert.setHeaderText("ERRORE CONNESSIONE AL SERVER");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return false;
        }
        
        try {
            out.println(JSONParser.getLogInJSON(username, password).toString());
                        
            boolean result = new JSONObject(in.readLine()).getBoolean("result");
            
            if(!result){
                Alert alert = new Alert(Alert.AlertType.ERROR, "USERNAME / PASSWORD ERRATI O UTENTE GIA' LOGGATO", ButtonType.OK);            
                alert.setHeaderText("ERRORE CONNESSIONE AL SERVER");                
                alert.showAndWait();
                return false;
            }
        } catch (JSONException | IOException ex) {
            System.err.println(ex);
        }
        
        setUsername(username);
        
        this.writerThread = new Writer(in, messages, clientsConnected, showIp, disconnectSem, chats, username, newPrivMessages);         
        writerThread.start();
        setUserLogged(true);
        
        return true;
    }
    
    /**
     * Metodo per mandare una richiesta di registrazione al server
     * @param serverData ServerData dati del server
     * @param username String username
     * @param password String password
     * @return Boolean true se la registrazione è andata a buon fine
     */
    public boolean singUp(ServerData serverData, String username, String password){
        try{        
            socket = new Socket(InetAddress.getByName(serverData.getIp()),  serverData.getPort()); 
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(), true);
        }catch(IOException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);            
            alert.setHeaderText("ERRORE CONNESSIONE AL SERVER");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return false;
        }
        
        try{
            out.println(JSONParser.getSingUpJSON(username, password).toString());
            boolean result = new JSONObject(in.readLine()).getBoolean("result");
            
            if(!result){
                Alert alert = new Alert(Alert.AlertType.ERROR, "UTENTE GIA' REGISTRATO CON QUESTO USERNAME", ButtonType.OK);            
                alert.setHeaderText("ERRORE REGISTRAZIONE");                
                alert.showAndWait();
                return false;
            }
        }catch(JSONException | IOException ex){
            System.err.println(ex.getMessage());
        }
        
        setUsername(username);
        
        this.writerThread = new Writer(in, messages, clientsConnected, showIp, disconnectSem, chats, username, newPrivMessages);         
        writerThread.start();
        setUserLogged(true);
        
        return true;
    }
    
    // EVENTS -------------------------------------------------    
    
    /**
     * LOG OUT Handler
     * Chiudo la connessione con il socket e stoppo il thread che scrive i messaggi
     */
    public EventHandler<MouseEvent> logOutHandler = e -> {
        if(!getUserLogged()) return;
                
        try{
            out.println(JSONParser.getLogOutJSON(getUsername()));
        }catch(JSONException jsonEx){
            System.err.println(jsonEx);
        }
        
        writerThread.ferma();
        
        try{
            disconnectSem.acquire();                       
            out.flush();
            out.close();
            in.close();
            socket.close();            
        }catch(IOException | InterruptedException ex){            
            System.err.println(ex);
        }
        
        setUsername(""); 
        setMessages("");
        setUserLogged(false);
        setNewPrivMessages(false);
        clientsConnected.get().clear();
        chats.get().clear();
    };  
    
    /**
     * LogOut Handler Getter
     * @return EventHandler
     */
    public EventHandler<MouseEvent> getLogOutHandler(){
        return this.logOutHandler;
    }

    /**
     * Manda un messaggio al server
     */
    public void sendMessage(){
        if(!getUserLogged()) return;
        
        try{
            out.println(JSONParser.getNormalMessageJSON(getMessageToSend(), getUsername()));
        }catch(JSONException ex){
            System.err.println(ex);
        }
        
        setMessageToSend("");
    }
    
    /**
     * Manda un messaggio privato ad un client
     * @param destinationClient ClientData dove mandare il messaggio
     */
    public void sendPrivateMessage(ClientData destinationClient){
        if(!getUserLogged() && destinationClient.getUsername().equals("")) return;
        
        try{
            out.println(JSONParser.getPrivateMessageJSON(getPrivateMessageToSend(), getUsername(), destinationClient));
        }catch(JSONException ex){
            System.err.println(ex);
        }
        
        setPrivateMessageToSend("");
    }
}
