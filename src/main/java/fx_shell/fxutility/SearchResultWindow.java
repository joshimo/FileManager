package fx_shell.fxutility;

import file.model.FileUnit;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Vector;


public class SearchResultWindow {

    private Stage primaryStage;
    private Scene scene;
    private Parent parent;

    private static Vector<FileUnit> searchResult;

    public static Vector<FileUnit> getSearchResult() {
        return searchResult;
    }

    public SearchResultWindow(Vector<FileUnit> result) {

        searchResult = result;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/search_result_window.fxml"));

        try {
            parent = loader.load();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        scene = new Scene(parent);
        primaryStage = new Stage();
        primaryStage.setWidth(getMaximumFilePathLength() * 6);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    protected static int getMaximumFilePathLength() {
        int length = 75;
        for (FileUnit f : searchResult)
            if (f.getAbsolutePath().length() > length) length = f.getAbsolutePath().length();
        return length;
    }
}