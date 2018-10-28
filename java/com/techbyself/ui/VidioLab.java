package com.techbyself.ui;


import com.techbyself.vodplay.util.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class VidioLab {
    private static VidioLab sVidioLab;

    private List<Video> sVidios;

    public static VidioLab get() {
        if (sVidioLab == null) {
            sVidioLab = new VidioLab();
        }
        return sVidioLab;
    }

    private VidioLab() {
        sVidios = new ArrayList<>();

    }

    public List<Video> getVidios() {

        return sVidios;
    }
    public List<Video> getAllVidios( ) {
        List<Video>  videos=new   ArrayList<Video>();
        videos=MyDatabaseHelper.getAllVideo();
        return videos;
    }

    public List<Video> getRecentVidios( ) {
        List<Video>  videos=new   ArrayList<Video>();
        videos=MyDatabaseHelper.getRecentViewVideo();
        return videos;
    }

    public Video getCrime(int id) {
        for (Video vidio : sVidios) {
            if (vidio.getId()==id) {
                return vidio;
            }
        }

        return null;
    }
}


