package com.techbyself.vodplay.util;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.aliyun.vodplayer.downloader.AliyunDownloadManager;
import com.aliyun.vodplayer.media.AliyunVidSts;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final String  CURRENT_TOKEN="current_token";
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;
    public static final String REGION_CN_HANGZHOU = "cn-hangzhou";
    public static final String LOCAL_HOSTSERVER_IP = "http://59.110.228.212:8080/sts/get";
    public static final String LOCAL_HOSTSERVER_URL = "http://59.110.228.212:8080/sts/getVideoUrl";
    private AliyunVidSts mVidSts = new AliyunVidSts();
    private String downloadUrl;
    private AliyunDownloadManager mDownloadManager;
    private String mSavePath;
    private DownloadTask downloadTask;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };

    @Override
    protected Integer doInBackground(String... params) {
        String mVideoid = params[0];
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(LOCAL_HOSTSERVER_IP)
                .build();
        try {
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            String  responsebody=response.body().string();
            Map strmap=(Map) JSON.parse(responsebody);
            sAccessKeyId = strmap.get("accessKeyId").toString();
            sAccessKeySecret =strmap.get("accessKeySecret").toString();
            sSecurityToken =strmap.get("securityToken").toString();
            DownloadService.TokenMap.put(CURRENT_TOKEN,strmap);
            downloadBinder.startDownload(mVideoid,sAccessKeyId,sAccessKeySecret,sSecurityToken);

            return 1;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  0;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    @Override
    protected void onPostExecute(Integer status) {

    }

    public void pauseDownload() {
        isPaused = true;
    }


    public void cancelDownload() {
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

    public static String playurl;
    public static String sAccessKeyId;
    public static String sAccessKeySecret;
    public static String sSecurityToken;

}