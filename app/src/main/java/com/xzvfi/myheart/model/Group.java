package com.xzvfi.myheart.model;

import java.io.Serializable;

/**
 * Created by xzvfi on 2016-07-15.
 */
public class Group implements Serializable {
    private int id;
    private String group_name;
    private int group_user_num;
    private int group_activated;

    public Group(int id, String group_name, int group_user_num) {
        this.id = id;
        this.group_name = group_name;
        this.group_user_num = group_user_num;
        this.group_activated = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return group_name;
    }

    public void setName(String name) {
        this.group_name = name;
    }

    public int getUserNum() {
        return group_user_num;
    }

    public void setUserNum(int user_num) {
        this.group_user_num = user_num;
    }

    public int isGroupActivated() {
        return group_activated;
    }

    public void setGroupActivated(int group_activated) {
        this.group_activated = group_activated;
    }
}
