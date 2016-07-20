package com.xzvfi.myheart.model;

import java.io.Serializable;

/**
 * Created by xzvfi on 2016-07-15.
 */
public class User implements Serializable{
    String user_name;
    String token;
    int user_group;
    int heart_num;

    public User(String user_name, String token, int user_group, int heart_num) {
        this.user_name = user_name;
        this.token = token;
        this.user_group = user_group;
        this.heart_num = heart_num;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserGroup() {
        return user_group;
    }

    public void setUserGroup(int user_group) {
        this.user_group = user_group;
    }

    public int getHeartNum() {
        return heart_num;
    }

    public void setHeartNum(int heart_num) {
        this.heart_num = heart_num;
    }
}
