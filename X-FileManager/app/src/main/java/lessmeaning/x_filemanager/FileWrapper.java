package lessmeaning.x_filemanager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

/**
 * Created by Максим on 14.10.2016.
 */
public class FileWrapper implements View.OnClickListener, View.OnLongClickListener {
    private final File file;
    private final MainActivity activity;
    private final TextView view;
    public final int ICON_SIZE;

    public FileWrapper(MainActivity activity, File file, TextView view){
        this.file = file;
        this.activity = activity;
        this.view = view;
        ICON_SIZE = (int)(110.0 * Helper.scaleX);
        detType();
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        activity.openFile(file);
    }

    public File getFile() {
        return file;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public String getName() {
        return file.getName();
    }

    public boolean renameFile(String newFileName) {
        if(newFileName.contains(".")) return false;
        String parts[] = file.getName().split("\\.");
        String extinsion = "";
        if(parts.length > 1){
            extinsion = "." + parts[1];
        }
        return file.renameTo(
                new File(file.getParentFile().getAbsolutePath() +
                        "/" +
                        newFileName +
                        extinsion));
    }

    public void shareFile(){
        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType(Helper.typeOfFile(file));
        activity.startActivity(intent);
    }

    public void delete() {
        String fileName = file.getName();
        if(Helper.deleteFile(file)){
            Toast.makeText(activity,
                    "deleted " +
                    fileName,
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(activity,
                    "failed to delete " +
                            fileName,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void choose(boolean choice){
        final int BACK = choice ? R.color.backgroundcolor : R.drawable.fileitem;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(activity.getDrawable(BACK));
        }else{
            view.setBackgroundDrawable(activity.getResources().getDrawable(BACK));
        }

    }

    @Override
    public boolean onLongClick(View view) {
        choose(true);
        activity.showPopMenu(this);
        return true;
    }

    private void detType() {
        String name = file.getName();
        int type = R.mipmap.unknown_type;;
        if (file.isDirectory()) {
            type = R.mipmap.folder_icon;
        }else if (name.contains(Crypto.EXTENSION)) {
            type = R.mipmap.encrypted_icon;
        }else if (name.contains(".jp") || name.contains(".png")){
            type = R.mipmap.jpg_icon;
        } else if (name.contains(".avi")) {
            type = R.mipmap.avi_icon;
        } else if (name.contains(".bmp")) {
            type = R.mipmap.bmp_icon;
        } else if (name.contains(".css")) {
            type = R.mipmap.css_icon;
        } else if (name.contains(".doc")) {
            type = R.mipmap.doc_icon;
        } else if (name.contains(".gif")) {
            type = R.mipmap.gif_icon;
        } else if (name.contains(".htm")) {
            type = R.mipmap.htm_icon;
        } else if (name.contains(".ini")) {
            type = R.mipmap.ini_icon;
        } else if (name.contains(".mov")) {
            type = R.mipmap.mov_icon;
        } else if (name.contains("mpeg")) {
            type = R.mipmap.mpeg_icon;
        } else if (name.contains(".mp")) {
            type = R.mipmap.mp3_icon;
        } else if (name.contains(".pdf") ||
                name.contains(".djvu")) {
            type = R.mipmap.pdf_icon;
        } else if (name.contains(".ppt")) {
            type = R.mipmap.ppt_icon;
        } else if (name.contains(".rar")) {
            type = R.mipmap.rar_icon;
        } else if (name.contains(".txt") ||
                name.contains(".text") ||
                name.contains(".rtf") ||
                name.contains(".fb2")) {
            type = R.mipmap.txt_icon;
        } else if (name.contains(".tiff")) {
            type = R.mipmap.tiff_icon;
        } else if (name.contains(".url")) {
            type = R.mipmap.url_icon;
        } else if (name.contains(".wav")) {
            type = R.mipmap.wav_icon;
        } else if (name.contains(".wm")) {
            type = R.mipmap.wav_icon;
        } else if (name.contains(".zip")) {
            type = R.mipmap.zip_icon;
        }
        Drawable pic;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pic = activity.getDrawable(type);
        }else{
            pic = activity.getResources().getDrawable(type);
        }
        pic.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
        view.setCompoundDrawables(pic, null, null, null);

    }

}
