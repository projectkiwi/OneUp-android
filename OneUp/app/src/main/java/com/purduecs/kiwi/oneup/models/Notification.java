package com.purduecs.kiwi.oneup.models;

/**
 * Created by Adam on 4/7/16.
 */
public class Notification {
    public String challenge_id;
    public String desc;
    public String image;

    public Notification(String image, String challenge_id, String desc) {
        this.challenge_id = challenge_id;
        this.image = image;
        this.desc = desc;
    }
}
