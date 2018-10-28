package com.techbyself.ui;

import com.techbyself.vodplay.util.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class LessonLab {
    private static LessonLab sLessonLab;

    private List<Lesson> sLesson_explains=null;

    public static LessonLab get() {
        if (sLessonLab == null) {
            sLessonLab = new LessonLab();
        }
        return sLessonLab;
    }

    private LessonLab() {
        sLesson_explains = new ArrayList<>();

    }


    public List<Lesson> getCurrentLessonExplain( String  lessonid) {
        sLesson_explains=MyDatabaseHelper.getLessonExplain(lessonid);
        return sLesson_explains;
    }

   public    String  getNewWordsExplainStr (List<Lesson> sLessons,int current_pos){
    Lesson  lesson=sLessons.get(current_pos);
    if(lesson==null){
        return  "";
    }
    String  firstx=lesson.getFirstx();
    String  firstxnum[]=firstx.split(",");
    StringBuffer  firstxmm=new StringBuffer();
            for(int ind=0;ind<firstxnum.length;ind++) {
                String mxss[] = firstxnum[ind].split(":");
                firstxmm.append(mxss[0]).append("  : ").append(mxss[1]).append("\n");
            }
       return   firstxmm.toString();
   }

    public    String  getOldUnclearWordsExplainStr (List<Lesson> sLessons,int current_pos){
        String  bstr=new String();
        Lesson  lesson=sLessons.get(current_pos);
        if(lesson==null){
            return  "";
        }
        String  secondx=lesson.getSecondx();
        if(secondx==null||secondx.length()<1){
            return  bstr;
        }
        String  firstxnum[]=secondx.split(",");
        StringBuffer  firstxmm=new StringBuffer();
        for(int ind=0;ind<firstxnum.length;ind++) {
            String mxss[] = firstxnum[ind].split(":");
            firstxmm.append(mxss[0]).append("  : ").append(mxss[1]).append("\n");
        }
        return   firstxmm.toString();
    }





    public    Lesson  getLesson (List<Lesson> sLessons,int current_pos){
        Lesson  lesson=sLessons.get(current_pos);
        if(lesson==null){
            return  null;
        }
        return  lesson;

    }


}



