
package servergui.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.json.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
                boolean conn = Boolean.parseBoolean(user.getElementsByTagName("connected").item(0).getTextContent());
                
                
                if(name.equals(username) && conn != true){
                    user.getElementsByTagName("connected").item(0).setTextContent("true");
                    try {
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(new File("src/servergui/users.xml"));
                        transformer.transform(source, result);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    
                    
                    return psw.equals(password);
                }
                
                i++;
            }            
        }catch(IOException | ParserConfigurationException | SAXException ex ){
            System.err.println(ex.getMessage());            
        }
        return false;
    }
    
    @Override
    public void run(){        
        try {       
            JSONObject logIn = new JSONObject(in.readLine());
            boolean logInResult = logIn(logIn);
            
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
            
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File("src/servergui/users.xml"));
            
            doc.getDocumentElement().normalize();
            NodeList users = doc.getElementsByTagName("user");
            
            int i = 0;
            while(i<users.getLength()){
                Element user = (Element)users.item(i);
                boolean conn = Boolean.parseBoolean(user.getElementsByTagName("connected").item(0).getTextContent());
                String name = user.getElementsByTagName("username").item(0).getTextContent();
                
                if(clientData.getUsername().equals(name) && conn == true){
                    user.getElementsByTagName("connected").item(0).setTextContent("false");
                    try {
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(new File("src/servergui/users.xml"));
                        transformer.transform(source, result);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    
                    
                   
                }
                
                i++;
            
            }
            
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
