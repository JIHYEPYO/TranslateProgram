package com.example.pyojihye.translateprogram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.pyojihye.translateprogram.Movement.Const;
import com.example.pyojihye.translateprogram.R;

public class SelectModeActivity extends AppCompatActivity {
    private final String TAG="SelectModeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Select Mode");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        Log.d(TAG,"onCreate()");

    }

    public void onImageTrainingClick(View v){
        Intent intentTrainingOption = new Intent(this, TrainingOptionActivity.class);
        startActivity(intentTrainingOption);
        Log.d(TAG,"onImageTrainingClick()");
    }

    public void onImageViewerClick(View v){
        Const.delete.clear();
        Const.delete_num=0;
        Intent intentViewerOption = new Intent(this, ViewerOptionActivity.class);
        startActivity(intentViewerOption);
        Log.d(TAG,"onImageViewerClick()");
    }
}