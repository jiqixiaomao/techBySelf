package com.techbyself.vodplay;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;


import com.baidu.speech.asr.SpeechConstant;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.techbyself.baiduvoice.BaiduVoiceRecog;
import com.techbyself.ui.Lesson;
import com.techbyself.ui.LessonLab;
import com.techbyself.vodplay.util.CardPopup;
import com.techbyself.vodplay.util.Storage;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.bignerdranch.anriod.vidioplayer.R;
import com.techbyself.vodplay.util.TechResourceUtil;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ckb on 18/1/21.
 */

public class VideoPlayerActivity extends AppCompatActivity  implements EventListener {
    private int  player_status=0;
    private AliyunVodPlayerView mAliyunVodPlayerView = null;
    private  BaiduVoiceInit bdvoice=null;
    private  SurfaceView surfceView= null;
    private CardPopup popup=null;
    private  TextView  tvtextView;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS");
    public   String CURRENT_VIDEO = "47607882ca824b7fb034cf97820d8395";
    private BaiduVoiceRecog recog;
    View   asktagview;
    public  int[]  intervaltime={300,102,25,34,32,26,29,43,30,30,30,60,20,60,20,70,40,50,30,40,50,50,30,30,30,50};
    private   String CURRENT_VIDEO_TH = "1141";
    private List<String> logStrs = new ArrayList<>();
    private TextView tvLogs;
    private String  nceth;
    int current_pos=0;
    private AliyunVodPlayer mAliVodPlayer;
    private   List<Lesson> sLessons;
    private EventManager asr;
    private   int mCycleSeq=0;
    private   int mLastCyclePos=0;
    private   int user_config_wait_time=6000;
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvtextView.setText(R.string.loading);
        }
    };

    Runnable runnable = new Runnable() {
        @Override public void run() {
            int current_lesson_explain_pos=-1;
            int next_run_time=0;
            if(current_pos>=intervaltime.length){
                handler.removeCallbacks(runnable);
                return;
            }
            if(player_status==1){
                if(recog.sRecog_State==0&&mCycleSeq==0) {
                    mAliVodPlayer.pause();
                    asktagview.setVisibility(asktagview.VISIBLE);
                    next_run_time = user_config_wait_time;
                    LessonLab sLessonLab = LessonLab.get();
                    sLessons = sLessonLab.getCurrentLessonExplain(nceth);
                    Lesson sCurrentLsesson = sLessonLab.getLesson(sLessons, current_pos - 1);
                    recog.startRecog(sCurrentLsesson, current_pos);
                    recog.sRecog_State=1;
                }
               else  if(recog.sRecog_State==1) {
                    if (recog.userSpeakingFlag == 0) {
                        String voicecheck = recog.getUserVoiceCheckResult();
                        //用户什么都没说
                        if (voicecheck == null) {
                            LessonLab sLessonLab = LessonLab.get();
                            String newWordsExplainStr = new String();
                            //第一次用户没有说出任何话
                            if (mCycleSeq == 0) {
                                if (popup != null) {
                                    popup.dismiss();
                                }
                                newWordsExplainStr = sLessonLab.getNewWordsExplainStr(sLessons, current_pos - 1);
                                popup = new CardPopup(VideoPlayerActivity.this, newWordsExplainStr);
                                popup.showOnAnchor(asktagview, RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.CENTER, false);
                                mCycleSeq = 1;
                                next_run_time = user_config_wait_time;
                            }
                            //第二次用户没有说出任何话
                            else if (mCycleSeq == 1) {
                                if (popup != null) {
                                    popup.dismiss();
                                }
                                next_run_time = 1000;
                                recog.stopRecog();
                                newWordsExplainStr = sLessonLab.getOldUnclearWordsExplainStr(sLessons, current_pos - 1);
                                if (newWordsExplainStr != null && newWordsExplainStr.length() > 0) {
                                    popup = new CardPopup(VideoPlayerActivity.this, newWordsExplainStr);
                                    popup.showOnAnchor(asktagview, RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.CENTER, false);
                                    next_run_time = user_config_wait_time;
                                }
                                mCycleSeq = 2;
                            } else {
                                Lesson sCurrentLsesson = sLessonLab.getLesson(sLessons, current_pos - 1);
                                String newWordsStr = sLessonLab.getNewWordsExplainStr(sLessons, current_pos - 1);
                                String oldWordsStr = sLessonLab.getOldUnclearWordsExplainStr(sLessons, current_pos - 1);
                                String sentensExplainstr = sCurrentLsesson.getFanyi();
                                bdvoice.Speeking(newWordsStr + oldWordsStr + newWordsExplainStr + sentensExplainstr);
                                int nextRunTimeDepandonWords = newWordsExplainStr.length() + newWordsStr.length() + oldWordsStr.length();
                                mCycleSeq = 0;
                                recog.sRecog_State = 0;
                                player_status = 0;
                                mLastCyclePos = 0;
                                next_run_time = 2500 * nextRunTimeDepandonWords / 10;
                            }


                        } else {
                            if (popup != null) {
                                popup.dismiss();
                            }
                            //用户说了一些，可能是翻译，也可能是闲聊天
                            String unclearWords = recog.getUserUnclearWords(voicecheck);
                            if (voicecheck != null && voicecheck.length() > 0) {
                                //用户有一些单词没有翻译出来
                                recog.stopRecog();
                                if (unclearWords != null && unclearWords.length() > 0) {
                                    popup = new CardPopup(VideoPlayerActivity.this, unclearWords);
                                    popup.showOnAnchor(asktagview, RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.CENTER, false);
                                    LessonLab sLessonLab = LessonLab.get();
                                    Lesson sCurrentLsesson = sLessonLab.getLesson(sLessons, current_pos - 1);
                                    String sentensExplainstr = sCurrentLsesson.getFanyi();
                                    bdvoice.Speeking(unclearWords + sentensExplainstr);
                                    int nextRunTimeDepandonWords = unclearWords.length();
                                    next_run_time = 2500 * nextRunTimeDepandonWords / 10;
                                } else {
                                    next_run_time = 1000;
                                }
                                mCycleSeq = 0;
                                recog.sRecog_State = 0;
                                player_status = 0;
                                mLastCyclePos = 0;


                            } else {
                                //用户第二次什么都没有说对
                                if (mLastCyclePos == 4) {
                                    mLastCyclePos = 0;
                                    mCycleSeq = 2;
                                    next_run_time = 500;
                                }
                                //用户第一次什么都没有说对
                                else {
                                    mLastCyclePos = 4;
                                    mCycleSeq = 0;
                                    next_run_time = user_config_wait_time;
                                }
                                recog.setUserVoiceCheckResult(null);

                            }

                        }

                    }
                }
                else{
                    next_run_time = 2000;
                }

            }else{
                if(popup!=null) {
                    popup.dismiss();
                }
                mAliVodPlayer.start();
                player_status=1;
                current_pos=current_pos+1;
                next_run_time=intervaltime[current_pos]*100;
                asktagview.setVisibility(asktagview.GONE);

            }
            handler.postDelayed(runnable,next_run_time);
    } };
    Runnable voiceInitrunnable = new Runnable() {
        @Override public void run() {
            bdvoice=new BaiduVoiceInit(VideoPlayerActivity.this);

        } };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
            asktagview=findViewById(R.id.lan_begin_ask_content);
           if(asktagview==null) {
               asktagview = findViewById(R.id.port_begin_ask_content);
           }

        mAliVodPlayer = new AliyunVodPlayer(this);
        initPlayer();
        nceth=getIntent().getStringExtra("nceth");
        prepareAndStart(getIntent().getStringExtra("video"));
       handler.postDelayed(voiceInitrunnable,1000 );
        tvtextView=findViewById(R.id.pop_explan_content);
        logStrs.add(format.format(new Date()) + "on create");
        recog=new BaiduVoiceRecog();
        recog.onCreateRecog(this);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (mAliVodPlayer != null) {
            mAliVodPlayer.start();
            int delaytime=intervaltime[current_pos]*1000;
            handler.postDelayed(runnable,delaytime );
        }

    }
    @Override

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_video_player);
        asktagview=findViewById(R.id.lan_begin_ask_content);
        if(asktagview==null) {
            asktagview = findViewById(R.id.port_begin_ask_content);
        }
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        recog.stopRecog();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        mAliVodPlayer.pause();
        handler.removeCallbacks(runnable);
        VideoPlayerActivity.this.finish();
        recog.stopRecog();
        Log.i("ActivityMiniRecog","On pause");
    }

    @Override
    public  void   onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(runnable);
        recog.onDestroy();
    }
    @Override
    protected void onPause(){
        super.onPause();
        recog.onPause();
        Log.i("ActivityMiniRecog","On pause");
    }

    private void initPlayer() {
       /*
        mAliyunVodPlayerView = (AliyunVodPlayerView)findViewById(R.id.video_view);
        //保持屏幕敞亮
        mAliyunVodPlayerView.setKeepScreenOn(true);
        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save_cache";
        //mAliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s *, 300 /*大小，MB*);
      /*  mAliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
        mAliyunVodPlayerView.setCirclePlay(true);
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.setOnPreparedListener(new VideoPlayerActivity.MyPrepareListener(this));
        mAliyunVodPlayerView.setOnCompletionListener(new VideoPlayerActivity.MyCompletionListener(this));
        mAliyunVodPlayerView.setOnFirstFrameStartListener(new VideoPlayerActivity.MyFrameInfoListener(this));
        */
        surfceView = findViewById(R.id.video_view);
        surfceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                holder.setKeepScreenOn(true);
                if (mAliVodPlayer != null) {
                    mAliVodPlayer.setSurface(holder.getSurface());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if (mAliVodPlayer != null) {
                    mAliVodPlayer.surfaceChanged();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (mAliVodPlayer != null) {
                    mAliVodPlayer.setSurface(null);
                }
            }
        });
    }
    private static class MyPrepareListener implements IAliyunVodPlayer.OnPreparedListener {

        private WeakReference<VideoPlayerActivity> activityWeakReference;

        public MyPrepareListener(VideoPlayerActivity skinActivity) {
            activityWeakReference = new WeakReference<VideoPlayerActivity>(skinActivity);
        }

        @Override
        public void onPrepared() {
            VideoPlayerActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onPrepared();
            }
        }
    }

    private void onPrepared() {
        logStrs.add(format.format(new Date()) + getString(R.string.log_prepare_success));

        for (String log : logStrs) {
            tvLogs.append(log + "\n");
        }
        Toast.makeText(VideoPlayerActivity.this.getApplicationContext(), R.string.toast_prepare_success,
                Toast.LENGTH_SHORT).show();
    }
    private void prepareAndStart(String videoFile) {
        if (TextUtils.isEmpty(videoFile)) {
            finish();
            return;
        }
        AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        asb.setSource(videoFile);
        AliyunLocalSource localSource = asb.build();
        mAliVodPlayer.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                int seektime=intervaltime[current_pos]*100;
                mAliVodPlayer.seekTo(seektime);
                player_status=1;
                mAliVodPlayer.start();
                current_pos=current_pos+1;
                int delaytime=intervaltime[current_pos]*100;
                handler.postDelayed(runnable,delaytime);
            }
        });
        mAliVodPlayer.prepareAsync(localSource);
    }

    public static void startPlayerActivity(Context context, String videoid,String  nceth) {
        Intent it = new Intent(context, VideoPlayerActivity.class);
        String  videoFile=Storage.getVideoPath(context,videoid);
        it.putExtra("video", videoFile);
        it.putExtra("nceth", nceth);
        context.startActivity(it);
    }

    private static class MyCompletionListener implements IAliyunVodPlayer.OnCompletionListener {

        private WeakReference<VideoPlayerActivity> activityWeakReference;

        public MyCompletionListener(VideoPlayerActivity skinActivity) {
            activityWeakReference = new WeakReference<VideoPlayerActivity>(skinActivity);
        }

        @Override
        public void onCompletion() {

            VideoPlayerActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onCompletion();
            }
        }
    }

    private static class MyFrameInfoListener implements IAliyunVodPlayer.OnFirstFrameStartListener {

        private WeakReference<VideoPlayerActivity> activityWeakReference;

        public MyFrameInfoListener(VideoPlayerActivity skinActivity) {
            activityWeakReference = new WeakReference<VideoPlayerActivity>(skinActivity);
        }

        @Override
        public void onFirstFrameStart() {

            VideoPlayerActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onFirstFrameStart();
            }
        }
    }

    private void onCompletion() {
        logStrs.add(format.format(new Date()) + getString(R.string.log_play_completion));
        for (String log : logStrs) {
            tvLogs.append(log + "\n");
        }
        Toast.makeText(VideoPlayerActivity.this.getApplicationContext(), R.string.toast_play_compleion,
                Toast.LENGTH_SHORT).show();

        // 当前视频播放结束, 播放下一个视频
      //  onNext();
    }
    private void onFirstFrameStart() {
        Map<String, String> debugInfo = mAliyunVodPlayerView.getAllDebugInfo();
        long createPts = 0;
        if (debugInfo.get("create_player") != null) {
            String time = debugInfo.get("create_player");
            createPts = (long)Double.parseDouble(time);
            logStrs.add(format.format(new Date(createPts)) + getString(R.string.log_player_create_success));
        }
        if (debugInfo.get("open-url") != null) {
            String time = debugInfo.get("open-url");
            long openPts = (long)Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(openPts)) + getString(R.string.log_open_url_success));
        }
        if (debugInfo.get("find-stream") != null) {
            String time = debugInfo.get("find-stream");
            long findPts = (long)Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(findPts)) + getString(R.string.log_request_stream_success));
        }
        if (debugInfo.get("open-stream") != null) {
            String time = debugInfo.get("open-stream");
            long openPts = (long)Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(openPts)) + getString(R.string.log_start_open_stream));
        }
        logStrs.add(format.format(new Date()) + getString(R.string.log_first_frame_played));
        for (String log : logStrs) {
            tvLogs.append(log + "\n");
        }
    }
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;


        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            if (params.contains("\"nlu_result\"")) {
                if (length > 0 && data.length > 0) {
                    logTxt += ", 语义解析结果：" + new String(data, offset, length);
                }
            }
        } else if (data != null) {
            logTxt += " ;data length=" + data.length;
        }
        printLog(logTxt);
    }

    private void printLog(String text) {
        if (logTime) {
            text += "  ;time=" + System.currentTimeMillis();
        }
        text += "\n";
        Log.i(getClass().getName(), text);
    }

    private void start() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event

        if (enableOffline) {
            params.put(SpeechConstant.DECODER, 2);
        }
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0);
        // params.put(SpeechConstant.NLU, "enable");
        // params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        // params.put(SpeechConstant.IN_FILE, "res:///com/baidu/android/voicedemo/16k_test.pcm");
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
    protected boolean enableOffline = false;
    private PopupWindow mPopWindow;
    private boolean logTime = true;
}
