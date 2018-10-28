package com.techbyself.vodplay.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.techbyself.ui.Lesson;
import com.techbyself.ui.Video;


public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final int[] NCE_TH = {72, 48, 30, 24};
    private static final String DBName = "BookStore.db";

    //数据库版本号
    private static final int DBVersion = 1;
    SQLiteDatabase innerdb;
    public static final String CREATE_VIEDO = "create table VIDEO ("
            + "id integer primary key autoincrement, "
            + "nce_th integer, "
            + "title text, "
            + "videoid text, "
            + "position integer, "
            + "status integer, "
            + "filename text)";

    public static final String VIEW_RECORD = "create table VIEW_RECORD ("
            + "id integer primary key autoincrement, "
            + "nce_th integer, "
            + "title text, "
            + "videoid text)";
    public static final String WORD_DICT = "create table WORD_DICT ("
            + "id integer primary key autoincrement, "
            + "newword text, "
            + "simplemean text, "
            + "moremean text, "
            + "fullmean text)";

    public static final String LESSON_DICT = "create table LESSON_DICT ("
            + "id integer primary key autoincrement, "
            + "nce_th text, "
            + "firstx text, "
            + "secondx text, "
            +"keyword_check text,"
            +"keyword_pos text,"
            + "fanyi text)";

    public static final String USER_STUDY_RECORD = "create table USER_STUDY_RECORD ("
            + "id integer primary key autoincrement, "
            + "nce_th text, "
            + "lesson_pos int, "
            + "user_fanyi text, "
            + "study_date text)";

    private Context mContext;

    public MyDatabaseHelper(Context context) {
        super(context, DBName, null, DBVersion);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("drop table if exists VIDEO");
        db.execSQL("drop table if exists VIEW_RECORD");
        db.execSQL("drop table if exists WORD_DICT");
        db.execSQL("drop table if exists LESSON_DICT");
        db.execSQL("drop table if exists USER_STUDY_RECORD");
        db.execSQL(CREATE_VIEDO);
        db.execSQL(VIEW_RECORD);
        db.execSQL(WORD_DICT);
        db.execSQL(LESSON_DICT);
        db.execSQL(USER_STUDY_RECORD);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
        innerdb=db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists VIDEO");
        db.execSQL("drop table if exists VIEW_RECORD");
        db.execSQL("drop table if exists WORD_DICT");
        db.execSQL("drop table if exists LESSON_DICT");
        onCreate(db);
    }
    public  void initWordDict(Element element) {
        SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("insert into WORD_DICT(newword,simplemean,moremean,fullmean) values(");
        NodeList nodeList = element.getElementsByTagName("item");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element lan = (Element) nodeList.item(i);
            StringBuffer sqlstradd = new StringBuffer();
            sqlstradd.append("\"" +lan.getAttribute("id")+ "\"");
            sqlstradd.append(",\"" + lan.getElementsByTagName("simple").item(0).getTextContent() + "\"");
            sqlstradd.append(",\"" + lan.getElementsByTagName("more").item(0).getTextContent() + "\"");
            sqlstradd.append(",\"" + lan.getElementsByTagName("full").item(0).getTextContent() + "\")");
            db.execSQL(sqlstr.toString() + sqlstradd.toString());
        }

    }

    public  void initLessonExplain(Element element) {

        SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
        db.execSQL("drop table if exists LESSON_DICT");
        db.execSQL(LESSON_DICT);
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("insert into LESSON_DICT(nce_th,firstx,secondx,fanyi,keyword_check,keyword_pos) values(");
        NodeList nodeList = element.getElementsByTagName("item");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element lan = (Element) nodeList.item(i);
            StringBuffer sqlstradd = new StringBuffer();
            sqlstradd.append("\"" +lan.getAttribute("id") + "\"");
            sqlstradd.append(",\"" + lan.getElementsByTagName("firth").item(0).getTextContent() + "\"");
            sqlstradd.append(",\"" + lan.getElementsByTagName("second").item(0).getTextContent() + "\"");
            sqlstradd.append(",\"" + lan.getElementsByTagName("fanyi").item(0).getTextContent() + "\"");
            sqlstradd.append(",\"" + lan.getElementsByTagName("keyword_check").item(0).getTextContent() + "\"");
            sqlstradd.append(",\"" + lan.getElementsByTagName("keyword_pos").item(0).getTextContent() + "\")");
            db.execSQL(sqlstr.toString() + sqlstradd.toString());
        }

    }

    public  static  void saveUserFanyiRecord(String  nceth,int lesson_pos,String  userfanyi) {
        SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
        StringBuffer sqlstradd = new StringBuffer();
        ContentValues values = new ContentValues();
         values.put("nce_th", nceth);
         values.put("lesson_pos", lesson_pos);
         values.put("user_fanyi", userfanyi);
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd-HHmmss");
        String  datestr=dateFormat.format(new Date());
        values.put("study_date", datestr);
        db.insert("USER_STUDY_RECORD", null, values); // 插入第一条数据
        values.clear();
    }


    public void updateRecord(String videoid, String filename) {
        SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        // 开始组装第一条数据
        values.put("videoid", videoid);
        values.put("filename", filename);
        values.put("position", 0);
        db.insert("VIDEO", null, values); // 插入第一条数据
        values.clear();
    }

    public int CheckInitRecord() {
        String stu_sql = "select count(*) from  VIDEO";
        SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
        Cursor cursor = db.rawQuery(stu_sql, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;

    }

    public  void initSourceXml(Element element) {
        SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("insert into VIDEO(nce_th,videoid,title) values(");
        NodeList nodeList = element.getElementsByTagName("item");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element lan = (Element) nodeList.item(i);
            StringBuffer sqlstradd = new StringBuffer();
            sqlstradd.append(lan.getAttribute("id"));
            sqlstradd.append(",\"" + lan.getElementsByTagName("videoid").item(0).getTextContent() + "\"");
            sqlstradd.append(",\"" + lan.getElementsByTagName("title").item(0).getTextContent() + "\")");
            db.execSQL(sqlstr.toString() + sqlstradd.toString());
        }

    }



    public static  ArrayList<Video> getAllVideo( ) {
        ArrayList<Video>  videolist= new ArrayList<Video>();
        SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
        Cursor  cursor =db.query("VIDEO",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Video  curvideo=new Video();
                String   title=cursor.getString(cursor.getColumnIndex("title"));
                String   videoid=cursor.getString(cursor.getColumnIndex("videoid"));
                String   nce_th=(cursor.getString(cursor.getColumnIndex("nce_th")));
                int nceth_num=Integer.parseInt(nce_th.substring(1,nce_th.length()));
                curvideo.setId(nceth_num);
                curvideo.setTitle(title);
                curvideo.setVidioid(videoid);
                curvideo.setNceth(nce_th);
                int  curidx=nceth_num*2-1;
                int   curnextidx=nceth_num*2;
                curvideo.setVidioName("第" +curidx+"&"+curnextidx+"课");
                videolist.add(curvideo);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return  videolist;
    }

    public static ArrayList<Video> getRecentViewVideo( ) {
        ArrayList<Video>  videolist= new ArrayList<Video>();
        SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
        Cursor  cursor =db.query(true,"VIEW_RECORD",null,null,null,null,null,"id desc","25");
        if(cursor.moveToFirst()){
            do{
                Video  curvideo=new Video();
                String   title=cursor.getString(cursor.getColumnIndex("title"));
                String   videoid=cursor.getString(cursor.getColumnIndex("videoid"));
                String   nce_th=(cursor.getString(cursor.getColumnIndex("nce_th")));
                int nceth_num=Integer.parseInt(nce_th.substring(1,nce_th.length()));
                curvideo.setId(nceth_num);
                curvideo.setTitle(title);
                curvideo.setVidioid(videoid);
                curvideo.setNceth(nce_th);
                int  curidx=nceth_num*2-1;
                int   curnextidx=nceth_num*2;
                curvideo.setVidioName("第"+curidx+"&"+curnextidx+"课");
                videolist.add(curvideo);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return  videolist;
    }

    public static void addRecentStudyRecord(Video  video) {
        SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
        String sqlstr="insert into VIEW_RECORD(nce_th,videoid,title) values("+video.getNceth()+",\""+video.getVidioid()+"\",\""+video.getTitle()+"\")";
        db.execSQL(sqlstr);
    }



    public static  ArrayList<Lesson> getLessonExplain(String  lessonid ) {
        ArrayList<Lesson>  lessonexplainlist= new ArrayList<Lesson>();
        SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
        Cursor  cursor =db.query("LESSON_DICT",null,"nce_th=?",new String[]{lessonid},null,null,null);
        if(cursor.moveToFirst()){
            do{
                Lesson  lesson=new Lesson();
                String   nceth=cursor.getString(cursor.getColumnIndex("nce_th"));
                String   firstx=cursor.getString(cursor.getColumnIndex("firstx"));
                String   secondx=(cursor.getString(cursor.getColumnIndex("secondx")));
                String   fanyi=(cursor.getString(cursor.getColumnIndex("fanyi")));
                String   keywordcheck=(cursor.getString(cursor.getColumnIndex("keyword_check")));
                String   keywordpos=(cursor.getString(cursor.getColumnIndex("keyword_pos")));
                lesson.setFanyi(fanyi);
                lesson.setFirstx(firstx);
                lesson.setSecondx(secondx);
                lesson.setKeywordCheck(keywordcheck);
                lesson.setKeywordPos(keywordpos);
                lessonexplainlist.add(lesson);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return  lessonexplainlist;
    }

  public static void  updateDBinfo(){
      SQLiteDatabase db = MyTechApplication.getmDBHelper().getWritableDatabase();
      db.execSQL("drop table if exists VIDEO");
      db.execSQL("drop table if exists VIEW_RECORD");
      db.execSQL("drop table if exists WORD_DICT");
      db.execSQL("drop table if exists LESSON_EXPLAIN");
      db.execSQL(CREATE_VIEDO);
      db.execSQL(VIEW_RECORD);
      db.execSQL(WORD_DICT);
      db.execSQL(LESSON_DICT);
  }

}