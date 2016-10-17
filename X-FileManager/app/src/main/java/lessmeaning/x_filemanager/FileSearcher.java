package lessmeaning.x_filemanager;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Максим on 17.10.2016.
 */
public class FileSearcher {
    /*
    TODO: search in folder names
    TODO: search by lowerCase
     */
    volatile boolean searching = false;
    final String TAG = "searching";
    MyHandler handler;
    Thread searchThread;
    volatile ArrayList<String> resOfSearch;
    public FileSearcher( MyHandler handler){
        this.handler = handler;
        handler.searcher = this;
    }
    public void cancelSearch(){
        if(searching && searchThread != null && searchThread.isAlive()){
            searching = false;
            searchThread.interrupt();
//            try {
//                searchThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
    public void startSearch(final String name, final File dir){
        if(dir == null || !dir.exists() || !dir.isDirectory())
            return;

        cancelSearch();
        Log.d(TAG, "startSearch: reached");
        searching = true;
        resOfSearch = null;
        searchThread = new Thread(){
            @Override
            public void run() {
                resOfSearch = search(name, dir);
                Log.d(TAG, "run: reached");
                handler.sendEmptyMessage(MyHandler.SEARCH_FINISHED);
            }
        };
        searchThread.start();
    }

    private ArrayList<String> search(final String expression, File file){
        if(!searching) return null;
        ArrayList<String> res = new ArrayList<>();
        ArrayList<String> one ;
        if(!file.exists()){
            return res;
        }
        if(file.isDirectory()){
            for(File child : file.listFiles()){
                one = search(expression, child);
                if(one == null)
                    return null;
                res.addAll(one);
            }
        }else {
            Log.d(TAG, "search: " + file.getName());
            Log.d(TAG, "search: " + expression);
            if(file.getName().contains(expression)){
                Log.d(TAG, "search: find" + file.getName());
                res.add(file.getAbsolutePath());
            }
        }
        return res;
    }
}
