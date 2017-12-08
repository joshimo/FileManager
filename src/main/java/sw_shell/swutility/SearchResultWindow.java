package sw_shell.swutility;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SearchResultWindow extends JFrame {

    public SearchResultWindow(int screenWidth, int screenHeight) {
        super("Search result");
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocation((int) ((screenWidth - 800) / 2), (int) ((screenHeight - 800) / 2));
    }

    public SearchResultWindow(ArrayList<String> searchResult) {
        super("Search result");
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setSize(800, 600);

        Object[] searchResultList = searchResult.toArray();

        JList list = new JList(searchResultList);
        JScrollPane resultPane = new JScrollPane(list);

        list.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });

        this.add(resultPane);
    }

    public void Show() {
        this.setVisible(true);
    }

    public void Hide() {
        this.setVisible(false);
    }
}
