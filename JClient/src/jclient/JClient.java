
package jclient;

import java.io.*;
import java.net.*;
import java.util.*;

public class JClient {

    public static void main(String[] args) {
        BufferedReader in = null;
        PrintStream out = null;
        Socket socket = null;
        Scanner input = new Scanner(System.in);
        
        try{
            socket = new Socket("localhost", 4000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(), true);
            
            System.out.print(in.readLine());
            String nomeClient = input.nextLine();
            out.println(nomeClient);
            
            Writer w = new Writer(in);
            w.start();
            
            boolean close = false;
            while(!close){                                  
                String msg = input.nextLine();
                if (msg.equalsIgnoreCase("/exit"))
                    close = true;
                
                out.println(msg);
            }

            w.fine();   
            out.close();
            in.close();
            socket.close();
        }catch(Exception ex){            
            System.out.println(ex.getMessage());
        }        
    }
    
}
