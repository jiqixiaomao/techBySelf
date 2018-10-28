package com.techbyself.ui;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


import com.bignerdranch.anriod.vidioplayer.R;


public class MainActivity extends AppCompatActivity {
    private   static   final   String  DOWNLOAD_ZIP_URL="http://666dx.pc6.com/dx3/bdreader_mini.zip";
    private static final String DOWNLOAD_URL =
            "http://shouji.360tpcdn.com/150527/c90d7a6a8cded5b5da95ae1ee6382875/com.tencent.mm_561.apk";
    private static final String TAG = "SingleActivity";
    private long  mReference = 0 ;
    private Button button1;
    private Button button2;

    private String downloadUrl;




    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadface);

    }

    void setDownloadFilePath( DownloadManager.Request request ){
        Environment.getExternalStoragePublicDirectory("vidioplayer").mkdir();
        request.setDestinationInExternalPublicDir(  "/vidioplayer/"  , "v2.mp4" ) ;

    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction() ;
            if( action.equals( DownloadManager.ACTION_DOWNLOAD_COMPLETE  )){
                //下载完成了
                //获取当前完成任务的ID
                long  reference = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID , -1 );
                Toast.makeText( MainActivity.this , "下载完成了" ,  Toast.LENGTH_SHORT ).show() ;

            }

            if( action.equals( DownloadManager.ACTION_NOTIFICATION_CLICKED )){
                //广播被点击了
                Toast.makeText( MainActivity.this , "广播被点击了" ,  Toast.LENGTH_SHORT ).show() ;
            }
        }
    };
    void setNotification(DownloadManager.Request request ) {
        //设置Notification的标题
        request.setTitle( "微信下载" ) ;

        //设置描述
        request.setDescription( "5.3.6" ) ;

        //request.setNotificationVisibility( Request.VISIBILITY_VISIBLE ) ;

        request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED ) ;

        //request.setNotificationVisibility( Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION ) ;

        //request.setNotificationVisibility( Request.VISIBILITY_HIDDEN ) ;
    }



}