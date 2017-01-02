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
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pyojihye.translateprogram.Movement.Const;
import com.example.pyojihye.translateprogram.Movement.ModeTextView;
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

public class TrainingActivity extends AppCompatActivity {

    private final String TAG = "TrainingActivity";
    private ModeTextView modeTextViewTraining;
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
    private boolean change = false;
    public String htmlText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        setTitle("Training Mode");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_mode);

        modeTextViewTraining = (ModeTextView) findViewById(R.id.ModeTextViewTraining);
        Typeface face = Typeface.createFromAsset(getAssets(), "D2Coding.ttc");
        modeTextViewTraining.setTypeface(face);
        imageViewStart = (ImageView) findViewById(R.id.imageViewStart);
        imageViewPast = (ImageView) findViewById(R.id.imageViewPast);
        imageViewFuture = (ImageView) findViewById(R.id.imageViewFuture);
        imageViewStart.setImageResource(R.drawable.start);
        numberPercent = (TextView) findViewById(R.id.numberPercent);
        modeTextViewTraining.setVisibility(View.INVISIBLE);

//        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onResume() {
//        Log.d(TAG, "onResume()");
        super.onResume();
        replace.clear();
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream;
        String strPath = null;

        imageViewStart.setImageResource(R.drawable.start);
        modeTextViewTraining.setVisibility(View.INVISIBLE);
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
                }

                int point = 0;
                for (int i = 0; i < buf.length(); i++) {
                    if (buf.charAt(i) == ' ') {
                        replace.add(buf.substring(point, i + 1));
                        origin.add(buf.substring(point, i + 1));
                        point = i + 1;
                    }
                    if (buf.charAt(i) == '\n') {
                        replace.add(buf.substring(point, i));
                        replace.set(replace.size() - 1, replace.get(replace.size() - 1) + "\n");

                        origin.add(buf.substring(point, i));
                        origin.set(replace.size() - 1, origin.get(origin.size() - 1) + "\n");
                        point = i + 1;
                    }
                }

                fileInputStream.close();

                String str = "";
                for (int i = 0; i < replace.size(); i++) {
                    str += replace.get(i);
                }
                modeTextViewTraining.setText(str);
            }
//            Log.d(TAG, "onResume()");
            num = 100;
            handler.sendEmptyMessage(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStartClick(View v) {
//        Log.d(TAG, "onStartClick()");
        if (!startChange) {
            modeTextViewTraining.setVisibility(View.VISIBLE);
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
            change = false;
            imageViewStart.setImageResource(R.drawable.start);
            imageViewPast.setImageResource(R.drawable.past);
            imageViewFuture.setImageResource(R.drawable.future);
            imageViewPast.setClickable(true);
            imageViewFuture.setClickable(true);
        }
    }

    public void onPastClick(View v) {
//        Log.d(TAG, "onPastClick()");
        int position;

        if (currentPosition != 0) {
            if (!change) {
                replace.set(currentPosition - 1, origin.get(currentPosition - 1));
            } else {
                startPosition = currentPosition - 1;
                replace.set(currentPosition - 1, origin.get(currentPosition - 1));
            }

            currentPosition--;

            String str = "";
            for (String s : replace) {
                str += s;
            }
            replaceTextView = str;
            PercentCalculate();
            handler.sendEmptyMessage(0);
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.numberPercent), R.string.snack_bar_training, Snackbar.LENGTH_LONG);

            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    public void onFutureClick(View v) {
        Log.d(TAG, "onFutureClick()");
        change = true;
        Training();
    }

    private void TrainingInit() {
        Log.d(TAG, "TrainingInit()");

        currentPosition = 0;
        startPosition = 0;
        endPosition = replace.size();

        Log.v("Word count: ", endPosition + "");
    }

    private void Training() {
        if (currentPosition < endPosition) {
            String text = "";

            String spacebar = "";
            text = replace.get(currentPosition);

            for (int i = 0; i < text.length(); i++) {
                if (text.substring(i, text.length()).equals("\n")) {
                    spacebar = spacebar + "\n";
                    replace.set(currentPosition, spacebar);
                    startPosition = currentPosition + 1;
                    break;
                } else {
                    spacebar = spacebar + " ";
                    replace.set(currentPosition, spacebar);
                }
            }

            String str = "";

            for (int i = startPosition; i < endPosition; i++) {
                str += replace.get(i);
            }

            replaceTextView = str;

            handler.sendEmptyMessage(0);
            PercentCalculate();
            currentPosition++;

//            Log.v("startPosition :", startPosition + "");
//            Log.v("endPosition :", endPosition + "");
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
                    String st="                          ";
                    if(replaceTextView.contains(st)){
                        replaceTextView=replaceTextView.replaceAll(st,"");
//                        startPosition=currentPosition;
                    }
                    modeTextViewTraining.setText(replaceTextView);

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown()");

        //'뒤로가기'키가 눌렸을때 종료여부를 묻는 다이얼로그 띄움
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            trainingThread.pause();
            trainingThread.interrupt();
        }
        return super.onKeyDown(keyCode, event);
    }
}