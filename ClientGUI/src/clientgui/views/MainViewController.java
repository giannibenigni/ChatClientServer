/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientgui.views;

import clientgui.models.MainModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class MainViewController implements Initializable {

    private boolean menuIsOpen = false;
    
    @FXML
    private MainModel model;
    
    @FXML
    private BorderPane borderPane;
    
    @FXML
    private ImageView imageConnStatus;
    
    @FXML
    private CheckBox showIpCheck;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Parent ui = null;
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/clientgui/views/LoginView.fxml"));
            ui = (Parent)fxmlLoader.load();
            fxmlLoader.<LoginViewController>getController().setMainModel(model);
        }catch(IOException ex){
            System.err.println(ex);
        }
        borderPane.setCenter(ui);
        
        // aggiungo un listener per modificare l'immagine dello stato (loggato/nonLoggato)
        model.userLoggedProperty().addListener((Boolean) -> {
            String image = model.getUserLogged() ? "clientgui/views/images/connTrueIcon.png" :  "clientgui/views/images/connFalseIcon.png";
            imageConnStatus.setImage(new Image(image)); 
        });
        
        showIpCheck.selectedProperty().bindBidirectional(model.showIpProperty());
    }

    public BorderPane getBorderPane(){
        return borderPane;
    }
    
    @FXML
    private void open_sidebar(MouseEvent event) {        
        try {
            if (!menuIsOpen) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MenuView.fxml"));
                
                Parent sidebar = (Parent)fxmlLoader.load();
                fxmlLoader.<MenuViewController>getController().setModel(model);                            
                
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

    @FXML
    private void exit_click(MouseEvent event) {
        model.logOutHandler.handle(event);
        ((Stage)borderPane.getScene().getWindow()).close();
    }
    
    @FXML 
    private void logIn_click(MouseEvent event){        
        Parent ui = null;
        try{            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clientgui/views/LoginView.fxml"));      
            ui = (Parent)loader.load();
            loader.<LoginViewController>getController().setMainModel(model);                
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        borderPane.setCenter(ui);
        borderPane.requestFocus();
    }
}
