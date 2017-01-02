package com.example.pyojihye.translateprogram.Activity;

import java.util.Date;

/**
 * Created by nsc1303-PJH on 2016-12-09.
 */

public class DataBase {

    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String date;

    public DataBase(String text) {

        this.text = text;
    }

    public DataBase(String text, String name) {
        this.text = text;
        this.name = name;
    }

    public DataBase(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public DataBase(String text, String name, String photoUrl, String date) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
