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
    public static final int COPY_FINISHED = 123, COPY_FAILED = 8,
            SEARCH_FINISHED = 333, SEARCH_ITEM = 158,
            CRYPT_FINISHED = 222;

    public MyHandler(MainActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == COPY_FINISHED) {
            activity.copyFinished();
        } else if (msg.what == COPY_FAILED) {
            activity.copyFailed();
        } else if (msg.what == SEARCH_FINISHED) {
            activity.onMySearchFinished();
        } else if (msg.what == SEARCH_ITEM) {
            if (msg.arg1 != searcher.getCurrentThread()) return;
            activity.addItem((String) msg.obj);
        } else if (msg.what == CRYPT_FINISHED){
            activity.encrytFinished((boolean)msg.obj);
        }
    }
}
