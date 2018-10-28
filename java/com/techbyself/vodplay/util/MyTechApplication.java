package com.techbyself.vodplay.util;

import android.app.Application;

public class MyTechApplication  extends Application {
    //数据库辅助类实例
    private static MyDatabaseHelper mDBHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        mDBHelper = new MyDatabaseHelper(getApplicationContext());
    }
    //返回DBHelper实例， public static DBHelper getmDBHelper(){ return mDBHelper; } }

    public static MyDatabaseHelper getmDBHelper(){
        return mDBHelper;
    }

}
