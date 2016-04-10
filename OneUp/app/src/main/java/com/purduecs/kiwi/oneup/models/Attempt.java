package com.purduecs.kiwi.oneup.models;

import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.format.Time;

import java.util.Date;

/**
 * Created by Adam on 4/2/16.
 */
public class Attempt {
    public String id;

    public int place;
    public int number;
    public String desc;
    public Date time;

    public int votes_num;
    public String owner;
    public int likes_num;
    public boolean has_liked;
    public String gif;
    public String video; // can be null
    public String image;


    //0 is picture, 1 is video
    public int mediaType;
    public Bitmap picture;
    //public MediaStore.Video video;
    public String picture_url;
    final static int TYPE_PICTURE = 0;
    final static int TYPE_VIDEO = 1;


    public Attempt(int place, String s, int n, String d, String w, Date t) {
        this.place = place;
        this.image = s;
        this.number = n;
        this.desc = d;
        this.owner = w;
        this.time = t;
    }

    public Attempt () {

    }
}
