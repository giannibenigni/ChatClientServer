
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
 * @author Eugenio
 */
public class Connection extends Thread{        
    private Socket Client = null;
    private BufferedReader in = null;
    private PrintStream out = null;
    
    private boolean vai = true;
    private Semaphore semStop = new Semaphore(0);
    
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
     
    private void ferma(){
        this.vai = false;        
    }
    
    /**
     * metodo che fa il login del Client
     * @param logInData JSONObject log in data
     * @return JSONObject messaggio da inviare al client che indica se è riuscito a fare il login o no
     */
    private JSONObject logIn(JSONObject logInData){
        try{                        
            clientData.setUsername(logInData.getJSONObject("newUserData").getString("username"));
            clientData.setPassword(logInData.getJSONObject("newUserData").getString("password"));             
            clientData.setIp(Client.getInetAddress().toString());  
            
            logInData.getJSONObject("newUserData").put("ip",Client.getInetAddress().toString());
            
            
            if(!checkUsernamePassword(clientData.getUsername(), clientData.getPassword())){
                return JSONParser.getLogInResult(false, 1);
            }
            
            listSem.acquire();
            boolean giaLoggato = !OpenConnection.stream().noneMatch((elem)->elem.getUsername().equals(getUsername()) && elem != this);
            listSem.release();
            if(giaLoggato){
                return JSONParser.getLogInResult(false, 2);
            }
            
            if(isBannto(clientData.getUsername())){
                return JSONParser.getLogInResult(false, 3);
            }
            
            return JSONParser.getLogInResult(true, -1);
        }catch(JSONException | InterruptedException ex){
            System.err.println(ex.getMessage());            
        }
        
        try {
            return JSONParser.getLogInResult(false, 0);
        } catch (JSONException ex) { }
        return null;
    }        
    
    /**
     * Metodo per controllare se un client è bannato
     * @param username String client da controllare
     * @return Boolean true se bannato
     */
    private boolean isBannto(String username){
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
                
                if(name.equals(username)){
                    if(user.getElementsByTagName("bannato").getLength() >= 1){
                        return true;
                    }
                    break;
                }                
                i++;
            }            
        }catch(IOException | ParserConfigurationException | SAXException ex){
            System.err.println(ex);            
        }
        return false;
    }
    
    /**
     * Metodo che controlla se lo username e la password sono corretti
     * @param username String username
     * @param password String password
     * @return Boolean dati corretti
     */
    private boolean checkUsernamePassword(String username, String password){  
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
                    return psw.equals(password);
                }                
                i++;
            }            
        }catch(IOException | ParserConfigurationException | SAXException ex){
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
            if(!checkUsernamePassword(clientData.getUsername(), clientData.getPassword())){
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
    
    /**
     * Metodo per Kickare un client
     * @param clientToKick ClientData client da kikkare
     * @return Boolean true se il kick è andato a buon fine
     */
    private boolean kick(ClientData clientToKick){
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
                
                if(name.equals(clientToKick.getUsername())){                    
                    NodeList kicks = user.getElementsByTagName("kick");
                    int j = 0;
                    while(j < kicks.getLength()){
                        Element kick = (Element)kicks.item(j);
                        // controllo se non ho gia kikkato questo utente
                        if(kick.getElementsByTagName("username").item(0).getTextContent().equals(getUsername())){
                            return false;
                        }
                        j++;
                    }
                    
                    // se non gia kikkato aggiungo un kick
                    Element newKick = doc.createElement("kick");
                    Element username = doc.createElement("username");
                    username.appendChild(doc.createTextNode(getUsername()));   
                    newKick.appendChild(username);
                    
                    NodeList kicksTags = user.getElementsByTagName("kicks");
                    if(kicksTags.getLength() <= 0){
                        Element kicksTag = doc.createElement("kicks");
                        kicksTag.appendChild(newKick); 
                        user.appendChild(kicksTag);                        
                    }else{                        
                        kicksTags.item(0).appendChild(newKick);
                    }
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
                i++;
            }            
        }catch(IOException | ParserConfigurationException | SAXException | DOMException | TransformerException ex){
            System.err.println(ex);            
        }
        return false;
    }
    
    /**
     * Metodo per controllare se un user deve essere bannato
     * @param clientToCheck Clientdata da controllare
     * @return Boolean true se è da bannare
     */
    private boolean checkBan(ClientData clientToCheck){
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
                       
                if(name.equals(clientToCheck.getUsername())){                 
                    if(user.getElementsByTagName("kick").getLength() >= 3){
                        user.appendChild(doc.createElement("bannato"));
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
                    break;
                }
                
                i++;
            }            
        }catch(Exception ex){
            System.err.println(ex);            
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
                JSONObject logInResult = logIn(msg);            
                // trasmetto al client il risultato del logIn
                writeMessage(logInResult.toString()); 
                
                if(logInResult.getBoolean("result") == false){
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
            
            while(vai){                
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
                    case 10: //kick
                        json.getJSONObject("senderUser").put("ip", clientData.getIp());
                        
                        String username = json.getJSONObject("userToKick").getString("username");
                        String ip = json.getJSONObject("userToKick").getString("ip");
                        ClientData clientToKick = new ClientData(username, ip);
                        
                        if(kick(clientToKick)){
                            listSem.acquire();
                            for(Connection conn: OpenConnection){
                                conn.writeMessage(JSONParser.getServerNormalMessageJSON(getUsername()+" ha kikkato: "+username).toString());
                            }
                            listSem.release();
                                                        
                            messagesSem.acquire();
                            outputString.set(outputString.get()+"\n"+getUsername()+" ha kikkato: "+username); 
                            messagesSem.release();
                        }else{
                            writeMessage(JSONParser.getKickResponse(false).toString());
                        }                                              
                        
                        if(checkBan(clientToKick)){
                            int indexToBan = 0;
                            listSem.acquire();
                            for(Connection conn: OpenConnection){
                                conn.writeMessage(JSONParser.getBanMessageJSON(clientToKick).toString());
                                if(conn.getUsername().equals(username)){
                                    indexToBan = OpenConnection.indexOf(conn);
                                }
                            }
                            final Connection clientToBan = OpenConnection.get(indexToBan);
                            clientToBan.writeMessage(JSONParser.getBanMessageJSON(clientToKick).toString());
                            Platform.runLater(() -> { 
                                OpenConnection.remove(clientToBan); 
                            });                            
                            listSem.release();  
                            
                            messagesSem.acquire();
                            outputString.set(outputString.get()+"\n"+username+" è stato BANNATO"); 
                            messagesSem.release();
                            
                            clientToBan.ferma();
                            clientToBan.semStop.acquire();
                            clientToBan.out.flush();
                            clientToBan.out.close(); 
                            clientToBan.in.close();
                            clientToBan.Client.close();  
                        }                        
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
        semStop.release();
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
