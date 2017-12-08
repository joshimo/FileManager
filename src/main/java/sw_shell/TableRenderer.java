package sw_shell;

import java.util.Vector;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class TableRenderer extends JLabel implements TableCellRenderer {

    Vector<Vector<String>> data;

    public TableRenderer(Vector<Vector<String>> data) {
        this.data = data;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        String iconFolderPath = "src\\main\\resources\\collection1\\";

        ImageIcon upIcon = new ImageIcon(iconFolderPath + "up2.png");
        ImageIcon folderIcon = new ImageIcon(iconFolderPath + "folder32.png");
        ImageIcon fileIcon = new ImageIcon(iconFolderPath + "config32.png");
        ImageIcon fontIcon = new ImageIcon(iconFolderPath + "font32.png");
        ImageIcon exeIcon = new ImageIcon(iconFolderPath + "start32.png");
        ImageIcon acadIcon = new ImageIcon(iconFolderPath + "autocad32.png");
        ImageIcon aviIcon = new ImageIcon(iconFolderPath + "avi32.png");
        ImageIcon javaIcon = new ImageIcon(iconFolderPath + "java32.png");
        ImageIcon bmpIcon = new ImageIcon(iconFolderPath + "bmp32.png");
        ImageIcon cssIcon = new ImageIcon(iconFolderPath + "css32.png");
        ImageIcon wordIcon = new ImageIcon(iconFolderPath + "docx32.png");
        ImageIcon gifIcon = new ImageIcon(iconFolderPath + "gif32.png");
        ImageIcon iniIcon = new ImageIcon(iconFolderPath + "ini32.png");
        ImageIcon jpegIcon = new ImageIcon(iconFolderPath + "jpeg32.png");
        ImageIcon musicIcon = new ImageIcon(iconFolderPath + "midi32.png");
        ImageIcon movIcon = new ImageIcon(iconFolderPath + "mov32.png");
        ImageIcon mpegIcon = new ImageIcon(iconFolderPath + "mpeg32.png");
        ImageIcon pdfIcon = new ImageIcon(iconFolderPath + "pdf32.png");
        ImageIcon ppointIcon = new ImageIcon(iconFolderPath + "ppt32.png");
        ImageIcon psdIcon = new ImageIcon(iconFolderPath + "psd32.png");
        ImageIcon rarIcon = new ImageIcon(iconFolderPath + "rar32.png");
        ImageIcon textIcon = new ImageIcon(iconFolderPath + "readme32.png");
        ImageIcon torrentIcon = new ImageIcon(iconFolderPath + "torrent32.png");
        ImageIcon htmlIcon = new ImageIcon(iconFolderPath + "url32.png");
        ImageIcon wavIcon = new ImageIcon(iconFolderPath + "wav32.png");
        ImageIcon wmvIcon = new ImageIcon(iconFolderPath + "wmv32.png");
        ImageIcon excelIcon = new ImageIcon(iconFolderPath + "xlsx32.png");
        ImageIcon zipIcon = new ImageIcon(iconFolderPath + "zip32.png");

        if ((row > 0) & (column == 0))
            switch (data.get(row).get(1).toLowerCase()) {
                case "dir":
                case "ftpdir":
                    setIcon(folderIcon);
                    break;
                case "exe":
                    setIcon(exeIcon);
                    break;
                case "dwg":
                    setIcon(acadIcon);
                    break;
                case "avi":
                    setIcon(aviIcon);
                    break;
                case "bmp":
                case "tif":
                case "tiff":
                case "png":
                case "pcx":
                case "eps":
                    setIcon(bmpIcon);
                    break;
                case "css":
                    setIcon(cssIcon);
                    break;
                case "doc":
                case "docx":
                case "rtf":
                    setIcon(wordIcon);
                    break;
                case "gif":
                    setIcon(gifIcon);
                    break;
                case "bat":
                case "ini":
                case "cfg":
                case "sys":
                    setIcon(iniIcon);
                    break;
                case "jpg":
                case "jpeg":
                case "raw":
                    setIcon(jpegIcon);
                    break;
                case "java":
                case "jar":
                case "jre":
                    setIcon(javaIcon);
                    break;
                case "mp3":
                    setIcon(musicIcon);
                    break;
                case "mov":
                    setIcon(movIcon);
                    break;
                case "mpg":
                case "mpeg":
                    setIcon(mpegIcon);
                    break;
                case "pdf":
                    setIcon(pdfIcon);
                    break;
                case "ppt":
                case "pptx":
                    setIcon(ppointIcon);
                    break;
                case "psd":
                    setIcon(psdIcon);
                    break;
                case "rar":
                case "tar":
                case "arj":
                case "lha":
                case "ace":
                case "cab":
                    setIcon(rarIcon);
                    break;
                case "txt":
                case "nfo":
                case "me":
                case "readme":
                    setIcon(textIcon);
                    break;
                case "ttf":
                case "font":
                case "fon":
                    setIcon(fontIcon);
                    break;
                case "torrent":
                    setIcon(torrentIcon);
                    break;
                case "htm":
                case "html":
                case "url":
                    setIcon(htmlIcon);
                    break;
                case "wav":
                    setIcon(wavIcon);
                    break;
                case "wmv":
                    setIcon(wmvIcon);
                    break;
                case "xls":
                case "xlsx":
                case "csv":
                    setIcon(excelIcon);
                    break;
                case "zip":
                case "7z":
                    setIcon(zipIcon);
                    break;
                default:
                    setIcon(fileIcon);
                    break;
            }
        else
            setIcon(null);

        if ((row == 0) & (column == 0)) {
            setHorizontalAlignment(CENTER);
            setForeground(new Color(150, 150, 190));
            setIcon(upIcon);
        }

        if ((column == 1) | (column == 3) | (column == 4))
            setHorizontalAlignment(CENTER);
        else
            if (row != 0) setHorizontalAlignment(LEFT);

        if (row !=0) {
            if (data.get(row).get(1).equals("DIR") | data.get(row).get(1).equals("ftpDIR")) setForeground(new Color(10, 10, 60));
            else setForeground(new Color(90, 90, 150));
            if (data.get(row).get(1).toLowerCase().equals("exe")) setForeground(new Color(90, 150, 90));
            if (data.get(row).get(4).contains("H")) setForeground(new Color(150, 150, 150));
            if (data.get(row).get(4).contains("R")) setForeground(new Color(250, 120, 120));
        }

        if (isSelected) {
            setOpaque(true);
            setBackground(new Color(200,200,250));
        }
        else
            setOpaque(false);

        setText(data.get(row).get(column));
        return this;
    }
}