package com.purduecs.kiwi.oneup.models;

/**
 * Created by Adam on 4/2/16.
 */
public class Attempt {
    public int place;
    public String image;
    public int number;
    public String desc;
    public String winner;
    public String time;

    public Attempt(int place, String s, int n, String d, String w, String t) {
        this.place = place;
        this.image = s;
        this.number = n;
        this.desc = d;
        this.winner = w;
        this.time = t;
    }
}
