package fx_shell;

import file.controller.*;

import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.beans.value.ChangeListener;

public class FileManagerFXShell extends Application {

    protected static int screenWidth;
    protected static int screenHeight;

    private static double windowWidth;
    private static double windowHeight;

    private Stage primaryStage;
    private Scene scene;

    protected Controller controller = FileController.getInstance();

    public static double getWindowWidth() {
        return windowWidth;
    }

    public static double getWindowHeight() {
        return windowHeight;
    }

    public FileManagerFXShell() {
        screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
        screenHeight = (int) Screen.getPrimary().getBounds().getHeight();

        windowWidth = screenWidth * 0.8;
        windowHeight = screenHeight * 0.8;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initRootLayout();
    }

    public void initRootLayout() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/main_shell.fxml"));
            Parent parent = loader.load();

            FXController fxController = loader.getController();

            primaryStage.setTitle("File Manager");
            primaryStage.setFullScreen(false);
            scene = new Scene(parent);

            scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    windowWidth = (double) newValue;
                    fxController.resize();
                }
            });

            scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    windowHeight = (double) newValue;
                    fxController.resize();
                }
            });

            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setHeight(windowHeight);
            primaryStage.setWidth(windowWidth);
            primaryStage.show();
        }
        catch (Exception IOE) {
            IOE.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}