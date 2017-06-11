package com.ncslab.pyojihye.translateprogram.Movement;

/**
 * Created by nsc1303-PJH on 2016-12-09.
 */

public class ViewerDataBase {

    private String id;
    private String userName;
    private String time;
    private String delete;
    private int gap;
    private String text;

    public ViewerDataBase(String userName, String time, String delete, int gap, String text) {
        this.userName = userName;
        this.time = time;
        this.delete = delete;
        this.gap = gap;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
