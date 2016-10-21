package lessmeaning.x_filemanager;

import android.content.Intent;
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
    public static final String TAG = "searchagain";

    public FileWrapper(MainActivity activity, File file, TextView view){
        this.file = file;
        this.activity = activity;
        this.view = view;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

    }
//TODO init in add item

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: filewrapper " + file.getName());
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
}
