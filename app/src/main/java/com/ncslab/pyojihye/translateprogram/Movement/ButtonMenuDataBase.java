package com.ncslab.pyojihye.translateprogram.Movement;

/**
 * Created by nsc1303-PJH on 2016-12-09.
 */

public class ButtonMenuDataBase {

    private String id;
    private String userName;
    private String time;
    private String pushButton;
    private String activityName;

    public ButtonMenuDataBase(String userName, String time, String pushButton,String activityName) {
        this.userName = userName;
        this.time = time;
        this.pushButton = pushButton;
        this.activityName = activityName;
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

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.pushButton = activityName;
    }
}
