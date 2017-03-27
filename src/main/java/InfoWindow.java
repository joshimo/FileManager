/**
 * Created by y.golota on 24.11.2016.
 */

//package main.java;

import javax.swing.*;
import java.awt.*;

public class InfoWindow extends JFrame {

    JTextPane HLabel = new JTextPane();
    JTextPane MLabel = new JTextPane();
    JTextPane LLabel = new JTextPane();

    Dimension dim;

    public InfoWindow() {
        super();
    }

    synchronized public void Show() {
        super.setVisible(true);
    }

    synchronized public void Hide() {
        super.setVisible(false);
    }

    public InfoWindow(String name, int L, int H) {
        dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) ((dim.getWidth() - L) * 0.5), (int) ((dim.getHeight() - H) * 0.5));

        HLabel.setText("");
        HLabel.setBounds(L / 10, 0, L, H / 4);
        HLabel.setEditable(false);
        HLabel.setVisible(true);
        HLabel.setFont(new Font("Arial", Font.BOLD, 14));
        HLabel.setBackground(new Color(225, 225, 255));

        MLabel.setText("");
        MLabel.setBounds(10, H / 2 - 10, L, H / 8);
        MLabel.setEditable(false);
        MLabel.setVisible(true);
        MLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        MLabel.setBackground(new Color(225, 225, 255));

        LLabel.setText("");
        LLabel.setBounds(10, H * 3 / 4 - 20, L, H / 8);
        LLabel.setEditable(false);
        LLabel.setVisible(true);
        LLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        LLabel.setBackground(new Color(225, 225, 255));

        super.setResizable(false);
        super.setSize(L, H);
        super.setDefaultCloseOperation(HIDE_ON_CLOSE);
        super.setLayout(null);
        super.setName(name);

        super.add(HLabel);
        super.add(MLabel);
        super.add(LLabel);

        super.getContentPane().setBackground(new Color(225, 225, 255));
        Hide();
    }

    synchronized public void SetInfoText (String text) {
        HLabel.setText(text);
        super.revalidate();
    }

    synchronized public void SetUnderText (String text) {
        LLabel.setText(text);
        super.revalidate();
    }

    synchronized public void SetMainText(String text) {
        MLabel.setText(text);
        super.revalidate();
    }
}