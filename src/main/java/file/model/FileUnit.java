package file.model;

import sun.util.calendar.CalendarUtils;

import java.io.File;
import java.util.Date;
import java.util.StringTokenizer;
import org.apache.commons.net.ftp.*;

public class FileUnit extends File implements ftpfile {

    /** FILETYPE CONSTANTS */

    /** System files */
    public final int PARENT = -1;
    public final int DIRECTORY = 0;
    public final int FILE = 1;

    public final int EXECUTABLE = 11;
    public final int COMPILED = 12;
    public final int BAT_SCRIPT = 13;
    public final int CMD_SCRIPT = 14;

    public final int INI_SYSTEM = 21;
    public final int CFG_SYSTEM = 22;
    public final int SYS_SYSTEM = 23;
    public final int TMP_SYSTEM = 24;
    public final int TORRENT_SYSTEM = 31;
    public final int LOG_SYSTEM = 41;
    public final int NFO_SYSTEM = 42;
    public final int ME_SYSTEM = 43;
    public final int README_SYSTEM = 44;

    /** Font files */
    public final int TTF_FONT = 71;
    public final int FONT_FONT = 72;
    public final int FON_FONT = 73;

    /** Archive files */
    public final int RAR_ARCHIVE = 101;
    public final int ZIP_ARCHIVE = 102;
    public final int ARJ_ARCHIVE = 103;
    public final int SZ_ARCHIVE = 104;
    public final int CAB_ARCHIVE = 105;
    public final int ACE_ARCHIVE = 106;
    public final int GZ_ARCHIVE = 107;
    public final int TAR_ARCHIVE = 108;
    public final int LHA_ARCHIVE = 109;
    public final int JAR_ARCHIVE = 110;
    public final int WAR_ARCHIVE = 111;

    /** Document files */
    public final int ACAD_DWG = 201;
    public final int WINWORD_DOCUMENT = 202;
    public final int EXCEL_DOCUMENT = 203;
    public final int PPOINT_DOCUMENT = 204;
    public final int TXT_DOCUMENT = 205;
    public final int PDF_DOCUMENT = 206;
    public final int DJVU_DOCUMENT = 207;
    public final int PSD_DOCUMENT = 208;

    /** Video files*/
    public final int AVI_VIDEO = 301;
    public final int MPEG_VIDEO = 302;
    public final int MOV_VIDEO = 303;
    public final int WMV_VIDEO = 303;

    /** Audio files*/
    public final int WAV_AUDIO = 401;
    public final int MPEG_AUDIO = 402;
    public final int FLAC_AUDIO = 403;
    public final int APE_AUDIO = 404;

    /** Image files*/
    public final int BMP_IMG = 501;
    public final int JPG_IMG = 502;
    public final int TIFF_IMG = 503;
    public final int GIF_IMG = 504;
    public final int PNG_IMG = 505;
    public final int EPS_IMG = 506;
    public final int TGA_IMG = 507;
    public final int PCX_IMG = 508;
    public final int RAW_IMG = 509;

    /** Listing files*/
    public final int JAVA_PROG = 601;
    public final int CPP_PROG = 602;
    public final int CSS_PROG = 603;
    public final int JS_PROG = 604;

    /** Internet files */
    public final int HTM_BROWSER = 701;
    public final int HTML_BROWSER = 702;
    public final int URL_BROWSER = 703;

    private String fileExtension;
    private String fileName;
    private String fileAbsoluteName;
    private String ftpFileAbsoluteName;
    private String ftpFileParentDirectory;
    private String fileSize;
    private Long size;
    private String attributesLetters;
    private String creationDate;
    private int fileTypeConstant;
    private String fileTypeDescription;

    private FTPFile ftpFile;

    private boolean isFile;
    private boolean isDirectory;
    private boolean isLocked = false;
    private boolean isExecutable = false;
    private boolean isBlank = false;
    private boolean isFTPfile = false;


    /** ftpfile interface implementation */

    public FTPFile getFTPfile() {
        return ftpFile;
    }

    public boolean isFTPfile() {
        return isFTPfile;
    }

    public void addFTPFilePath(String serverAddr, String ftpDirectory) {
        ftpFileParentDirectory = ftpDirectory;
        ftpFileAbsoluteName = "ftp://" + serverAddr + File.separator + ftpDirectory + File.separator + fileName;
    }


    /** GETTERS */

    public String getFileName() {
        return fileName;
    }

    public String getFileAbsoluteName() {
        return fileAbsoluteName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getAttributesLetters() {
        return attributesLetters;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public int getFileTypeConstant() {
        return fileTypeConstant;
    }

    public String getFileTypeDescription() {
        return fileTypeDescription;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isExecutable() {
        return isExecutable;
    }

    public boolean isBlank() {
        return isBlank;
    }


    /** SETTERS */

    public void setLocked(boolean locked) {
        isLocked = locked;
    }


    /** CONSTRUCTORS */

    /** Blank constructor for an empty file */
    public FileUnit() {
        super("");
        this.fileName = "UP";
        this.isBlank = true;
        fileExtension = "";
        fileSize = "";
        attributesLetters = "";
        creationDate = "";
        fileTypeConstant = PARENT;
        fileTypeDescription = identify();
    }

    public FileUnit (String fileName) {
        super(fileName);

        this.fileName = super.getName();
        fileAbsoluteName = this.getAbsolutePath();
        fileExtension = getExtension();
        size = this.length();
        fileSize = getSize();
        attributesLetters = getAttribues();
        creationDate = getDate();
        fileTypeConstant = createFileTypeConstant();
        fileTypeDescription = identify();

        if (fileExtension.toLowerCase().equals("exe")
            || fileExtension.toLowerCase().equals("com")
            || fileExtension.toLowerCase().equals("bat")
            || fileExtension.toLowerCase().equals("cmd"))
                isExecutable = true;
    }

    public FileUnit (String filename, String parentDirectory) {
        this(parentDirectory + File.separator + filename);
    }

    public FileUnit (File file) {
        this(file.getAbsolutePath());
    }

    /** FTPFile constructor */
    public FileUnit (FTPFile ftpFile) {
        super("");
        this.isFTPfile = true;
        this.ftpFile = ftpFile;
        this.fileName = ftpFile.getName();
        fileAbsoluteName = this.getAbsolutePath();
        fileExtension = getExtension();
        size = ftpFile.getSize();
        fileSize = getSize();
        attributesLetters = "";
        creationDate = "";
        fileTypeConstant = createFileTypeConstant();
        fileTypeDescription = identify();

        if (fileExtension.toLowerCase().equals("exe")
                || fileExtension.toLowerCase().equals("com")
                || fileExtension.toLowerCase().equals("bat")
                || fileExtension.toLowerCase().equals("cmd"))
            isExecutable = true;
    }


    /** PRIVATE CLASS METHODS */

    private String getSize() {
        String filesize = "";

        if (size < 1024)
            filesize = size.toString();
        if (size >= 1024)
            filesize = (double) Math.round((double) 10 * size / 1024) / 10 + "k";
        if (size >= (double) 1024 * 1024)
            filesize = (double) Math.round((double) 10 * size / (1024 * 1024)) / 10 + "M";
        if (size >= (double) 1024 * 1024 * 1024)
            filesize = (double) Math.round((double) 10 * size / (1024 * 1024 * 1024)) / 10 + "G";

        return filesize;
    }

    private String getExtension() {
        String extension = "";

        StringTokenizer fileTokenizer = new StringTokenizer(fileName, ".");
        if (this.isFile())
            while (fileTokenizer.hasMoreElements()) extension = fileTokenizer.nextToken();
        if (this.isDirectory())
            extension = "DIR";

        return extension;
    }

    private String getAttribues() {
        String attributes = "";
        if (!this.canWrite()) attributes += "R";
        if (this.isHidden()) attributes += "H";
        return attributes;
    }

    private String getDate() {
        Date date = new Date(this.lastModified()) {
            @SuppressWarnings("deprecation")
            @Override
            public int getYear() {
                return super.getYear() + 1900;
            }
            @SuppressWarnings("deprecation")
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder(28);
                CalendarUtils.sprintf0d(sb, super.getHours(), 2).append(':');   // HH
                CalendarUtils.sprintf0d(sb, super.getMinutes(), 2).append(':'); // mm
                CalendarUtils.sprintf0d(sb, super.getSeconds(), 2).append(", "); // ss
                CalendarUtils.sprintf0d(sb, super.getDay(), 2).append('.'); //day
                CalendarUtils.sprintf0d(sb, super.getMonth(), 2).append('.'); //month
                sb.append(this.getYear());  // yyyy
                return sb.toString();
            }
        };
        return date.toString();
    }

    private int createFileTypeConstant() {
        int type;
        if (this.isDirectory()) {
            type = DIRECTORY;
            return type;
        }
        switch (fileExtension.toLowerCase()) {
            case "ftpdir": type = DIRECTORY;
                break;
            case "exe": type = EXECUTABLE;
                break;
            case "dwg": type = ACAD_DWG;
                break;
            case "avi": type = AVI_VIDEO;
                break;
            case "bmp": type = BMP_IMG;
                break;
            case "tif": type = TIFF_IMG;
                break;
            case "tiff": type = TIFF_IMG;
                break;
            case "png": type = PNG_IMG;
                break;
            case "pcx": type = PCX_IMG;
                break;
            case "gif": type = GIF_IMG;
                break;
            case "eps": type = EPS_IMG;
                break;
            case "css": type = CSS_PROG;
                break;
            case "doc":
            case "docx":
            case "rtf": type = WINWORD_DOCUMENT;
                break;
            case "com": type = COMPILED;
                break;
            case "bat": type = BAT_SCRIPT;
                break;
            case "cmd": type = CMD_SCRIPT;
                break;
            case "ini": type = INI_SYSTEM;
                break;
            case "cfg": type = CFG_SYSTEM;
                break;
            case "sys": type = SYS_SYSTEM;
                break;
            case "jpg": type = JPG_IMG;
                break;
            case "tmp": type = TMP_SYSTEM;
                break;
            case "jpeg": type = JPG_IMG;
                break;
            case "tga": type = TGA_IMG;
                break;
            case "raw": type = RAW_IMG;
                break;
            case "java": type = JAVA_PROG;
                break;
            case "jar": type = JAR_ARCHIVE;
                break;
            case "war": type = WAR_ARCHIVE;
                break;
            case "cpp": type = CPP_PROG;
                break;
            case "js": type = JS_PROG;
                break;
            case "jre": type = JAVA_PROG;
                break;
            case "mp3": type = MPEG_AUDIO;
                break;
            case "flac": type = FLAC_AUDIO;
                break;
            case "ape": type = APE_AUDIO;
                break;
            case "mov": type = MOV_VIDEO;
                break;
            case "mpg":
            case "mpeg": type = MPEG_VIDEO;
                break;
            case "pdf": type = PDF_DOCUMENT;
                break;
            case "ppt": type = PPOINT_DOCUMENT;
                break;
            case "pptx": type = PPOINT_DOCUMENT;
                break;
            case "djvu": type = DJVU_DOCUMENT;
                break;
            case "psd": type = PSD_DOCUMENT;
                break;
            case "rar": type = RAR_ARCHIVE;
                break;
            case "tar": type = TAR_ARCHIVE;
                break;
            case "arj": type = ARJ_ARCHIVE;
                break;
            case "lha": type = LHA_ARCHIVE;
                break;
            case "ace": type = ACE_ARCHIVE;
                break;
            case "cab": type = CAB_ARCHIVE;
                break;
            case "txt": type = TXT_DOCUMENT;
                break;
            case "logEvent": type = LOG_SYSTEM;
                break;
            case "nfo": type = NFO_SYSTEM;
                break;
            case "me": type = ME_SYSTEM;
                break;
            case "readme": type = README_SYSTEM;
                break;
            case "ttf": type = TTF_FONT;
                break;
            case "font": type = FONT_FONT;
                break;
            case "fon": type = FON_FONT;
                break;
            case "torrent": type = TORRENT_SYSTEM;
                break;
            case "htm": type = HTM_BROWSER;
                break;
            case "html": type = HTML_BROWSER;
                break;
            case "url": type = URL_BROWSER;
                break;
            case "wav": type = WAV_AUDIO;
                break;
            case "wmv": type = WMV_VIDEO;
                break;
            case "xls":
            case "xlsx":
            case "csv": type = EXCEL_DOCUMENT;
                break;
            case "zip": type = ZIP_ARCHIVE;
                break;
            case "gz": type = GZ_ARCHIVE;
                break;
            case "7z": type = SZ_ARCHIVE;
                break;
            default: {
                if (this.isFile()) type = FILE;
                else
                    if (this.isDirectory()) type = DIRECTORY;
                    else
                        return (-1);
                break;
            }
        }
        return type;
    }

    private String identify() {
        String fileTypeDescription = "";
        if (fileTypeConstant == -1) fileTypeDescription = "To Parent Directory";
        if (fileTypeConstant == 0) fileTypeDescription = "Directory";
        if (fileTypeConstant == 1) fileTypeDescription = "File";
        if (fileTypeConstant > 1 && fileTypeConstant < 13) fileTypeDescription = "Application file";
        if (fileTypeConstant >= 13 && fileTypeConstant <= 14) fileTypeDescription = "Runnable script file";
        if (fileTypeConstant >= 20 && fileTypeConstant < 30) fileTypeDescription = "System file";
        if (fileTypeConstant >= 40 && fileTypeConstant < 50) fileTypeDescription = "Info file";
        if (fileTypeConstant >= 70 && fileTypeConstant < 80) fileTypeDescription = "System font file";
        if (fileTypeConstant >= 100 && fileTypeConstant < 200) fileTypeDescription = "Archive file";
        if (fileTypeConstant >= 200 && fileTypeConstant < 300) fileTypeDescription = "Document or drawing file";
        if (fileTypeConstant >= 300 && fileTypeConstant < 400) fileTypeDescription = "Video file";
        if (fileTypeConstant >= 400 && fileTypeConstant < 500) fileTypeDescription = "Audio file";
        if (fileTypeConstant >= 500 && fileTypeConstant < 600) fileTypeDescription = "Image file";
        if (fileTypeConstant >= 600 && fileTypeConstant < 700) fileTypeDescription = "Program listing file";
        if (fileTypeConstant >= 700 && fileTypeConstant < 800) fileTypeDescription = "Internet browser file";
        return fileTypeDescription;
    }


    /** OVERRIDDEN METHODS FROM PARENT File CLASS */

    @Override
    public String getAbsolutePath() {
        if (!isFTPfile)
            return super.getAbsolutePath();
        else
            return ftpFileAbsoluteName;
    }

    @Override
    public String getParent() {
        if (!isFTPfile)
            return super.getParent();
        else
            return ftpFileParentDirectory;
    }

    @Override
    public String getName() {
        if (!isFTPfile)
            return super.getName();
        else
            return ftpFile.getName();
    }

    @Override
    public boolean isFile() {
        if (this.isFTPfile)
            return ftpFile.isFile();
        else
            return super.isFile();
    }

    @Override
    public boolean isDirectory() {
        if (this.isFTPfile)
            return ftpFile.isDirectory();
        else
            return super.isDirectory();
    }

    @Override
    public String toString() {
        return  "\n*****************************************************************************************************" +
                "\nFilename: " + this.fileName +
                "\nFull path: " + this.getAbsolutePath() +
                "\nParental directory: " + this.getParent() +
                "\nExtension: " + fileExtension +
                "\nSize: " + fileSize +
                "\nType: " + fileTypeDescription +
                "\nFiletype constant: " + fileTypeConstant +
                "\nAttributes: " + attributesLetters +
                "\nLast modified: " + creationDate +
                "\nIs executable: " + isExecutable +
                "\nIs directory: " + this.isDirectory() +
                "\nAccess lock: " + isLocked +
                "\n*****************************************************************************************************";
    }
}