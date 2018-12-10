/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientgui.views;

import clientgui.classes.ClientData;
import clientgui.models.MainModel;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class ListOnlineViewController implements Initializable {

    private MainModel model;
    
    private ClientData selectedClient = new ClientData();
    
    @FXML
    private ListView<ClientData> listClients;
    
    @FXML
    private JFXButton btnKick;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        btnKick.setVisible(false); 
        listClients.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                selectedClient = newValue;
                btnKick.setVisible(true);
                btnKick.setText("Kick User: "+selectedClient.getUsername()); 
            }
        });
    }    
    
    public void setModel(MainModel model){
        this.model = model;
        setBindings();
    }
    
    private void setBindings(){
        listClients.itemsProperty().bindBidirectional(model.clientsConnectedProperty()); 
    }

    @FXML
    private void kick_click(MouseEvent event) {
        // Gianni qui
    }
}
