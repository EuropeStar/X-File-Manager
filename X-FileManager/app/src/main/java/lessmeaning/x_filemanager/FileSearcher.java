package lessmeaning.x_filemanager;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Максим on 17.10.2016.
 */
public class FileSearcher {

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
    public void startSearch(String n, final File dir){
        final String name = n.toLowerCase();
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
        if(file == null || !file.exists()){
            return res;
        }
        if(file.getName().toLowerCase().contains(expression)){
            res.add(file.getAbsolutePath());
        }
        if(file.isDirectory()){
            for(File child : file.listFiles()){
                if(child == null) continue;
                one = search(expression, child);
                if(one == null)
                    return null;
                res.addAll(one);
            }
        }
        return res;
    }
}
