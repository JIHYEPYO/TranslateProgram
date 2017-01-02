package com.example.pyojihye.translateprogram.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pyojihye.translateprogram.Movement.Const;
import com.example.pyojihye.translateprogram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.pyojihye.translateprogram.Movement.Const.MESSAGE_URL;
import static com.example.pyojihye.translateprogram.Movement.Const.strItem;
import static com.example.pyojihye.translateprogram.Movement.Const.strPath;

public class PathActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final String TAG = "PathActivity";
    private final String ANONYMOUS = "ANONYMOUS";
    private final String MESSAGES_CHILD = "File";


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<DataBase, SelectModeActivity.MessageViewHolder> mFirebaseAdapter;

    private String mUsername;

    final private int APP_PERMISSION_STORAGE = 1;
    String root = "";
    String path = "";
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
        Log.d(TAG, "onCreate()");

        // Set default username is anonymous.
        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
        }

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<DataBase, SelectModeActivity.MessageViewHolder>(
                DataBase.class,
                R.layout.item_message,
                SelectModeActivity.MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {


            @Override
            protected DataBase parseSnapshot(DataSnapshot snapshot) {
                DataBase DataBase = super.parseSnapshot(snapshot);
                if (DataBase != null) {
                    DataBase.setId(snapshot.getKey());
                }
                return DataBase;
            }


            @Override
            protected void populateViewHolder(SelectModeActivity.MessageViewHolder viewHolder,
                                              DataBase DataBase, int position) {
                viewHolder.messageTextView.setText(DataBase.getText());
                viewHolder.messengerTextView.setText(DataBase.getName());

                // write this message to the on-device index
                FirebaseAppIndex.getInstance().update(getMessageIndexable(DataBase));

                // log a view action on it
                FirebaseUserActions.getInstance().end(getMessageViewAction(DataBase));
            }
        };
    }

    private Action getMessageViewAction(DataBase OpenDoorMessage) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(OpenDoorMessage.getName(), MESSAGE_URL.concat(OpenDoorMessage.getId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    private Indexable getMessageIndexable(DataBase OpenDoorMessage) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername == OpenDoorMessage.getName())
                .setName(OpenDoorMessage.getName())
                .setUrl(MESSAGE_URL.concat(OpenDoorMessage.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(MESSAGE_URL.concat(OpenDoorMessage.getId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(OpenDoorMessage.getText())
                .setUrl(MESSAGE_URL.concat(OpenDoorMessage.getId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown()");

        //'뒤로가기'키가 눌렸을때 종료여부를 묻는 다이얼로그 띄움
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder d = new AlertDialog.Builder(PathActivity.this);
            d.setTitle(getString(R.string.dialog_title));
            d.setMessage(getString(R.string.dialog_contents));
            d.setIcon(R.mipmap.ic_launcher);

            d.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    PathActivity.this.finish();
                }
            });

            d.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
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


    private void checkPermission() {
        Log.d(TAG, "checkPermission()");

        if (android.os.Build.VERSION.SDK_INT < 23) { //23버전보다 낮을때
            root = Environment.getExternalStorageDirectory().getAbsolutePath();
            String[] fileList = getFileList(root);

            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i] != null) {
//                Log.d("tag", fileList[i]);
                }
            }
            initListView();
            fileListArray(fileList);
        } else { //23버전보다 높을때
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                //최초 거부를 선택하면 두번째부터 이벤트 발생 & 권한 획득이 필요한 이유를 설명
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                }

                //요청 팝업 팝업 선택시 onRequestPermissionsResult 이동
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        APP_PERMISSION_STORAGE);

            }
            //권한이 있는 경우
            else {
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
        Log.d(TAG, "onRequestPermissionResult()");

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

    public void initListView() {
        Log.d(TAG, "initListView()");

        arrayFile = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayFile);

        listFile = (ListView) findViewById(R.id.listViewPath);
        listFile.setAdapter(adapter);
        listFile.setOnItemClickListener(this);
    }

    public void fileListArray(String[] fileList) {
        Log.d(TAG, "fileListArray()");

        if (fileList == null) {
            return;
        }

        arrayFile.clear();
        if (root.length() < path.length()) {
            arrayFile.add("..");
        }

        for (int i = 0; i < fileList.length; i++) {
//            Log.d("tag",fileList[i]);
            arrayFile.add(fileList[i]);
        }

        ArrayAdapter adapter = (ArrayAdapter) listFile.getAdapter();
        adapter.notifyDataSetChanged();
    }

    public String[] getFileList(String strPath) {
        Log.d(TAG, "getFileList()");

        File fileRoot = new File(strPath);

        if (fileRoot.isDirectory() == false) {
            return null;
        }

        path = strPath;
        textMsg.setText(path);
        String[] fileList = fileRoot.list();
        return fileList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick()");

        strItem = arrayFile.get(position);
        strPath = getCurrentPath(strItem);
        String[] fileList = getFileList(strPath);
        fileListArray(fileList);

        if (strItem.endsWith("txt")) {
            long time = System.currentTimeMillis();
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
            String str = dayTime.format(new Date(time));

            DataBase dataBase = new DataBase(strItem, strPath, mUsername, str);
            mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(dataBase);
            Intent selectIntent = new Intent(getApplicationContext(), SelectModeActivity.class);
            startActivity(selectIntent);
        } else if (strItem.contains(".") && !strItem.startsWith(".")) {
            Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.listViewPath), R.string.snack_bar_format, Snackbar.LENGTH_LONG);
            View v = snackbar.getView();
            TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    public String getCurrentPath(String strFolder) {
        Log.d(TAG, "getCurrentPath()");

        String strPath;
        if (strFolder == "..") {
            int pos = path.lastIndexOf("/");
            strPath = path.substring(0, pos);
        } else {
            strPath = path + "/" + strFolder;
        }
        return strPath;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}