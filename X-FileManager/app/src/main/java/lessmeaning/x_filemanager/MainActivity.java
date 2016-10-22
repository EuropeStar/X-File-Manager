package lessmeaning.x_filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, TextWatcher {

    private ListView filesView;
    private ProgressBar bar;
    private File currentFile;
    private File rootCopy;
    private TextView head;
    private Button paste, addButton;
    private AlertDialog waitingCopy;
    private volatile boolean copyStopped;
    private File copyingFile;
    private boolean isSearching = false;
    private RelativeLayout hat;
    private EditText searchInput;
    private MyHandler handler;
    private FileSearcher searcher;
    private MyArrayAdapter adapter;
    /*
    TODO: change design of drawer
    TODO: icons
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"
            }, 102);
        }
        setContentView(R.layout.activity_main);
        NavigationView view = (NavigationView) findViewById(R.id.nav_view);
        view.setNavigationItemSelectedListener(this);
        hat = (RelativeLayout) findViewById(R.id.hat);
        addButton = (Button) findViewById(R.id.newfile);
        addButton.setOnClickListener(this);
        paste = (Button) findViewById(R.id.paste);
        paste.setOnClickListener(this);
        filesView = (ListView) findViewById(R.id.childfiles);
        head = (TextView) findViewById(R.id.currentFile);
        currentFile = Environment.getExternalStorageDirectory();
        head.setText(currentFile.getName());
        handler = new MyHandler(this);
        searchInput = new EditText(this);
        searcher = new FileSearcher(handler);
        openDirectory();
    }

    private void startFileSearch(){
        isSearching = true;
        setVisibility(View.GONE);
        LayoutInflater inflater = getLayoutInflater();
        searchInput = (EditText) inflater.inflate(R.layout.search_edittext, hat, false);
        searchInput.addTextChangedListener(this);
        bar = new ProgressBar(this);
        bar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        hat.addView(searchInput);
        hat.addView(bar);
        bar.setVisibility(View.GONE);
        setFiles(new ArrayList<String>());
    }

    private void setVisibility(final int VIS){
        paste.setVisibility(VIS);
        addButton.setVisibility(VIS);
        head.setVisibility(VIS);
    }

    private void cancelFileSearching(){
        Log.d("design", "cancelFileSearching: !!!");
        if(!isSearching) return;
        isSearching = false;
        searcher.cancelSearch();
        removeKeyBoard();
        searchInput.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
        hat.removeView(searchInput);
        setVisibility(View.VISIBLE);
    }

    public void addItem(String newItem) {
        if(adapter != null) {
            adapter.add(newItem);
        }
    }

    public void onMySearchFinished() {
        if(bar != null){
            bar.setVisibility(View.GONE);
        }
    }

    private void openDirectory() {
        if(isSearching)
            cancelFileSearching();
        else if(searchInput != null){
            searchInput.setVisibility(View.GONE);
        }
        Log.d("ListView",currentFile.toString());
        File children[] = currentFile.listFiles();
        ArrayList<String> values = new ArrayList<>(children.length);
        for(File child : children){
            values.add(child.getAbsolutePath());
        }
        head.setText(currentFile.getName());
        setFiles(values);
    }

    public void setFiles(ArrayList<String> values){
        adapter = new MyArrayAdapter(this,values,filesView);
    }

    public boolean openFile(File file){
        if(!file.isDirectory()){
            openFileInOtherApp(file);
            return true;
        }
        if(!file.exists()){
            return false;
        }
        if(file.listFiles() == null){
            return true;
        }
        currentFile = file;
        openDirectory();
        return true;
    }

    private void openFileInOtherApp(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri,Helper.typeOfFile(file));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean openUpperDir(){
        File parent = currentFile.getParentFile();
        if(parent == null) return false;
        return openFile(parent);
    }

    private void openCreationDialog(final boolean isDir) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText text = new EditText(this);
        builder.setView(text);
        builder.setPositiveButton("Make", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                create(text.getText().toString(), isDir);
                openDirectory();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private void create(String fileName, boolean isDir) {
        File file = new File(currentFile.getAbsolutePath() + "/" + fileName);
        if(isDir){
            file.mkdir();
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                fileWrapper.choose(false);
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

    private void startCopy(final File from) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ProgressBar progress = new ProgressBar(this);
        builder.setView(progress);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                copyStopped = true;
            }
        });
        waitingCopy = builder.create();
        waitingCopy.show();
        copyStopped = false;
        copyingFile = null;
        rootCopy = null;

        new Thread() {
            @Override
            public void run() {
                if (copy(from, currentFile)) {
                    handler.sendEmptyMessage(MyHandler.COPY_FINISHED);
                } else {
                    handler.sendEmptyMessage(MyHandler.COPY_FAILED);
                }
            }
        }.start();
    }

    private File createNameOfChild(File dir, File from){
        File res = new File(dir.getAbsolutePath() + "/" +
                from.getName());

        while(res.exists()){
            res = new File(res.getParentFile().getAbsolutePath() + "/" +
                    "copy_" +
                    res.getName());

        }
        return res;
    }

    public boolean copy(File from, File dir) {
        if(copyStopped) return false;
        if(rootCopy == null){
            rootCopy = createNameOfChild(dir, from);
        }else {
            if (from.getAbsolutePath().equals(rootCopy.getAbsolutePath())) return true;
        }

        if(from.isDirectory()){
            dir = createNameOfChild(dir, from);
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

    public void copyFinished() {
        Toast.makeText(this,
                "copy successful",
                Toast.LENGTH_SHORT).show();
        waitingCopy.cancel();
        openDirectory();
    }

    public void copyFailed() {
        Toast.makeText(this,
                "copy failed",
                Toast.LENGTH_SHORT).show();
        if(copyingFile != null || copyingFile.exists()){
            Helper.deleteFile(copyingFile);
        }
    }

    private void removeKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == paste.getId()){
            if(Helper.bufferPath == null) return;
            File from = new File(Helper.bufferPath);
            if(!from.exists()) return;
            startCopy(from);
        }else if (view.getId() == addButton.getId()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton("File", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    openCreationDialog(false);
                }
            });
            builder.setNegativeButton("Folder", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    openCreationDialog(true);
                }
            });
            builder.create().show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isSearching) {
                openDirectory();
                return true;
            }else if(openUpperDir()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        cancelFileSearching();
        String msg = "";
        if (id == R.id.home) {
            currentFile = Environment.getExternalStorageDirectory();
            openDirectory();
            msg = "home";
        } else if (id == R.id.search) {
            msg = "search";
            startFileSearch();
        } else if (id == R.id.encrypted) {
            msg = "encrypted";
        }

        Toast.makeText(this,
                msg,
                Toast.LENGTH_SHORT).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(isSearching){
            setFiles(new ArrayList<String>());
            if(bar != null)
                bar.setVisibility(View.VISIBLE);
            searcher.startSearch(charSequence.toString(),currentFile);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {}

}