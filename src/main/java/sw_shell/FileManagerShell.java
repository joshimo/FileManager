package sw_shell;

import file.controller.*;
import file.utility.*;
import sw_shell.swutility.*;
import sw_shell.messanger.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/** File manager view sw_shell */
public class FileManagerShell extends JFrame{

    final static int LEFT_PANEL = -1;
    final static int RIGHT_PANEL = 1;

    protected static int screenWidth;
    protected static int screenHeight;

    private int windowWidth;
    private int windowHeight;

    private Dimension dim;

    private int leftFilePanelX0;
    private int leftFilePanelY0;
    private int rightFilePanelX0;
    private int rightFilePanelY0;
    private int previewPanelX0;
    private int previewPanelY0;
    private int currentPathPanelX0;
    private int currentPathPanelY0;

    private int leftDrivesButtonsPanelX0;
    private int leftDrivesButtonsPanelY0;
    private int rightDrivesButtonsPanelX0;
    private int rightDrivesButtonsPanelY0;
    private int preOptionsPanelX0;
    private int preOptionsPanelY0;
    private int fButtonPanelX0;
    private int fButtonPanelY0;

    private int filePanelL;
    private int filePanelH;
    private int previewPanelL;
    private int previewPanelH;
    private int preOptionsPanelL;
    private int preOptionsPanelH;
    private int drivesButtonsPanelL;
    private int drivesButtonsPanelH;
    private int currentPathPanelL;
    private int currentPathPanelH;
    private int fButtonPanelL;
    private int fButtonPanelH;

    private static int activePanel = 0;

    private boolean showPreview = false;
    private boolean showHTML = false;
    private boolean showZIP = true;
    private boolean showDIR = true;
    private boolean showTXT = true;

    protected static Controller controller = FileController.getInstance();
    protected static Utility utility = FileUtility.getInstance();

    protected Messenger messenger = MessageSystem.getInstance();

    private DriveButtonsPanel leftDriveButtons;
    private DriveButtonsPanel rightDriveButtons;

    private PreviewOptionsPanel previewOptionsPanel;

    private PreviewPanel previewPanel;

    protected static FileListPanel rightFileListPanel;
    protected static FileListPanel leftFileListPanel;

    private FunctionalButtonsPanel functionalButtonsPanel;

    protected static ParentPathPanel parentPathPanel = ParentPathPanel.getInstance();

    private SearchResultWindow searchResultWindow;

    final static String iconFolderPath = "src\\main\\resources\\icons\\";

    {
        dim = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) dim.getWidth();
        screenHeight = (int) dim.getHeight();
        windowWidth = (int) (0.8 * screenWidth);
        windowHeight = (int) (0.8 * screenHeight);
    }

    public static void setActivePanel(int panelNum) {
        activePanel = panelNum;
    }

    public static int getActivePanel() {
        return activePanel;
    }

    /** Constructor */
    public FileManagerShell() {
        super("File Manager");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setLocation(((screenWidth - windowWidth) / 2), ((screenHeight - windowHeight) / 2));
        this.initWindow(windowWidth, windowHeight, false);
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                windowWidth = e.getComponent().getWidth();
                windowHeight = e.getComponent().getHeight();
                initWindow(windowWidth, windowHeight, true);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
        this.setVisible(true);

        searchResultWindow = new SearchResultWindow(screenWidth, screenHeight);
        searchResultWindow.Hide();
    }

    private void initWindow(int windowWidth, int windowHeight, boolean resized) {

        this.setSize(windowWidth, windowHeight);

        if (!resized) {
            leftDriveButtons = new DriveButtonsPanel(LEFT_PANEL, controller, parentPathPanel, messenger);
            rightDriveButtons = new DriveButtonsPanel(RIGHT_PANEL, controller, parentPathPanel, messenger);
            previewPanel = new PreviewPanel();
            previewOptionsPanel = new PreviewOptionsPanel();
            rightFileListPanel = new FileListPanel(RIGHT_PANEL, controller, parentPathPanel, messenger);
            leftFileListPanel = new FileListPanel(LEFT_PANEL, controller, parentPathPanel, messenger);
            functionalButtonsPanel = new FunctionalButtonsPanel();
        }

        if (showPreview) {
            filePanelH = windowHeight - 185;
            filePanelL = (int) (windowWidth * 0.3) - 10;

            previewPanelH = windowHeight - 185;
            previewPanelL = (int) (windowWidth * 0.4 - 15);
        }
        else {
            showPreview = false;
            previewPanel.setVisible(false);

            filePanelH = windowHeight - 185;
            filePanelL = (int) (windowWidth * 0.5) - 20;
        }

        drivesButtonsPanelH = 60;
        drivesButtonsPanelL = (int) (windowWidth * 0.3) - 10;

        preOptionsPanelH = 60;
        preOptionsPanelL = (int) (windowWidth * 0.4 - 15); //PreviewPanelL;

        fButtonPanelH = 40;
        fButtonPanelL = windowWidth - 25;

        currentPathPanelH = 25;
        currentPathPanelL = windowWidth - 25;

        leftFilePanelX0 = 5;
        leftFilePanelY0 = 70;

        rightFilePanelX0 = windowWidth - filePanelL - 20;
        rightFilePanelY0 = 70;

        previewPanelX0 = filePanelL + 10;
        previewPanelY0 = 70;

        leftDrivesButtonsPanelX0 = 5;
        leftDrivesButtonsPanelY0 = 5;

        preOptionsPanelX0 = (int) (windowWidth * 0.3);
        preOptionsPanelY0 = 5;

        rightDrivesButtonsPanelX0 = windowWidth - drivesButtonsPanelL - 20;
        rightDrivesButtonsPanelY0 = 5;

        fButtonPanelX0 = 5;
        fButtonPanelY0 = windowHeight - 110;

        currentPathPanelX0 = 5;
        currentPathPanelY0 = windowHeight - 65;

        leftDriveButtons.setBounds(leftDrivesButtonsPanelX0, leftDrivesButtonsPanelY0, drivesButtonsPanelL, drivesButtonsPanelH);
        rightDriveButtons.setBounds(rightDrivesButtonsPanelX0, rightDrivesButtonsPanelY0, drivesButtonsPanelL, drivesButtonsPanelH);

        leftFileListPanel.setBounds(leftFilePanelX0, leftFilePanelY0, filePanelL, filePanelH);
        rightFileListPanel.setBounds(rightFilePanelX0, rightFilePanelY0, filePanelL, filePanelH);

        previewOptionsPanel.setBounds(preOptionsPanelX0, preOptionsPanelY0, preOptionsPanelL, preOptionsPanelH);

        previewPanel.setBounds(previewPanelX0, previewPanelY0, previewPanelL, previewPanelH);

        functionalButtonsPanel.setBounds(fButtonPanelX0, fButtonPanelY0, fButtonPanelL, fButtonPanelH);
        functionalButtonsPanel.setBorder(new TitledBorder(""));

        parentPathPanel.setBounds(currentPathPanelX0, currentPathPanelY0, currentPathPanelL, currentPathPanelH);
        parentPathPanel.setFont(new Font("Dialog", Font.BOLD, 14));
        parentPathPanel.setBorder(new TitledBorder(""));

        this.add(previewPanel);
        this.add(leftDriveButtons);
        this.add(rightDriveButtons);
        this.add(previewOptionsPanel);
        this.add(rightFileListPanel);
        this.add(leftFileListPanel);
        this.add(functionalButtonsPanel);
        this.add(parentPathPanel);
    }

    private class PreviewOptionsPanel extends JPanel {
        JCheckBox previewCheckBox = new JCheckBox("Show preview");
        JCheckBox previewHTMLCheckBox = new JCheckBox("Show HTML");
        JCheckBox previewZIPCheckBox = new JCheckBox("Show ZIP");
        JCheckBox previewDIRCheckBox = new JCheckBox("Show DIR");
        JCheckBox previewTXTCheckBox = new JCheckBox("Show TXT");

        public PreviewOptionsPanel() {

            Settable CheckBox = (b) -> {
                previewHTMLCheckBox.setEnabled(b);
                previewZIPCheckBox.setEnabled(b);
                previewDIRCheckBox.setEnabled(b);
                previewTXTCheckBox.setEnabled(b);
            };

            previewCheckBox.setSelected(showPreview);
            previewHTMLCheckBox.setSelected(showHTML);
            previewZIPCheckBox.setSelected(showZIP);
            previewDIRCheckBox.setSelected(showDIR);
            previewTXTCheckBox.setSelected(showTXT);

            CheckBox.Set(showPreview);

            super.add(previewCheckBox);
            super.add(previewHTMLCheckBox);
            super.add(previewZIPCheckBox);
            super.add(previewDIRCheckBox);
            super.add(previewTXTCheckBox);

            previewCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {

                    if (previewCheckBox.isSelected()) {
                        showPreview = true;
                        previewPanel.setVisible(showPreview);
                        CheckBox.Set(showPreview);
                    } else {
                        showPreview = false;
                        previewPanel.setVisible(showPreview);
                        CheckBox.Set(showPreview);
                    }

                    initWindow(windowWidth, windowHeight, true);
                }
            });

            previewHTMLCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (previewHTMLCheckBox.isSelected())
                        showHTML = true;
                    else
                        showHTML = false;
                }
            });

            previewZIPCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (previewZIPCheckBox.isSelected())
                        showZIP = true;
                    else
                        showZIP = false;
                }
            });

            previewDIRCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (previewDIRCheckBox.isSelected())
                        showDIR = true;
                    else
                        showDIR = false;
                }
            });

            previewTXTCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (previewTXTCheckBox.isSelected())
                        showTXT = true;
                    else
                        showTXT = false;
                }
            });

            super.setVisible(true);
            super.setBorder(new TitledBorder("Preview Options"));
        }
    }

    private class FunctionalButtonsPanel extends JPanel {
        ImageIcon refresh = new ImageIcon(iconFolderPath + "refresh.png");
        ImageIcon newDir = new ImageIcon(iconFolderPath + "newfolder.png");
        ImageIcon rename = new ImageIcon(iconFolderPath + "rename.png");
        ImageIcon copy = new ImageIcon(iconFolderPath + "copy.png");
        ImageIcon delete = new ImageIcon(iconFolderPath + "delete1.png");
        ImageIcon search = new ImageIcon(iconFolderPath + "search.png");
        ImageIcon tree = new ImageIcon(iconFolderPath + "tree.png");
        ImageIcon viewer = new ImageIcon(iconFolderPath + "viewer.png");

        JButton refreshButton;
        JButton newDirButton;
        JButton renameButton;
        JButton copyButton;
        JButton deleteButton;
        JButton searchButton;
        JButton treeButton;
        JButton viewerButton;

        File source = new File("");
        File destination = new File("");

        private FunctionalButtonsPanel() {
            refreshButton = new JButton("Refresh");
            refreshButton.setIcon(refresh);

            newDirButton = new JButton("Create New Directory");
            newDirButton.setIcon(newDir);
            newDirButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newDirName = messenger.ShowInputMessage("New directory name:");
                    File newDirectory = null;

                    if (activePanel == LEFT_PANEL)
                        newDirectory = new File(leftFileListPanel.getCurrentDirectoryPath() + File.separator + newDirName);
                    if (activePanel == RIGHT_PANEL)
                        newDirectory = new File(rightFileListPanel.getCurrentDirectoryPath() + File.separator + newDirName);

                    controller.createNewDirectory(newDirectory);

                    if (activePanel == LEFT_PANEL) leftFileListPanel.refresh();
                    if (activePanel == RIGHT_PANEL) rightFileListPanel.refresh();
                }
            });

            renameButton = new JButton("Rename");
            renameButton.setIcon(rename);
            renameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newName = messenger.ShowInputMessage("New name:");
                    File oldFile = null;

                    if (activePanel == LEFT_PANEL)
                        oldFile = new File(leftFileListPanel.getCurrentDirectoryPath() + File.separator + leftFileListPanel.getCurrentFileName());
                    if (activePanel == RIGHT_PANEL)
                        oldFile = new File(rightFileListPanel.getCurrentDirectoryPath() + File.separator + rightFileListPanel.getCurrentFileName());

                    controller.renameFileOrDirectory(newName, oldFile);

                    if (activePanel == LEFT_PANEL) leftFileListPanel.refresh();
                    if (activePanel == RIGHT_PANEL) rightFileListPanel.refresh();
                }
            });

            copyButton = new JButton("Copy");
            copyButton.setIcon(copy);
            copyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if (activePanel == LEFT_PANEL) {
                        source = new File(leftFileListPanel.getCurrentDirectoryPath() + File.separator + leftFileListPanel.getCurrentFileName());
                        destination = new File(rightFileListPanel.getCurrentDirectoryPath() + File.separator + leftFileListPanel.getCurrentFileName());
                    }
                    if (activePanel == RIGHT_PANEL) {
                        source = new File(rightFileListPanel.getCurrentDirectoryPath() + File.separator + rightFileListPanel.getCurrentFileName());
                        destination = new File(leftFileListPanel.getCurrentDirectoryPath() + File.separator + rightFileListPanel.getCurrentFileName());
                    }

                    int confirm = messenger.ConfirmMessage("Copy\n" + source.getAbsolutePath() + "\nto\n" + destination.getAbsolutePath());

                    if (confirm == Messenger.OK_OPTION) {
                        new Thread() {
                            public void run() {
                                controller.copyFileOrDirectory(source, destination);
                                if (activePanel == LEFT_PANEL) rightFileListPanel.refresh();
                                if (activePanel == RIGHT_PANEL) leftFileListPanel.refresh();
                            }
                        }.start();
                    }
                }
            });

            deleteButton = new JButton("Delete");
            deleteButton.setIcon(delete);
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if (activePanel == LEFT_PANEL)
                        source = new File(leftFileListPanel.getCurrentDirectoryPath() + File.separator + leftFileListPanel.getCurrentFileName());
                    if (activePanel == RIGHT_PANEL)
                        source = new File(rightFileListPanel.getCurrentDirectoryPath() + File.separator + rightFileListPanel.getCurrentFileName());

                    new Thread() {
                        public void run() {
                            controller.deleteFileOrDirectory(source);

                            if (activePanel == LEFT_PANEL) leftFileListPanel.refresh();
                            if (activePanel == RIGHT_PANEL) rightFileListPanel.refresh();
                        }
                    }.start();
                }
            });

            searchButton = new JButton("Search");
            searchButton.setIcon(search);

            treeButton = new JButton("Show Tree");
            treeButton.setIcon(tree);

            viewerButton = new JButton("Open Picture old.Viewer");
            viewerButton.setIcon(viewer);

            super.setLayout(new FlowLayout());
            super.add(refreshButton);
            super.add(newDirButton);
            super.add(renameButton);
            super.add(copyButton);
            super.add(deleteButton);
            super.add(searchButton);
            super.add(treeButton);
            super.add(viewerButton);
        }
    }

    public static void main(String[] args) {
        new FileManagerShell();
    }
}