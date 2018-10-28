package com.techbyself.baiduvoice;

import android.util.Log;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CompareVocieUtil {

    public static void main(String[] args) throws PinyinException {

        CompareVocieUtil  viuc=new CompareVocieUtil();
        viuc.compareVoiceWithDataTest("她对看到的一切都提问题");



    }

    public  static String  compareVoiceWithData(String dbdata,String dbpos,String  uservoice){
        StringBuffer  recordpos=new StringBuffer();
        try {
            String  uservoicepinyin= PinyinHelper.convertToPinyinString(uservoice,"",PinyinFormat.WITHOUT_TONE);
            printLog(uservoicepinyin);
            String  strarray[]=dbdata.split(",");
            String  strarraypos[]=dbpos.split(",");
            for(int ind=0;ind<strarray.length;ind++){
                int exist_flag=uservoicepinyin.indexOf(strarray[ind]);
                System.out.println(strarray[ind]+"   "+exist_flag);
                if(exist_flag>=0){
                    recordpos.append(strarraypos[ind]).append(",");
                }

            }
            //    System.out.print(recordpos.toString());
            //  System.out.print(uservoicepinyin);
        }
        catch (  PinyinException  e){
            e.printStackTrace();
        }

        return   recordpos.toString();
    }


    public  static String  compareVoiceWithDataTest(String  uservoice){
       String  dbdata="meijianshi,yiqie,meiyijian,suoyoushi,quanbu,wenwenti,wengwenti,,tiwenti,tiwengti,wengwengti,wengwenti,wenchu,wengchu,tichu,wenti,wengti,kan,jiandao";
       String  dbpos="0,0,0,0,0,1,1,1,1,1,1,1,1,1,2,2,3,3";
       StringBuffer  recordpos=new StringBuffer();
     try {
            String  uservoicepinyin= PinyinHelper.convertToPinyinString(uservoice,"",PinyinFormat.WITHOUT_TONE);
            String  strarray[]=dbdata.split(",");
             String  strarraypos[]=dbpos.split(",");
            for(int ind=0;ind<strarray.length;ind++){
                int exist_flag=uservoicepinyin.indexOf(strarray[ind]);
                System.out.println(strarray[ind]+"   "+exist_flag);
                if(exist_flag>=0){
                    recordpos.append(strarraypos[ind]).append(",");
                }

            }
       System.out.print(recordpos.toString());
       System.out.print(uservoicepinyin);
        }
      catch (  PinyinException  e){
            e.printStackTrace();
      }

    return   recordpos.toString();
 }


 public   String  testComplaer(){
        String userCheckResult="0";
     String  result[];
     if(userCheckResult==null||userCheckResult.length()<=0){
         return  null;
     }
     StringBuffer  allstr=new StringBuffer();
     allstr.append("invited:邀请,party:聚会,").append("children:儿童,");
     String   allstr_array[]=allstr.toString().split(",");
     int configStrLen=allstr_array.length;
     StringBuffer  backstr=new StringBuffer();
     for(int coind=0;coind<configStrLen;coind++){
         String curint=String.valueOf(coind);
         int exist_flag=userCheckResult.indexOf(curint);
         if(exist_flag<=0){
             String  currentstr[]=allstr_array[coind].split(":");
             backstr.append(currentstr[0]).append(" : ").append(currentstr[1]).append("\n");
         }
     }

    System.out.print(backstr.toString());
     return  backstr.toString();

 }

    private  static  void printLog(String text) {

        text += "\n";
        Log.i("com.techbyself.baiduvo:", text);

    }

    private boolean logTime = true;
}

