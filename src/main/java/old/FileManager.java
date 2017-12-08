package old; /**
 * Created by y.golota on 11.10.2016.
 */

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import sun.util.calendar.CalendarUtils;

public class FileManager {

    double WindowWidth;
    double WindowHeight;

    Dimension dim;

    int LeftFilePanelX0;
    int LeftFilePanelY0;
    int RightFilePanelX0;
    int RightFilePanelY0;
    int PreviewPanelX0;
    int PreviewPanelY0;
    int CurrentPathPanelX0;
    int CurrentPathPanelY0;

    int LeftDrivesButtonsPanelX0;
    int LeftDrivesButtonsPanelY0;
    int RightDrivesButtonsPanelX0;
    int RightDrivesButtonsPanelY0;
    int PreOptionsPanelX0;
    int PreOptionsPanelY0;
    int FButtonPanelX0;
    int FButtonPanelY0;

    int FilePanelL;
    int FilePanelH;
    int PreviewPanelL;
    int PreviewPanelH;
    int PreOptionsPanelL;
    int PreOptionsPanelH;
    int DrivesButtonsPanelL;
    int DrivesButtonsPanelH;
    int CurrentPathPanelL;
    int CurrentPathPanelH;
    int FButtonPanelL;
    int FButtonPanelH;

    static String[] CurrentPath = new String[2];
    String[] CurrentSelection = new String[2];

    String ftpAddress = "";
    String defaultFTPuser = "anonymous";
    String defaultFTPpass = "noname@noname.com";

    static String iconFolderPath = "src\\main\\resources\\icons\\";

    final int BOTH = 2;

    File[] Folder = new File[2];

    FTPClient ftpClient = new FTPClient();

    boolean ShowPreview = false;
    boolean ShowHTML = false;
    boolean ShowZIP = true;
    boolean ShowDIR = true;
    boolean ShowTXT = true;

    boolean[] FTPmode = new boolean[2];

    JFrame Window;

    JTable[] FileTable = new JTable[2];

    PreviewPanel Preview = new PreviewPanel();

    JTextPane CurrentPathPane = new JTextPane();

    DriveButtons LeftDrivesButtonPanel;
    DriveButtons RightDrivesButtonPanel;

    PreviewOptionsPanel PreOptionsPanel = new PreviewOptionsPanel();

    FilePanel LeftFilePanel;
    FilePanel RightFilePanel;

    FuctionalButtons FunctionButtonsPanel = new FuctionalButtons();

    InfoWindow info;

    Refreshable Panels = (n) -> {

        if (n == 0) {
            CreateFileTables(n, FTPmode[n]);
            Window.remove(LeftFilePanel);
            LeftFilePanel = new FilePanel(FileTable[0], 0);
        }
        if (n == 1) {
            CreateFileTables(n, FTPmode[n]);
            Window.remove(RightFilePanel);
            RightFilePanel = new FilePanel(FileTable[1], 1);
        }
        if (n == BOTH) {
            CreateFileTables(0, FTPmode[0]);
            CreateFileTables(1, FTPmode[1]);
            Window.remove(LeftFilePanel);
            Window.remove(RightFilePanel);
            LeftFilePanel = new FilePanel(FileTable[0], 0);
            RightFilePanel = new FilePanel(FileTable[1], 1);
        }
        Window.repaint();
        Window.revalidate();
        InitWindow();
    };

    public FileManager() {

        Window = new JFrame("File Manager");

        CurrentPath[0] = "D:\\";
        CurrentPath[1] = "D:\\";

        Folder[0] = new File(CurrentPath[0]);
        Folder[1] = new File(CurrentPath[1]);

        CreateFileTables(0, false);
        CreateFileTables(1, false);

        LeftFilePanel = new FilePanel(FileTable[0], 0);
        RightFilePanel = new FilePanel(FileTable[1], 1);

        LeftDrivesButtonPanel = new DriveButtons(0);
        RightDrivesButtonPanel = new DriveButtons(1);

        CurrentPathPane.setText(Folder[0].getAbsolutePath());
        CurrentPathPane.setEditable(false);

        dim = Toolkit.getDefaultToolkit().getScreenSize();

        WindowWidth = 0.92 * dim.getWidth();
        WindowHeight = 0.92 * dim.getHeight();

        Window.setResizable(true);
        Window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Window.setLayout(null);
        Window.setSize((int) WindowWidth,(int) WindowHeight);

        info= new InfoWindow(null, 480, 160);

        InitWindow();
        Window.setVisible(true);
        Window.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (Window.getWidth() < 1024) {
                    Window.setResizable(false);
                    Window.setSize(1280, Window.getHeight());
                    InitWindow();
                    Window.revalidate();
                    Window.setResizable(true);
                }
                if (Window.getHeight() < 300) {
                    Window.setResizable(false);
                    Window.setSize(Window.getWidth(), 400);
                    InitWindow();
                    Window.revalidate();
                    Window.setResizable(true);
                }
                InitWindow();
            }

            @Override
            public void componentMoved(ComponentEvent e) { }

            @Override
            public void componentShown(ComponentEvent e) { }

            @Override
            public void componentHidden(ComponentEvent e) { }
        });
    }

    private void CreateFileTables(int n, boolean ftpMode) {
        String[] header = new String[] {"File name", "Ext", "Size", "Date", "Attrib"};
        String[][] data;

        if (!ftpMode) data = getSortedFolderList(Folder[n]);
        else data = getSortedFolderList(ftpClient);

        DefaultTableModel dtm = new DefaultTableModel(data, header) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        TableRenderer Renderer = new TableRenderer(data, n);

        FileTable[n] = new JTable(dtm) {
            @Override
            public String getToolTipText(MouseEvent event) {
                return (String) getValueAt(rowAtPoint(event.getPoint()), columnAtPoint(event.getPoint()));
            }
        };
        FileTable[n].setDefaultRenderer(Object.class, Renderer);
    }

    private void ResizeFilePanel(int n) {
        FileTable[n].setAutoResizeMode(0);
        FileTable[n].getColumnModel().getColumn(0).setPreferredWidth((int) (0.65 * (FilePanelL - 16)));
        FileTable[n].getColumnModel().getColumn(1).setPreferredWidth((int) (0.07 * (FilePanelL - 16)));
        FileTable[n].getColumnModel().getColumn(2).setPreferredWidth((int) (0.08 * (FilePanelL - 16)));
        FileTable[n].getColumnModel().getColumn(3).setPreferredWidth((int) (0.15 * (FilePanelL - 16)));
        FileTable[n].getColumnModel().getColumn(4).setPreferredWidth((int) (0.05 * (FilePanelL - 16)));
        FileTable[n].setRowHeight(36);
    }

    private void InitWindow() {

        WindowWidth = Window.getWidth();
        WindowHeight = Window.getHeight();

        if (ShowPreview) {
            FilePanelH = (int) WindowHeight - 185;
            FilePanelL = (int) (WindowWidth * 0.3) - 10;

            PreviewPanelH = (int) WindowHeight - 185;
            PreviewPanelL = (int) (WindowWidth * 0.4 - 15);
        }
        else {
            ShowPreview = false;
            Preview.panel.setVisible(false);

            FilePanelH = (int) WindowHeight - 185;
            FilePanelL = (int) (WindowWidth * 0.5) - 20;
        }

        DrivesButtonsPanelH = 60;
        DrivesButtonsPanelL = (int) (WindowWidth * 0.3) - 10; //FilePanelL;

        PreOptionsPanelH = 60;
        PreOptionsPanelL = (int) (WindowWidth * 0.4 - 15); //PreviewPanelL;

        FButtonPanelH = 40;
        FButtonPanelL = (int) WindowWidth - 25;

        CurrentPathPanelH = 25;
        CurrentPathPanelL = (int) WindowWidth - 25;

        LeftFilePanelX0 = 5;
        LeftFilePanelY0 = 70;

        RightFilePanelX0 = (int) WindowWidth - FilePanelL - 20;
        RightFilePanelY0 = 70;

        PreviewPanelX0 = FilePanelL + 10;
        PreviewPanelY0 = 70;

        LeftDrivesButtonsPanelX0 = 5;
        LeftDrivesButtonsPanelY0 = 5;

        PreOptionsPanelX0 = (int) (WindowWidth * 0.3);
        PreOptionsPanelY0 = 5;

        RightDrivesButtonsPanelX0 = (int) WindowWidth - DrivesButtonsPanelL - 20;
        RightDrivesButtonsPanelY0 = 5;

        FButtonPanelX0 = 5;
        FButtonPanelY0 = (int) WindowHeight - 110;

        CurrentPathPanelX0 = 5;
        CurrentPathPanelY0 = (int) WindowHeight - 65;

        LeftDrivesButtonPanel.setBounds(LeftDrivesButtonsPanelX0, LeftDrivesButtonsPanelY0, DrivesButtonsPanelL, DrivesButtonsPanelH);
        RightDrivesButtonPanel.setBounds(RightDrivesButtonsPanelX0, RightDrivesButtonsPanelY0, DrivesButtonsPanelL, DrivesButtonsPanelH);

        LeftFilePanel.setBounds(LeftFilePanelX0, LeftFilePanelY0, FilePanelL, FilePanelH);
        RightFilePanel.setBounds(RightFilePanelX0, RightFilePanelY0, FilePanelL, FilePanelH);

        ResizeFilePanel(0);
        ResizeFilePanel(1);

        PreOptionsPanel.setBounds(PreOptionsPanelX0, PreOptionsPanelY0, PreOptionsPanelL, PreOptionsPanelH);

        Preview.panel.setBounds(PreviewPanelX0, PreviewPanelY0, PreviewPanelL, PreviewPanelH);

        FunctionButtonsPanel.setBounds(FButtonPanelX0, FButtonPanelY0, FButtonPanelL, FButtonPanelH);
        FunctionButtonsPanel.setBorder(new TitledBorder(""));

        CurrentPathPane.setBounds(CurrentPathPanelX0, CurrentPathPanelY0, CurrentPathPanelL, CurrentPathPanelH);
        CurrentPathPane.setFont(new Font("Dialog", Font.BOLD, 14));
        CurrentPathPane.setBorder(new TitledBorder(""));

        Window.add(Preview.panel);
        Window.add(LeftDrivesButtonPanel);
        Window.add(RightDrivesButtonPanel);
        Window.add(PreOptionsPanel);
        Window.add(RightFilePanel);
        Window.add(LeftFilePanel);
        Window.add(FunctionButtonsPanel);
        Window.add(CurrentPathPane);
    }

    private String[][] getSortedFolderList(File folderPath) {
        File[] folderList = folderPath.listFiles();
        String[][] FileList = new String[folderList.length + 1][5];

        if (folderList != null) {
            int j = 1;
            FileList[0][0] = "...<UP>...";
            FileList[0][1] = "";
            FileList[0][2] = "";
            FileList[0][3] = "";
            FileList[0][4] = "";

            Dateble GetDate = (File file) -> {
                Date date = new Date(file.lastModified()) {
                    @SuppressWarnings("deprecation")
                    @Override
                    public int getYear() {
                        return super.getYear() + 1900;
                    }
                    @SuppressWarnings("deprecation")
                    @Override
                    public String toString() {
                        StringBuilder sb = new StringBuilder(28);
                        CalendarUtils.sprintf0d(sb, super.getHours(), 2).append(':');   // HH
                        CalendarUtils.sprintf0d(sb, super.getMinutes(), 2).append(':'); // mm
                        CalendarUtils.sprintf0d(sb, super.getSeconds(), 2).append(", "); // ss
                        CalendarUtils.sprintf0d(sb, super.getDay(), 2).append('.'); //day
                        CalendarUtils.sprintf0d(sb, super.getMonth(), 2).append('.'); //month
                        sb.append(this.getYear());  // yyyy
                        return sb.toString();
                    }
                };
                return date.toString();
            };

            for (File file : folderList) {

                FileList[j][3] = GetDate.GetDate(file);

                if (file.isDirectory()) {
                    FileList[j][0] = file.getName() + "\\";
                    FileList[j][1] = "DIR";

                    FileList[j][4] = "";
                    if (!file.canWrite()) FileList[j][4] = FileList[j][4] + "R";
                    if (file.isHidden()) FileList[j][4] = FileList[j][4] + "H";
                    j ++;
                }
            }

            for (File file : folderList) {

                if (file.isFile()) {
                    FileList[j][0] = file.getName();
                    String extension = FileList[j][0].substring(FileList[j][0].lastIndexOf('.') + 1, FileList[j][0].length());
                    FileList[j][1] = extension;
                    FileList[j][3] = GetDate.GetDate(file);

                    FileList[j][4] = "";
                    if (!file.canWrite()) FileList[j][4] = FileList[j][4] + "R";
                    if (file.isHidden()) FileList[j][4] = FileList[j][4] + "H";

                    if (file.length() < 1024)
                        FileList[j][2] = file.length() + "";
                    if (file.length() >= 1024)
                        FileList[j][2] = (double) Math.round((double) 10 * file.length() / 1024) / 10 + "k";
                    if (file.length() >= (double) 1024 * 1024)
                        FileList[j][2] = (double) Math.round((double) 10 * file.length() / (1024 * 1024)) / 10 + "M";
                    if (file.length() >= (double) 1024 * 1024 * 1024)
                        FileList[j][2] = (double) Math.round((double) 10 * file.length() / (1024 * 1024 * 1024)) / 10 + "G";
                    j ++;
                }
            }
        }
        return FileList;
    }

    private String[][] getSortedFolderList(FTPClient ftpClient) {

        FTPFile[] files = null;

        try {
            files = ftpClient.listFiles();
        }
        catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "IOException in private String[] getSortedFolderList(FTPClient ftpClient)");
        }

        String[][] FileList = new String[files.length + 1][5];
        if (files.length > 0) {
            int j = 1;
            FileList[0][0] = "...< UP >...";
            FileList[0][1] = "";
            FileList[0][2] = "";
            FileList[0][3] = "";
            FileList[0][4] = "";

            for (int s = 0; s < files.length; s ++) {
                if (files[s].isDirectory()) {
                    FileList[j][0] = files[s].getName();
                    FileList[j][1] = "ftpDIR";
                    FileList[j][2] = "";
                    FileList[j][3] = "";
                    FileList[j][4] = "";
                    j ++;
                }
            }

            for (int s = 0; s < files.length; s ++) {
                if (files[s].isFile()) {
                    FileList[j][0] = files[s].getName();
                    FileList[j][1] = FileList[j][0].substring(FileList[j][0].lastIndexOf('.') + 1, FileList[j][0].length());
                    long size = files[s].getSize();
                    if (size < 1024)
                        FileList[j][2] = files[s].getSize() + "";
                    if (size >= 1024)
                        FileList[j][2] = (double) Math.round((double) 10 * size / 1024) / 10 + "k";
                    if (size >= (double) 1024 * 1024)
                        FileList[j][2] = (double) Math.round((double) 10 * size / (1024 * 1024)) / 10 + "M";
                    if (size >= (double) 1024 * 1024 * 1024)
                        FileList[j][2] = (double) Math.round((double) 10 * size / (1024 * 1024 * 1024)) / 10 + "G";
                    FileList[j][3] = "";
                    FileList[j][4] = "";
                    j ++;
                }
            }
        }

        return FileList;
    }

    public class FilePanel extends JScrollPane {

        public FilePanel(JTable FolderList, int n) {

            super(FolderList);

            FolderList.setRowSelectionInterval(0,0);

            FolderList.addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (e.getClickCount() == 1) {
                        try {
                            CurrentSelection[n] = FolderList.getValueAt(FolderList.getSelectedRow(), 0).toString();
                        } catch (NullPointerException e1) { }
                        CurrentPathPane.setText(CurrentPath[n] + CurrentSelection[n]);
                        Preview.Preview(CurrentPath[n] + CurrentSelection[n]);
                    }

                    if (e.getClickCount() == 2) {
                        if (FolderList.getSelectedRow() == 0)
                            GoToParentDirectory(n);
                        else {
                            CurrentSelection[n] = FolderList.getValueAt(FolderList.getSelectedRow(), 0).toString() + "\\";
                            GoToSubdirectory(n);
                        }
                    }
                }
            });

            FolderList.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    int keycode = e.getKeyCode();
                    if (keycode == 10) {
                        CurrentSelection[n] = FolderList.getValueAt(FolderList.getSelectedRow(), FolderList.getSelectedColumn()).toString() + "\\";
                        if (FolderList.getSelectedRow() == 0)
                            GoToParentDirectory(n);
                        else
                            GoToSubdirectory(n);
                    }
                    else {
                        try {
                            if ((keycode == 40) & (FolderList.getSelectedRow() < FolderList.getRowCount() - 1)) CurrentSelection[n] = FolderList.getValueAt(FolderList.getSelectedRow() + 1, 0).toString();
                            if ((keycode == 38) & (FolderList.getSelectedRow() > 1)) CurrentSelection[n] = FolderList.getValueAt(FolderList.getSelectedRow() - 1, 0).toString();
                            if (keycode == 27) GoToParentDirectory(n);
                        }
                        catch (NullPointerException e1) {}
                        CurrentPathPane.setText(CurrentPath[n] + CurrentSelection[n]);
                        Preview.Preview(CurrentPath[n] + CurrentSelection[n]);
                    }
                }
            });
        }

        private void GoToParentDirectory(int n) {
            if (!FTPmode[n]) {
                if (CurrentPath[n].length() > 4) {
                    Folder[n] = new File(CurrentPath[n]);
                    CurrentPath[n] = Folder[n].getParent() + "\\";
                    Folder[n] = new File(CurrentPath[n]);
                    CurrentPathPane.setText(CurrentPath[n]);
                    Panels.Refresh(n);
                }
            }
            else {
                try {
                    ftpClient.changeToParentDirectory();
                    CreateFileTables(n, FTPmode[n]);
                    CurrentPath[n] = ftpAddress + "\\" + ftpClient.printWorkingDirectory() + "\\";
                    CurrentPathPane.setText(CurrentPath[n]);
                    Panels.Refresh(n);
                }
                catch (IOException ioe) {}
            }
        }

        private void GoToSubdirectory(int n) {
            if (!FTPmode[n]) {
                Folder[n] = new File(CurrentPath[n] + CurrentSelection[n]);
                if (Folder[n].isDirectory()) {
                    CurrentPath[n] = CurrentPath[n] + CurrentSelection[n];
                    CurrentPathPane.setText(CurrentPath[n]);
                    try {
                        Panels.Refresh(n);
                    }
                    catch (NullPointerException e2) { }
                }
                if (Folder[n].isFile()) {
                    try {
                        Desktop.getDesktop().open(Folder[n]);
                    }
                    catch (NullPointerException e21) { }
                    catch (IOException e22) {
                        JOptionPane.showMessageDialog(null, "Error opening unitmodel!");
                    }
                }
            }
            else {
                try { ftpClient.changeWorkingDirectory(CurrentSelection[n]); }
                catch (IOException ioe) {}
                CreateFileTables(n, FTPmode[n]);
                CurrentPath[n] = CurrentPath[n] + "\\" + CurrentSelection[n];
                CurrentPathPane.setText(CurrentPath[n]);
                Panels.Refresh(n);
            }
        }
    }

    private class DriveButtons extends JPanel {

        public DriveButtons(int n) {
            RefreshDriveButtons(n);
        }

        public void RefreshDriveButtons(int n) {
            super.removeAll();

            TitledBorder border = new TitledBorder("");
            if (n == 0) border.setTitle("Left Drives");
            if (n == 1) border.setTitle("Right Drives");

            File[] roots = File.listRoots();

            ImageIcon driveIcon = new ImageIcon(iconFolderPath + "disk.png");
            ImageIcon ftpIcon = new ImageIcon(iconFolderPath + "ftp.png");

            JButton[] DriveButtons = new JButton[roots.length];

            JButton ftp = new JButton("ftp://");
            ftp.setIcon(ftpIcon);

            ftp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FTPmode[n] = true;
                    ftpAddress = JOptionPane.showInputDialog("Enter ftp address:");
                    ftpClient = new FTPClient();
                    try {
                        ftpClient.connect(ftpAddress);

                        if (ftpClient.isConnected()) System.out.println("Connected to " + ftpAddress);

                        if (ftpClient.login(defaultFTPuser, defaultFTPpass))
                            System.out.println("Login in " + ftpAddress + " successfull");
                        else {
                            String FTPuser = JOptionPane.showInputDialog(null, "User name:");
                            String FTPpass = JOptionPane.showInputDialog(null, "Password:");
                            if (ftpClient.login(FTPuser, FTPpass)) System.out.println("Login in " + ftpAddress + " successfull");
                            else {
                                JOptionPane.showMessageDialog(null, "Wrong username or password! Connection failed");
                                ftpClient.disconnect();
                            }
                        }

                        ftpClient.enterLocalPassiveMode();

                        CreateFileTables(n, FTPmode[n]);
                        CurrentPath[n] = ftpAddress + "\\";
                        CurrentPathPane.setText(CurrentPath[n]);

                        Panels.Refresh(n);

                        if (!ftpClient.isConnected()) System.out.println("Disconnected...");
                    }

                    catch (UnknownHostException e50) {
                        JOptionPane.showMessageDialog(null, "Unknown host! Connection failed");
                    }

                    catch (IOException e52) {
                        JOptionPane.showMessageDialog(null, "Unknown host! Connection failed");
                        System.out.println("Exception: " + e52.toString());
                    }
                }
            });

            for (int i = 0; i < roots.length; i++) {
                String name = roots[i].toString();
                DriveButtons[i] = new JButton(name);
                DriveButtons[i].setIcon(driveIcon);
                int counter = i;
                DriveButtons[i].addActionListener( (e) -> {
                        FTPmode[n] = false;
                        File driveName = new File(name);
                        Folder[n] = driveName;
                        Panels.Refresh(n);
                        CurrentPath[n] = name;
                        CurrentPathPane.setText(name);
                });

                DriveButtons[i].setVisible(true);
                super.add(DriveButtons[i]);
            }

            super.add(ftp);

            super.setVisible(true);
            super.setBorder(border);
        }
    }

    private class PreviewOptionsPanel extends JPanel {

        public PreviewOptionsPanel() {
            JCheckBox PreviewCheckBox = new JCheckBox("Show preview");
            JCheckBox PreviewHTMLCheckBox = new JCheckBox("Show HTML");
            JCheckBox PreviewZIPCheckBox = new JCheckBox("Show ZIP");
            JCheckBox PreviewDIRCheckBox = new JCheckBox("Show DIR");
            JCheckBox PreviewTXTCheckBox = new JCheckBox("Show TXT");

            Settable CheckBox = (b) -> {
                PreviewHTMLCheckBox.setEnabled(b);
                PreviewZIPCheckBox.setEnabled(b);
                PreviewDIRCheckBox.setEnabled(b);
                PreviewTXTCheckBox.setEnabled(b);
            };

            PreviewCheckBox.setSelected(ShowPreview);
            PreviewHTMLCheckBox.setSelected(ShowHTML);
            PreviewZIPCheckBox.setSelected(ShowZIP);
            PreviewDIRCheckBox.setSelected(ShowDIR);
            PreviewTXTCheckBox.setSelected(ShowTXT);

            CheckBox.Set(ShowPreview);

            super.add(PreviewCheckBox);
            super.add(PreviewHTMLCheckBox);
            super.add(PreviewZIPCheckBox);
            super.add(PreviewDIRCheckBox);
            super.add(PreviewTXTCheckBox);

            PreviewCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {

                    if (PreviewCheckBox.isSelected()) {
                        ShowPreview = true;
                        Preview.panel.setVisible(ShowPreview);
                        CheckBox.Set(ShowPreview);
                    }
                    else {
                        ShowPreview = false;
                        Preview.panel.setVisible(ShowPreview);
                        CheckBox.Set(ShowPreview);
                    }

                    InitWindow();
                }
            });

            PreviewHTMLCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (PreviewHTMLCheckBox.isSelected())
                        ShowHTML = true;
                    else
                        ShowHTML = false;
                }
            });

            PreviewZIPCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (PreviewZIPCheckBox.isSelected())
                        ShowZIP = true;
                    else
                        ShowZIP = false;
                }
            });

            PreviewDIRCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (PreviewDIRCheckBox.isSelected())
                        ShowDIR = true;
                    else
                        ShowDIR = false;
                }
            });

            PreviewTXTCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (PreviewTXTCheckBox.isSelected())
                        ShowTXT = true;
                    else
                        ShowTXT = false;
                }
            });

            super.setVisible(true);
            super.setBorder(new TitledBorder("Preview Options"));
        }
    }

    private class PreviewPanel {

        JComponent panel;
        JEditorPane jta = new JEditorPane();

        public PreviewPanel() {
            panel = new JScrollPane(jta);
            jta.setEditable(false);
            panel.setVisible(ShowPreview);
            panel.setBorder(new TitledBorder("Preview"));
        }

        private void Preview(String filename) {
            File file = new File(filename);
            String extension;
            if (file.isFile()) {
                extension = filename.substring(filename.lastIndexOf('.') + 1, filename.length());
                switch (extension.toLowerCase()) {
                    case "java":
                    case "ini":
                    case "logEvent":
                    case "bat":
                    case "me":
                    case "nfo":
                    case "biz":
                    case "txt":
                        if (ShowTXT) {
                            jta.setForeground(Color.BLUE);
                            jta.setText(PreviewTXT(filename));
                            jta.setCaretPosition(0);
                        }
                        break;
                    case "zip":
                        if (ShowZIP) {
                            jta.setForeground(Color.LIGHT_GRAY);
                            jta.setText(PreviewZIP(filename));
                            jta.setCaretPosition(0);
                        }
                        break;
                    case "htm":
                    case "html":
                        if (ShowHTML) {
                            try {
                                jta.setPage("unitmodel:///" + filename);
                            } catch (IOException e) {}
                            jta.addHyperlinkListener(new HyperlinkListener() {
                                @Override
                                public void hyperlinkUpdate(HyperlinkEvent e) {

                                }
                            });
                        }
                        break;
                    case "url":
                        if (ShowHTML) {
                            try {
                                jta.setPage(filename);
                            }
                            catch (IOException e) {}
                        }
                        break;
                    default: jta.setText("");
                }

            }
            if ((file.isDirectory()) & ShowDIR) {
                try {
                    String list = "";
                    String[][] s = getSortedFolderList(file);
                    for (int c = 0; c < s.length; c ++)
                        list = list + "\n" + s[c][0];
                    jta.setForeground(Color.black);
                    jta.setText(list);
                    jta.setCaretPosition(0);
                }
                catch(NullPointerException e71) {
                    jta.setText("ACCESS DENIED!");
                }
            }
        }

        private String PreviewTXT(String filename) {
            String TXT = "";
            try {
                FileInputStream fis = new FileInputStream(filename);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                while(true) {
                    String line = reader.readLine();
                    if (!(line == null)) TXT = TXT + "\n" + line;
                    else break;
                }
                reader.close();
            }
            catch (FileNotFoundException e){}
            catch (Exception e){}

            return TXT;
        }

        private String PreviewZIP(String filename) {
            String TXT = "";
            File zipfile = new File(filename);

            try {
                ZipFile zf = new ZipFile(zipfile, Charset.forName("CP866"));
                Enumeration ziplist = zf.entries();
                while (ziplist.hasMoreElements()) {
                    ZipEntry f = (ZipEntry) ziplist.nextElement();
                    TXT = TXT + "\n" + f.getName();
                }
                zf.close();
            }
            catch (IOException e3) {}
            return TXT;
        }

        private String PreviewURL(String filename) {
            String TXT = "";
            try {
                FileInputStream fis = new FileInputStream(filename);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                while(true) {
                    String line = reader.readLine();
                    if (!(line == null)) TXT = TXT + "\n" + line;
                    else break;
                }
                reader.close();
            }
            catch (FileNotFoundException e){}
            catch (Exception e){}

            return TXT;
        }
    }

    private class SearchWindow extends JFrame {

        public SearchWindow(ArrayList<String> searchResult) {
            super("Search result");
            super.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            super.setSize(800, 600);

            Object[] searchResultList = searchResult.toArray();

            JList list = new JList(searchResultList);
            JScrollPane resultPane = new JScrollPane(list);

            list.addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (e.getClickCount() == 2) {
                        Folder[0] = new File (list.getSelectedValue().toString()).getParentFile();
                        CurrentPath[0] = Folder[0].getAbsolutePath() + "\\";
                        CurrentPathPane.setText(CurrentPath[0]);
                        Panels.Refresh(0);
                    }
                }
            });

            super.add(resultPane);
        }

        public void Show() {
            super.setVisible(true);
        }

        public void Hide() {
            super.setVisible(false);
        }
    }

    private class FuctionalButtons extends JPanel {

        double fCopyPercent = 0;
        double dCopyPercent = 0;
        long dirSize = 0;
        int dirFileNumber = 0;
        int currentFileNumber = 0;

        ArrayList<String> searchResult = new ArrayList<>();
        ArrayList<String> treeList = new ArrayList<>();

        public FuctionalButtons() {

            ImageIcon refresh = new ImageIcon(iconFolderPath + "refresh.png");
            ImageIcon newDir = new ImageIcon(iconFolderPath + "newfolder.png");
            ImageIcon rename = new ImageIcon(iconFolderPath + "rename.png");
            ImageIcon copy = new ImageIcon(iconFolderPath + "copy.png");
            ImageIcon delete = new ImageIcon(iconFolderPath + "delete1.png");
            ImageIcon search = new ImageIcon(iconFolderPath + "search.png");
            ImageIcon tree = new ImageIcon(iconFolderPath + "tree.png");
            ImageIcon viewer = new ImageIcon(iconFolderPath + "viewer.png");

            JButton RefreshButton = new JButton("Refresh");
            RefreshButton.setIcon(refresh);
            JButton NewDirButton = new JButton("Create New Directory");
            NewDirButton.setIcon(newDir);
            JButton RenameButton = new JButton("Rename");
            RenameButton.setIcon(rename);
            JButton CopyButton = new JButton("Copy");
            CopyButton.setIcon(copy);
            JButton DeleteButton = new JButton("Delete");
            DeleteButton.setIcon(delete);
            JButton SearchButton = new JButton("Search");
            SearchButton.setIcon(search);
            JButton TreeButton = new JButton("Show Tree");
            TreeButton.setIcon(tree);
            JButton ViewerButton = new JButton("Open Picture old.Viewer");
            ViewerButton.setIcon(viewer);

            Pathable NewDir = () -> {
                if (CurrentPathPane.getText().equals(CurrentPath[0] + CurrentSelection[0])) return CurrentPath[0];
                if (CurrentPathPane.getText().equals(CurrentPath[1] + CurrentSelection[1])) return CurrentPath[1];
                return "";
            };
            Pathable Rename = () -> {
                if (CurrentPathPane.getText().equals(CurrentPath[0] + CurrentSelection[0])) return CurrentPath[0] + CurrentSelection[0];
                if (CurrentPathPane.getText().equals(CurrentPath[1] + CurrentSelection[1])) return CurrentPath[1] + CurrentSelection[1];
                return "";
            };
            Pathable Delete = Rename;

            RefreshButton.addActionListener(
               (e) -> {
                    LeftDrivesButtonPanel.RefreshDriveButtons(0);
                    RightDrivesButtonPanel.RefreshDriveButtons(1);
                    Panels.Refresh(BOTH);
                });

            NewDirButton.addActionListener(
                (e) -> {
                    String name = JOptionPane.showInputDialog(null, "New directory name:");
                    String path = NewDir.DetectPath();

                    try {
                        if ((!path.isEmpty()) & (!name.isEmpty()) & (!name.contains("*")))
                            CreateNewDir(path + name);
                        else
                            JOptionPane.showMessageDialog(null, "Directory " + name + " was not created!");
                    }
                    catch (NullPointerException e2) {
                        JOptionPane.showMessageDialog(null, "Directory " + name + " was not created!");
                    }

                    Panels.Refresh(BOTH);
                });

            RenameButton.addActionListener(
               (e) -> {
                    try {
                        String newName = JOptionPane.showInputDialog(null, "New directory name:");
                        String path = Rename.DetectPath();

                        File oldFileName = new File(path);
                        File newFilename = new File(oldFileName.getParent() + "\\" + newName);

                        //System.out.println("Old unitmodel: " + path.toString());
                        //System.out.println("New unitmodel: " + oldFileName.getParent() + "\\" + newName);

                        if ((newName != null) & (!newName.isEmpty()) & (!newName.contains("*"))) oldFileName.renameTo(newFilename);
                        else {
                            if (oldFileName.isDirectory())
                                JOptionPane.showMessageDialog(null, "Directory was not renamed!");
                            if (oldFileName.isFile())
                                JOptionPane.showMessageDialog(null, "File was not renamed!");
                            }
                        }
                        catch (NullPointerException e1) {}

                    Panels.Refresh(BOTH);
            });

            CopyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String sourcePath = "";
                    String destinationPath = "";
                    if (!FTPmode[0] & !FTPmode[1]) {
                        if (CurrentPathPane.getText().equals(CurrentPath[0] + CurrentSelection[0])) {
                            sourcePath = CurrentPath[0] + CurrentSelection[0];
                            destinationPath = CurrentPath[1] + CurrentSelection[0];
                        }
                        if (CurrentPathPane.getText().equals(CurrentPath[1] + CurrentSelection[1])) {
                            sourcePath = CurrentPath[1] + CurrentSelection[1];
                            destinationPath = CurrentPath[0] + CurrentSelection[1];
                        }
                        if (new File(sourcePath).isFile()) {
                            int confirmation = JOptionPane.showConfirmDialog(null, "Copy unitmodel " + sourcePath + "\n" + "to " + destinationPath);
                            //System.out.println("confirmation = " + confirmation);
                            if (confirmation == JOptionPane.OK_OPTION) {
                                Thread copy = new FileCopyThread(sourcePath, destinationPath, false);
                                copy.start();
                            }
                        }
                        if (new File(sourcePath).isDirectory()) {
                            int confirmation = JOptionPane.showConfirmDialog(null, "Copy directory " + sourcePath + "\n" + "to " + destinationPath);
                            //System.out.println("confirmation = " + confirmation);
                            if (confirmation == JOptionPane.OK_OPTION) {
                                Thread copyDir = new DirectoryCopyThread(sourcePath, destinationPath, false);
                                copyDir.start();
                            }
                        }
                        //Panels.Refresh(BOTH);
                    }
                    else {
                        if (FTPmode[0] & (CurrentPath[0] + CurrentSelection[0]).equals(CurrentPathPane.getText())) {
                            sourcePath = CurrentSelection[0];
                            destinationPath = CurrentPath[1] + CurrentSelection[0];

                            int confirmation = JOptionPane.showConfirmDialog(null, "Copy unitmodel " + sourcePath + " from FTP " + ftpAddress + "\n" + "to " + destinationPath);
                            if (confirmation == JOptionPane.OK_OPTION) {
                                Thread copy = new FileCopyThread(sourcePath, destinationPath, true);
                                copy.start();
                                if (!copy.isAlive()) Panels.Refresh(1);
                            }
                        }
                        if (FTPmode[1] & (CurrentPath[1] + CurrentSelection[1]).equals(CurrentPathPane.getText())) {

                            sourcePath = CurrentSelection[1];
                            destinationPath = CurrentPath[0] + CurrentSelection[1];

                            int confirmation = JOptionPane.showConfirmDialog(null, "Copy unitmodel " + sourcePath + " from FTP " + ftpAddress + "\n" + "to " + destinationPath);
                            if (confirmation == JOptionPane.OK_OPTION) {
                                Thread copy = new FileCopyThread(sourcePath, destinationPath, true);
                                copy.start();
                                if (!copy.isAlive()) Panels.Refresh(0);
                            }
                        }
                    }
                }
            });

            DeleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String sourcePath = Delete.DetectPath();
                    boolean isDeleted = false;

                    File fileToDelete = new File(sourcePath);
                    if (fileToDelete.isFile()) {
                        int confirmation = JOptionPane.showConfirmDialog(null, "Delete unitmodel " + sourcePath);
                        if (confirmation == JOptionPane.OK_OPTION) isDeleted = DeleteFile(sourcePath);
                        if (isDeleted) JOptionPane.showMessageDialog(null, "File " + sourcePath + " was deleted!");
                        else JOptionPane.showMessageDialog(null, "File " + sourcePath + " was not deleted!");
                        Panels.Refresh(BOTH);
                    }
                    if (fileToDelete.isDirectory()) {
                        int confirmation = JOptionPane.showConfirmDialog(null, "Delete directory " + sourcePath);
                        //System.out.println("confirmation = " + confirmation);
                        if (confirmation == JOptionPane.OK_OPTION) {
                            Thread deleteDirectory = new DeleteDirectoryThread(sourcePath);
                            deleteDirectory.start();
                        }
                    }
                }
            });

            SearchButton.addActionListener(
                (e) -> {
                    String searchFilename = JOptionPane.showInputDialog("Filename or mask for search:");
                    if ((!searchFilename.isEmpty()) & !(searchFilename == null)) {
                        Thread searchFile = new SearchFileThread(searchFilename, CurrentPath[0]);
                        searchFile.start();
                    }
            });

            TreeButton.addActionListener(
                (e) -> {
                    Thread makeTree = new MakeTreeThread(CurrentPath[0]);
                    makeTree.start();
            });

            ViewerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (CurrentPathPane.getText().equals(CurrentPath[0] + CurrentSelection[0])) {
                        Thread viewer = new PictureViewerTread(CurrentPath[0]);
                        viewer.start();
                    }
                    if (CurrentPathPane.getText().equals(CurrentPath[1] + CurrentSelection[1])) {
                        Thread viewer = new PictureViewerTread(CurrentPath[1]);
                        viewer.start();
                    }
                }
            });

            super.setLayout(new FlowLayout());
            super.add(RefreshButton);
            super.add(NewDirButton);
            super.add(RenameButton);
            super.add(CopyButton);
            super.add(DeleteButton);
            super.add(SearchButton);
            super.add(TreeButton);
            super.add(ViewerButton);
        }

        private void CreateNewDir(String path) {
            File dirPath = new File(path);
            dirPath.mkdir();
        }

        private void CopyFile(String source, String destination, boolean ftpMode) {

            File file = new File(destination);
            boolean overwrite = true;

            if (file.exists()) {
                int option = JOptionPane.showConfirmDialog(null, "File " + destination + " already exists. Do you want to overwrite it?");
                if (option == JOptionPane.OK_OPTION) overwrite = true;
                else overwrite = false;
            }

            if (overwrite) {
                try (FileOutputStream fos = new FileOutputStream(file);
                     FileInputStream fis = new FileInputStream(new File(source)) ) {
                    if (!ftpMode) {
                        int bufferLength = 8;
                        int fileLength = fis.available();
                        if (fileLength >= 16777216) bufferLength = 131072;
                        if ((fileLength >= 1048576) & (fileLength < 16777216)) bufferLength = 8192;
                        if ((fileLength >= 65536) & (fileLength < 1048576)) bufferLength = 4096;
                        if ((fileLength >= 4096) & (fileLength < 65536)) bufferLength = 256;
                        if ((fileLength >= 1024) & (fileLength < 4096)) bufferLength = 64;
                        byte[] buffer = new byte[bufferLength];
                        byte[] oneByte = new byte[1];
                        double add = 1.0 * 100 * buffer.length / fileLength;
                        while (fis.available() > 0) {
                            if (fis.available() >= bufferLength) {
                                fis.read(buffer);
                                fos.write(buffer);
                                fCopyPercent = fCopyPercent + add;
                                info.SetUnderText("Complete: " + (int) fCopyPercent + "%");
                            } else {
                                fis.read(oneByte);
                                fos.write(oneByte);
                            }
                        }
                        fCopyPercent = 0;
                        fos.flush();
                    } else {
                        info.Show();
                        info.SetUnderText("");
                        info.SetMainText("");
                        info.SetInfoText("Retrieving unitmodel " + source + " from server " + ftpAddress + ", wait...");
                        if (ftpClient.retrieveFile(source, fos)) {
                            info.Hide();
                            JOptionPane.showMessageDialog(null, "File copied");
                        } else {
                            info.Hide();
                            JOptionPane.showMessageDialog(null, "File was not copied!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    fos.close();
                }
                catch (FileNotFoundException fnf) {
                    JOptionPane.showMessageDialog(null, "Cannot copy " + source + "\n" + "File not found exception!", "Exception", JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException io) {
                    JOptionPane.showMessageDialog(null, "Cannot copy " + source + "\n" + "Unexpected end of unitmodel!", "Exception", JOptionPane.ERROR_MESSAGE);
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "File " + destination + " was skipped!");
            }
        }

        private class FileCopyThread extends Thread {
            String source;
            String destination;
            boolean ftpMode;

            FileCopyThread(String source, String destination, boolean ftpMode) {
                this.source = source;
                this.destination = destination;
                this.ftpMode = ftpMode;
            }

            public void run() {
                if (!ftpMode) {
                    info.Show();
                    info.SetMainText("Copy");
                    info.SetInfoText("Copying unitmodel" + source + " in progress, wait..");
                }

                CopyFile(source, destination, ftpMode);
                info.Hide();
                if (!ftpMode) {
                    Panels.Refresh(BOTH);
                    JOptionPane.showMessageDialog(null, "Copy completed!");
                }
                else {
                    if (FTPmode[0]) Panels.Refresh(1);
                    if (FTPmode[1]) Panels.Refresh(0);
                }
            }
        }

        private class DirectoryCopyThread extends Thread {
            String source;
            String destination;
            boolean ftpMode;

            DirectoryCopyThread(String source, String destination, boolean ftpMode) {
                this.source = source;
                this.destination = destination;
                this.ftpMode = ftpMode;
            }

            private String getDirSize(String source) {
                String ds = "0";
                long size = ScanDirectorySize(source);
                if (size >= 1024 * 1024 * 1024) ds = size / (1024 * 1024 * 1024) + " Gb";
                if ((size >= 1024 * 1024) & (size < 1024 * 1024 * 1024)) ds = size / (1024 * 1024) + " Mb";
                if ((size >= 1024) & (size < 1024 * 1024)) ds = size / 1024 + " kb";
                if (size < 1024) ds = size + " bytes";
                return ds;
            }

            synchronized public void run() {
                info.Show();
                info.SetMainText("Copy");
                info.SetInfoText("Copying directory " + source + " in progress, wait" + "\n" + "Total size: " + getDirSize(source));
                dirSize = ScanDirectorySize(source);
                dirFileNumber = getTotalFileNumber(source);
                dCopyPercent = 0;
                System.out.println("Total size = " + dirSize + ", Total unitmodel number = " + dirFileNumber);
                CopyDirectory(source, destination);
                info.Hide();
                currentFileNumber = 0;
                JOptionPane.showMessageDialog(null, "Copy completed!");
                Panels.Refresh(BOTH);
            }
        }

        private class DeleteDirectoryThread extends Thread {

            String source;

            DeleteDirectoryThread(String source) {
                this.source = source;
            }

            public void run() {
                info.Show();
                info.SetMainText("Deleting");
                info.SetInfoText("Deleting directory " + source + " in progress, wait");
                dirFileNumber = getTotalFileNumber(source);
                DeleteDirectory(source);
                info.Hide();
                currentFileNumber = 0;
                JOptionPane.showMessageDialog(null, "Directory deleted!");
                Panels.Refresh(BOTH);
            }
        }

        private class SearchFileThread extends Thread {

            String filename;
            String folder;

            public SearchFileThread(String filename, String folder) {
                this.filename = filename;
                this.folder = folder;
            }

            public void run() {
                info.Show();
                info.SetInfoText("Searching " + filename + " in progress, wait");
                searchResult = CompositeSearchFile(filename, folder);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        SearchWindow sw = new SearchWindow(searchResult);
                        sw.Show();
                    }
                });
                info.Hide();
            }
        }

        private class MakeTreeThread extends Thread {

            String folder;

            public MakeTreeThread(String folder) {
                this.folder = folder;
            }

            public void run() {
                info.Show();
                info.SetInfoText("Creating tree, please wait");
                treeList = CreateTree(folder);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        SearchWindow sw = new SearchWindow(treeList);
                        sw.Show();
                    }
                });
                info.Hide();
            }
        }

        private class PictureViewerTread extends Thread {

            String directory;

            PictureViewerTread(String directory) {
                this.directory = directory;
            }

            public void run() {
                new Viewer(directory);
            }
        }

        private boolean DeleteFile(String source) {
                File file = new File(source);
                return file.delete();
        }

        private void DeleteDirectory(String source) {

            File[] fileList = new File(source).listFiles();
            for (File f : fileList) {
                if (f.isDirectory()) DeleteDirectory(source + "\\" + f.getName());
                if (f.isFile()) {
                    if (DeleteFile(source + "\\" + f.getName())) currentFileNumber ++;
                }
                info.SetMainText("Deleting " + currentFileNumber + "\\" + dirFileNumber);
            }
            DeleteFile(source);
        }

        private void CopyDirectory(String source, String destination) {
            CreateNewDir(destination);

            File[] fileList = new File(source).listFiles();
            for (File f : fileList) {
                if (f.isDirectory()) CopyDirectory(source + "\\" + f.getName(), destination + "\\" + f.getName());
                if (f.isFile()) {
                    currentFileNumber ++;
                    CopyFile(source + "\\" + f.getName(), destination + "\\" + f.getName(), false);
                }
                dCopyPercent = dCopyPercent + 100 * 1.0 * f.length() / dirSize;
                info.SetMainText("Total: " + (int) dCopyPercent + "%, copying unitmodel " + currentFileNumber + "\\" + dirFileNumber);
            }
        }

        private long ScanDirectorySize(String source) {
            long size = 0;
            File[] fileList = new File(source).listFiles();
            for (File f : fileList) {
                if (f.isFile()) size = size + f.length();
                if (f.isDirectory()) size = size + ScanDirectorySize(source + "\\" + f.getName());
            }
            return size;
        }

        private ArrayList<String> SearchFile(String searchRequest, String folder) {
            ArrayList<String> searchResult = new ArrayList<>();
            File directory = new File (folder);
            File[] dirList = directory.listFiles();

                for (File f : dirList) {
                    if (f.getName().contains(searchRequest))
                        searchResult.add(f.getAbsolutePath());
                    if (f.isDirectory())
                        info.SetUnderText(f.getAbsolutePath());
                    try {
                        searchResult.addAll(SearchFile(searchRequest, f.getAbsolutePath()));
                    }
                    catch (NullPointerException e) {}
                }

            return searchResult;
        }

        private ArrayList<String> CompositeSearchFile(String searchRequest, String folder) {
            ArrayList<String> searchResult = new ArrayList<>();
            ArrayList<String> result = new ArrayList<>();

            searchRequest = searchRequest.replace("*", "/");
            String[] rq = searchRequest.split("/");

            if  (!rq[0].isEmpty())
                searchResult = SearchFile(rq[0], folder);
            else
                searchResult = SearchFile(rq[1], folder);

            for (int c = 0; c < searchResult.size(); c ++)
                for (String request : rq)
                    if (!searchResult.get(c).contains(request))
                        searchResult.set(c, "null");
            for (String s : searchResult)
                if (!s.equals("null")) result.add(s);

            return result;
        }

        private ArrayList<String> CreateTree(String folder) {
            ArrayList<String> tree = new ArrayList<>();
            File directory = new File (folder);
            File[] dirList = directory.listFiles();

            for (File f : dirList) {
                if (f.isDirectory()) {
                    tree.add(f.getAbsolutePath());
                    info.SetUnderText(f.getAbsolutePath());
                    try {
                        tree.addAll(CreateTree(folder + "\\" + f.getName()));
                    }
                    catch (NullPointerException e) {}
                }
            }

            return tree;
        }

        private int getTotalFileNumber(String source) {
            int count = 0;
            File[] fileList = new File(source).listFiles();
            for (File f : fileList) {
                if (f.isFile()) count ++;
                if (f.isDirectory()) count = count + getTotalFileNumber(source + "\\" + f.getName());
            }
            return count;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileManager());
    }
}