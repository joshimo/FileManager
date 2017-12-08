package file.controller.monitor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class FXMonitowWindowController {

    @FXML
    private Label upperText;
    @FXML
    private Label middleText;
    @FXML
    private Label lowerText;

    @FXML
    private ProgressBar middleProgress;
    @FXML
    private ProgressBar lowerProgress;


    @FXML
    private void initialize() {
    }

    public void setUpperText(String text) {
        upperText.setText(text);
    }

    public void setMiddleText(String text) {
        middleText.setText(text);
    }

    public void setLowerText(String text) {
        lowerText.setText(text);
    }

    public void setMiddleProgress(double progress) {
        middleProgress.setProgress(progress);
    }

    public void setLowerProgress(double progress) {
       lowerProgress.setProgress(progress);
    }

    public void ShowProgress (boolean showProgress) {
        middleProgress.setVisible(showProgress);
        lowerProgress.setVisible(showProgress);
    }

    public void Clear() {
        upperText.setText("");
        middleText.setText("");
        lowerText.setText("");
        middleProgress.setProgress(0);
        lowerProgress.setProgress(0);
    }
}