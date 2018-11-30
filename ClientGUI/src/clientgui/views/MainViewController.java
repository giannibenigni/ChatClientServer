/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientgui.views;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class MainViewController implements Initializable {

    private boolean menuIsOpen = false;
    
    @FXML
    private BorderPane borderPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Parent ui = null;
        try{
            ui = FXMLLoader.load(getClass().getResource("GlobalChatView.fxml"));
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        borderPane.setCenter(ui);
    }

    @FXML
    private void open_sidebar(MouseEvent event) {        
        try {
            if (!menuIsOpen) {
                Parent sidebar;
                sidebar = FXMLLoader.load(getClass().getResource("MenuView.fxml"));
                borderPane.setLeft(sidebar);
                menuIsOpen = true;
            } else {
                borderPane.setLeft(null);
                menuIsOpen = false;
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
