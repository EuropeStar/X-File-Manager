package lessmeaning.x_filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {

    ListView filesView;
    File currentFile;
    TextView head;
    Button paste;
    View mainView;
    AlertDialog waitingCopy;
    private Thread copyThread;
    private boolean stopped;
    private File copyingFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainView = findViewById(R.id.mainView);
        paste = (Button) findViewById(R.id.paste);
        paste.setOnClickListener(this);
        filesView = (ListView) findViewById(R.id.childfiles);
        head = (TextView) findViewById(R.id.currentFile);
        currentFile = Environment.getExternalStorageDirectory();
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

    public void openFile(File file){
        if(!file.isDirectory()){
            openFileInOtherApp(file);
            return;
        }
        if(file.listFiles() == null){
            Toast.makeText(this, "Empty folder", Toast.LENGTH_SHORT).show();
            return;
        }
        currentFile = file;
        head.setText(currentFile.getName());
        openDirectory();
    }

    private void openFileInOtherApp(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri,Helper.typeOfFile(file));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            openUpperDir();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void openUpperDir(){
        File parent = currentFile.getParentFile();
        if(parent == null) return;
        openFile(parent);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == paste.getId()){
            if(Helper.bufferPath == null) return;
            File from = new File(Helper.bufferPath);
            if(!from.exists()) return;
            startCopy(from);
        }
    }

    private void startCopy(final File from) {
        final MyHandler handler = new MyHandler(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ProgressBar progress = new ProgressBar(this);
        builder.setView(progress);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stopped = true;
            }
        });
        waitingCopy = builder.show();
        waitingCopy.show();
        stopped = false;
        copyingFile = null;
        copyThread = new Thread() {
            @Override
            public void run() {
                if(copy(from, currentFile)){
                    handler.sendEmptyMessage(MyHandler.COPY_FINISHED);
                }else {
                    handler.sendEmptyMessage(MyHandler.COPY_FAILED);
                }
            }
        };
        copyThread.start();
    }

    public boolean copy(File from, File dir) {
        if(stopped) return false;

        if(from.isDirectory()){
            dir = new File(dir.getAbsolutePath() + "/" +
                            from.getName());
            while(dir.exists()){
                dir = new File(dir.getParentFile().getAbsolutePath() + "/" +
                                "copy_" +
                                dir.getName());

            }
            if(copyingFile == null){
                copyingFile = dir;
            }
            dir.mkdirs();
            boolean succes = true;
            for(File file : from.listFiles()){
                if(!copy(file, dir))
                    succes = false;
            }
            return succes;
        }else {
            return copyOneFile(from, dir);
        }
    }

    private boolean copyOneFile(File from, File dir) {
        File to = new File(dir.getAbsolutePath() +
                "/" +
                from.getName());
        while(to.exists()){
            to = new File(to.getParentFile().getAbsolutePath() + "/" +
                    "copy_" +
                            to.getName());
        }
        if(copyingFile == null){
            copyingFile = to;
        }
        try {
            FileInputStream  input = new FileInputStream(from);
            FileOutputStream output = new FileOutputStream(to);
            byte bytes[] = new byte[1024];
            int num;
            while((num = input.read(bytes)) != -1){
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

    public void showPopMenu(final FileWrapper fileWrapper) {
        PopupMenu popupMenu = new PopupMenu(this, head);
        popupMenu.inflate(R.menu.popmenu);
        final boolean isDir = fileWrapper.isDirectory();
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.delete:
                                fileWrapper.delete();
                                openDirectory();
                                return true;
                            case R.id.open:
                                openFile(fileWrapper.getFile());
                                return true;
                            case R.id.copy:
                                Helper.bufferPath = fileWrapper.
                                        getFile().
                                        getAbsolutePath();
                                return true;
                            case R.id.encrypt:
                                if(isDir){
                                    Toast.makeText(getApplicationContext(),
                                            "You cannot encrypt directory",
                                            Toast.LENGTH_LONG).show();
                                }else{
                                    //TODO : encrypt
                                }
                                return true;
                            case R.id.share:
                                if(isDir){
                                    Toast.makeText(getApplicationContext(),
                                            "You cannot share directory",
                                            Toast.LENGTH_LONG).show();
                                }else{
                                    fileWrapper.shareFile();
                                }
                                return true;
                            case R.id.rename:
                                openRenameDialog(fileWrapper);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "onDismiss",
                        Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }

    private void openRenameDialog(final FileWrapper fileWrapper) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText text = new EditText(this);
        text.setHint(fileWrapper.getName().split("\\.")[0]);
        builder.setView(text);
        builder.setMessage("Rename")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(fileWrapper.renameFile(text.getText().toString())){
                            Toast.makeText(getApplicationContext(),
                                    "Renamed",
                                    Toast.LENGTH_SHORT).show();
                            openDirectory();
                        }else{
                            Toast.makeText(getApplicationContext(),
                                    "Can't do this",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void copyFinished() {
//        TODO: <code>this</code>
        Toast.makeText(this,
                "copy finished",
                Toast.LENGTH_LONG).show();
        waitingCopy.cancel();
        openDirectory();
    }

    public void copyFailed() {
        if(copyingFile != null || copyingFile.exists()){
            Helper.deleteFile(copyingFile);
        }
    }
}