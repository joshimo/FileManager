package fx_shell;

import file.model.*;
import file.controller.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.io.File;

import fx_shell.fxutility.SearchResultWindow;
import fx_shell.messanger.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;


public class FXController {

    @FXML
    private Double width;
    @FXML
    private Double height;

    private final int LEFT_PANEL = -1;
    private final int RIGHT_PANEL = 1;

    private boolean rightPanelFTPmode = false;
    private boolean leftPanelFTPmode = false;

    private int activePanel = LEFT_PANEL;

    private Controller fileController = FileController.getInstance();
    private FTPFileController ftpController = FTPFileController.getInstance();

    private FileManagerFXShell shell = new FileManagerFXShell();

    private Vector<FileUnit> rightDriveList;
    private Vector<FileUnit> leftDriveList;

    private Vector<FileUnit> searchResult;

    private ObservableList<FileUnit> leftFileList = FXCollections.observableArrayList();
    private ObservableList<FileUnit> rightFileList = FXCollections.observableArrayList();

    private ObservableList<DriveButton> leftButtons = FXCollections.observableArrayList();
    private ObservableList<DriveButton> rightButtons = FXCollections.observableArrayList();

    private Messenger messenger;

    @FXML
    private FileUnit currentFile;
    @FXML
    private FileUnit currentLeftFile;
    @FXML
    private FileUnit currentRightFile;
    @FXML
    private FileUnit currentLeftDir;
    private String currentFTPleftDir = "";
    @FXML
    private FileUnit currentRightDir;
    private String currentFTPrightDir = "";

    @FXML
    private TableView<FileUnit> leftFileTable;
    @FXML
    private TableView<FileUnit> rightFileTable;

    @FXML
    private TableColumn<FileUnit, String> leftFilenameColumn;
    @FXML
    private TableColumn<FileUnit, String> leftExtColumn;
    @FXML
    private TableColumn<FileUnit, String> leftSizeColumn;
    @FXML
    private TableColumn<FileUnit, String> leftDateColumn;
    @FXML
    private TableColumn<FileUnit, String> leftAttrColumn;

    @FXML
    private TableColumn<FileUnit, String> rightFilenameColumn;
    @FXML
    private TableColumn<FileUnit, String> rightExtColumn;
    @FXML
    private TableColumn<FileUnit, String> rightSizeColumn;
    @FXML
    private TableColumn<FileUnit, String> rightDateColumn;
    @FXML
    private TableColumn<FileUnit, String> rightAttrColumn;

    @FXML
    private PathPanel pathPanel;

    @FXML
    private ButtonBar leftDrivesButtons;
    @FXML
    private ButtonBar rightDrivesButtons;
    @FXML
    private ButtonBar functionalButtons;


    public FXController() {
        this.fileController.setGUI(FileController.FX);
    }

    @FXML
    private void initialize() {

        rightDriveList = fileController.getDrivesList();
        leftDriveList = fileController.getDrivesList();

        currentFile = leftDriveList.get(0);
        currentLeftFile = leftDriveList.get(0);
        currentLeftDir = leftDriveList.get(0);

        currentRightFile = rightDriveList.get(0);
        currentRightDir = rightDriveList.get(0);

        createFileList(currentLeftFile, LEFT_PANEL);
        createFileList(currentRightFile, RIGHT_PANEL);

        pathPanel.setFile(leftDriveList.get(0));

        createDrivesPanels();
        createFilePanels();
        resize();

        leftFileTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileUnit pointer = leftFileTable.getSelectionModel().getSelectedItem();
                handleSelection(pointer, LEFT_PANEL);
                if (event.getClickCount() == 2)
                    handleDoubleclick(pointer, LEFT_PANEL);
            }
        });

        leftFileTable.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                FileUnit pointer = leftFileTable.getSelectionModel().getSelectedItem();
                handleSelection(pointer, LEFT_PANEL);
                if (event.getCode().name().equals("ENTER"))
                    handleDoubleclick(pointer, LEFT_PANEL);
            }
        });

        rightFileTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileUnit pointer = rightFileTable.getSelectionModel().getSelectedItem();
                handleSelection(pointer, RIGHT_PANEL);
                if (event.getClickCount() == 2)
                    handleDoubleclick(pointer, RIGHT_PANEL);
            }
        });

        rightFileTable.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                FileUnit pointer = rightFileTable.getSelectionModel().getSelectedItem();
                handleSelection(pointer, RIGHT_PANEL);
                if (event.getCode().name().equals("ENTER"))
                    handleDoubleclick(pointer, RIGHT_PANEL);
            }
        });

        messenger = FXShellMessageSystem.getInstance();
    }

    protected void resize() {

        width = shell.getWindowWidth();
        height = shell.getWindowHeight();

        leftDrivesButtons.setPrefWidth(0.5 * (width - 30));
        leftDrivesButtons.setLayoutX(10);

        rightDrivesButtons.setPrefWidth(0.5 * (width - 30));
        rightDrivesButtons.setLayoutX(0.5 * width + 5);

        leftFileTable.setPrefWidth(0.5 * (width - 30));
        leftFileTable.setPrefHeight(height - 200);
        leftFileTable.setLayoutX(10);
        leftFileTable.setLayoutY(80);

        leftFilenameColumn.setPrefWidth(0.5 * width - 290);

        rightFileTable.setPrefWidth(0.5 * (width - 30));
        rightFileTable.setPrefHeight(height - 200);
        rightFileTable.setLayoutX(0.5 * width + 5);
        rightFileTable.setLayoutY(80);

        rightFilenameColumn.setPrefWidth(0.5 * width - 290);

        functionalButtons.setPrefWidth(width - 20);
        functionalButtons.setLayoutY(height - 110);

        pathPanel.setPrefWidth(width - 20);
        pathPanel.setLayoutY(height - 40);
    }

    private void handleSelection(FileUnit pointer, int panelNum) {

        activePanel = panelNum;

        if (!isFTPmode(panelNum)) {

            if (panelNum == RIGHT_PANEL) {
                if (pointer == null)
                    rightFileTable.getSelectionModel().select(0);
                else
                    if (!pointer.isBlank()) {
                        currentFile = pointer;
                        currentRightFile = pointer;
                        currentRightDir = new FileUnit(pointer.getParentFile());
                        pathPanel.setFile(currentFile);
                    }
                    else
                        if (currentRightDir.getParentFile() != null)
                            pathPanel.setFile(currentRightDir.getParentFile());
            }

            if (panelNum == LEFT_PANEL) {
                if (pointer == null)
                    leftFileTable.getSelectionModel().select(0);
                else
                    if (!pointer.isBlank()) {
                        currentFile = pointer;
                        currentLeftFile = pointer;
                        currentLeftDir = new FileUnit(pointer.getParentFile());
                        pathPanel.setFile(currentFile);
                    }
                    else
                        if (currentLeftDir.getParentFile() != null)
                            pathPanel.setFile(currentLeftDir.getParentFile());
            }
        }

        else {
            currentFile = pointer;
            pathPanel.setFile(currentFile);
            if (panelNum == RIGHT_PANEL)
                currentRightFile = pointer;
            if (panelNum == LEFT_PANEL)
                currentLeftFile = pointer;
        }
    }

    private void handleDoubleclick(FileUnit pointer, int panelNum) {

        if (!isFTPmode(panelNum)) {

            if (activePanel == LEFT_PANEL) {
                if (pointer.isBlank()) {
                    if (currentLeftDir.getParentFile() != null)
                        currentLeftDir = new FileUnit(currentLeftDir.getParentFile());
                    pathPanel.setFile(currentLeftDir);
                    createFileList(currentLeftDir, LEFT_PANEL);
                }
                else {
                    if (currentFile.isDirectory()) {
                        currentLeftDir = currentLeftFile;
                        createFileList(currentLeftDir, LEFT_PANEL);
                    }
                    if (currentFile.isFile())
                        fileController.executeFile(currentFile);
                }
            }

            if (activePanel == RIGHT_PANEL) {
                if (pointer.isBlank()) {
                    if (currentRightDir.getParentFile() != null)
                        currentRightDir = new FileUnit(currentRightDir.getParentFile());
                    pathPanel.setFile(currentRightDir);
                    createFileList(currentRightDir, RIGHT_PANEL);
                }
                else {
                    if (currentFile.isDirectory()) {
                        currentRightDir = currentRightFile;
                        createFileList(currentRightDir, RIGHT_PANEL);
                    }
                    if (currentFile.isFile())
                        fileController.executeFile(currentFile);
                }
            }
        }

        else {

            if (activePanel == LEFT_PANEL) {
                if (currentFile.isDirectory())
                    currentFTPleftDir = currentFile.getParent() + File.separator + currentFile.getName();
                if (pointer.isBlank())
                    currentFTPleftDir = getParentDirectoryPath(currentFTPleftDir);
                createFTPFileList(currentFTPleftDir, activePanel);
            }
            if (activePanel == RIGHT_PANEL) {
                if (currentFile.isDirectory())
                    currentFTPrightDir = currentFile.getParent() + File.separator + currentFile.getName();
                if (pointer.isBlank())
                    currentFTPrightDir = getParentDirectoryPath(currentFTPrightDir);
                createFTPFileList(currentFTPrightDir, activePanel);
            }
        }
    }

    @FXML
    public void handleRefreshButtonAction(ActionEvent event) {
        createDrivesPanels();
    }

    @FXML
    public void handleNewDirButtonAction(ActionEvent event) {
        String newDirName = messenger.ShowInputMessage("New dir");
        FileUnit newDirectory;

        if (activePanel == LEFT_PANEL) {
            newDirectory = new FileUnit(currentLeftDir.getAbsolutePath() + File.separator + newDirName);
            fileController.createNewDirectory(newDirectory);
            createFileList(currentLeftDir, LEFT_PANEL);
        }
        if (activePanel == RIGHT_PANEL) {
            newDirectory = new FileUnit(currentRightDir.getAbsolutePath() + File.separator + newDirName);
            fileController.createNewDirectory(newDirectory);
            createFileList(currentRightDir, RIGHT_PANEL);
        }
    }

    @FXML
    public void handleRenameButtonAction(ActionEvent event) {
        String newUnitName = messenger.ShowInputMessage("Renamed directory new name");
        FileUnit oldUnit;

        if (activePanel == LEFT_PANEL) {
            oldUnit = new FileUnit(currentLeftFile.getAbsolutePath());
            fileController.renameFileOrDirectory(newUnitName, oldUnit);
            createFileList(currentLeftDir, LEFT_PANEL);
        }
        if (activePanel == RIGHT_PANEL) {
            oldUnit = new FileUnit(currentRightFile.getAbsolutePath());
            fileController.renameFileOrDirectory(newUnitName, oldUnit);
            createFileList(currentRightDir, RIGHT_PANEL);
        }
    }

    @FXML
    public void handleCopyButtonAction(ActionEvent event) {
        if (activePanel == LEFT_PANEL && !isFTPmode(RIGHT_PANEL)) {
            if (messenger.ConfirmMessage("Copy " + currentLeftFile.getName() +
                                         "\nto " + currentRightDir.getAbsolutePath() + "?") == 0)
                new Thread() {
                    @Override
                    public void run() {
                        if (!isFTPmode(LEFT_PANEL)) {
                            fileController.copyFileOrDirectory(currentLeftFile, currentRightDir);
                            createFileList(currentRightDir, RIGHT_PANEL);
                        }
                        else {
                            try {
                                ftpController.copyFTPfileOrDirectory(currentLeftFile, currentRightDir.getAbsolutePath());
                                createFileList(currentRightDir, RIGHT_PANEL);
                            }
                            catch (IOException ioe) {
                                ioe.printStackTrace();
                            }
                        }
                    }
                }.start();
        }

        if (activePanel == RIGHT_PANEL && !isFTPmode(LEFT_PANEL)) {
            if (messenger.ConfirmMessage("Copy " + currentRightFile.getName() +
                                         "\nto " + currentLeftDir.getAbsolutePath() + "?") == 0)
                new Thread() {
                    @Override
                    public void run() {
                        if(!isFTPmode(RIGHT_PANEL)) {
                            fileController.copyFileOrDirectory(currentRightFile, currentLeftDir);
                            createFileList(currentLeftDir, LEFT_PANEL);
                        }
                        else {
                            try {
                                ftpController.copyFTPfileOrDirectory(currentRightFile, currentLeftDir.getAbsolutePath());
                                createFileList(currentLeftDir, LEFT_PANEL);
                            }
                            catch (IOException ioe) {
                                ioe.printStackTrace();
                            }
                        }
                    }
                }.start();
        }
    }

    @FXML
    public void handleDeleteButtonAction(ActionEvent event) {
        if (activePanel == LEFT_PANEL) {
            if (messenger.ConfirmMessage("Are You sure to delete\n" + currentLeftFile.getAbsolutePath()  + "  ?") == 0)
                new Thread() {
                    @Override
                    public void run() {
                        fileController.deleteFileOrDirectory(currentLeftFile);
                        createFileList(currentLeftDir, LEFT_PANEL);
                    }
                }.start();
        }

        if (activePanel == RIGHT_PANEL) {
            if (messenger.ConfirmMessage("Are You sure to delete\n" + currentRightFile.getAbsolutePath()  + "  ?") == 0)
                new Thread() {
                    @Override
                    public void run() {
                        fileController.deleteFileOrDirectory(currentRightFile);
                        createFileList(currentRightDir, RIGHT_PANEL);
                    }
                }.start();
        }
    }

    @FXML
    public void handleSearchButtonAction(ActionEvent event) {
        String mask = messenger.ShowInputMessage("Search file mask:");
        searchResult = new Vector<>();
        Thread searchThread =  new Thread() {
            @Override
            public void run() {
                if (activePanel == LEFT_PANEL)
                    searchResult = (Vector) fileController.searchFile(mask, currentLeftDir);
                if (activePanel == RIGHT_PANEL)
                    searchResult = (Vector) fileController.searchFile(mask, currentRightDir);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        new SearchResultWindow(searchResult);
                    }
                });
            }
        };

        searchThread.start();
    }

    @FXML
    public void handleTreeButtonAction(ActionEvent event) {
        System.out.println("You clicked Tree!");
    }

    @FXML
    public void handleViewerButtonAction(ActionEvent event) {
        System.out.println("You clicked Viewer!");
    }

    private void createFileList(File currentDirectory, int panelNum) {

        setFTPmode(false, panelNum);

        synchronized (leftFileList) {
            if (panelNum == LEFT_PANEL) {
                if (leftFileList.size() > 0)
                    leftFileList.remove(0, leftFileList.size());
                leftFileList.addAll(fileController.getFileList(currentDirectory));
                leftFileList.add(0, new FileUnit());
            }
        }

        synchronized (rightFileList) {
            if (panelNum == RIGHT_PANEL) {
                if (rightFileList.size() > 0)
                    rightFileList.remove(0, rightFileList.size());
                rightFileList.addAll(fileController.getFileList(currentDirectory));
                rightFileList.add(0, new FileUnit());
            }
        }
    }

    private synchronized void createFTPFileList(String currentDirectory, int panelNum) {

        setFTPmode(true, panelNum);

        try {

            if (panelNum == LEFT_PANEL) {
                if (leftFileList.size() > 0)
                    leftFileList.remove(0, leftFileList.size());
                leftFileList.addAll(ftpController.getFileList(currentDirectory));
                leftFileList.add(0, new FileUnit());
            }

            if (panelNum == RIGHT_PANEL) {
                if (rightFileList.size() > 0)
                    rightFileList.remove(0, rightFileList.size());
                rightFileList.addAll(ftpController.getFileList(currentDirectory));
                rightFileList.add(0, new FileUnit());
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private String getParentDirectoryPath(String directory) {

        List<String> str = Arrays.asList(directory.split("\\\\"));
        String parentDirPath = "";

        for (int count = 0; count < str.size() - 1; count ++)
            if (str.get(count).length() > 0 && !str.get(count).equals(" ") && !str.get(count).equals("/"))
                parentDirPath += File.separator + str.get(count);

        return parentDirPath;
    }

    private void setFTPmode(boolean ftpMode, int activePanel) {
        if (activePanel == LEFT_PANEL) leftPanelFTPmode = ftpMode;
        if (activePanel == RIGHT_PANEL) rightPanelFTPmode = ftpMode;
    }

    private boolean isFTPmode(int activePanel) {
        if (activePanel == LEFT_PANEL) return leftPanelFTPmode;
        if (activePanel == RIGHT_PANEL) return rightPanelFTPmode;
        return false;
    }

    private void createFilePanels() {

        leftFilenameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        leftFilenameColumn.setCellFactory(new CellRenderer());
        leftExtColumn.setCellValueFactory(new PropertyValueFactory<>("fileExtension"));
        leftExtColumn.setCellFactory(new CellRenderer());
        leftSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        leftSizeColumn.setCellFactory(new CellRenderer());
        leftDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        leftDateColumn.setCellFactory(new CellRenderer());
        leftAttrColumn.setCellValueFactory(new PropertyValueFactory<>("attributesLetters"));
        leftAttrColumn.setCellFactory(new CellRenderer());

        rightFilenameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        rightFilenameColumn.setCellFactory(new CellRenderer());
        rightExtColumn.setCellValueFactory(new PropertyValueFactory<>("fileExtension"));
        rightExtColumn.setCellFactory(new CellRenderer());
        rightSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        rightSizeColumn.setCellFactory(new CellRenderer());
        rightDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        rightDateColumn.setCellFactory(new CellRenderer());
        rightAttrColumn.setCellValueFactory(new PropertyValueFactory<>("attributesLetters"));
        rightAttrColumn.setCellFactory(new CellRenderer());

        leftFileTable.setItems(leftFileList);
        rightFileTable.setItems(rightFileList);
    }

    class CellRenderer implements Callback {

        FileUnit row;
        Tooltip tooltip;

        @Override
        public TableCell<FileUnit, String> call(Object param) {
            return new TableCell<FileUnit, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    row = (FileUnit) getTableRow().getItem();

                    if (item != null && row != null) {

                        setText(item);
                        tooltip = new Tooltip(row.getFileTypeDescription());
                        setTooltip(tooltip);

                        if (row.isBlank())
                            setStyle("-fx-alignment: center; -fx-font-weight: bold");
                        else {
                            if (row.isDirectory())
                                setStyle("-fx-alignment: center-left; -fx-font-weight: bold");
                            if (row.isDirectory() && row.getAttributesLetters().contains("H"))
                                setStyle("-fx-text-fill: darkgray; -fx-alignment: center-left; -fx-font-weight: bold");
                            if (row.isDirectory() && row.getAttributesLetters().contains("R"))
                                setStyle("-fx-text-fill: orangered; -fx-alignment: center-left; -fx-font-weight: bold");

                            if (row.isFile())
                                setStyle("-fx-alignment: center-left; -fx-font-weight: normal");
                            if (row.isFile() && row.getAttributesLetters().contains("H"))
                                setStyle("-fx-text-fill: darkgray; -fx-alignment: center-left; -fx-font-weight: normal");
                            if (row.isFile() && row.getAttributesLetters().contains("R"))
                                setStyle("-fx-text-fill: orangered; -fx-alignment: center-left; -fx-font-weight: normal");
                            if (row.isFile() && row.isExecutable())
                                setStyle("-fx-text-fill: green; -fx-alignment: center-left; -fx-font-weight: normal; -fx-font-style: italic");
                        }
                    }
                    else {
                        setText(null);
                    }
                }
            };
        }
    }

    private void createDrivesPanels() {

        if (leftDrivesButtons.getButtons().size() > 0) {
            leftDrivesButtons.getButtons().remove(0, leftDrivesButtons.getButtons().size());
            leftDriveList.removeAllElements();
        }

        if (rightDrivesButtons.getButtons().size() > 0) {
            rightDrivesButtons.getButtons().remove(0, rightDrivesButtons.getButtons().size());
            rightDriveList.removeAllElements();
        }

        for (FileUnit drive : leftDriveList) {
            DriveButton button = new DriveButton(drive);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    createFileList(button.getDrive().getAbsoluteFile(), -1);
                    pathPanel.setFile(button.getDrive());
                    currentFile = button.getDrive();
                    currentLeftDir = button.getDrive();
                }
            });

            leftButtons.add(button);
        }

        for (FileUnit drive : rightDriveList) {
            DriveButton button = new DriveButton(drive);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    createFileList(button.getDrive().getAbsoluteFile(), 1);
                    pathPanel.setFile(button.getDrive());
                    currentFile = button.getDrive();
                    currentRightDir = button.getDrive();
                }
            });

            rightButtons.add(button);
        }

        FTPButton leftFTPbutton = new FTPButton();
        leftFTPbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connectToFTP(LEFT_PANEL);
                pathPanel.setFile(leftFileList.get(1));
            }
        });

        FTPButton rightFTPbutton = new FTPButton();
        rightFTPbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connectToFTP(RIGHT_PANEL);
                pathPanel.setFile(rightFileList.get(1));
            }
        });

        leftDrivesButtons.getButtons().addAll(leftButtons);
        leftDrivesButtons.getButtons().add(leftFTPbutton);

        rightDrivesButtons.getButtons().addAll(rightButtons);
        rightDrivesButtons.getButtons().add(rightFTPbutton);
    }

    private void connectToFTP(int activePanel) {

        String server;// = messenger.ShowInputMessage("FTP server address:");
        String login;// = messenger.ShowInputMessage("FTP server User name:");
        String password;// = messenger.ShowInputMessage("User password:");

        server = "ftp.intel.com";
        login = "anonymous";
        password = "noname@noname.com";

        /*server = "58.56.159.37";
        login = "pkuser";
        password = "PK-DW,123";*/

        try {
            ftpController.login(server, login, password);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        createFTPFileList("", activePanel);
    }
}