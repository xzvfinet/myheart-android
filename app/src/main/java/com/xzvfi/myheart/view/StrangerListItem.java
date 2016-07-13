package com.xzvfi.myheart.view;

import android.graphics.drawable.Drawable;

/**
 * Created by xzvfi on 2016-07-13.
 */
public class StrangerListItem {
    private Drawable faceDrawable;
    private String name;
    private String description;

    public Drawable getFaceDrawable() {
        return faceDrawable;
    }

    public void setFaceDrawable(Drawable faceDrawable) {
        this.faceDrawable = faceDrawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
