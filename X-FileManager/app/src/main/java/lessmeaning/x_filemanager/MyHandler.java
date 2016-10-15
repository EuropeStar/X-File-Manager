package lessmeaning.x_filemanager;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Максим on 15.10.2016.
 */
public class MyHandler extends Handler {
    private final MainActivity activity;
    public static final int COPY_FINISHED = 123, COPY_FAILED = 8;
    public MyHandler(MainActivity activity){
        super();
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        if(msg.what == COPY_FINISHED){
            activity.copyFinished();
        }else if(msg.what == COPY_FAILED){
            activity.copyFailed();
        }
    }
}
