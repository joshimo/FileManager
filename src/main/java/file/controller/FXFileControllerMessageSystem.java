package file.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;
import javafx.scene.control.*;
import java.util.Optional;

public class FXFileControllerMessageSystem implements Message {

    private Stage stage;
    private Scene scene;
    private AnchorPane parent;
    Optional<ButtonType> result;

    public FXFileControllerMessageSystem() {
    }

    @Override
    public void ShowInfoMessage(String message) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Info");
                alert.setHeaderText("Information Alert");
                alert.setContentText(message);
                alert.show();
            }
        });
    }

    @Override
    public void ShowErrorMessage(String message) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error!");
                alert.setContentText(message);
                alert.show();
            }
        });
    }

    @Override
    public int ConfirmMessage(String message) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(message);
                alert.setContentText(message);
                result = alert.showAndWait();

            }
        });

        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            return 1;
        }
        else
            return 0;
    }
}
