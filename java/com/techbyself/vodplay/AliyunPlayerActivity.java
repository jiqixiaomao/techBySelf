package com.techbyself.vodplay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.techbyself.vodplay.util.AsyncHandler;
import com.techbyself.vodplay.util.Storage;
import com.techbyself.vodplay.util.StsAuthHelper;
import com.aliyun.vodplayer.downloader.AliyunDownloadConfig;
import com.aliyun.vodplayer.downloader.AliyunDownloadInfoListener;
import com.aliyun.vodplayer.downloader.AliyunDownloadManager;
import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.aliyun.vodplayer.downloader.AliyunRefreshStsCallback;
import com.aliyun.vodplayer.media.AliyunVidSts;

import java.io.File;
import java.util.List;
import com.bignerdranch.anriod.vidioplayer.R;

public class AliyunPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    private AliyunDownloadManager mDownloadManager;

    private AliyunVidSts mVidSts = new AliyunVidSts();

    private Button mStsAuthBtn;
    private Button mStartDownloadBtn;
    private Button mStartPlayBtn;
    private TextView mConsole;

    private String mSavePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plyer_demo);
        mStsAuthBtn = findViewById(R.id.main_sts_auth);
        mStartDownloadBtn = findViewById(R.id.main_start_download);
        mStartPlayBtn = findViewById(R.id.main_start_play);
        mConsole = findViewById(R.id.main_console);

        mStsAuthBtn.setOnClickListener(this);
        mStartDownloadBtn.setOnClickListener(this);
        mStartPlayBtn.setOnClickListener(this);

        mDownloadManager = AliyunDownloadManager.getInstance(this);

        configDownload();

    }


    private void startDownload() {
        //TODO 替换成真实的要下载的视频VID
        mVidSts.setVid("47607882ca824b7fb034cf97820d8395");
        mDownloadManager.setDownloadInfoListener(mDownloadInfoListener);
        mDownloadManager.setRefreshStsCallback(mRefreshStsCallback);
        console(getString(R.string.demo_sts_auth_init_succ));
        mDownloadManager.prepareDownloadMedia(mVidSts);
    }

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


    AliyunRefreshStsCallback mRefreshStsCallback = new AliyunRefreshStsCallback() {

        @Override
        public AliyunVidSts refreshSts(String vid, String quality, String format, String title, boolean encript) {
            mVidSts.setVid(vid);
            mVidSts.setQuality(quality);
            return mVidSts;
        }
    };

    private void  configDownload() {
        AliyunDownloadConfig config = new AliyunDownloadConfig();
        //设置加密文件路径。使用安全下载的用户必须设置（在准备下载之前设置），普通下载可以不用设置。
        config.setSecretImagePath(Storage.getEncryptedFile().toString());
        //设置保存路径。请确保有SD卡访问权限。
        config.setDownloadDir(Storage.getVideoDir(this).toString());
        //设置最大下载个数，最多允许同时开启4个下载
        config.setMaxNums(4);
        AliyunDownloadManager.getInstance(this).setDownloadConfig(config);
    }

    @Override
    public void onClick(View view) {
        if (view == mStsAuthBtn) {
            AsyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (StsAuthHelper.getStstokenAscy() ) {
                        mVidSts.setAcId(StsAuthHelper.sAccessKeyId);
                        mVidSts.setAkSceret(StsAuthHelper.sAccessKeySecret);
                        mVidSts.setSecurityToken(StsAuthHelper.sSecurityToken);
                        console(getString(R.string.demo_sts_auth_init_succ));
                    }else {
                        console(getString(R.string.demo_sts_auth_init_fail));
                    }
                }
            });
        } else if (view == mStartDownloadBtn) {
            AsyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    File externalDir = Environment.getExternalStorageDirectory();
                    File rootDir = new File(externalDir, "AliSafePlayer/video");
                    File[]  files=rootDir.listFiles();
                    if (StsAuthHelper.getStstoken() ) {
                        mVidSts.setAcId(StsAuthHelper.sAccessKeyId);
                        mVidSts.setAkSceret(StsAuthHelper.sAccessKeySecret);
                        mVidSts.setSecurityToken(StsAuthHelper.sSecurityToken);
                        startDownload();
                        console(getString(R.string.demo_sts_auth_init_succ));
                    }else {
                        console(getString(R.string.demo_sts_auth_init_fail));
                    }
                }
            });
        } else if (view == mStartPlayBtn) {
              //  VideoPlayerActivity.startPlayerActivity(this, mSavePath);
          // String  vid= "47607882ca824b7fb034cf97820d8395";
           // getStstoken();
         // String acid=  mVidSts.getAcId();
         // String akscret=   mVidSts.getAkSceret();
         // String  securityToken =mVidSts.getSecurityToken();
          //VideoPlayerActivity.startPlayerActivitySts(this, vid,acid,akscret,securityToken);
           // VideoPlayerActivity.startPlayerActivity(this, mSavePath);

        }
    }

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

    public   void  getStstoken(){
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                if (StsAuthHelper.getStstoken() ) {
                    mVidSts.setAcId(StsAuthHelper.sAccessKeyId);
                    mVidSts.setAkSceret(StsAuthHelper.sAccessKeySecret);
                    mVidSts.setSecurityToken(StsAuthHelper.sSecurityToken);
                    console(getString(R.string.demo_sts_auth_init_succ));
                }else {
                    console(getString(R.string.demo_sts_auth_init_fail));
                }
            }
        });
    }

    public static void startFacedActivity(Context context, String videoid) {
        Intent it = new Intent(context, AliyunPlayerActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra("video", videoid);
        context.startActivity(it);
    }
}
