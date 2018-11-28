
package jserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import org.json.*;

public class JServer extends Thread{
    private ServerSocket Server;
    
    private ArrayList<Connect> ConnessioniAperte;
    private Semaphore listSem;
    
    public static void main(String[] args) throws Exception{
        new JServer();
    }
    
    public JServer() throws Exception{
        listSem = new Semaphore(1);
        ConnessioniAperte = new ArrayList<Connect>();
        Server = new ServerSocket(4000);
        System.out.println("*****  Il server è in attesa sulla porta 4000.");
        this.start();
    }
    
    @Override
    public void run(){
        while(true){
            try{
                System.out.println("*****  In attesa di connessione.\n");
                Socket client = Server.accept();
                System.out.println("*****  Connessione accettata da: "+client.getInetAddress()+"\n");
                listSem.acquire();
                ConnessioniAperte.add(new Connect(client));
                listSem.release();
            }catch(Exception ex){
                System.err.println(ex);
            }
        }
    }        
    
    class Connect extends Thread{
        private Socket Client = null;
        private BufferedReader In = null;
        private PrintStream Out = null;
        
        private Semaphore outSem = null;
        
        private ClientData clientData = new ClientData(); 
        
        public Connect(){}
        
        public Connect(Socket client){            
            Client = client;
            
            try{
                In = new BufferedReader(new InputStreamReader(Client.getInputStream()));
                Out = new PrintStream(Client.getOutputStream(), true);
            }catch(Exception e){
                try{
                    Client.close();
                }catch(Exception e1){
                    System.out.println(e.getMessage());
                }
                return;
            }
            outSem = new Semaphore(1);
            
            this.start();
        }
        
        /**
         * Metodo per scrivere un messaggio al client
         * @param message Messaggio da scrivere
         * @throws InterruptedException 
         */
        public void writeMessage(String message) throws InterruptedException{
            outSem.acquire();
            Out.println(message);            
            outSem.release();
        }
        
        @Override
        public void run(){
            try{                
                // Da decommentare se si usa il client su console
                //writeMessage("Inserisci il NomeUtente: ");
                
                // Leggo i dati di login
                JSONObject logIn = new JSONObject(In.readLine());
                clientData.setUsername(logIn.getJSONObject("newUserData").getString("username"));
                clientData.setIp(Client.getInetAddress().toString()); 
                logIn.getJSONObject("newUserData").put("ip", Client.getInetAddress().toString());
                
                // mi creo un array con i dati di tutti i client connessi eccetto me stesso
                ArrayList<ClientData> clientsData = new ArrayList<>();
                listSem.acquire();                
                for (Connect connection: ConnessioniAperte) {
                    if(connection == this)
                        clientsData.add(connection.clientData);
                }
                listSem.release();
                
                //invio al client la lista dei clientConnessi
                writeMessage(JSONParser.getClientListJSON(clientsData).toString());

                //trasmetto il login a tutti i client
                listSem.acquire();
                for(Connect connection: ConnessioniAperte){
                    connection.writeMessage(logIn.toString());                    
                }
                listSem.release();
                System.out.println("-- BENVENUTO/A "+ clientData.getUsername());                    
                
                while (true) {
                    JSONObject json = new JSONObject(In.readLine());
                    
                    if(json.getInt("messageType") == 2)
                        disconnect(json);
                    
                    json.getJSONObject("from").put("ip", clientData.getIp());
                    listSem.acquire();
                    for(Connect connection: ConnessioniAperte){
                        connection.writeMessage(json.toString());
                    }
                    listSem.release();
                    System.out.println(clientData.getUsername()+" -> "+json.getString("messageText"));
                }                
            }catch(Exception ex){
                System.err.println(ex);
            }
        }
        
        /**
         * Metodo che gestisce la disconnessione del client
         * @param json JSONObject jsonMessage from client
         */
        public void disconnect(JSONObject json){
            try {                
                json.getJSONObject("userData").put("ip", clientData.getIp());
                listSem.acquire();
                for(Connect connection: ConnessioniAperte){
                    connection.writeMessage(json.toString());
                }
                ConnessioniAperte.remove(this);
                listSem.release();
                System.out.println("-- "+clientData.getUsername() + " ha ABBANDONATO la chat.");

                Out.flush();
                Out.close();
                In.close();
                Client.close(); 
                return;
            } catch (Exception ex) {
                System.err.println(ex);
                return;
            }            
        }
    }
}
