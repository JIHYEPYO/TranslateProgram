package com.ncslab.pyojihye.translateprogram.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ncslab.pyojihye.translateprogram.Activity.LoginActivity;
import com.ncslab.pyojihye.translateprogram.Activity.SelectModeActivity;
import com.ncslab.pyojihye.translateprogram.Activity.ViewerActivity;
import com.ncslab.pyojihye.translateprogram.Movement.ButtonViewerDataBase;
import com.ncslab.pyojihye.translateprogram.Movement.Const;
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
import static com.ncslab.pyojihye.translateprogram.Movement.Const.gap;

public class ViewerModeOptionFragment extends Fragment {
    private final String TAG = "ViewerModeOptionFragment";
    private final String MESSAGES_CHILD = "ButtonViewer";

    private static ViewerModeOptionFragment instance = new ViewerModeOptionFragment();
    private EditText editTextDelete;
    private EditText editTextGap;
    private Button buttonDelete;
    private Button buttonPrint;
    Bundle arguments = new Bundle();

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mUsername;
    private String mPhotoUrl;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ButtonViewerDataBase, SelectModeActivity.MessageViewHolder> mFirebaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.d(TAG,"onCreateView()");

        View v = inflater.inflate(R.layout.fragment_viewer_mode_option, container, false);

        editTextDelete = (EditText) v.findViewById(R.id.editTextDeleteWord);
        editTextGap = (EditText) v.findViewById(R.id.editTextOutputGap);
        buttonDelete = (Button) v.findViewById(R.id.ButtonDelete);

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

        buttonDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Log.d(TAG,"deleteButtonClick()");

                View parent = (View) view.getParent();
                if (editTextDelete.getText().toString().equals("")) {
                    Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.ButtonDelete), R.string.snack_bar_delete, Snackbar.LENGTH_LONG);

                    View v = snackbar.getView();
                    TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                } else {
                    boolean deleteEqual = false;
//                    Log.v("buttonDelete : ", editTextDelete.getText().toString() + "");
                    for (int i = 0; i < Const.delete_num; i++) {
                        if (Const.delete.get(i).equals(editTextDelete.getText().toString())) {
//                            Log.v("EQUAL!!", Const.delete.get(i));
                            deleteEqual = true;
                            Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.ButtonDelete), R.string.snack_bar_equal, Snackbar.LENGTH_LONG);
                            View v = snackbar.getView();
                            TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            snackbar.show();
                        }
                    }
                    if (deleteEqual != true) {
                        Const.delete.add(editTextDelete.getText().toString());
                        Const.delete_num++;

                        downKeyboard(getContext(), editTextDelete);

                        long time = System.currentTimeMillis();
                        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String str2 = dayTime.format(new Date(time));

                        ButtonViewerDataBase dataBase = new ButtonViewerDataBase(mUsername, str2, "Delete",editTextDelete.getText().toString());
                        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(dataBase);

                        Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.ButtonDelete), R.string.snack_bar_delete_success, Snackbar.LENGTH_LONG);
                        View v = snackbar.getView();
                        TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.show();
                        editTextDelete.setText("");
                    }
                }
            }
        });

        buttonPrint = (Button) v.findViewById(R.id.buttonPrint);

        buttonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG,"PrintButtonClick()");

                View parent = (View) view.getParent();
                if (editTextGap.getText().toString().equals("")) {
                    Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.buttonPrint), R.string.snack_bar_gap, Snackbar.LENGTH_LONG);

                    View v = snackbar.getView();
                    TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                } else {
                    gap = Integer.parseInt(editTextGap.getText().toString());

                    long time = System.currentTimeMillis();
                    SimpleDateFormat dayTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String str2 = dayTime.format(new Date(time));

                    ButtonViewerDataBase dataBase = new ButtonViewerDataBase(mUsername, str2, "Gap",editTextGap.getText().toString());
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(dataBase);

                    Intent intentViewer = new Intent(getActivity(), ViewerActivity.class);
                    startActivity(intentViewer);
                }
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


    public static synchronized ViewerModeOptionFragment getInstance() {
        return instance;
    }

    public static void downKeyboard(Context context, EditText editText) {
        InputMethodManager mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
