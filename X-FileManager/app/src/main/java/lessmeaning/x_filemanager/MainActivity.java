package lessmeaning.x_filemanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {

    ListView filesView;
    File currentFile;
    TextView head;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        back = (Button) findViewById(R.id.button);
        back.setOnClickListener(this);
        filesView = (ListView) findViewById(R.id.childfiles);
        head = (TextView) findViewById(R.id.currentFile);
        currentFile = new File("/sdcard/");
        head.setText(currentFile.getName());
        openDirectory();
    }

    private void openDirectory() {
        Log.d("ListView",currentFile.toString());
        File children[] = currentFile.listFiles();
        String values[] = new String[children.length];
        for (int i = 0; i < values.length; i++){
            values[i] = children[i].getAbsolutePath();
        }
        new MyArrayAdapter(this,values,filesView);
    }

    public void openFile(String fileName){
        File next = new File(fileName);
        if(!next.isDirectory()){
            openFileInOtherApp(next);
            return;
        }
        if(next.listFiles() == null){
            Toast.makeText(this, "Empty folder", Toast.LENGTH_SHORT).show();
            return;
        }
        currentFile = new File(fileName);
        head.setText(currentFile.getName());
        openDirectory();
    }

    private void openFileInOtherApp(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url = file.getName();
        if (url.contains(".doc") || url.contains(".docx") || url.contains(".odt")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.contains(".ppt") || url.contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.contains(".xls") || url.contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.contains(".zip") || url.contains(".rar")) {
            // ZIP Files
            intent.setDataAndType(uri, "application/zip");
        } else if (url.contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.contains(".wav") || url.contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") ||
                url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == back.getId()){
            File parent = currentFile.getParentFile();
            if(parent == null) return;
            openFile(parent.getAbsolutePath() + "/");
        }
    }
}