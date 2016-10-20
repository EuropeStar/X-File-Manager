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
    final String TAG = "newsearch";
    public static final int COPY_FINISHED = 123, COPY_FAILED = 8, SEARCH_FINISHED = 333, SEARCH_ITEM = 158;
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
            activity.onMySearchFinished();
        }else if(msg.what == SEARCH_ITEM){
            Log.d(TAG, "handleMessage: arg1 = " + msg.arg1);
            Log.d(TAG, "handleMessage: currentThread == " + searcher.getCurrentThread());
            if(msg.arg1 != searcher.getCurrentThread()) return;
            activity.addItem((String)msg.obj);

        }
    }
}
