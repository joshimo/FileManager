package sw_shell.messanger;

import javax.swing.*;

public class MessageSystem implements Messenger {

    private static MessageSystem ourInstance = new MessageSystem();

    public static MessageSystem getInstance() {
        return ourInstance;
    }

    private MessageSystem() {}

    @Override
    public void ShowInfoMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    @Override
    public String ShowInputMessage(String message) {
        return JOptionPane.showInputDialog(message);
    };

    @Override
    public void ShowErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public int ConfirmMessage(String message) {
        return JOptionPane.showConfirmDialog(null, message);
    }
}