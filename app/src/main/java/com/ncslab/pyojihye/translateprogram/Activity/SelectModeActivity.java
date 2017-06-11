package com.ncslab.pyojihye.translateprogram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ncslab.pyojihye.translateprogram.Movement.ButtonMenuDataBase;
import com.ncslab.pyojihye.translateprogram.Movement.ModeDataBase;
import com.ncslab.pyojihye.translateprogram.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ncslab.pyojihye.translateprogram.Movement.Const.MESSAGE_URL;
import static com.ncslab.pyojihye.translateprogram.Movement.Const.delete;

public class SelectModeActivity extends AppCompatActivity {
    private final String TAG = "SelectModeActivity";
    private final String ANONYMOUS = "ANONYMOUS";
    private final String MESSAGES_CHILD = "Mode";
    private final String MESSAGE_MENU="ButtonMenu";


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ModeDataBase, MessageViewHolder> mFirebaseAdapter;

    private String mUsername;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Select Mode");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
//        Log.d(TAG, "onCreate()");

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
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ModeDataBase, MessageViewHolder>(
                ModeDataBase.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {


            @Override
            protected ModeDataBase parseSnapshot(DataSnapshot snapshot) {
                ModeDataBase DataBase = super.parseSnapshot(snapshot);
                if (DataBase != null) {
                    DataBase.setId(snapshot.getKey());
                }
                return DataBase;
            }


            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              ModeDataBase DataBase, int position) {
                viewHolder.messageTextView.setText(DataBase.getMode());
                viewHolder.messengerTextView.setText(DataBase.getUserName());

                // write this message to the on-device index
                FirebaseAppIndex.getInstance().update(getMessageIndexable(DataBase));

                // log a view action on it
                FirebaseUserActions.getInstance().end(getMessageViewAction(DataBase));
            }
        };
    }

    private Action getMessageViewAction(ModeDataBase modeDataBase) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(modeDataBase.getUserName(), MESSAGE_URL.concat(modeDataBase.getId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    private Indexable getMessageIndexable(ModeDataBase modeDataBase) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername == modeDataBase.getUserName())
                .setName(modeDataBase.getUserName())
                .setUrl(MESSAGE_URL.concat(modeDataBase.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(MESSAGE_URL.concat(modeDataBase.getId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(modeDataBase.getMode())
                .setUrl(MESSAGE_URL.concat(modeDataBase.getId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    public void onImageTrainingClick(View v) {
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String str = dayTime.format(new Date(time));

        ModeDataBase dataBase = new ModeDataBase(str, mUsername, "Training Mode");
        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(dataBase);

        Intent intentTrainingOption = new Intent(this, TrainingOptionActivity.class);
        startActivity(intentTrainingOption);
//        Log.d(TAG, "onImageTrainingClick()");
    }

    public void onImageViewerClick(View v) {
        delete.clear();

        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String str = dayTime.format(new Date(time));

        ModeDataBase dataBase = new ModeDataBase(str, mUsername, "Viewer Mode");
        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(dataBase);

        Intent intentViewerOption = new Intent(this, ViewerOptionActivity.class);
        startActivity(intentViewerOption);
//        Log.d(TAG, "onImageViewerClick()");
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