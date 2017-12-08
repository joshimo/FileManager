package file.controller;

import file.controller.monitor.*;
import file.model.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.io.output.CountingOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class FTPFileController implements FTPcontroller {

    private String ftpserver;
    private String name;
    private String password;

    private FTPClient ftpClient;
    private Vector<FileUnit> fileList;
    private Vector<String> dirList;

    private Monitor monitor = new FXMonitorWindow();

    private static FTPFileController ourInstance = new FTPFileController();

    public static FTPFileController getInstance() {
        return ourInstance;
    }

    public void login(String server, String userName, String userPassword) throws IOException {
        ftpserver = server;
        name = userName;
        password = userPassword;

        ftpClient.connect(ftpserver);
        ftpClient.login(name, password);
        ftpClient.enterLocalPassiveMode();
    }

    private FTPFileController() {
        ftpClient = new FTPClient();
    }

    public Vector<FileUnit> getFileList(String directory) throws IOException {

        Vector<FileUnit> directoryList = new Vector<>();
        FTPFile[] files;

        if (ftpClient.isConnected())
            files= ftpClient.listFiles(directory);
        else {
            login(ftpserver, name, password);
            files= ftpClient.listFiles(directory);
        }

        for (FTPFile f : files)
            if (f.isDirectory()) directoryList.add(new FileUnit(f));
        for (FTPFile f : files)
            if (f.isFile()) directoryList.add(new FileUnit(f));

        for (FileUnit f : directoryList)
            f.addFTPFilePath(ftpserver, directory);

        return directoryList;
    }

    private void copyFTPfile(FileUnit source, String destination) {
        FTPFile ftpFile = source.getFTPfile();

        try (FileOutputStream fos = new FileOutputStream(new File(destination + File.separator + ftpFile.getName()))) {
            CountingOutputStream cos = new CountingOutputStream(fos) {
                double progress;
                protected void beforeWrite(int n) {
                    super.beforeWrite(n);
                    progress = 1.0 * getCount() / ftpFile.getSize();
                    monitor.SetLowerText("Copying current file completed: " + 0.01 * Math.round(10000 * progress) + "%");
                    monitor.SetLowerProgress(progress);
                }
            };

            ftpClient.retrieveFile(source.getParent() + File.separator + source.getName(), cos);

            fos.close();
            cos.close();
        }
        catch (IOException IOE) {
            IOE.printStackTrace();
        }
    }

    private void prepareFTPfilesCopyList(FileUnit source, String destination) throws IOException {
        FTPFile[] files;
        String path = source.getParent() + File.separator + source.getName();

        if (ftpClient.isConnected())
            files = ftpClient.listFiles(path);
        else {
            login(ftpserver, name, password);
            files = ftpClient.listFiles(path);
        }

        for (FTPFile ftpFile : files) {

            FileUnit ftpUnit = new FileUnit(ftpFile);
            ftpUnit.addFTPFilePath(ftpserver, path);

            if (ftpFile.isFile())
                fileList.add(ftpUnit);
            if (ftpFile.isDirectory())
                prepareFTPfilesCopyList(ftpUnit, path);
        }
    }

    public void copyFTPfileOrDirectory(FileUnit source, String destination) throws IOException {
        FTPFile sourceFTPfile = source.getFTPfile();

        long totalFileSize = 0;
        long copiedSize = 0;
        double progress = 0.0;

        File directory;
        FileUnit currentFile;
        String directoryName;

        monitor.Clear();
        monitor.Show();

        if (sourceFTPfile.isFile()) {
            monitor.SetUpperText("Copying file\n" + source.getAbsolutePath() + "\nto\n " + destination + File.separator + sourceFTPfile.getName());
            copyFTPfile(source, destination);
        }

        if (sourceFTPfile.isDirectory()) {
            fileList = new Vector<>();

            monitor.SetUpperText("Copying directory\n" + source.getAbsolutePath() + "\nto\n " + destination + File.separator + sourceFTPfile.getName());
            monitor.SetMiddleText("Praparing for copy, please wait...");

            prepareFTPfilesCopyList(source, destination);

            for (FileUnit f : fileList)
                totalFileSize += f.getFTPfile().getSize();

            for (int counter = 0; counter < fileList.size(); counter ++) {

                currentFile = fileList.get(counter);
                directoryName = (currentFile.getParent() + "").replace(source.getParent(), ""); /** ATTENTION!!! */
                directory = new File(destination + File.separator + directoryName);
                if (!directory.exists()) directory.mkdirs();

                copyFTPfile(currentFile, destination + File.separator + directoryName);

                copiedSize += currentFile.getFTPfile().getSize();
                progress = 1.0 * copiedSize / totalFileSize;
                monitor.SetMiddleText("Copying directory: " + (counter + 1) + " from " + fileList.size() + ",  " + 0.01 * Math.round(10000 * progress) + "% finished");
                monitor.SetMiddleProgress(progress);
            }

        }

        monitor.Hide();
        monitor.Clear();
    }

}