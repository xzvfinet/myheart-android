package com.xzvfi.myheart.model;

import java.io.Serializable;

/**
 * Created by xzvfi on 2016-07-15.
 */
public class User implements Serializable{
    String user_id;
    String user_name;
    String user_token;
    String user_gcm_token;
    String user_description;
    int user_group;
    int user_heart_num;

    public User(String user_id, String user_name, String user_token, String user_gcm_token, String user_description, int user_group, int user_heart_num) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_token = user_token;
        this.user_gcm_token = user_gcm_token;
        this.user_description = user_description;
        this.user_group = user_group;
        this.user_heart_num = user_heart_num;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getToken() {
        return user_token;
    }

    public void setToken(String user_token) {
        this.user_token = user_token;
    }

    public String getUser_gcm_token() {
        return user_gcm_token;
    }

    public void setUser_gcm_token(String user_gcm_token) {
        this.user_gcm_token = user_gcm_token;
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
