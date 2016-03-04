package com.purduecs.kiwi.oneup.models;

/**
 * Created by Adam on 3/3/16.
 */
public class Challenge {
    public String name;
    public int id;
    public String owner;
    public String[] categories;
    public int score;
    public float time;
    public String desc;

        /*public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getOwner() { return  owner; }
        public void setOwner(String owner) { this.owner = owner;}
        public String[] getCategories() { return  categories; }
        public void setCategories(String[] owner) { this.categories = categories;}*/

    public Challenge() {

    }

    public Challenge(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
