
package clientgui;

import clientgui.views.MainViewController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Eugenio
 */
public class ClientGUI extends Application {
        
    private double xOffset = 0;
    private double yOffset = 0;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/MainView.fxml"));
        root = (Parent)loader.load();
        BorderPane borderPane = loader.<MainViewController>getController().getBorderPane();
        Scene scene = new Scene(root);
        
        borderPane.getTop().setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        borderPane.getTop().setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
            stage.setOpacity(0.7f);
        });
        
        borderPane.getTop().setOnDragDone((e) -> {
            stage.setOpacity(1.0f);
        });
        
        borderPane.getTop().setOnMouseReleased((e) -> {
            stage.setOpacity(1.0f);
        });
        
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
