
package servergui.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.json.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
    private Semaphore clientDataSem = null;
    
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
        clientDataSem = new Semaphore(1);
        
        this.start();
    }
    
    public String getUsername(){
        String temp = "";
        try {
            clientDataSem.acquire();
            temp = clientData.getUsername();
            clientDataSem.release();
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
        
        return temp;
    }
        
    public void writeMessage(String message) throws InterruptedException{        
        outSem.acquire();
        out.println(message);
        outSem.release();
    }    
     
    /**
     * metodo che fa il login del Client
     * @param logInData JSONObject log in data
     * @return Boolean true se il logIn va a bun fine
     */
    private boolean logIn(JSONObject logInData){
        try{                        
            clientData.setUsername(logInData.getJSONObject("newUserData").getString("username"));
            clientData.setPassword(logInData.getJSONObject("newUserData").getString("password"));             
            clientData.setIp(Client.getInetAddress().toString());  
            
            logInData.getJSONObject("newUserData").put("ip",Client.getInetAddress().toString());
            
            return checkUser(clientData.getUsername(), clientData.getPassword());            
        }catch(JSONException ex){
            System.err.println(ex.getMessage());            
        }
        return false;
    }        
    
    /**
     * Metodo che controlla i dati del client 
     * @param username String username
     * @param password String password
     * @return Boolean dati corretti
     */
    private boolean checkUser(String username, String password){  
        try{            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File("src/servergui/users.xml"));            
            doc.getDocumentElement().normalize();
            NodeList users = doc.getElementsByTagName("user");
            
            int i = 0;
            while(i<users.getLength()){
                Element user = (Element)users.item(i);
                String name = user.getElementsByTagName("username").item(0).getTextContent();
                String psw = user.getElementsByTagName("password").item(0).getTextContent();
                
                if(name.equals(username)){
                    listSem.acquire();
                    boolean giaLoggato = !OpenConnection.stream().noneMatch((elem)->elem.getUsername().equals(name) && elem != this);
                    listSem.release();
                    
                    return psw.equals(password) && !giaLoggato;
                }                
                i++;
            }            
        }catch(IOException | ParserConfigurationException | SAXException | InterruptedException ex){
            System.err.println(ex);            
        }
        return false;
    }
    
    /**
     * Metodo per fare la registrazione dki un nuovo utente
     * @param signUpData JSONObject client data information
     * @return Boolean true se la registrazione è andata a buon fine
     */
    public boolean signUp(JSONObject signUpData){
        try{                        
            clientData.setUsername(signUpData.getJSONObject("userData").getString("username"));
            clientData.setPassword(signUpData.getJSONObject("userData").getString("password"));             
            clientData.setIp(Client.getInetAddress().toString());  
            
            // controllo se esiste gia un account con queste credenziali, se non esiste lo creo
            if(!checkUser(clientData.getUsername(), clientData.getPassword())){
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new File("src/servergui/users.xml"));

                doc.getDocumentElement().normalize();
                Element users = doc.getDocumentElement();   
                
                Element newUser = doc.createElement("user");
                Element username = doc.createElement("username");
                username.appendChild(doc.createTextNode(clientData.getUsername()));                
                Element password = doc.createElement("password");
                password.appendChild(doc.createTextNode(clientData.getPassword()));
                newUser.appendChild(username);
                newUser.appendChild(password);
                
                users.appendChild(newUser);  
                
                // Use a Transformer for output
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer tr = tFactory.newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");                                
                
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("src/servergui/users.xml")); 
                tr.transform(source, result);
                return true;
            }
        }catch(JSONException | IOException | SAXException | ParserConfigurationException | DOMException | TransformerException ex){
            System.err.println(ex.getMessage());            
        }
        return false;
    }
    
    @Override
    public void run(){        
        try {       
            JSONObject msg = new JSONObject(in.readLine());
            
            // controllo se è una richiesta di registrazione
            if(msg.getInt("messageType") == 7){
                boolean signUpResult = signUp(msg);
                
                msg = JSONParser.SignUpLogInConverter(msg);
                msg.getJSONObject("newUserData").put("ip",Client.getInetAddress().toString());
                
                // trasmetto al client il risultato della Registrazione
                writeMessage(JSONParser.getSignUpResult(signUpResult).toString()); 
                
                if(!signUpResult){
                    listSem.acquire();
                    Platform.runLater(() -> { 
                        OpenConnection.remove(this);
                    });
                    listSem.release();
                    return;
                }                                
            }else{
                boolean logInResult = logIn(msg);            
                // trasmetto al client il risultato del logIn
                writeMessage(JSONParser.getLogInResult(logInResult).toString()); 
                
                if(!logInResult){
                    listSem.acquire();
                    Platform.runLater(() -> { 
                        OpenConnection.remove(this); 
                    });
                    listSem.release();
                    return;
                }
            }                        

            // mi creo un array con i dati di tutti i client connessi eccetto me stesso
            ArrayList<ClientData> clientsData = new ArrayList<>();            
            listSem.acquire();                
            for (Connection connection: OpenConnection) {
                if(connection != this)
                    clientsData.add(connection.clientData);
            }
            listSem.release();
            
            //Invio al client la lista dei client connessi
            writeMessage(JSONParser.getClientListJSON(clientsData).toString());
            
            //Trasmetto il login a tutti i client eccetto questo
            listSem.acquire();
            for(Connection connect : OpenConnection){
                if(connect != this){
                    connect.writeMessage(msg.toString());
                }
            }
            listSem.release();
            
            messagesSem.acquire();
            
            outputString.set(outputString.get()+"\nBENVENUTO "+clientData.getUsername()+"["+clientData.getIp()+"]");
            
            messagesSem.release();
            
            while(true){                
                JSONObject json = new JSONObject(in.readLine());

                switch(json.getInt("messageType")){
                    case 2: // log out
                        disconnect(json);
                        return;
                    case 5: // private message
                        json.getJSONObject("from").put("ip", clientData.getIp());
                        listSem.acquire();
                        for(Connection connect: OpenConnection){
                            if(json.getJSONObject("to").getString("username").equals(connect.getUsername()) || json.getJSONObject("from").getString("username").equals(connect.getUsername())){
                                connect.writeMessage(json.toString());                                
                            }
                        }
                        listSem.release(); 
                        break;                    
                    default: // normal message
                        json.getJSONObject("from").put("ip", clientData.getIp());

                        listSem.acquire();
                        for(Connection connect: OpenConnection){                    
                            connect.writeMessage(json.toString());
                        }
                        listSem.release();     

                        messagesSem.acquire();
                        outputString.set(outputString.get()+"\n<"+clientData.getUsername()+"["+clientData.getIp()+"]> "+json.getString("messageText")); 
                        messagesSem.release(); 
                        break;
                }                              
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
