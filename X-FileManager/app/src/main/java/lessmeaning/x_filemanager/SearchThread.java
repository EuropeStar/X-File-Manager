package lessmeaning.x_filemanager;

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
    ArrayList<String> res;
    final private File dir;
    private volatile boolean searching;
    public SearchThread(String expression, File dir, MyHandler handler, FileSearcher parent){
        this.parent = parent;
        this.expression = expression;
        this.handler = handler;
        this.dir = dir;
        Log.d(TAG, "SearchThread: ");
        res = new ArrayList<>();
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
            res.add(file.getAbsolutePath());
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

    private void sendMessage() {
        if(parent.setRes(this,res)) {
            handler.sendEmptyMessage(MyHandler.SEARCH_FINISHED);
        }
    }
}
