package com.techbyself.baiduvoice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.bignerdranch.anriod.vidioplayer.R;
import com.techbyself.ui.Lesson;
import com.techbyself.vodplay.util.MyDatabaseHelper;
import com.techbyself.vodplay.util.Storage;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaiduVoiceRecog   implements  EventListener {

    protected TextView txtResult;
    protected Button btn;
    protected Button stopBtn;
    private static String DESC_TEXT = "精简版识别，带有SDK唤醒运行的最少代码，仅仅展示如何调用，\n" +
            "也可以用来反馈测试SDK输入参数及输出回调。\n" +
            "本示例需要自行根据文档填写参数，可以使用之前识别示例中的日志中的参数。\n" +
            "需要完整版请参见之前的识别示例。\n" +
            "需要测试离线命令词识别功能可以将本类中的enableOffline改成true，首次测试离线命令词请联网使用。之后请说出“打电话给张三”";

    private EventManager asr;

    private boolean logTime = true;
    //当前用户翻译的识别结果
    private  String sCurrent_UserVoice_RecogResult="";
    private   Lesson  sCurrent_lesson;
    private   int sCurrent_pos;
    protected boolean enableOffline = false; // 测试离线命令词，需要改成true
    private   String  sCurrent_Check_Result;
    public   int  sRecog_State=0;
    public  int   userSpeakingFlag=0;

    /**
     *
     * 点击开始按钮
     * 测试参数填在这里
     */
    public void startRecog(Lesson current_lesson, int current_pos) {
        sCurrent_lesson=current_lesson;
        sCurrent_pos=current_pos;
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event

        if (enableOffline) {
            params.put(SpeechConstant.DECODER, 2);
        }
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        // params.put(SpeechConstant.NLU, "enable");
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        File  filepabase=Storage.getVoiceRecogDir();
        SimpleDateFormat  dateFormat=new SimpleDateFormat("HHmmss");
        String  datestr=dateFormat.format(new Date());
        String filename=filepabase+"/"+datestr+"-"+current_lesson+".pcm";
        params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        params.put(SpeechConstant.OUT_FILE, filename);
        // params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        // params.put(SpeechConstant.PROP ,20000);
        // params.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号
        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
        // 复制此段可以自动检测错误
        String json = null; // 可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        printLog("输入参数：" + json);
    }

    /**
     * 点击停止按钮
     */
    public void stopRecog() {
        printLog("停止识别：ASR_STOP");
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }


    /**
     * enableOffline设为true时，在onCreate中调用
     */
    public void loadOfflineEngine() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.DECODER, 2);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets://baidu_speech_grammar.bsg");
        asr.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, new JSONObject(params).toString(), null, 0, 0);
    }

    /**
     * enableOffline为true时，在onDestory中调用，与loadOfflineEngine对应
     */
    public void unloadOfflineEngine() {
        asr.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0); //
    }

    public void onCreateRecog(Activity activity) {

        //initPermission(activity);
        asr = EventManagerFactory.create(activity, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法
        if (enableOffline) {
            loadOfflineEngine(); // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    public void onPause(){
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        Log.i("ActivityMiniRecog","On pause");
    }

    public void onDestroy() {
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        if (enableOffline) {
            unloadOfflineEngine(); // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }

        // 必须与registerListener成对出现，否则可能造成内存泄露
        asr.unregisterListener(this);
    }

    //   EventListener  回调方法
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;


        if (params != null && !params.isEmpty()) {
           // logTxt += " duola_test_pos ||;params :" + params;

        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            userSpeakingFlag=1;
            if (params.contains("\"nlu_result\"")) {
                if (length > 0 && data.length > 0) {
                   // logTxt += ", 语义解析结果：" + new String(data, offset, length);
                }
            }
        } else if (data != null) {
            //logTxt += " ;data length=" + data.length;
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            printLog(logTxt+params);
            userSpeakingFlag=0;
            int   resultpos= params.indexOf("best_result");
            int end_pos=params.indexOf("result_type");
            if(resultpos<0||end_pos<0){
                return;
            }
            sCurrent_UserVoice_RecogResult=sCurrent_UserVoice_RecogResult+params.substring(resultpos+16,end_pos-5);
            printLog(sCurrent_UserVoice_RecogResult);
            if(sCurrent_UserVoice_RecogResult!=null){
                String  keywordCheck=sCurrent_lesson.getKeywordCheck();
                String  keywordPos=sCurrent_lesson.getKeywordPos();
                String sCheckResult=CompareVocieUtil.compareVoiceWithData(keywordCheck,keywordPos,sCurrent_UserVoice_RecogResult);
                sCurrent_lesson.setKeywordCheckResult(sCheckResult);
            }
            else{
                sCurrent_lesson.setKeywordCheckResult(null);
            }
            String  nceTh=sCurrent_lesson.getNceth();
            MyDatabaseHelper.saveUserFanyiRecord(nceTh,sCurrent_pos,sCurrent_UserVoice_RecogResult);
            String  result = "||;params :" +getUserVoiceCheckResult()+"  "+sCurrent_pos+"   "+sCurrent_UserVoice_RecogResult;
            printLog(result);
        }


    }

    public   String  getUserVoiceCheckResult(){
        if(sCurrent_lesson==null){
            return  null;
        }
        String  lessonCheckResult=sCurrent_lesson.getKeywordCheckResult();
        return  lessonCheckResult;

    }

    public   void  setUserVoiceCheckResult(String  uservoice){
        if(sCurrent_lesson==null){
            return  ;
        }
      sCurrent_lesson.setKeywordCheckResult(uservoice );


    }


    public   String  getUserUnclearWords(String userCheckResult){
        String  result[];
        if(userCheckResult==null||userCheckResult.length()<=0){
           return  null;
        }
        StringBuffer  allstr=new StringBuffer();
        allstr.append( sCurrent_lesson.getFirstx()+",").append(sCurrent_lesson.getSecondx());
        String   allstr_array[]=allstr.toString().split(",");
        int configStrLen=allstr_array.length;
        StringBuffer  backstr=new StringBuffer();
        for(int coind=0;coind<configStrLen;coind++){
            String curint=String.valueOf(coind);
            int exist_flag=userCheckResult.indexOf(curint);
            if(exist_flag<0){
                String  currentstr[]=allstr_array[coind].split(":");
                backstr.append(currentstr[0]).append(" : ").append(currentstr[1]).append("\n");
            }
        }


        return  backstr.toString();

    }


    private void printLog(String text) {
        if (logTime) {
            text += "  ;time=" + System.currentTimeMillis();
        }
        text += "\n";
        Log.i(getClass().getName(), text);

    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    public void initPermission(Activity activity) {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions( activity, toApplyList.toArray(tmpList), 123);
        }

    }
public   void setCurrent_UserVoice_RecogResultNull(){
    sCurrent_UserVoice_RecogResult="";
}

}
