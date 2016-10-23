package lessmeaning.x_filemanager;

import android.os.Message;

import java.io.File;

/**
 * Created by Максим on 23.10.2016.
 */
public class FileEncryptor{

    public static final String TAG = "crypt";

    private final MyHandler hanler;

    public FileEncryptor(MyHandler handler){
        this.hanler = handler;
    }

    public boolean startEncrypt(final File file,final String pass){
        if(file == null || file.isDirectory() || !file.exists()) return false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                try {
                    Crypto.encryptFile(file,pass);
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(success){
                    File newFile = new File(file.getParentFile().getAbsolutePath() + 
                            "/" + file.getName() + Crypto.EXTENSION);
                    file.renameTo(newFile);
                }
                sendMessage(success);
            }
        }).start();
        return true;
    }

    public void sendMessage(boolean success){
        Message msg = new Message();
        msg.obj = success;
        msg.what = MyHandler.CRYPT_FINISHED;
        hanler.sendMessage(msg);
    }

    public boolean startDecrypt(final File fileMain, final String pass){
        if(fileMain == null || fileMain.isDirectory() || !fileMain.exists()) return false;
        final File file = new File(fileMain.getParentFile().getAbsolutePath() + "/" +
                fileMain.getName().split(Crypto.EXTENSION)[0]);
        Helper.copyFromTo(fileMain, file);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                try {
                    Crypto.decryptFile(file, pass);
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(success) {
                    fileMain.delete();
                } else {
                    file.delete();
                }
                sendMessage(success);
            }
        }).start();

        return true;
    }
}
