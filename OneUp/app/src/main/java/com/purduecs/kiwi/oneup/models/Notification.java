package com.purduecs.kiwi.oneup.models;

import java.util.Date;

/**
 * Created by Adam on 4/7/16.
 */
public class Notification {
    public String challenge_id;
    public String desc;
    public String user;
    public String image;
    public Date time;

    public Notification() {

    }

    public Notification(String image, String challenge_id, String desc) {
        this.challenge_id = challenge_id;
        this.image = image;
        this.desc = desc;
    }
}
