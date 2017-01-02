package com.example.pyojihye.translateprogram.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.pyojihye.translateprogram.Movement.Const;
import com.example.pyojihye.translateprogram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.pyojihye.translateprogram.Movement.Const.MESSAGE_URL;
import static com.example.pyojihye.translateprogram.Movement.Const.delete;
import static com.example.pyojihye.translateprogram.Movement.Const.gap;

public class ViewerActivity extends Activity {
    private final String TAG = "ViewerActivity";
    private final String ANONYMOUS = "ANONYMOUS";
    private final String MESSAGES_CHILD = "Viewer";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mUsername;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<DataBase, SelectModeActivity.MessageViewHolder> mFirebaseAdapter;

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

                long time = System.currentTimeMillis();
                SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/MM/DD hh:mm:ss");
                String str2 = dayTime.format(new Date(time));

                DataBase dataBase = new DataBase(buf.toString(), mUsername, str2);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(dataBase);

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
