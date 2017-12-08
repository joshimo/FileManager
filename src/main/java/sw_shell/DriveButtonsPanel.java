package sw_shell;

import file.model.*;
import file.controller.*;
import sw_shell.messanger.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

class DriveButtonsPanel extends JPanel {

    private Vector<FileUnit> drivesList;
    private Vector<DriveButton> drivesButtons;

    private Controller controller;

    private ParentPathPanel parentPathPanel;

    private Messenger messenger;

    private int panelNum;
    private TitledBorder border;

    private class DriveButton extends JButton {

        private FileUnit drive;

        public FileUnit getDrive() {
            return drive;
        }

        DriveButton(FileUnit drive) {
            super(drive.getAbsolutePath());
            this.drive = drive;
        }
    }

    protected DriveButtonsPanel(int panelNum, Controller controller, ParentPathPanel parentPathPanel, Messenger messenger) {
        super.removeAll();
        this.panelNum = panelNum;
        this.controller = controller;
        this.parentPathPanel = parentPathPanel;
        this.messenger = messenger;
        if (panelNum == -1) border = new TitledBorder("Left");
        if (panelNum == 1) border = new TitledBorder("Right");
        this.setBorder(border);
        createDriveButtons();
    }

    private void createFileList(FileUnit drive) {
        try {
            if (DriveButtonsPanel.this.panelNum == -1) {
                FileManagerShell.leftFileListPanel.setCurrentDirectoryPath(drive.getAbsolutePath());
                FileManagerShell.leftFileListPanel.refresh();
                parentPathPanel.setPath(drive.getAbsolutePath());
            }
            if (DriveButtonsPanel.this.panelNum == 1) {
                FileManagerShell.rightFileListPanel.setCurrentDirectoryPath(drive.getAbsolutePath());
                FileManagerShell.rightFileListPanel.refresh();
                parentPathPanel.setPath(drive.getAbsolutePath());
            }
        }
        catch (NullPointerException npe) {
            messenger.ShowErrorMessage("Null pointer exception!\n" + npe.toString());
        }
    }

    private class DriveButtonListener implements ActionListener {

        private FileUnit drive;

        DriveButtonListener(DriveButton driveButton) {
            this.drive = driveButton.getDrive();
        }

        public void actionPerformed(ActionEvent e) {
            createFileList(drive);
        }
    }

    private void createDriveButtons() {
        try {
            drivesList = controller.getDrivesList();
            drivesButtons = new Vector<>();
            for (FileUnit currentDrive : drivesList)
                drivesButtons.add(new DriveButton(currentDrive));
            for (DriveButton currentButton : drivesButtons) {
                currentButton.addActionListener(new DriveButtonListener(currentButton));
                this.add(currentButton);
            }
        }
        catch (NullPointerException npe) {
            messenger.ShowErrorMessage("Null pointer exception!\n" + npe.toString());
        }
    }
}