package com.xzvfi.myheart.model;

import java.io.Serializable;

/**
 * Created by xzvfi on 2016-07-15.
 */
public class User implements Serializable{
    String user_name;
    String user_social_token;
    String user_description;
    int user_group;
    int user_heart_num;

    public User(String user_name, String user_social_token, String user_description, int user_group, int user_heart_num) {
        this.user_name = user_name;
        this.user_social_token = user_social_token;
        this.user_description = user_description;
        this.user_group = user_group;
        this.user_heart_num = user_heart_num;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getToken() {
        return user_social_token;
    }

    public void setToken(String user_social_token) {
        this.user_social_token = user_social_token;
    }

    public int getUserGroup() {
        return user_group;
    }

    public void setUserGroup(int user_group) {
        this.user_group = user_group;
    }

    public int getHeartNum() {
        return user_heart_num;
    }

    public void setHeartNum(int user_heart_num) {
        this.user_heart_num = user_heart_num;
    }

    public String getUserDescription() {
        return user_description;
    }

    public void setUserDescription(String user_description) {
        this.user_description = user_description;
    }
}
