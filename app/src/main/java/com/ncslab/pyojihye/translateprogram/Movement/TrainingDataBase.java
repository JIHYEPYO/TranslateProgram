package com.ncslab.pyojihye.translateprogram.Movement;

/**
 * Created by nsc1303-PJH on 2016-12-09.
 */

public class TrainingDataBase {

    private String id;
    private String userName;
    private String time;
    private int wpm;
    private String text;

    public TrainingDataBase(String userName, String time, int wpm, String text) {
        this.userName = userName;
        this.time = time;
        this.wpm = wpm;
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

    public int getWpm() {
        return wpm;
    }

    public void setWpm(int wpm) {
        this.wpm = wpm;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
