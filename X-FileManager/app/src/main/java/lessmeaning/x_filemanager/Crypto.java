package lessmeaning.x_filemanager;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;

/**
 * Created by avispa on 21.10.2016.
 */
public class Crypto {

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
}
