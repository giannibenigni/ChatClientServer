/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientgui.views;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class MenuViewController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {   }    

    @FXML
    private void globalChat_click(MouseEvent event) {
        BorderPane borderPaneMain = (BorderPane) ((Node) event.getSource()).getScene().getRoot();
        loadUI("GlobalChatView", borderPaneMain);
    }

    @FXML
    private void privateMessage_click(MouseEvent event) {
        BorderPane borderPaneMain = (BorderPane) ((Node) event.getSource()).getScene().getRoot();
        loadUI("PrivateMessageView", borderPaneMain);
    }    
    
    /**
     * Metodo che carica una ui
     * @param ui Strign name
     */
    private void loadUI(String uiName, BorderPane borderPane){
        Parent ui = null;
        try{
            ui = FXMLLoader.load(getClass().getResource(uiName+".fxml"));
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        borderPane.setCenter(ui);
    }
}
