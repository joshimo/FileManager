package file.controller.monitor;

import file.controller.monitor.*;

import javax.swing.*;
import java.awt.*;

/** Infopanel for copy and delete file process */
public class SwingMonitorWindow extends JFrame implements Monitor {

    private JTextPane HLabel = new JTextPane();
    private JTextPane MLabel = new JTextPane();
    private JTextPane LLabel = new JTextPane();

    private int L = 640;
    private int H = 270;

    private Dimension dim;

    public SwingMonitorWindow() {
        super("Swing");
        this.setVisible(false);
        this.setAlwaysOnTop(true);
        dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) ((dim.getWidth() - L) * 0.5), (int) ((dim.getHeight() - H) * 0.5));
        dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) ((dim.getWidth() - L) * 0.5), (int) ((dim.getHeight() - H) * 0.5));

        HLabel.setText("");
        HLabel.setBounds(30, 0, L - 60, H - 90);
        HLabel.setEditable(false);
        HLabel.setVisible(true);
        HLabel.setFont(new Font("Arial", Font.BOLD, 14));
        HLabel.setBackground(new Color(225, 225, 255));

        MLabel.setText("");
        MLabel.setBounds(10, H - 80, L - 20, 20);
        MLabel.setEditable(false);
        MLabel.setVisible(true);
        MLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        MLabel.setBackground(new Color(225, 225, 255));

        LLabel.setText("");
        LLabel.setBounds(10, H - 55, L - 20, 20);
        LLabel.setEditable(false);
        LLabel.setVisible(true);
        LLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        LLabel.setBackground(new Color(225, 225, 255));

        this.setResizable(false);
        this.setSize(L, H);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setLayout(null);

        this.add(HLabel);
        this.add(MLabel);
        this.add(LLabel);

        this.getContentPane().setBackground(new Color(225, 225, 255));
    }

    synchronized public void Show() {
        this.setVisible(true);
    }

    synchronized public void Hide() {
        this.setVisible(false);
    }

    public void SetUpperText (String text) {
        HLabel.setText(text);
    }

    public void SetMiddleText (String text) {
        MLabel.setText(text);
    }

    public void SetLowerText(String text) {
        LLabel.setText(text);
    }

    public void getInstance() {

    }

}