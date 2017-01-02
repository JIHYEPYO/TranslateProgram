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

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.pyojihye.translateprogram.Movement.Const.MESSAGE_URL;
import static com.example.pyojihye.translateprogram.Movement.Const.wpm;

public class TrainingOptionActivity extends AppCompatActivity {
    private final String TAG = "TrainingOptionActivity";
    private final String ANONYMOUS = "ANONYMOUS";
    private final String MESSAGES_CHILD = "Training";


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<DataBase, SelectModeActivity.MessageViewHolder> mFirebaseAdapter;

    private String mUsername;

    public EditText editTextWPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        setTitle("Training Option");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_mode_option);

        editTextWPM = (EditText) findViewById(R.id.editTextWPM);

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
        } else {
            wpm = Integer.parseInt(editTextWPM.getText().toString());
            long time = System.currentTimeMillis();
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/MM/DD hh:mm:ss");
            String str = dayTime.format(new Date(time));

            DataBase dataBase = new DataBase("WPM : " + wpm, mUsername, str);
            mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(dataBase);
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
                mFirebaseAuth.signOut();
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
