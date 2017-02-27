package com.example.pyojihye.translateprogram.Movement;

/**
 * Created by nsc1303-PJH on 2016-12-09.
 */

public class ModeDataBase {

    private String id;
    private String mode;
    private String userName;
    private String time;

    public ModeDataBase(String time, String userName, String mode) {

        this.time = time;
        this.userName = userName;
        this.mode = mode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
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
}
