package com.ncslab.pyojihye.translateprogram.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ncslab.pyojihye.translateprogram.Activity.LoginActivity;
import com.ncslab.pyojihye.translateprogram.Activity.SelectModeActivity;
import com.ncslab.pyojihye.translateprogram.Movement.ButtonViewerDataBase;
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

import static com.ncslab.pyojihye.translateprogram.Movement.Const.MESSAGE_URL;
import static com.ncslab.pyojihye.translateprogram.Movement.Const.delete;
import static com.ncslab.pyojihye.translateprogram.Movement.Const.delete_num;

public class ViewerModeOptionListFragment extends android.support.v4.app.Fragment {
    private final String TAG = "ViewerModeOptionListFragment";
    private final String MESSAGES_CHILD = "ButtonViewer";

    private static ViewerModeOptionListFragment instance = new ViewerModeOptionListFragment();
    ArrayAdapter<String> adapter;
    ListView listViewDelete;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mUsername;
    private String mPhotoUrl;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ButtonViewerDataBase, SelectModeActivity.MessageViewHolder> mFirebaseAdapter;


    @Override
    public void onResume() {
//        Log.d(TAG,"onResume()");

        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.d(TAG,"onCreateView()");
        View v = inflater.inflate(R.layout.fragment_viewer_mode_option_list, container, false);


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return v;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
        }

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ButtonViewerDataBase, SelectModeActivity.MessageViewHolder>(
                ButtonViewerDataBase.class,
                R.layout.item_message,
                SelectModeActivity.MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {


            @Override
            protected ButtonViewerDataBase parseSnapshot(DataSnapshot snapshot) {
                ButtonViewerDataBase DataBase = super.parseSnapshot(snapshot);
                if (DataBase != null) {
                    DataBase.setId(snapshot.getKey());
                }
                return DataBase;
            }


            @Override
            protected void populateViewHolder(SelectModeActivity.MessageViewHolder viewHolder,
                                              ButtonViewerDataBase DataBase, int position) {
                viewHolder.messageTextView.setText(DataBase.getUserName());
                viewHolder.messengerTextView.setText(DataBase.getPushButton());

                // write this message to the on-device index
                FirebaseAppIndex.getInstance().update(getMessageIndexable(DataBase));

                // log a view action on it
                FirebaseUserActions.getInstance().end(getMessageViewAction(DataBase));
            }
        };

        listViewDelete = (ListView) v.findViewById(R.id.listViewDeleteWordList);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, delete);
        listViewDelete.setAdapter(adapter);
        listViewDelete.setLongClickable(true);
        listViewDelete.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                long time = System.currentTimeMillis();
                SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String str2 = dayTime.format(new Date(time));

                ButtonViewerDataBase dataBase = new ButtonViewerDataBase(mUsername, str2, "Delete Cancel", delete.get(position));
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(dataBase);

                delete.remove(position);
                delete_num--;

                onResume();
                return true;
            }
        });
        return v;
    }

    private Action getMessageViewAction(ButtonViewerDataBase dataBase) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(dataBase.getUserName(), MESSAGE_URL.concat(dataBase.getId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    private Indexable getMessageIndexable(ButtonViewerDataBase database) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername == database.getUserName())
                .setName(database.getPushButton())
                .setUrl(MESSAGE_URL.concat(database.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(MESSAGE_URL.concat(database.getId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(database.getPushButton())
                .setUrl(MESSAGE_URL.concat(database.getId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    public static synchronized ViewerModeOptionListFragment getInstance() {
        return instance;
    }
}
