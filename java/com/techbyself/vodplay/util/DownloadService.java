package com.techbyself.vodplay.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.aliyun.vodplayer.downloader.AliyunDownloadConfig;
import com.aliyun.vodplayer.downloader.AliyunDownloadInfoListener;
import com.aliyun.vodplayer.downloader.AliyunDownloadManager;
import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.aliyun.vodplayer.downloader.AliyunRefreshStsCallback;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.bignerdranch.anriod.vidioplayer.R;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadService extends Service {
  //  public static final int[] NCE_COUNT = {72,48,30,24};

    private AliyunVidSts mVidSts = new AliyunVidSts();
    private String videoid;
    private AliyunDownloadManager mDownloadManager;
    private String mSavePath;
    private DownloadTask downloadTask;
    public  static Map TokenMap=new HashMap<>();
    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager = AliyunDownloadManager.getInstance(this);
        configDownload();
      TechResourceUtil.initXmlinfo(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

   public class DownloadBinder extends Binder {

        public void startDownload(String videoid,String acId,String akSceret,String securityToken ) {
          //  mVidSts.setVid("47607882ca824b7fb034cf97820d8395");
            mVidSts.setVid(videoid);
            mDownloadManager.setDownloadInfoListener(mDownloadInfoListener);
            mDownloadManager.setRefreshStsCallback(mRefreshStsCallback);
            console(getString(R.string.demo_sts_auth_init_succ));
            mDownloadManager.prepareDownloadMedia(mVidSts);
        }

        public void getDownloadToken(String  mVideoid) {
            videoid = mVideoid;
            downloadTask = new DownloadTask();
            downloadTask.execute(mVideoid);
        }

    }

    class GetTokenSuccReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {

        }

    }

    AliyunRefreshStsCallback mRefreshStsCallback = new AliyunRefreshStsCallback() {

        @Override
        public AliyunVidSts refreshSts(String vid, String quality, String format, String title, boolean encript) {
            mVidSts.setVid(vid);
            mVidSts.setQuality(quality);
            return mVidSts;
        }
    };
    private AliyunDownloadInfoListener mDownloadInfoListener = new AliyunDownloadInfoListener() {
        @Override
        public void onPrepared(List<AliyunDownloadMediaInfo> list) {
            Log.d("Download", "onPrepared");
            AliyunDownloadMediaInfo info = list.get(0);
            mSavePath = info.getSavePath();
            File downloadFile = new File(mSavePath);
            if (downloadFile.exists()) {
                console(getString(R.string.demo_downloaded, info.getTitle()));
                return;
            }
            //添加要下载的info到downloadManager中：
            mDownloadManager.addDownloadMedia(info);
            //开始下载，info为prepare要下载的info。
            mDownloadManager.startDownloadMedia(info);
        }

        @Override
        public void onStart(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
            console(getString(R.string.demo_downloading, aliyunDownloadMediaInfo.getTitle(), 0));
        }

        @Override
        public void onProgress(AliyunDownloadMediaInfo aliyunDownloadMediaInfo, int i) {
            console(getString(R.string.demo_downloading, aliyunDownloadMediaInfo.getTitle(), i));
        }

        @Override
        public void onStop(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
            Log.d("Download", "onStop");
        }

        @Override
        public void onCompletion(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
            Log.d("Download", "completion");
            console(getString(R.string.demo_download_succ, aliyunDownloadMediaInfo.getTitle()));
        }

        @Override
        public void onError(AliyunDownloadMediaInfo aliyunDownloadMediaInfo, int i, String s, String s1) {
            Log.d("Download", i + ", " + s + ", " + s1);
        }

        @Override
        public void onWait(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
            Log.d("Download", "onWait");
        }

        @Override
        public void onM3u8IndexUpdate(AliyunDownloadMediaInfo aliyunDownloadMediaInfo, int i) {
            Log.d("Download", "onM3u8IndexUpdate");
        }
    };

    private void console(final String text) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mConsole.setText(text);
            return;
        }
          mConsole.post(new Runnable() {
            @Override
            public void run() {
                console(text);
            }
        });
    }

    private void configDownload() {
        AliyunDownloadConfig config = new AliyunDownloadConfig();
        //设置加密文件路径。使用安全下载的用户必须设置（在准备下载之前设置），普通下载可以不用设置。
        config.setSecretImagePath(Storage.getEncryptedFile().toString());
        //设置保存路径。请确保有SD卡访问权限。
        config.setDownloadDir(Storage.getVideoDir(this).toString());
        //设置最大下载个数，最多允许同时开启4个下载
        config.setMaxNums(4);
        AliyunDownloadManager.getInstance(this).setDownloadConfig(config);
    }




    private TextView mConsole;


}
