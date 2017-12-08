package file.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import file.model.*;

/** File CRUD controller interface */
public interface Controller {

    Vector<FileUnit> getDrivesList();

    Vector<FileUnit> getFileList(File parentDirectory);
    Vector<FileUnit> getFileList(String parentDirectory);

    void setGUI(int GUI);

    boolean renameFileOrDirectory(String newFileName, File destination);
    boolean renameFileOrDirectory(String newFileName, String destination);

    void copyFileOrDirectory(File source, File destination);
    void copyFileOrDirectory(String source, String destination);

    boolean deleteFileOrDirectory(File source);
    boolean deleteFileOrDirectory(String source);

    void createNewDirectory(File destination);
    void createNewDirectory(String destination);

    void executeFile(File file);
    void executeFile(String filename);

    Collection<?> searchFile(String searchRequest, File directory);
}