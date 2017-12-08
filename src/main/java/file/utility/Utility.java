package file.utility;

import java.io.File;
import java.util.ArrayList;
import file.model.*;
import file.controller.*;

/** File utility controller interface */
public interface Utility {

    ArrayList<String> CreateTree(File directory);

    String GetFilePreview(File directory);
}
