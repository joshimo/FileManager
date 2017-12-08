package file.controller.monitor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;


public class FXMonitorWindow implements Monitor {

    private static double windowWidth = 600;
    private static double windowHeight = 290;

    private Stage primaryStage;
    private Scene scene;
    private AnchorPane parentPane;

    private FXMonitowWindowController fxController;


    public FXMonitorWindow() {
        this.primaryStage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/file_monitor_window.fxml"));

        try {
            parentPane = loader.load();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        fxController = loader.getController();

        primaryStage.setTitle("Progress");
        scene = new Scene(parentPane);
        primaryStage.setScene(scene);

        primaryStage.setHeight(windowHeight);
        primaryStage.setWidth(windowWidth);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.hide();
    }

    synchronized public void Show() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                primaryStage.show();
            }
        });

    }

    synchronized public void Hide() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                primaryStage.hide();
            }
        });
    }

    public void SetUpperText (String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fxController.setUpperText(text);
            }
        });
    }

    public void SetMiddleText (String text) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                fxController.setMiddleText(text);
            }
        });
    }

    public void SetLowerText(String text) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                fxController.setLowerText(text);
            }
        });
    }

    public void SetMiddleProgress(double progress) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                fxController.setMiddleProgress(progress);
            }
        });
    }

    public void SetLowerProgress(double progress) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                fxController.setLowerProgress(progress);
            }
        });
    }

    public void ShowProgress(boolean showProgress) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                fxController.ShowProgress(showProgress);
            }
        });
    }

    public void Clear() {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                fxController.Clear();
            }
        });
    }
}
