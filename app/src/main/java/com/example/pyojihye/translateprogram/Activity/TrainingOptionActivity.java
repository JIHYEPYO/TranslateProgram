package com.example.pyojihye.translateprogram.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pyojihye.translateprogram.Movement.Const;
import com.example.pyojihye.translateprogram.R;

public class TrainingOptionActivity extends AppCompatActivity {
    private final String TAG="TrainingOptionActivity";

    public EditText editTextWPM;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.d(TAG,"onCreate()");

        setTitle("Training Option");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_mode_option);

        editTextWPM=(EditText)findViewById(R.id.editTextWPM);
    }

    public void onButtonStartClick(View v){
        Log.d(TAG,"onButtonStartClick()");

        if(editTextWPM.getText().toString().equals("")) {
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
        }else if(Integer.parseInt(editTextWPM.getText().toString())<=0){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.editTextWPM), R.string.snack_bar_wpm_minus, Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }else{
            Const.wpm=Integer.parseInt(editTextWPM.getText().toString());
            Intent intentStart = new Intent(this, TrainingActivity.class);
            startActivity(intentStart);
        }
    }
}
