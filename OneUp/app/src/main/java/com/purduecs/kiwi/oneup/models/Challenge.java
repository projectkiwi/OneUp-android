package com.purduecs.kiwi.oneup.models;

import android.graphics.Bitmap;
import android.provider.MediaStore;

/**
 * Created by Adam on 3/3/16.
 */
public class Challenge {
    public String id;
    public String attempt_id;
    public String name;
    public String image;
    public String owner;
    public String[] categories;
    public int score;
    public String time;
    public String desc;
    public String previewImage;
    public String pattern;
    // 0 is unliked, 1 is liked, 2 is liked a past attempt
    public int likes;
    public int liked;
    public boolean bookmarked;

    public String location;
    public int debug_flag = 0;

        /*public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getId() { return image; }
        public void setId(int image) { this.image = image; }
        public String getOwner() { return  owner; }
        public void setOwner(String owner) { this.owner = owner;}
        public String[] getCategories() { return  categories; }
        public void setCategories(String[] owner) { this.categories = categories;}*/

    public Challenge() {

    }
}
