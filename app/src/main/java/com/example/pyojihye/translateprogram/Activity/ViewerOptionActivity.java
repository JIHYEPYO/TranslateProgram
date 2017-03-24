package com.example.pyojihye.translateprogram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.example.pyojihye.translateprogram.Movement.ButtonMenuDataBase;
import com.example.pyojihye.translateprogram.Movement.MyFragmentPagerAdapter;
import com.example.pyojihye.translateprogram.Movement.ButtonMenuDataBase;
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

public class ViewerOptionActivity extends AppCompatActivity {
    private final String TAG = "ViewerOptionActivity";
    private final String ANONYMOUS = "ANONYMOUS";
    private final String MESSAGE_MENU = "ButtonMenu";


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mUsername;
    private String mPhotoUrl;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ButtonMenuDataBase, SelectModeActivity.MessageViewHolder> mFirebaseAdapter;

    public MyFragmentPagerAdapter myFragmentPagerAdapter;
    private static ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        setTitle("Viewer Option");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer_mode_option);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(pager);

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
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
        
        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ButtonMenuDataBase, SelectModeActivity.MessageViewHolder>(
                ButtonMenuDataBase.class,
                R.layout.item_message,
                SelectModeActivity.MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGE_MENU)) {


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

    private Indexable getMessageIndexable(ButtonMenuDataBase dataBase) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername == dataBase.getUserName())
                .setName(dataBase.getPushButton())
                .setUrl(MESSAGE_URL.concat(dataBase.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(MESSAGE_URL.concat(dataBase.getId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(dataBase.getPushButton())
                .setUrl(MESSAGE_URL.concat(dataBase.getId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    public static void setPager(int index) {
        pager.setCurrentItem(index);
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