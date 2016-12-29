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
import java.util.ArrayList;
import java.util.List;

import static com.example.pyojihye.translateprogram.Movement.Const.replace;
import static com.example.pyojihye.translateprogram.Movement.Const.replace_num;
import static com.example.pyojihye.translateprogram.R.id.TextViewTraining;

public class TrainingActivity extends AppCompatActivity {

    private final String TAG = "TrainingActivity";
    private TextView textViewTraining;
    private ImageView imageViewStart;
    private ImageView imageViewPast;
    private ImageView imageViewFuture;
    private TextView numberPercent;
    private boolean startChange = false;

    private int currentPosition;
    private int startPosition;
    private int endPosition;
    public String replaceTextView;
    private int num;
    public double percent;
    private boolean threadStart = false;
    private List<String> origin = new ArrayList<>();
    private TrainingThread trainingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        setTitle("Training Mode");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_mode);

        textViewTraining = (TextView) findViewById(TextViewTraining);
        Typeface face = Typeface.createFromAsset(getAssets(), "D2Coding.ttc");
        textViewTraining.setTypeface(face);
        imageViewStart = (ImageView) findViewById(R.id.imageViewStart);
        imageViewPast = (ImageView) findViewById(R.id.imageViewPast);
        imageViewFuture = (ImageView) findViewById(R.id.imageViewFuture);
        imageViewStart.setImageResource(R.drawable.start);
        numberPercent = (TextView) findViewById(R.id.numberPercent);
        textViewTraining.setVisibility(View.INVISIBLE);
        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        replace.clear();
        replace_num = 0;
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream;
        String strPath = null;

        imageViewStart.setImageResource(R.drawable.start);
        textViewTraining.setVisibility(View.INVISIBLE);
        imageViewPast.setImageResource(0);
        imageViewFuture.setImageResource(0);
        imageViewPast.setClickable(false);
        imageViewFuture.setClickable(false);

        try {
            File path = new File(Const.strPath);
            fileInputStream = new FileInputStream(path);

            if (fileInputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                StringBuffer buf = new StringBuffer();

                while ((strPath = bufferedReader.readLine()) != null) {
                    buf.append(strPath + '\n');
//                    Log.v("strPath: ", strPath+"");
                }

                int point = 0;
                for (int i = 0; i < buf.length(); i++) {
                    if (buf.charAt(i) == ' ' || buf.charAt(i) == '\n') {
                        replace.add(buf.substring(point, i + 1));
                        origin.add(buf.substring(point, i + 1));
                        point = i + 1;
                    }
                }

                fileInputStream.close();
                textViewTraining.setText(buf.toString());
            }
            Log.d(TAG, "onResume()");
            num = 100;
            handler.sendEmptyMessage(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStartClick(View v) {

        Log.d(TAG, "onStartClick()");
        if (!startChange) {
            textViewTraining.setVisibility(View.VISIBLE);
            startChange = true;
            imageViewStart.setImageResource(R.drawable.pause);
            imageViewPast.setImageResource(0);
            imageViewFuture.setImageResource(0);
            imageViewPast.setClickable(false);
            imageViewFuture.setClickable(false);
            trainingThread = new TrainingThread();
            if (threadStart) {
                trainingThread.restart();
            } else {
                trainingThread.start();
            }
        } else {
            startChange = false;
            imageViewStart.setImageResource(R.drawable.start);
            imageViewPast.setImageResource(R.drawable.past);
            imageViewFuture.setImageResource(R.drawable.future);
            imageViewPast.setClickable(true);
            imageViewFuture.setClickable(true);
        }
        Log.d(TAG, "onStartClick()");
    }

    public void onPastClick(View v) {
        Log.d(TAG, "onPastClick()");
        int position;
        if (currentPosition != 0) {
            replace.set(currentPosition - 1, origin.get(currentPosition - 1));
            currentPosition--;

            String str = "";
            for (String s : replace) {
                str += s;
            }
            replaceTextView = str;

            handler.sendEmptyMessage(0);
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(TextViewTraining), R.string.snack_bar_training, Snackbar.LENGTH_LONG);

            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    public void onFutureClick(View v) {
        Log.d(TAG, "onFutureClick()");
        Training();
    }

    private void TrainingInit() {
        Log.d(TAG, "TrainingInit()");

        currentPosition = 0;
        startPosition = 0;
        endPosition = replace.size();

        Log.v("Word count: ", endPosition + "");
//        //안드로이드 휴대폰 상에서 몇줄인지를 나타내는 구문
//        textViewTraining.post(new Runnable() {
//            @Override
//            public void run() {
//                LineNum=textViewTraining.getLineCount();
//                Log.v("Line count: ", textViewTraining.getLineCount()+"");
//            }
//        });
    }

    private void Training() {
        if (currentPosition < endPosition) {
            String text = "";
//                Log.v("textView :",textViewTraining.getText()+"");

            String spacebar = "";
            text = replace.get(currentPosition);

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == '\n') {
                    spacebar = spacebar + "\n";
                } else {
                    spacebar = spacebar + " ";
                }
            }
            replace.set(currentPosition, spacebar);
            String str = "";
            for (String s : replace) {
                str += s;
            }
            replaceTextView = str;

            handler.sendEmptyMessage(0);
            PercentCalculate();
            currentPosition++;

            Log.v("startPosition :", startPosition + "");
            Log.v("endPosition :", endPosition + "");
        } else {
            PercentCalculate();
            startChange = true;
            trainingThread.restart();
        }
    }

    private void PercentCalculate() {
        percent = ((double) currentPosition / (double) endPosition) * 100;
        if (percent < 99 && startChange) {
            percent++;
        }
        num = 100 - (int) percent;
        Log.v("num: ", num + "");
        handler.sendEmptyMessage(1);
    }


    class TrainingThread extends Thread {
        private boolean pause = false;

        void pause() {
            pause = true;
        }

        synchronized void restart() {
            notify();
            pause = false;
            Log.d(TAG, "restart()");
        }

        @Override
        public void run() {
            threadStart = true;
            TrainingInit();
            Log.d(TAG, "run()");

            while (true) {
                try {
                    if (startChange) {
                        sleep(60000 / Const.wpm);
                        Training();
                        if (percent >= 100) {
                            pause();
                            handler.sendEmptyMessage(2);
                        }
                        synchronized (this) {
                            if (pause) {
                                wait();
                            }
                        }
                    } else {

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    textViewTraining.setText(replaceTextView);
                    //                Log.v("replaceTextView :",replaceTextView+"");
                    break;
                case 1:
                    numberPercent.setText(num + "%");
                    break;
                case 2:
                    startChange = false;
                    AlertDialog.Builder d = new AlertDialog.Builder(TrainingActivity.this);
                    d.setTitle(getString(R.string.dialog_restart));
                    d.setMessage(getString(R.string.dialog_contents_restart));
                    d.setIcon(R.mipmap.ic_launcher);

                    d.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
//                            Intent intent = getIntent();
//                            startActivity(intent);
//                            finish();
                            onResume();
                            threadStart = false;
                        }
                    });

                    d.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent selectIntent = new Intent(getApplicationContext(), TrainingOptionActivity.class);
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