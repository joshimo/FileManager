package file.model;

import org.apache.commons.net.ftp.FTPFile;
import java.io.Serializable;


public interface ftpfile extends Serializable {

    FTPFile getFTPfile();

    boolean isFTPfile();

    void addFTPFilePath(String ftpPath, String ftpDir);
}
