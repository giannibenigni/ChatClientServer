
package jserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class JServer extends Thread{
    private ServerSocket Server;
    
    public ArrayList<Connect> ConnessioniAperte;
    
    public static void main(String[] args) throws Exception{
        new JServer();
    }
    
    public JServer() throws Exception{
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
                ConnessioniAperte.add(new Connect(client));
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
        
        private String ClientName = "";
        
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
                writeMessage("Inserisci il NomeUtente: ");
                
                ClientName = In.readLine();
                
                for(Connect connection: ConnessioniAperte){
                    connection.writeMessage("-- BENVENUTO/A "+ ClientName);                    
                }
                System.out.println("-- BENVENUTO/A "+ ClientName);                    
                
                while (true) {                    
                    String msg = In.readLine();
                    if(msg.equalsIgnoreCase("/exit")){
                        for(Connect connection: ConnessioniAperte){
                            connection.writeMessage("-- "+ClientName + " ha ABBANDONATO la chat.");
                        }
                        System.out.println("-- "+ClientName + " ha ABBANDONATO la chat.");
                        
                        ConnessioniAperte.remove(this);
                        Out.flush();
                        Out.close();
                        In.close();
                        Client.close(); 
                        return;
                    }

                    for(Connect connection: ConnessioniAperte){
                        connection.writeMessage(ClientName+" -> "+msg);
                    }
                    System.out.println(ClientName+" -> "+msg);
                    
                }
                
            }catch(Exception ex){
                System.err.println(ex);
            }
        }
    }
}
