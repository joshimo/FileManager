package file.controller;

/** Message system interface for file controller */
interface Message {

    public final int OK_OPTION = 0;
    public final int NO_OPTION = 1;
    public final int CANCEL_OPTION = 2;

    void ShowInfoMessage(String message);

    void ShowErrorMessage(String message);

    int ConfirmMessage(String message);
}