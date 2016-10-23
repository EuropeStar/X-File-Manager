package lessmeaning.x_filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Максим on 14.10.2016.
 */
public class Helper {// there is some static functions

    public static String bufferPath;
    public static float scaleX;
    public static final int WIDTH = 720;

    public static String typeOfFile(File file){
        String url = file.getName();
        String type;
        if (url.contains(".doc") || url.contains(".docx") || url.contains(".odt")) {
            // Word document
            type = "application/msword";
        } else if (url.contains(".pdf")) {
            // PDF file
            type = "application/pdf";
        } else if (url.contains(".ppt") || url.contains(".pptx")) {
            // Powerpoint file
            type =  "application/vnd.ms-powerpoint";
        } else if (url.contains(".xls") || url.contains(".xlsx")) {
            // Excel file
            type = "application/vnd.ms-excel";
        } else if (url.contains(".zip") || url.contains(".rar")) {
            // ZIP Files
            type = "application/zip";
        } else if (url.contains(".rtf")) {
            // RTF file
            type = "application/rtf";
        } else if (url.contains(".wav") || url.contains(".mp3")) {
            // WAV audio file
            type = "audio/x-wav";
        } else if (url.contains(".gif")) {
            // GIF file
            type = "image/gif";
        } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
            // JPG file
            type = "image/jpeg";
        } else if (url.contains(".txt")) {
            // Text file
            type = "text/plain";
        } else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") ||
                url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
            // Video files
            type = "video/*";
        } else {
            type = "*/*";
        }
        return type;
    }

    public static boolean deleteFile(File file){
        boolean success;
        if(file.isDirectory()){
            File children[] = file.listFiles();
            for(File child : children){
                deleteFile(child);
            }
            success = file.delete();
        }else {
            success = file.delete();
        }
        return success;
    }

    public static boolean copyFromTo(File from, File to){
        try {
            FileInputStream input = new FileInputStream(from);
            FileOutputStream output = new FileOutputStream(to);
            byte bytes[] = new byte[1024];
            int num;
            while ((num = input.read(bytes)) != -1) {
                output.write(bytes, 0, num);
            }
            input.close();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copyOneFile(File from, File dir) {
        File to = new File(dir.getAbsolutePath() +
                "/" +
                from.getName());
        while (to.exists()) {
            to = new File(to.getParentFile().getAbsolutePath() + "/" +
                    "copy_" +
                    to.getName());
        }

        return copyFromTo(from, to);
    }

}
