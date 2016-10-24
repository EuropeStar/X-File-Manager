package lessmeaning.x_filemanager;

import android.os.Environment;
import android.util.Log;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by avispa on 21.10.2016.
 */
public class Crypto {

    public static File storageEnc = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.system_file");
    private static Set<String> paths;
    public static final String EXTENSION = ".xfcrypted";

    public static void encryptFile(File input, String password) throws Exception {
        byte[] salt = "strong salt".getBytes();
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        String fileName = input.getAbsolutePath();
        File output = new File(fileName);
        FileInputStream fis = new FileInputStream(input);
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        input.delete();
        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(output), cipher);
        cos.write(iv);
        cos.write(buffer);
        cos.close();
        fis.close();
    }

    public static void decryptFile(File input, String password) throws Exception {
        byte[] salt = "strong salt".getBytes();
        byte[] iv = new byte[16];
        new FileInputStream(input).read(iv);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        String fileName = input.getAbsolutePath();
        File output = new File(fileName);
        FileInputStream fis = new FileInputStream(input);
        fis.skip(iv.length);
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        input.delete();
        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(output), cipher);
        cos.write(buffer);
        cos.close();
        fis.close();
    }

    public static String decryptFile1(File input, String password) throws Exception {
        byte[] salt = "strong salt".getBytes();
        byte[] iv = new byte[16];
        FileInputStream fis = new FileInputStream(input);
        fis.read(iv);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        System.out.println(cis.available() + " is available");
        byte[] message = new byte[fis.available()];
        cis.read(message);
        System.out.println(new String(message, "UTF-8"));
        cis.close();
        fis.close();
        return new String(message, "UTF-8");
    }

    public static void onStart(){
        paths = new HashSet<>();
        String TAG = "pizda";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(storageEnc));
            Log.d(TAG, "onStart: here bluatttt");

            while(reader.ready()) {
                String next = reader.readLine();
                Log.d(TAG, "onStart: e");
                paths.add(next);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void onFinish(){
        String TAG = "pizda";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(storageEnc));
            Iterator<String> it = paths.iterator();
            Log.d(TAG, "onFinish: yeas");
            while(it.hasNext()){
                String next = it.next();
                Log.d(TAG, "onFinish: " + next);
                writer.write(next);
                if(it.hasNext()){
                    writer.write("\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeFromEnc(File file) {
        String path = file.getAbsolutePath();
        paths.remove(path);
    }

    public static void addToEnc(File file){
        paths.add(file.getAbsolutePath());
    }

    public static ArrayList<String> getEncrypted(){
        ArrayList<String> res = new ArrayList<>();
        Iterator<String> it = paths.iterator();
        while(it.hasNext()){
            res.add(it.next());
        }
        return res;
    }
}
