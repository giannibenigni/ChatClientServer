
package clientgui.models;

import java.io.*;
import java.net.*;
import clientgui.Writer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Eugenio
 */
public class MainModel {
    private Writer writerThread;
    
    private boolean userLogged = false;
    private StringProperty messages = new SimpleStringProperty("Prova");
    
    private BufferedReader in = null;
    private PrintStream out = null;
    
    private Socket socket = null;
    
    /**
     * Metodo Costruttore
     */
    public MainModel(){
        try{
            socket = new Socket("localhost", 4000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(), true);
        }catch(Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("ERRORE CONNESSIONE AL SERVER");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            
            System.exit(1);
        } 
        
        this.writerThread = new Writer(in, messages);
        writerThread.start();
    }
    
    /**
     * Messages Getter
     * @return 
     */
    public String getMessages(){
        return this.messages.get();
    }
}
