package sw_shell;

import file.model.*;
import file.controller.*;
import sw_shell.messanger.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Vector;


class FileListPanel extends JScrollPane {

    final int panelNum;

    private Vector<Vector<String>> data;
    private Vector<String> header;
    private JTable fileTable;
    private Vector<FileUnit> fileList;

    TableRenderer tableRenderer = new TableRenderer(data);

    private String currentDirectoryPath = "C:" + File.separator;
    private String currentFileName = "";

    private File currentSelection;
    private File currentDirectory;

    private int pointer = 1;

    private boolean isActive = true;

    private Controller controller;
    private ParentPathPanel parentPathPanel;
    private Messenger messenger;

    private int x;
    private int y;
    private int width;
    private int height;

    DefaultTableModel dtm = new DefaultTableModel(data, header) {
        @Override
        public boolean isCellEditable(int rowIndex, int colIndex) {
            return false;
        }
    };

    public String getCurrentDirectoryPath() {
        return currentDirectoryPath;
    }

    public void setCurrentDirectoryPath(String currentDirectoryPath) {
        this.currentDirectoryPath = currentDirectoryPath;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    protected FileListPanel(int num, Controller controller, ParentPathPanel parentPathPanel, Messenger messenger) {
        panelNum = num;
        this.controller = controller;
        this.parentPathPanel = parentPathPanel;
        this.messenger = messenger;
        refresh();
    }

    protected void refresh() {

        try {
            fileList = controller.getFileList(currentDirectoryPath);
        }
        catch (NullPointerException npe) {
            messenger.ShowErrorMessage("Null pointer Exception!\n" + npe.toString());
        }

        createTables();
        dtm.setDataVector(data, header);
        tableRenderer = new TableRenderer(data);

        fileTable = new JTable(dtm);
        fileTable.setDefaultRenderer(Object.class, tableRenderer);
        fileTable.getSelectionModel().setSelectionInterval(1, 1);

        this.setViewportView(fileTable);

        fileTable.addMouseListener(new OnClickListener());
        fileTable.addFocusListener(new ActivityListener());
        resize(width);

        FileManagerShell.setActivePanel(panelNum);
    }

    private void createTables(){
        data = new Vector<>();
        header = new Vector<>();

        header.add(0, "File name");
        header.add(1, "Ext");
        header.add(2, "Size");
        header.add(3, "Date");
        header.add(4, "Attrib");

        Vector<String> row = new Vector<>();
        row.add(0, "...< UP >...");
        row.add(1, "");
        row.add(2, "");
        row.add(3, "");
        row.add(4, "");
        data.add(row);

        FileUnit file;

        for (int counter = 1; counter <= fileList.size(); counter ++) {
            row = new Vector<>();
            file = fileList.get(counter - 1);
            row.add(0, file.getName());
            row.add(1, file.getFileExtension());
            row.add(2, file.getFileSize());
            row.add(3, file.getCreationDate());
            row.add(4, file.getAttributesLetters());
            data.add(row);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        resize(width);
    }

    private void resize(int width) {
        fileTable.setAutoResizeMode(0);
        fileTable.getColumnModel().getColumn(0).setPreferredWidth((int) (0.65 * (width - 16)));
        fileTable.getColumnModel().getColumn(1).setPreferredWidth((int) (0.07 * (width - 16)));
        fileTable.getColumnModel().getColumn(2).setPreferredWidth((int) (0.08 * (width - 16)));
        fileTable.getColumnModel().getColumn(3).setPreferredWidth((int) (0.15 * (width - 16)));
        fileTable.getColumnModel().getColumn(4).setPreferredWidth((int) (0.05 * (width - 16)));
        fileTable.setRowHeight(36);
    }

    private class OnClickListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            super.mouseClicked(e);
            pointer = fileTable.getSelectedRow();
            currentFileName = fileTable.getValueAt(fileTable.getSelectedRow(), 0).toString();
            parentPathPanel.setPath(currentDirectoryPath);

            currentDirectory = new FileUnit(currentDirectoryPath);
            currentSelection = new FileUnit(currentDirectoryPath + File.separator + currentFileName);

            if (e.getClickCount() == 2) {
                try {
                    if (fileTable.getSelectedRow() == 0) {
                        if (currentDirectory.getParent() != null)
                            currentDirectoryPath = currentDirectory.getParent();
                    }
                    else {
                        if (currentSelection.isFile())
                            Desktop.getDesktop().open(currentSelection);
                        if (currentSelection.isDirectory())
                            currentDirectoryPath += File.separator + currentFileName;
                    }

                    try {
                        fileList = controller.getFileList(currentDirectoryPath);
                    }
                    catch (NullPointerException npe) {
                        messenger.ShowErrorMessage("Access denied on\n" + currentDirectoryPath);
                        currentDirectoryPath = currentSelection.getParent();
                    }

                    parentPathPanel.setPath(currentDirectoryPath);
                    refresh();
                }
                catch (IOException ioe) {
                    messenger.ShowErrorMessage("IO Exception!\n" + ioe.toString());
                }
            }
        }
    }

    private class ActivityListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            isActive = true;
            FileManagerShell.setActivePanel(panelNum);
        }

        @Override
        public void focusLost(FocusEvent e) {
            isActive = false;
        }
    }
}