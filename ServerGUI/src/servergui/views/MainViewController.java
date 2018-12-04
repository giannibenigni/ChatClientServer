/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servergui.views;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.swing.text.DefaultCaret;
import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;
import servergui.models.MainModel;

/**
 *
 * @author Gianni
 */
public class MainViewController implements Initializable {
    
   @FXML
    private TextField txtMessageToSend;
   
   @FXML
   private TextArea txtShowMessages;
    
    @FXML
    private MainModel model;
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model.messageToSendProperty().bindBidirectional(txtMessageToSend.textProperty());
        
        txtShowMessages.textProperty().addListener(new ChangeListener<Object>(){
        
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue){
                //txtShowMessages.setCaretPosition(txtShowMessages.getDocument().getLenght());
                //txtShowMessages.deselect();
                //txtShowMessages.setScrollTop(Double.MIN_VALUE);
                //DefaultCaret caret = (DefaultCaret) txtShowMessages.getC
                //caret.setUpdatePolicy(ALWAYS_UPDATE);
                System.out.println("ciao" + txtShowMessages.getLength());
            }
        
        });
    }    
    
}
