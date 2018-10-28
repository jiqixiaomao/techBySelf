package com.techbyself.baiduvoice;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import com.bignerdranch.anriod.vidioplayer.R;
/**
 *  集成文档： http://ai.baidu.com/docs#/ASR-Android-SDK/top 集成指南一节
 *  demo目录下doc_integration_DOCUMENT
 *      ASR-INTEGRATION-helloworld  ASR集成指南-集成到helloworld中 对应 ActivityMiniRecog
 *      ASR-INTEGRATION-TTS-DEMO ASR集成指南-集成到合成DEMO中 对应 ActivityRecog
 */

public class ActivityMiniRecog extends AppCompatActivity   {
    protected TextView txtLog;
    protected TextView txtResult;
    protected Button btn;
    protected Button stopBtn;
    private  BaiduVoiceRecog recog;
    private static String DESC_TEXT = "精简版识别，带有SDK唤醒运行的最少代码，仅仅展示如何调用，\n" +
            "也可以用来反馈测试SDK输入参数及输出回调。\n" +
            "本示例需要自行根据文档填写参数，可以使用之前识别示例中的日志中的参数。\n" +
            "需要完整版请参见之前的识别示例。\n" +
            "需要测试离线命令词识别功能可以将本类中的enableOffline改成true，首次测试离线命令词请联网使用。之后请说出“打电话给张三”";

    private EventManager asr;

    private boolean logTime = true;
    private    String CURRENT_VIDEO = "1141";
    protected boolean enableOffline = false; // 测试离线命令词，需要改成true

    /**
     *
     * 点击开始按钮
     * 测试参数填在这里
     */


    /**
     * 点击停止按钮
     */


    /**
     * enableOffline设为true时，在onCreate中调用
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_mini);
        initView();

        recog=new BaiduVoiceRecog();
        recog.initPermission(this);
        recog.onCreateRecog(this);
       // asr = EventManagerFactory.create(this, "asr");
       // asr.registerListener(this); //  EventListener 中 onEvent方法
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // recog.(CURRENT_VIDEO);
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // recog.stop();
            }
        });


}

    @Override
    protected void onPause(){
        super.onPause();
        recog.onPause();
        Log.i("ActivityMiniRecog","On pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recog.onDestroy();
        recog.unloadOfflineEngine(); // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启

        // 必须与registerListener成对出现，否则可能造成内存泄露

    }

    //   EventListener  回调方法




    private void initView() {
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtLog = (TextView) findViewById(R.id.txtLog);
        btn = (Button) findViewById(R.id.btn);
        stopBtn = (Button) findViewById(R.id.btn_stop);
      //  txtLog.setText(DESC_TEXT + "\n");
    }

    /**
     * android 6.0 以上需要动态申请权限
     */


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

}
