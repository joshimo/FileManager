package old; /**
 * Created by y.golota on 02.12.2016.
 */


import com.sun.java.swing.plaf.motif.MotifBorders;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Viewer {

    int H;
    int L;

    int counter = 0;

    String location;

    ArrayList<File> fileList;

    JFrame viewer = new JFrame("old.Viewer");

    ImagePanel imagePanel;

    JPanel bPanel = new JPanel();

    JButton next = new JButton("Next");
    JButton previous = new JButton("Previous");

    public Viewer (String location) {
        this.location = location;
        counter = 0;
        fileList = CreateFileList(location);
        imagePanel = new ImagePanel(fileList.get(counter));
        viewer.getContentPane().setBackground(Color.darkGray);
        viewer.setSize(1024, 768);
        viewer.setVisible(true);
        viewer.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                InitViewer();
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
    }

    private void InitViewer() {
        viewer.setVisible(true);

        bPanel.setLayout(new FlowLayout());

        H = viewer.getHeight();
        L = viewer.getWidth();

        bPanel.setBounds(10, H - 80, L - 36, 40);
        bPanel.setBorder(new MotifBorders.BevelBorder(true, Color.LIGHT_GRAY, Color.LIGHT_GRAY));
        bPanel.setBackground(Color.darkGray);

        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (counter > 0) {
                    counter --;
                    imagePanel.setImage(fileList.get(counter));
                    viewer.repaint();
                }
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (counter < fileList.size() - 1) {
                    counter ++;
                    imagePanel.setImage(fileList.get(counter));
                    viewer.repaint();
                }
            }
        });

        bPanel.add(previous);
        bPanel.add(next);
        bPanel.repaint();

        imagePanel.setBounds(10, 10, L - 36, H - 100);

        viewer.add(imagePanel);
        viewer.add(bPanel);
        viewer.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        viewer.setLayout(null);
    }

    private ArrayList<File> CreateFileList(String location) {
        ArrayList<File> fList = new ArrayList<File>();
        File sourceDir = new File(location);
        File[] dirList = sourceDir.listFiles();
        for (File f : dirList)
            if (f.getName().toLowerCase().contains(".jpg") | f.getName().toLowerCase().contains(".bmp"))
                fList.add(f);
        for (File f : fList) System.out.println(f.getName());

        return fList;
    }

    private class ImagePanel extends JPanel {

        BufferedImage image;

        int resizedL;
        int resizedH;

        private ImagePanel(File imFile) {
            super.setVisible(true);
            super.setBorder(new MotifBorders.BevelBorder(true, Color.LIGHT_GRAY, Color.LIGHT_GRAY));

            try {
                image = ImageIO.read(imFile);
            }
            catch (IOException e) {}
        }

        private void setImage(File imFile) {
            super.setVisible(true);
            super.setBorder(new MotifBorders.BevelBorder(true, Color.LIGHT_GRAY, Color.LIGHT_GRAY));

            try {
                image = ImageIO.read(imFile);
            }
            catch (IOException e) {}

            this.repaint();
        }

        private Image getResizedImage (Image image) {
            int origL = image.getWidth(new ImageObserver() {
                @Override
                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                    return false;
                }
            });
            int origH = image.getHeight(new ImageObserver() {
                @Override
                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                    return false;
                }
            });

            double ratioL = 1.0 * origL / this.getWidth();
            double ratioH = 1.0 * origH / this.getHeight();

            if (ratioL > ratioH) {
                resizedL = (int) (origL / ratioL);
                resizedH = (int) (origH / ratioL);
            }
            else {
                resizedL = (int) (origL / ratioH);
                resizedH = (int) (origH / ratioH);
            }

            return image.getScaledInstance(resizedL, resizedH, Image.SCALE_SMOOTH);
        }

        @Override
        public void paint(Graphics g) {
            g.drawImage(getResizedImage(image), (this.getWidth() - resizedL) / 2, (this.getHeight() - resizedH) / 2, null);
        }
    }
}