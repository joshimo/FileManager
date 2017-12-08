package file.controller.monitor;

/** Infopanel interface for copy and delete file process */

public interface Monitor {

    default void Show() {

    }

    default void Hide() {

    }

    default void SetUpperText (String text) {

    }

    default void SetMiddleText (String text) {

    }

    default void SetLowerText(String text) {

    }

    default void ShowProgress (boolean showProgress) {

    }

    default void SetMiddleProgress (double progress) {

    }

    default void SetLowerProgress(double progress) {

    }

    default void Clear() {

    }
}