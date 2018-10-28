package com.techbyself.vodplay.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TechResourceUtil {
    public static final int[] NCE_COUNT = {72,48,30,24};
    public Element findItem(Context base,String filename){
        try {
            InputStream is = base.getAssets().open(filename);
            DocumentBuilderFactory dBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dBuilderFactory.newDocumentBuilder();
            Document document = dBuilder.parse(is);
            Element element = (Element) document.getDocumentElement();
           return   element;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public  void  findXmlSource(Context base) {

            String gradefilename="firstgrade.xml";
            String worddictfilename="worddict1.xml";
            String lessonfilename="lessons1.xml";
             MyDatabaseHelper dbHelper =new  MyDatabaseHelper(base);
            int recordcount=dbHelper.CheckInitRecord();
             recordcount=0;
            if(recordcount<NCE_COUNT[0]) {
                Element gelement = this.findItem(base,gradefilename);
                dbHelper.initSourceXml(gelement);
                Element wordgelement = this.findItem(base,worddictfilename);
                dbHelper.initWordDict(wordgelement);
                Element lessonelement = this.findItem(base,lessonfilename);
                dbHelper.initLessonExplain(lessonelement);
            }

    }

    public static  void   initXmlinfo(Context base){
        TechResourceUtil  xmlutil=new TechResourceUtil();
        xmlutil.findXmlSource(base);
    }

    private MyDatabaseHelper dbHelper;
}
