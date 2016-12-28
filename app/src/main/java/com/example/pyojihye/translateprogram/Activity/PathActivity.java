package com.example.pyojihye.translateprogram.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pyojihye.translateprogram.Movement.Const;
import com.example.pyojihye.translateprogram.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PYOJIHYE on 2016-07-25.
 */
public class PathActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final String TAG="PathActivity";

    final private int APP_PERMISSION_STORAGE=1;
    String root="";
    String path="";
    TextView textMsg;
    ListView listFile;
    ArrayList<String> arrayFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Select Path");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        textMsg = (TextView) findViewById(R.id.textViewPath);
        checkPermission();
        Log.d(TAG,"onCreate()");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,"onKeyDown()");

        //빽(취소)키가 눌렸을때 종료여부를 묻는 다이얼로그 띄움
        if((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder d = new AlertDialog.Builder(PathActivity.this);
            d.setTitle(getString(R.string.dialog_title));
            d.setMessage(getString(R.string.dialog_contents));
            d.setIcon(R.mipmap.ic_launcher);

            d.setPositiveButton(getString(R.string.dialog_yes),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    PathActivity.this.finish();
                }
            });

            d.setNegativeButton(getString(R.string.dialog_no),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.cancel();
                }
            });
            d.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void checkPermission(){
        Log.d(TAG,"checkPermission()");

        if(android.os.Build.VERSION.SDK_INT < 23){ //23버전보다 낮을때
            root = Environment.getExternalStorageDirectory().getAbsolutePath();
            String[] fileList = getFileList(root);

            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i] != null) {
//                Log.d("tag", fileList[i]);
                }
            }
            initListView();
            fileListArray(fileList);
        }
        else{ //23버전보다 높을때
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ||checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                //최초 거부를 선택하면 두번째부터 이벤트 발생 & 권한 획득이 필요한 이융를 설명
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                }

                //요청 팝업 팝업 선택시 onRequestPermissionsResult 이동
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        APP_PERMISSION_STORAGE);

            }
            //권한이 있는 경우
            else{
                root = Environment.getExternalStorageDirectory().getAbsolutePath();
                String[] fileList = getFileList(root);

                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i] != null) {
//                Log.d("tag", fileList[i]);
                    }
                }
                initListView();
                fileListArray(fileList);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG,"onRequestPermissionResult()");

        root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String[] fileList = getFileList(root);

        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i] != null) {
//                Log.d("tag", fileList[i]);
            }
        }
        initListView();
        fileListArray(fileList);
    }

    public void initListView(){
        Log.d(TAG,"initListView()");

        arrayFile=new ArrayList<String>();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayFile);

        listFile = (ListView)findViewById(R.id.listViewPath);
        listFile.setAdapter(adapter);
        listFile.setOnItemClickListener(this);
    }

    public void fileListArray(String[] fileList){
        Log.d(TAG,"fileListArray()");

        if(fileList==null){
            return;
        }

        arrayFile.clear();
        if(root.length()<path.length()){
            arrayFile.add("..");
        }

        for(int i=0;i<fileList.length;i++){
//            Log.d("tag",fileList[i]);
            arrayFile.add(fileList[i]);
        }

        ArrayAdapter adapter = (ArrayAdapter)listFile.getAdapter();
        adapter.notifyDataSetChanged();
    }

    public String[] getFileList(String strPath){
        Log.d(TAG,"getFileList()");

        File fileRoot = new File(strPath);

        if(fileRoot.isDirectory()==false){
            return null;
        }

        path=strPath;
        textMsg.setText(path);
        String[] fileList=fileRoot.list();
        return fileList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"onItemClick()");

        Const.strItem = arrayFile.get(position);
        Const.strPath=getCurrentPath(Const.strItem);
        String[] fileList=getFileList(Const.strPath);
        fileListArray(fileList);

        if (Const.strItem.endsWith("txt")) {
            Intent selectIntent = new Intent(getApplicationContext(),SelectModeActivity.class);
            startActivity(selectIntent);
        }else if(Const.strItem.contains(".") && !Const.strItem.startsWith(".")){
            Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.listViewPath),R.string.snack_bar_format, Snackbar.LENGTH_LONG);
            View v = snackbar.getView();
            TextView textView = (TextView)v.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    public String getCurrentPath(String strFolder){
        Log.d(TAG,"getCurrentPath()");

        String strPath;
        if(strFolder==".."){
            int pos=path.lastIndexOf("/");
            strPath=path.substring(0,pos);
        }else{
            strPath=path+"/"+strFolder;
        }
        return strPath;
    }

}