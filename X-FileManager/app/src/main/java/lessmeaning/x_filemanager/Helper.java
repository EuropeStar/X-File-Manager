package lessmeaning.x_filemanager;

import java.io.File;

/**
 * Created by Максим on 14.10.2016.
 */
public class Helper {// there is some static functions

    public static String bufferPath;

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
}
