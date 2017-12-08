package file.controller;

import javax.swing.*;

class SwingMessageSystem implements Message {

    @Override
    public void ShowInfoMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    @Override
    public void ShowErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "!", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public int ConfirmMessage(String message) {
        return JOptionPane.showConfirmDialog(null, message);
    }
}
