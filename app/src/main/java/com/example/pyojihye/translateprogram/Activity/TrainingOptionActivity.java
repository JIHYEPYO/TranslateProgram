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

import com.example.pyojihye.translateprogram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.pyojihye.translateprogram.Movement.Const.screen;
import static com.example.pyojihye.translateprogram.Movement.Const.wpm;

public class TrainingOptionActivity extends AppCompatActivity {
    private final String TAG = "TrainingOptionActivity";
    private final String ANONYMOUS = "ANONYMOUS";
    private final String MESSAGES_CHILD = "Training";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

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
                mFirebaseAuth.signOut();
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.developer:
                Intent intent = new Intent(getApplicationContext(), DeveloperActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
