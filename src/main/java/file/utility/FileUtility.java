package file.utility;

import java.io.File;
import java.util.ArrayList;


public class FileUtility implements Utility {

    private static FileUtility ourInstance = new FileUtility();

    public static FileUtility getInstance() {
        return ourInstance;
    }

    private FileUtility() {
    }

    @Override
    public ArrayList<String> CreateTree(File directory) {
        return null;
    }

    @Override
    public String GetFilePreview(File directory) {
        return null;
    }
}