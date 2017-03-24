package com.example.pyojihye.translateprogram.Movement;

/**
 * Created by nsc1303-PJH on 2016-12-09.
 */

public class ButtonViewerDataBase {

    private String id;
    private String userName;
    private String time;
    private String pushButton;
    private String value;


    public ButtonViewerDataBase(String userName, String time, String pushButton, String value) {
        this.userName = userName;
        this.time = time;
        this.pushButton = pushButton;
        this.value = value;
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

    public String getPushButton() {
        return pushButton;
    }

    public void setPushButton(String pushButton) {
        this.pushButton = pushButton;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.pushButton = value;
    }
}
