package fx_shell;

import file.model.FileUnit;
import java.io.File;
import javafx.scene.control.TextField;

public class PathPanel extends TextField {

    private FileUnit file;

    public FileUnit getFile() {
        return file;
    }

    public void setFile(FileUnit file) {
        this.file = file;
        this.setText(this.file.getAbsolutePath());
    }

    public void setFile(File file) {
        setFile(new FileUnit(file));
    }
}