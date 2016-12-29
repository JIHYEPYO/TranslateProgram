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

public class ViewerActivity extends Activity {
    private final String TAG="ViewerActivity";

    public TextView textViewViewer;
    private int wordNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate()");

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
        Log.d(TAG,"onResume()");

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

                String countText = "";
                int beforeSpace = 0;
                int count=0;
                for (int i = 0; i < buf.length(); i++) { //파일에 담겨있는 글자 수만큼 반복
                    boolean countChange = false;
                    if (buf.charAt(i)==' '||buf.charAt(i)=='\n'||buf.charAt(i)==','||buf.charAt(i)==':'||buf.charAt(i)=='/'||buf.charAt(i)=='\"'||buf.charAt(i)=='\'') { //띄어쓰기로 wordNum값 증가 시키기 위해, 초기에 이거 Gap을 위해서 사용했었음
                        if (Const.gap != 0) { //Gap이 0이 아닐때
                            if (count==Const.gap || wordNum==0) { //Gap의 간격일경우에
                                String countUnderbar = "";
                                for (int j = 0; j < countText.length(); j++) {
                                    countUnderbar = countUnderbar + "_";
                                }
                                buf.replace(beforeSpace, i, countUnderbar);
                                countText = "";
                                countChange = true;
                                count=0;
                            }else{
                                count++;
                            }
                            if (countChange == false) { //countChange가 변경되지 않았을 때
                                countText = "";
                            }
                            beforeSpace = i + 1;
                        }
                        wordNum++;
                    }else { //Gap이 0이 아니거나 줄바꿈하지 않았을때.
                        countText += buf.charAt(i);
                    }
                }

//                Log.v("Replace : ",buf.toString());
                beforeSpace=0;
                if(Const.delete.size()>0){
                    String text="";
                    int find=0; //delete에서 찾아야 하는 단어 수

                    for(int i=0;i<buf.length();i++){ //파일에 담겨있는 글자 수만큼 반복
                        if(buf.charAt(i)==' '||buf.charAt(i)=='\n'||buf.charAt(i)==','||buf.charAt(i)==':'||buf.charAt(i)=='/'||buf.charAt(i)=='\"'||buf.charAt(i)=='\''){
                            String underbar="";
                            for(int j=0;j<text.length();j++){ //단어의 수만큼 _를 찍어주는거
                                underbar=underbar+"_";
                            }
                            boolean change=false;
                            for(int k=0;k<Const.delete.size();k++){
                                Log.v("검색한다",Const.delete.get(k));
                                if(Const.delete.get(k).equalsIgnoreCase(text)){
                                    buf.replace(beforeSpace, i, underbar);
                                    change=true;
                                    text="";
                                }else{
                                    find++;
                                    text="";
                                }
                                if(find==Const.delete.size() && change==false){
                                    text="";
                                }
                            }
                            beforeSpace=i+1;
                        }else{
                            text+=buf.charAt(i);
                        }
                    }
                }
//                Log.v("Replace : ",buf.toString());

                fileInputStream.close();
                textViewViewer.setText(buf.toString());
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
