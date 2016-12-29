package com.example.pyojihye.translateprogram.Activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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

import static com.example.pyojihye.translateprogram.Movement.Const.delete;
import static com.example.pyojihye.translateprogram.Movement.Const.gap;

public class ViewerActivity extends Activity {
    private final String TAG = "ViewerActivity";

    private List<String> word = new ArrayList<>();
    public TextView textViewViewer;
    private int wordNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        setTitle("Viewer Mode");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer_mode);
        textViewViewer = (TextView) findViewById(R.id.textViewViewer);
        Typeface face = Typeface.createFromAsset(getAssets(), "D2Coding.ttc");
        textViewViewer.setTypeface(face);
        textViewViewer.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");

        super.onResume();
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream;
        String strPath = null;

        try {
            File path = new File(Const.strPath);
            fileInputStream = new FileInputStream(path);

            if (fileInputStream != null) { //파일 존재시
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                StringBuffer buf = new StringBuffer();

                while ((strPath = bufferedReader.readLine()) != null) { //파일에 더이상 읽을 값이 없을때까지
                    buf.append(strPath + "\n");
                }

                //delete와 word 배열에 값 할당
                int point = 0;
                for (int i = 0; i < buf.length(); i++) {
                    if (buf.charAt(i) == ' ' || buf.charAt(i) == '\n') {
                        word.add(buf.substring(point, i + 1));
                        point = i + 1;
                    }
                }

                //Gap먼저 시작.
                //Gap이 0이 아닐때만 반복되도록 설정
                if (gap != 0) {
                    String text;
                    for (int i = 0; i < word.size(); i++) {
                        if (i % (gap + 1) == 0) {
                            String underbar = "";
                            text = word.get(i);
                            for (int j = 0; j < text.length() - 1; j++) {
                                underbar += "_";
                            }
                            if (text.charAt(text.length() - 1) == '\n') {
                                underbar += "\n";
                            } else {
                                underbar += " ";
                            }
                            word.set(i, underbar);
                        }
                    }
                }

                //삭제할 단어를 입력한 경우에만
                if (delete.size() > 0) {
                    String text;
                    for (int i = 0; i < word.size(); i++) {
                        for (int j = 0; j < delete.size(); j++) {
                            if (word.get(i).equals(delete.get(j) + " ") || word.get(i).equals(delete.get(j) + "\n")) {
                                text = word.get(i);

                                String underbar = "";
                                text = word.get(i);
                                for (int k = 0; k < text.length() - 1; k++) {
                                    underbar += "_";
                                }
                                if (text.charAt(text.length() - 1) == '\n') {
                                    underbar += "\n";
                                } else {
                                    underbar += " ";
                                }
                                word.set(i, underbar);
                            }
                        }
                    }
                }

                fileInputStream.close();

                String str = "";
                for (String s : word) {
                    str += s;
                }
                textViewViewer.setText(str);
            }
            Log.v("TextView : ", textViewViewer.getText().toString());
            Log.d(TAG, "onResume()");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
