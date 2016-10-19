package lessmeaning.x_filemanager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Максим on 17.10.2016.
 */

public class FileSearcher {

    private final MyHandler handler;
    private volatile ArrayList<String> resOfSearch;
    private SearchThread searchThread;

    public FileSearcher(MyHandler handler){
        this.handler = handler;
        handler.searcher = this;
    }

    public void cancelSearch(){
        if(searchThread != null){
            searchThread.cancelSearch();
        }
    }

    public void startSearch(String n, final File dir){
        final String name = n.toLowerCase();
        if(dir == null || !dir.exists() || !dir.isDirectory())
            return;
        resOfSearch = null;
        reloadSearcher(name, dir);
        new Thread(searchThread).start();
    }

    private void reloadSearcher(final String expression, File dir) {
        if(searchThread != null)
            searchThread.cancelSearch();
        searchThread = new SearchThread(expression, dir, handler, this);
    }

    public synchronized boolean setRes(SearchThread searcher, ArrayList<String> res) {
        if(this.searchThread == searcher){
            resOfSearch = res;
            return true;
        }
        return false;
    }

    public ArrayList<String> getResOfSearch() {
        return resOfSearch;
    }
}
