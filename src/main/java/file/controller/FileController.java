package file.controller;

import file.controller.monitor.*;
import file.model.FileUnit;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;


/**Implementation of file CRUD controller interface */

public class FileController implements Controller {

    private Monitor monitor;
    private Message info;

    private int GUI = 0;

    public static final int SWING = 0;
    public static final int FX = 1;

    private static FileController ourInstance = new FileController();

    private ArrayList<File> sourceFileList = new ArrayList<>();
    private ArrayList<File> destinationFileList = new ArrayList<>();

    public void setGUI(int GUI) {

        this.GUI = GUI;

        if (GUI == SWING) {
            monitor = new SwingMonitorWindow();
            info = new SwingMessageSystem();
        }
        if (GUI == FX) {
            monitor = new FXMonitorWindow();
            info = new FXFileControllerMessageSystem();
        }
    }

    private void setGUI() {
        setGUI(SWING);
    }

    public static FileController getInstance() {
        return ourInstance;
    }

    private FileController() {
        setGUI();
    }

    public Vector<FileUnit> getDrivesList() {
        Vector<FileUnit> drivesList = new Vector<>();
        File[] drivesArray = File.listRoots();
        for (File f : drivesArray) drivesList.add(new FileUnit(f));
        return drivesList;
    }

    public Vector<FileUnit> getFileList(File parentDirectory) {

        Vector<FileUnit> filesList = new Vector<>();
        File[] filesArray = parentDirectory.listFiles();
        for (File f : filesArray)
            if (f.isDirectory()) filesList.add(new FileUnit(f));
        for (File f : filesArray)
            if (f.isFile()) filesList.add(new FileUnit(f));
        return filesList;
    }

    public Vector<FileUnit> getFileList(String parentDirectory) {
        return getFileList(new File(parentDirectory));
    }


    public boolean renameFileOrDirectory(String newFileName, File destination) {
        if (CheckFileName(newFileName))
            return destination.renameTo(new File(destination.getParent() + File.separator + newFileName));
        else {
            info.ShowErrorMessage("Wrong filename!\nNew name contains wrong symbols!");
            return false;
        }
    }

    public boolean renameFileOrDirectory(String newFileName, String destination) {
        return renameFileOrDirectory(newFileName, new File(destination));
    }


    private boolean CheckFileName(String name) {

        if (name.isEmpty()) return false;
        if (name == null) return false;

        if (name.contains("*")) return false;
        if (name.contains("|")) return false;
        if (name.contains("\\")) return false;
        if (name.contains(":")) return false;
        if (name.contains("\"")) return false;
        if (name.contains("<")) return false;
        if (name.contains(">")) return false;
        if (name.contains("?")) return false;
        if (name.contains("/")) return false;

        return true;
    }

    private void CopyFile(File source, File destination) {

        boolean overwrite = true;

        if (!source.exists()) {
            info.ShowErrorMessage("Cannot copy file " + source.getAbsolutePath() + "\nFile not exist!");
            return;
        }

        if (destination.exists()) {
            int option = info.ConfirmMessage("File " + destination + " already exists. Do you want to overwrite it? ");
            if (option == info.OK_OPTION) overwrite = true;
            else overwrite = false;
        }

        monitor.SetUpperText("Copying file \n" + source.getName() + "\n\nto\n" + destination.getAbsolutePath());

        if (overwrite)
        try (FileOutputStream fos = new FileOutputStream(destination);
            FileInputStream fis = new FileInputStream(source) ) {
            int bufferLength = 8;
            int fileLength = fis.available();
            if (fileLength >= 16777216) bufferLength = 131072;
            if ((fileLength >= 1048576) & (fileLength < 16777216)) bufferLength = 8192;
            if ((fileLength >= 65536) & (fileLength < 1048576)) bufferLength = 4096;
            if ((fileLength >= 4096) & (fileLength < 65536)) bufferLength = 256;
            if ((fileLength >= 1024) & (fileLength < 4096)) bufferLength = 64;

            byte[] buffer = new byte[bufferLength];
            byte[] oneByte = new byte[1];

            double fCopyPercent = 0;
            double completed;
            double add = 1.0 * 100 * buffer.length / fileLength;
            while (fis.available() > 0) {
                if (fis.available() >= bufferLength) {
                    fis.read(buffer);
                    fos.write(buffer);
                    fCopyPercent += add;
                    completed = 1.0 * Math.round(fCopyPercent * 100) / 100;
                    monitor.SetLowerText("Copying current file completed: " + completed + "%");
                    monitor.SetLowerProgress(completed / 100);
                } else {
                    fis.read(oneByte);
                    fos.write(oneByte);
                }
            }
            fos.close();
        }
        catch (FileNotFoundException fnf) {
            info.ShowErrorMessage("Cannot copy " + source + "\n" + "File not found exception!");
        }
        catch (IOException io) {
            info.ShowErrorMessage("Cannot copy " + source + "\n" + io.toString());
        }

        else
            info.ShowInfoMessage("File \n" + destination.getName() + "\n was skipped!");
    }

    private boolean DeleteFile(File source) {
        if (!source.exists()) {
            info.ShowErrorMessage("Cannot delete file " + source.getAbsolutePath() + "\nFile does not exist!");
            return false;
        }
        else
            return source.delete();
    }

    public boolean deleteFileOrDirectory(File source) {

            if (source.isFile())
                return DeleteFile(source);
            if (source.isDirectory())
                return DeleteDirectory(source);
            else
                return false;
    }

    public boolean deleteFileOrDirectory(String source) {
        return deleteFileOrDirectory(new File(source));
    }


    public void createNewDirectory(File destination) {
        if (destination.exists()) {
            info.ShowInfoMessage("Directory " + destination.getAbsolutePath() + "\nalready exist!");
            return;
        }
        if (CheckFileName(destination.getName()))
            destination.mkdirs();
        else {
            info.ShowErrorMessage("Wrong directory name!\nName contains wrong symbols!");
            return;
        }
    }

    public void createNewDirectory(String destination) {
        createNewDirectory(new File(destination));
    }


    private void CopyDirectory(File source, File destination) {

        CreateSourceFileList(source);
        CreateDestinationFileList(source, destination);

        File currentSourceFile;
        File currentDestinationFile;

        long totalSize = 0;
        long copiedFileSize = 0;
        double copiedFilePercent = 0;
        for (File f : sourceFileList) totalSize += f.length();

        for (int count = 0; count < sourceFileList.size(); count ++) {

            currentSourceFile = sourceFileList.get(count);
            currentDestinationFile = destinationFileList.get(count);

            copiedFilePercent = 1.0 * Math.round(10000 * copiedFileSize / totalSize) / 100;
            monitor.SetMiddleText("Copying directory: " + count + " from " + sourceFileList.size() + ",  " + copiedFilePercent + "% finished");
            monitor.SetMiddleProgress(copiedFilePercent / 100);

            if (!currentDestinationFile.getParentFile().exists())
                createNewDirectory(currentDestinationFile.getParentFile());

            CopyFile(currentSourceFile, currentDestinationFile);
            copiedFileSize += currentSourceFile.length();
        }

    }

    private boolean DeleteDirectory(File directory) {

        if (directory.exists()) {
            File[] fileList = directory.listFiles();
            monitor.SetUpperText("Deleting directory " + directory.getAbsolutePath());
            monitor.SetMiddleText("Currently deleating:");
            for (File f : fileList) {
                monitor.SetLowerText(f.getAbsolutePath());
                if (f.isDirectory()) DeleteDirectory(f);
                if (f.isFile()) DeleteFile(f);
            }

            DeleteFile(directory);
        }
        else {
            info.ShowErrorMessage("Can`t delete directory " + directory.getAbsolutePath() + "\nDirectory does not exist");
            return false;
        }
        if (directory.exists())
            return false;
        else
            return true;
    }


    public void copyFileOrDirectory(File source, File destination) {

        this.sourceFileList = new ArrayList<>();
        this.destinationFileList = new ArrayList<>();

        monitor.Show();

        if (source.isFile())
            CopyFile(source, new File(destination.getAbsolutePath() + File.separator + source.getName()));
        if (source.isDirectory())
            CopyDirectory(source, new File(destination.getAbsolutePath() + File.separator + source.getName()));

        monitor.Hide();
    }

    public void copyFileOrDirectory(String source, String destination) {
        copyFileOrDirectory(new File(source), new File(destination));
    }

    public void executeFile(File file) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ioe) {
                    info.ShowErrorMessage("IO Exception while running file \n" + file.getAbsolutePath());
                    ioe.printStackTrace();
                }
            }
        }.start();
    }

    public void executeFile(String filename) {
        executeFile(new File(filename));
    }

    public Vector<FileUnit> searchFile(String searchRequest, File directory) {

        Vector<FileUnit> searchResult = new Vector<>();
        Vector<FileUnit> result = new Vector<>();

        Vector<String> requestList = new Vector<>();

        StringTokenizer tokenizer = new StringTokenizer(searchRequest);
        while (tokenizer.hasMoreElements()) requestList.add(tokenizer.nextToken("*"));

        monitor.Clear();
        monitor.ShowProgress(false);
        monitor.Show();

        for (String rq : requestList) searchResult.addAll(searchFileByName(rq, directory));

        for (FileUnit fu : searchResult) {
            int count = 0;
            while (true) {
                if (!fu.getName().contains(requestList.get(count)))
                    break;
                else
                    count ++;
                if (count == requestList.size()) {
                    if (!result.contains(fu)) result.add(fu);
                    break;
                }
            }
        }

        monitor.Hide();
        monitor.ShowProgress(true);
        monitor.Clear();

        return result;
    }

    private Vector<FileUnit> searchFileByName(String searchRequest, File directory) {

        Vector<FileUnit> searchResult = new Vector<>();
        Vector<FileUnit> dirList = getFileList(directory);

        for (FileUnit f : dirList) {
            if (f.getName().contains(searchRequest))
                searchResult.add(f);
            if (f.isDirectory())
                monitor.SetLowerText(f.getAbsolutePath());
            try {
                searchResult.addAll(searchFileByName(searchRequest, f.getAbsoluteFile()));
            }
            catch (NullPointerException e) {}
        }

        return searchResult;
    }

    private void CreateSourceFileList(File source) {
        File[] fileList = source.listFiles();

        for (File f : fileList) {
            if (f.isDirectory())
                CreateSourceFileList(f);
            if (f.isFile())
                sourceFileList.add(f);
        }
    }

    private void CreateDestinationFileList(File source, File destination) {
        for (File f : sourceFileList)
            destinationFileList.add(new File(f.getAbsolutePath().replace(source.getParent(), destination.getParent() + File.separator)));
    }

    /*public static void main(String... args) {
    }*/
}