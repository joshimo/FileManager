package sw_shell;

import javax.swing.*;

class ParentPathPanel extends JTextPane {

    private static ParentPathPanel ourInstance = new ParentPathPanel("C:");

    public static ParentPathPanel getInstance() {
        return ourInstance;
    }

    private ParentPathPanel(String path) {
        this.setText(path);
        this.setEditable(false);
    }

    protected void setPath(String path) {
        this.setText(path);
    }
}