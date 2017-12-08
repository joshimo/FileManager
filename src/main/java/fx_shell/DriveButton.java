package fx_shell;

import javafx.scene.control.Button;
import file.model.FileUnit;
import javafx.scene.image.ImageView;

public class DriveButton extends Button {

    private FileUnit drive;
    private String driveName;
    private ImageView icon;

    public FileUnit getDrive() {
        return drive;
    }

    public String getDriveName() {
        return driveName;
    }

    public DriveButton() {

    }

    public DriveButton(FileUnit drive) {
        super(drive.getAbsolutePath());
        this.icon = new ImageView("/icons/fx/icons8-HDD-24.png");
        this.drive = drive;
        this.driveName = drive.getAbsolutePath();
        this.graphicProperty().setValue(icon);
    }

    @Override
    public String toString() {
        return drive.getAbsolutePath();
    }
}