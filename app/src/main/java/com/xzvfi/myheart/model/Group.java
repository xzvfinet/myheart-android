package com.xzvfi.myheart.model;

/**
 * Created by xzvfi on 2016-07-15.
 */
public class Group {
    private String name;
    private int user_num;
    private boolean activated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUser_num() {
        return user_num;
    }

    public void setUser_num(int user_num) {
        this.user_num = user_num;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
