package lessmeaning.x_filemanager;

import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Максим on 15.10.2016.
 */
public class MyHandler extends Handler {
    private final MainActivity activity;
    public FileSearcher searcher;
    public static final int COPY_FINISHED = 123, COPY_FAILED = 8, SEARCH_FINISHED = 333;
    public MyHandler(MainActivity activity){
        super();
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
//        final String TAG = "searching";
        if(msg.what == COPY_FINISHED){
            activity.copyFinished();
        }else if(msg.what == COPY_FAILED){
            activity.copyFailed();
        }else if(msg.what == SEARCH_FINISHED){
            searcher.cancelSearch();
            ArrayList<String> rawFiles = searcher.getResOfSearch();
            if(rawFiles == null) return;
            String paths[] = new String[rawFiles.size()];
            for (int i = 0; i < paths.length; i++) {
                paths[i] = rawFiles.get(i);
            }

            activity.setFiles(paths);
        }
    }
}
