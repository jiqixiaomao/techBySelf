package com.techbyself.ui;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Video   extends DataSupport {
    private int mId;
    private String mVidioName;
    private  String  mNceth;
    private  String  mTitle;
    private String mVidioid;
    private int mSentenceNum;
    private ArrayList mSentence;
    private UUID  uuid;
    private Date LastReadTime;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Video() {

    }

    public Video(int id, String vidioName, int vidioDesc, String vidioid, int sentenceNum) {
        mId =id ;
        mVidioName=vidioName;
        mVidioid=vidioid;
        mSentenceNum=sentenceNum;
    }

    public String getNceth() {
        return mNceth;
    }

    public void setNceth(String mNceth) {
        this.mNceth = mNceth;
    }


    public String getVidioid() {
        return mVidioid;
    }

    public void setVidioid(String mVidioid) {
        this.mVidioid = mVidioid;
    }

    public String getVidioName() {
        return mVidioName;
    }

    public void setVidioName(String mVidioName) {
        this.mVidioName = mVidioName;
    }



    public int getSentenceNum() {
        return mSentenceNum;
    }

    public void setSentenceNum(int mSentenceNum) {
        this.mSentenceNum = mSentenceNum;
    }

    public int getId() {

        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }



}
