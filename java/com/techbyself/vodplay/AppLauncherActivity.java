package com.techbyself.vodplay;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.techbyself.vodplay.util.ApiHelper;
import com.techbyself.vodplay.util.AsyncHandler;
import com.techbyself.vodplay.util.DownloadService;
import com.techbyself.vodplay.util.Storage;
import com.bignerdranch.anriod.vidioplayer.R;
/**
 * Created by ckb on 18/1/9.
 */

public class AppLauncherActivity extends Activity {


    private static final int REQUEST_WRITE_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);
        //启动首页后，启动对阿里云的认证请求
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent); // 启动服务
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_STORAGE)) {
            copyEncryptedFile();
            startMainActivity();
        }
    }

    private void startMainActivity() {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
               // startActivity(new Intent(AppLauncherActivity.this, AliyunPlayerActivity.class));
                startActivity(new Intent(AppLauncherActivity.this, ActivityVidioFace.class));
                finish();
            }
        }, 1000);
    }

    public boolean checkPermission(String permission, int requestCode) {
        if (!hasPermission(permission)) {
            requestPermission(permission, requestCode);
            return false;
        }
        return true;
    }

    public boolean hasPermission(String permission) {
        if (ApiHelper.preM()) return true;
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermission(String permission, int requestCode) {
        if (shouldShowRequestPermissionRationale(permission)) {
            //TODO explain permission
        }
        requestPermissions(new String[]{permission}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        copyEncryptedFile();
                        startMainActivity();
                    } else {
                        finish();
                    }
                }
                break;
            }
            default: {
                break;
            }

        }
    }

    private void copyEncryptedFile() {
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!Storage.getEncryptedFile().exists()) {
                    Storage.moveEncryptedToStorage(getAssets());
                }
            }
        });
    }

}