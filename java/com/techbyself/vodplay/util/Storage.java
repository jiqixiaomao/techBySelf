package com.techbyself.vodplay.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ckb on 18/1/9.
 */

public final class Storage {

    private final static String APP_ROOT = "AliSafePlayer";
    private final static String VIDEO_ROOT = "video";
    private final static String BAIDUVOICE_ROOT = "baiduTTS";
    private final static String VIDEO_FILE_EXTR = "_LD_m3u8.mp4";
    private final static String SELFTECH_VOICE = "selfTech";

    public static File getAppRootDir() {
        File rootDir = null;
        boolean hasExternalStorage = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasExternalStorage) {
            try {
                File externalDir = Environment.getExternalStorageDirectory();
                rootDir = new File(externalDir, APP_ROOT);
                if (!rootDir.exists()) {
                    rootDir.mkdirs();
                }
            } catch (Exception e) {
                Log.e("Storage", "root error", e);
            }
        }
        return rootDir;
    }

    public static File getVideoDir(Context context) {
        File rootDir = getAppRootDir();
        if (rootDir != null && rootDir.isDirectory()) {
            File videoDir = new File(rootDir, VIDEO_ROOT);
            if (!videoDir.exists())
                videoDir.mkdirs();
            return videoDir;
        }
        return context.getFilesDir();
    }

    public static File getVoiceRecogDir( ) {
        File rootDir = getAppRootDir();
        if (rootDir != null && rootDir.isDirectory()) {
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
            String  datestr=dateFormat.format(new Date());
            File videoDir = new File(rootDir, SELFTECH_VOICE+"/"+datestr);
            if (!videoDir.exists())
                videoDir.mkdirs();
            return videoDir;
        }
        return null;
    }


    public static String  getBDVoiceFileDir(Context context) {
        File rootDir = getAppRootDir();
        if (rootDir != null && rootDir.isDirectory()) {
            File voiceDir = new File(rootDir, BAIDUVOICE_ROOT);
            if (!voiceDir.exists()) {
                voiceDir.mkdirs();
            }
            String  sourcefile1="bd_etts_text.dat";
            String  sourcefile2="bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
            File destfileX= new File(voiceDir+"/"+sourcefile1);
            if (!destfileX.exists()) {
                Storage.filecopy(context,sourcefile1,voiceDir+"/"+sourcefile1);
            }
            String  destfile2=voiceDir+ "bd_etts_text.dat";
            File destfileY= new File(destfile2);
            if (!destfileY.exists()) {
                Storage.filecopy(context,sourcefile2,voiceDir+"/"+sourcefile2);
            }
            return voiceDir.getAbsolutePath();
        }
        return null;
    }


    public static boolean moveEncryptedToStorage(AssetManager assetsManager) {
        AssetManager am = assetsManager;
        try {
            String[] files = am.list("");
            File encryptedFile = getEncryptedFile();
            for (String filename : files) {
                if (TextUtils.equals(filename, encryptedFile.getName())) {
                    InputStream is = am.open(filename);
                    OutputStream os = new FileOutputStream(encryptedFile);
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        os.write(buffer, 0, read);
                    }
                    is.close();
                    os.close();
                    return true;
                }
            }

        } catch (IOException e) {
            Log.e("Storage", "Error copying asset files ", e);
        }
        return false;
    }

    public static String getVideoPath(Context context, String videoid) {

        File videoDir = Storage.getVideoDir(context);
        File[] files = videoDir.listFiles();
        if (files.length < 1) {
            return null;
        }
        StringBuffer strs = new StringBuffer(videoid).append(VIDEO_FILE_EXTR);
        String videopath = new String();
        for (int fileidx = 0; fileidx < files.length; fileidx++) {
            String curvideoid = files[fileidx].getName();
            if (curvideoid.equalsIgnoreCase(strs.toString())) {
                videopath = files[fileidx].getAbsolutePath();
                return videopath;
            }

        }
        return videopath;
    }

    //TODO 设置安全文件
    public static File getEncryptedFile() {
        return new File(Storage.getAppRootDir(), "encryptedApp.dat");
    }

    public static void filecopy(Context context, String sourcefile, String destfile) {
        try {
            InputStream is = context.getAssets().open(sourcefile);
            FileOutputStream fos = new FileOutputStream(new File(destfile));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
            fos.flush();//刷新缓冲区
            is.close();
            fos.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //如果捕捉到错误则通知UI线程
        }
    }

}
