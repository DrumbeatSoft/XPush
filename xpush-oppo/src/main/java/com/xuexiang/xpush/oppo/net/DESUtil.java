package com.xuexiang.xpush.oppo.net;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * @program: drumbeatHR
 * @description:
 * @author: MengXF
 * @create: 2020-01-09 10:00
 */
public class DESUtil {

    /**
     * 鍋忕Щ鍙橀噺锛屽浐瀹氬崰8浣嶅瓧鑺�
     */
    private final static String IV_PARAMETER = "12345678";

    /**
     * 瀵嗛挜绠楁硶
     */
    private static final String ALGORITHM = "DES";

    /**
     * 鍔犲瘑/瑙ｅ瘑绠楁硶-宸ヤ綔妯″紡-濉厖妯″紡
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";

    /**
     * 榛樿缂栫爜
     */
    private static final String CHARSET = "utf-8";

    /**
     * 鐢熸垚key
     *
     * @param password
     * @return
     * @throws Exception
     */
    private static Key generateKey(String password) throws Exception {

        DESKeySpec dks = new DESKeySpec(password.getBytes(CHARSET));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }


    /**
     * DES鍔犲瘑瀛楃涓�
     *
     * @param password 鍔犲瘑瀵嗙爜锛岄暱搴︿笉鑳藉灏忎簬8浣�
     * @param data     寰呭姞瀵嗗瓧绗︿覆
     * @return 鍔犲瘑鍚庡唴瀹�
     */
    public static String encrypt(String password, String data) {

        if (password == null || password.length() < 8) {
            throw new RuntimeException("鍔犲瘑澶辫触锛宬ey涓嶈兘灏忎簬8浣�");
        }
        if (data == null) {
            return null;
        }
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return new String(Base64.getEncoder().encode(bytes));
            } else {
                return new String(Base64.getEncoder().encode(bytes));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES瑙ｅ瘑瀛楃涓�
     *
     * @param password 瑙ｅ瘑瀵嗙爜锛岄暱搴︿笉鑳藉灏忎簬8浣�
     * @param data     寰呰В瀵嗗瓧绗︿覆
     * @return 瑙ｅ瘑鍚庡唴瀹�
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String password, String data) {

        if (password == null || password.length() < 8) {
            throw new RuntimeException("鍔犲瘑澶辫触锛宬ey涓嶈兘灏忎簬8浣�");
        }
        if (data == null) {
            return null;
        }
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes(CHARSET))),
                    CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES鍔犲瘑鏂囦欢
     *
     * @param srcFile  寰呭姞瀵嗙殑鏂囦欢
     * @param destFile 鍔犲瘑鍚庡瓨鏀剧殑鏂囦欢璺緞
     * @return 鍔犲瘑鍚庣殑鏂囦欢璺緞
     */
    public static String encryptFile(String password, String srcFile, String destFile) {

        if (password == null || password.length() < 8) {
            throw new RuntimeException("鍔犲瘑澶辫触锛宬ey涓嶈兘灏忎簬8浣�");
        }
        try {
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey("p@ssw0rd123"), iv);
            InputStream is = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(destFile);
            CipherInputStream cis = new CipherInputStream(is, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = cis.read(buffer)) > 0) {
                out.write(buffer, 0, r);
            }
            cis.close();
            is.close();
            out.close();
            return destFile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * DES瑙ｅ瘑鏂囦欢
     *
     * @param srcFile  宸插姞瀵嗙殑鏂囦欢
     * @param destFile 瑙ｅ瘑鍚庡瓨鏀剧殑鏂囦欢璺緞
     * @return 瑙ｅ瘑鍚庣殑鏂囦欢璺緞
     */
    public static String decryptFile(String password, String srcFile, String destFile) {

        if (password == null || password.length() < 8) {
            throw new RuntimeException("鍔犲瘑澶辫触锛宬ey涓嶈兘灏忎簬8浣�");
        }
        try {
            File file = new File(destFile);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generateKey("p@ssw0rd123"), iv);
            InputStream is = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(destFile);
            CipherOutputStream cos = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = is.read(buffer)) >= 0) {
                cos.write(buffer, 0, r);
            }
            cos.close();
            is.close();
            out.close();
            return destFile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}