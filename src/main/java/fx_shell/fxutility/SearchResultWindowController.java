package fx_shell.fxutility;

import file.controller.Controller;
import file.controller.FileController;
import file.model.FileUnit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SearchResultWindowController {

    @FXML
    private TableView<FileUnit> searchResult;
    @FXML
    private TableColumn<FileUnit, String> searchResultColumn;

    @FXML
    private ObservableList<FileUnit> resultList = FXCollections.observableArrayList();

    private Controller fileController = FileController.getInstance();

    private String mask;
    private FileUnit directory;

    public SearchResultWindowController() {
        resultList.addAll(SearchResultWindow.getSearchResult());
    }

    @FXML
    private void initialize() {

        searchResultColumn.setCellValueFactory(new PropertyValueFactory<>("fileAbsoluteName"));
        searchResult.setItems(resultList);

        searchResult.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 )
                    fileController.executeFile(searchResult.getSelectionModel().getSelectedItem());
            }
        });

        searchResult.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().name().equals("ENTER"))
                    fileController.executeFile(searchResult.getSelectionModel().getSelectedItem());
            }
        });

        searchResult.setPrefWidth(SearchResultWindow.getMaximumFilePathLength() * 6 - 20);
        searchResultColumn.setPrefWidth(SearchResultWindow.getMaximumFilePathLength() * 6 - 35);
    }
}