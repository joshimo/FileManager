package fx_shell;

import file.model.FileUnit;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;


public class FTPButton extends DriveButton {

    private ImageView icon;

    public FTPButton() {
        this.setText("ftp://");
        this.icon = new ImageView("/icons/fx/icons8-FTP-24.png");
        this.graphicProperty().setValue(icon);
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
