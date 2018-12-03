/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientgui.views;

import clientgui.classes.ClientData;
import clientgui.models.MainModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class ListOnlineViewController implements Initializable {

    private MainModel model;
    
    @FXML
    private ListView<ClientData> listClients;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
    }    
    
    public void setModel(MainModel model){
        this.model = model;
        setBindings();
    }
    
    private void setBindings(){
        listClients.itemsProperty().bindBidirectional(model.clientsConnectedProperty()); 
    }
}
