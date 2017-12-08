package fx_shell.messanger;


import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class FXShellMessageSystem implements Messenger {

    private static FXShellMessageSystem ourInstance = new FXShellMessageSystem();

    public static FXShellMessageSystem getInstance() {
        return ourInstance;
    }

    private FXShellMessageSystem() {}

    @Override
    public void ShowInfoMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Imformation");
        alert.setContentText(message);
        alert.show();
    }

    @Override
    public String ShowInputMessage(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(message);
        dialog.setHeaderText(message);

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent())
            return result.get();
        else
            return "";
    }

    @Override
    public void ShowErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("ERROR OCCURED!");
        alert.setContentText(message);
        alert.show();
    }

    @Override
    public int ConfirmMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Please confirm");
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK)
            return 0;
        else
            return 1;
    }
}