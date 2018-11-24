
package clientgui;

import java.io.BufferedReader;
import java.io.IOException;
import javafx.beans.property.StringProperty;

/**
 * 
 * @author Eugenio
 */
public class Writer extends Thread{
    private boolean attivo;
    private BufferedReader in;    
    private StringProperty outputString;
    
    /**
     * Metodo Costruttore
     * @param buffer Input Stream del Server
     * @param s Stringa di output
     */
    public Writer(BufferedReader buffer, StringProperty s){
        this.in = buffer;
        this.outputString = s;
        this.attivo = true;
    }
    
    /**
     * Metodo per fermare il thread
     */
    public void ferma(){
        this.attivo = false;
    }
    
    /**
     * Metodo run del Thread
     */
    @Override
    public void run() {
        while(attivo){
            try {
//                this.outputString.set(new StringBuilder("\n")
//                                            .append(in.readLine()).toString());
                System.out.println(in.readLine());
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
}
