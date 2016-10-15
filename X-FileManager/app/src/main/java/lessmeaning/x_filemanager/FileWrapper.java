package lessmeaning.x_filemanager;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;

/**
 * Created by Максим on 14.10.2016.
 */
public class FileWrapper implements View.OnClickListener {
    private final File file;
    private final MainActivity activity;

    public FileWrapper(MainActivity activity,File file, Button open, Button opt){
        this.file = file;
        this.activity = activity;
        open.setOnClickListener(this);
        opt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.opt:
                activity.showPopMenu(this);
                return;
            case R.id.content:
                activity.openFile(file);
                return;
            default:
                throw new RuntimeException("unknown button is clicked");
        }
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
}
