package com.example.pyojihye.translateprogram.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pyojihye.translateprogram.Movement.Const;
import com.example.pyojihye.translateprogram.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.example.pyojihye.translateprogram.R.id.TextViewTraining;

/**
 * Created by PYOJIHYE on 2016-07-25.
 */
public class TrainingActivity extends AppCompatActivity {

    private final String TAG="TrainingActivity";
    private TextView textViewTraining;
    private ImageView imageViewStart;
    private ImageView imageViewPast;
    private ImageView imageViewFuture;
    private TextView numberPercent;
    private boolean startChange=false;

    private int lineNum;
    private int wordNum;
    private int currentPosition;
    private int startPosition;
    private int endPosition;
    private String currentWord;
    public String replaceTextView;
    private int num;
    private int originalEndPosition;
    private int originalCurrentPosition;
    public double percent;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate()");
        setTitle("Training Mode");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_mode);

        textViewTraining = (TextView)findViewById(TextViewTraining);
        Typeface face = Typeface.createFromAsset(getAssets(), "D2Coding.ttc");
        textViewTraining.setTypeface(face);
        imageViewStart=(ImageView)findViewById(R.id.imageViewStart);
        imageViewPast=(ImageView)findViewById(R.id.imageViewPast);
        imageViewFuture=(ImageView)findViewById(R.id.imageViewFuture);
        imageViewStart.setImageResource(R.drawable.start);
        numberPercent=(TextView)findViewById(R.id.numberPercent);
        textViewTraining.setVisibility(View.INVISIBLE);
        Log.d(TAG,"onCreate()");
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume()");
        super.onResume();
        Const.replace.clear();
        Const.replace_num=0;
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream;
        String strPath = null;

        originalCurrentPosition=0;
        PercentCalculate();

        imageViewStart.setImageResource(R.drawable.start);
        textViewTraining.setVisibility(View.INVISIBLE);

        try{
            File path = new File(Const.strPath);
            fileInputStream=new FileInputStream(path);

            if(fileInputStream!=null){
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                StringBuffer buf = new StringBuffer();

                while((strPath=bufferedReader.readLine())!=null){
                    buf.append(strPath+'\n');
                    lineNum++;
//                    Log.v("strPath: ", strPath+"");
                }
                Log.v("Line count: ", lineNum+"");

                for(int i=0;i<buf.length();i++){
                    if(buf.charAt(i)==' '){
                        wordNum++;
                    }
                }
                startPosition=0;
                endPosition=buf.length();
                originalEndPosition=endPosition;
                Log.v("Word count: ", wordNum+"");

                fileInputStream.close();
                textViewTraining.setText(buf.toString());
            }
            TrainingInit();
            Log.d(TAG,"onResume()");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStartClick(View v){
        Log.d(TAG,"onStartClick()");
        if(startChange==false){
            textViewTraining.setVisibility(View.VISIBLE);
            startChange=true;
            imageViewStart.setImageResource(R.drawable.pause);
            imageViewPast.setImageResource(0);
            imageViewFuture.setImageResource(0);
            imageViewPast.setClickable(false);
            imageViewFuture.setClickable(false);
            TrainingThread trainingThread = new TrainingThread();
            trainingThread.start();

        }else{
            startChange=false;
            imageViewStart.setImageResource(R.drawable.start);
            imageViewPast.setImageResource(R.drawable.past);
            imageViewFuture.setImageResource(R.drawable.future);
            imageViewPast.setClickable(true);
            imageViewFuture.setClickable(true);
        }
        Log.d(TAG,"onStartClick()");
    }

    public void onPastClick(View v){
        Log.d(TAG,"onPastClick()");
        StringBuffer buf = new StringBuffer(textViewTraining.getText().toString());
        int position;
        if(originalCurrentPosition!=0){
            if(currentPosition-Const.replace.get(Const.replace_num-1).length()<=0){
                position=0;
            }else{
                position=currentPosition-Const.replace.get(Const.replace_num-1).length();
            }

            if(currentPosition>0){
                buf.replace(position, currentPosition-1, Const.replace.get(Const.replace_num-1));
            }else{
                buf.replace(position, 0, Const.replace.get(Const.replace_num-1));
            }

            textViewTraining.setText(buf.toString());

            originalCurrentPosition=originalCurrentPosition-Const.replace.get(Const.replace_num-1).length();

            if(currentPosition!=0){
                currentPosition=currentPosition-Const.replace.get(Const.replace_num-1).length();
            }
            PercentCalculate();
            Const.replace_num--;
            Const.replace.remove(Const.replace_num);
        }else{
            Snackbar snackbar = Snackbar.make(findViewById(TextViewTraining), R.string.snack_bar_training, Snackbar.LENGTH_LONG);

            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    public void onFutureClick(View v){
        Log.d(TAG,"onFutureClick()");
        Training();
    }

    private void TrainingInit(){
        Log.d(TAG,"TrainingInit()");
        lineNum=0;
        wordNum=0;
        currentPosition =0;
        startPosition=0;
//        //안드로이드 휴대폰 상에서 몇줄인지를 나타내는 구문인것 같음
//        textViewTraining.post(new Runnable() {
//            @Override
//            public void run() {
//                LineNum=textViewTraining.getLineCount();
//                Log.v("Line count: ", textViewTraining.getLineCount()+"");
//            }
//        });
    }

    private void Training(){
        if(originalCurrentPosition < originalEndPosition){
            String text = "";
            for(currentPosition= startPosition; currentPosition <endPosition; currentPosition++){
//                Log.v("textView :",textViewTraining.getText()+"");
                originalCurrentPosition++;

                Log.v("originCurrentPosition",originalCurrentPosition+"");
                if(textViewTraining.getText().toString().charAt(currentPosition)==' '
                        ||textViewTraining.getText().toString().charAt(currentPosition)=='\n'){
                    if(text.length()>0||!text.contains("\n")||!text.contains(" ")){
                        String spacebar="";
                        for(int i=0;i<text.length();i++){
                            if(text.charAt(i)==5){
                                spacebar=spacebar+"　";
                            }else{
                                spacebar= spacebar+" ";
                            }
                        }
                        Const.replace.add(Const.replace_num,text+" ");
                        Const.replace_num++;
                        startPosition=currentPosition+1;

                        if(textViewTraining.getText().toString().charAt(currentPosition)=='\n'){
                            int i=0;
                            String space="";

                            Const.replace.remove(Const.replace_num-1);
                            Const.replace.add(Const.replace_num-1,text+"\n");
                            while(textViewTraining.getText().toString().charAt(i)!='\n'){
                                if(textViewTraining.getText().toString().charAt(i)==' '){
                                    space=space+" ";
                                }
                                i++;
                            }
                            Log.d(TAG,text);
                            text=space+text+"\n";
                            Log.d(TAG,text);
                            spacebar="";
                            currentPosition=0;
                            startPosition=0;
                        }
                        replaceTextView = textViewTraining.getText().toString().replaceFirst(text,spacebar);
                        handler.sendEmptyMessage(0);
                        PercentCalculate();
//                    Log.v("text : ", text + "");
//                    Log.v("space : ", spacebar + "");
//                    Log.v("replace : ", replaceTextView + "");
                        endPosition=replaceTextView.length();
                        Log.v("startPosition :",startPosition+"");
                        Log.v("endPosition :",endPosition+"");
                        break;
                    }
                }else{
                    text+=textViewTraining.getText().toString().charAt(currentPosition);
//                    Log.v("text : ", text + "");
                }
            }
        }
    }

    private void PercentCalculate(){
        percent=((double)originalCurrentPosition/(double)originalEndPosition)*100;
        if(percent<99 && startChange==true){
            percent++;
        }
        num=100- (int)percent;
        Log.v("num: ", num+"");
        handler.sendEmptyMessage(1);
    }


    class TrainingThread extends Thread{

        private boolean pause = false;

        void pause(){
            pause=true;
        }

        synchronized void restart(){
            notify();
            pause=false;
            Log.d(TAG,"restart()");

        }

        synchronized void exit(){
            interrupt();
        }

        @Override
        public void run() {

            Log.d(TAG,"run()");

            super.run();
            TrainingInit();
            try{
                while(startChange){
                    sleep(60000/ Const.wpm);
                    Training();
                    if(percent>=100){
                        pause();
                        handler.sendEmptyMessage(2);
                    }
                    synchronized (this){
                        if(pause){
                            wait();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    textViewTraining.setText(replaceTextView);
                    //                Log.v("replaceTextView :",replaceTextView+"");
                    break;
                case 1:
                    numberPercent.setText(num+"%");
                    break;
                case 2:
                    startChange=false;
                    AlertDialog.Builder d = new AlertDialog.Builder(TrainingActivity.this);
                    d.setTitle(getString(R.string.dialog_restart));
                    d.setMessage(getString(R.string.dialog_contents_restart));
                    d.setIcon(R.mipmap.ic_launcher);

                    d.setPositiveButton(getString(R.string.dialog_yes),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
//                            Intent intent = getIntent();
//                            startActivity(intent);
//                            finish();
                            onResume();
                        }
                    });

                    d.setNegativeButton(getString(R.string.dialog_no),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent selectIntent = new Intent(getApplicationContext(),TrainingOptionActivity.class);
                            startActivity(selectIntent);
                            finish();
                        }
                    });
                    d.show();
                    break;
            }
        }
    };
}