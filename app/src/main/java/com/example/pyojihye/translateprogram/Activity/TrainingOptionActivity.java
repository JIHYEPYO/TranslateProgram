package com.example.pyojihye.translateprogram.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pyojihye.translateprogram.Movement.ButtonMenuDataBase;
import com.example.pyojihye.translateprogram.Movement.FileDataBase;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.pyojihye.translateprogram.Movement.Const.MESSAGE_URL;
import static com.example.pyojihye.translateprogram.Movement.Const.screen;
import static com.example.pyojihye.translateprogram.Movement.Const.wpm;

public class TrainingOptionActivity extends AppCompatActivity {
    private final String TAG = "TrainingOptionActivity";
    private final String ANONYMOUS = "ANONYMOUS";
    private final String MESSAGES_CHILD = "Training";
    private final String MESSAGE_MENU="ButtonMenu";


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ButtonMenuDataBase, SelectModeActivity.MessageViewHolder> mFirebaseAdapter;

    private String mUsername;

    public EditText editTextWPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        setTitle("Training Option");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_mode_option);

        editTextWPM = (EditText) findViewById(R.id.editTextWPM);

        screen = false;

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
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ButtonMenuDataBase, SelectModeActivity.MessageViewHolder>(
                ButtonMenuDataBase.class,
                R.layout.item_message,
                SelectModeActivity.MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {


            @Override
            protected ButtonMenuDataBase parseSnapshot(DataSnapshot snapshot) {
                ButtonMenuDataBase DataBase = super.parseSnapshot(snapshot);
                if (DataBase != null) {
                    DataBase.setId(snapshot.getKey());
                }
                return DataBase;
            }


            @Override
            protected void populateViewHolder(SelectModeActivity.MessageViewHolder viewHolder,
                                              ButtonMenuDataBase DataBase, int position) {
                viewHolder.messageTextView.setText(DataBase.getUserName());
                viewHolder.messengerTextView.setText(DataBase.getPushButton());

                // write this message to the on-device index
                FirebaseAppIndex.getInstance().update(getMessageIndexable(DataBase));

                // log a view action on it
                FirebaseUserActions.getInstance().end(getMessageViewAction(DataBase));
            }
        };
    }

    private Action getMessageViewAction(ButtonMenuDataBase dataBase) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(dataBase.getUserName(), MESSAGE_URL.concat(dataBase.getId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    private Indexable getMessageIndexable(ButtonMenuDataBase fileDataBase) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername == fileDataBase.getUserName())
                .setName(fileDataBase.getPushButton())
                .setUrl(MESSAGE_URL.concat(fileDataBase.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(MESSAGE_URL.concat(fileDataBase.getId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(fileDataBase.getPushButton())
                .setUrl(MESSAGE_URL.concat(fileDataBase.getId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    public void onButtonStartClick(View v) {
        Log.d(TAG, "onButtonStartClick()");

        if (editTextWPM.getText().toString().equals("")) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.editTextWPM), R.string.snack_bar_wpm, Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#FF0000")).setAction("YES", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            editTextWPM.setText("100");
                        }
                    });

            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } else if (Integer.parseInt(editTextWPM.getText().toString()) <= 0) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.editTextWPM), R.string.snack_bar_wpm_minus, Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } else if (Integer.parseInt(editTextWPM.getText().toString()) > 500) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.editTextWPM), R.string.snack_bar_wpm_plus, Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } else {
            wpm = Integer.parseInt(editTextWPM.getText().toString());
            Intent intentStart = new Intent(this, TrainingActivity.class);
            startActivity(intentStart);
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
                long time = System.currentTimeMillis();
                SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String str = dayTime.format(new Date(time));

                ButtonMenuDataBase dataBase = new ButtonMenuDataBase(mUsername, str, "Sign Out", TAG);
                mFirebaseDatabaseReference.child(MESSAGE_MENU).push().setValue(dataBase);

                mFirebaseAuth.signOut();
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.developer:
                long time2 = System.currentTimeMillis();
                SimpleDateFormat dayTime2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String str2 = dayTime2.format(new Date(time2));

                ButtonMenuDataBase dataBase2 = new ButtonMenuDataBase(mUsername, str2, "Developer Info", TAG);
                mFirebaseDatabaseReference.child(MESSAGE_MENU).push().setValue(dataBase2);
                Intent intent = new Intent(getApplicationContext(), DeveloperActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
