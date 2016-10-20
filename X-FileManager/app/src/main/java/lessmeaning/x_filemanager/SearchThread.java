package lessmeaning.x_filemanager;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by Максим on 19.10.2016.
 */
public class SearchThread implements Runnable {
    final private String expression;
    final private MyHandler handler;
    final private FileSearcher parent;
    final String TAG = "searching";
    final private File dir;
    private final int ID;
    private volatile boolean searching;
    public SearchThread(String expression, File dir, MyHandler handler, FileSearcher parent, int id){
        this.ID = id;
        this.parent = parent;
        this.expression = expression;
        this.handler = handler;
        this.dir = dir;
        Log.d(TAG, "SearchThread: ");
        searching = true;
    }
    public void cancelSearch(){
        this.searching = false;
    }
    public void search(final String expression, File file){
        if(!searching) return;
        Log.d(TAG, "search: " + expression);
        if(file == null || !file.exists()) return ;
        if(file.getName().toLowerCase().contains(expression)){
            sendMessage(file.getAbsolutePath());
        }
        if(file.isDirectory()){
            File children[] = file.listFiles();
            if(children == null)
                return;
            for(File child : children){
                if(child == null) continue;
                search(expression, child);
            }
        }
//        showInLogs(res);
    }
    @Override
    public void run() {
        if(!searching) return;
        search(expression, dir);
        if(searching){
            sendMessage();
        }
    }

    private void sendMessage(final String PATH) {
        if(!searching) return;
        Message msg = new Message();
        msg.what = MyHandler.SEARCH_ITEM;
        msg.arg1 = ID;
        msg.obj = PATH;
        handler.sendMessage(msg);
    }

    private void sendMessage(){
        if(!searching) return;
        handler.sendEmptyMessage(MyHandler.SEARCH_FINISHED);
    }
}
