package com.gamecodeschool.escapewinter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prepare to load fastest time
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);

        final Button buttonPlay = (Button)findViewById(R.id.buttonPlay);
        //Listen for click

        //reference to text view
        final TextView textFastestTime = (TextView)findViewById(R.id.textHighScore);

        buttonPlay.setOnClickListener(this);

        //load fastest timrr
        //if not avaliable high sore = 1000000
        long fastestTime = prefs.getLong("fastestTime", 1000000);

        //put high score in textview
        textFastestTime.setText("Fastest Time: " + fastestTime);
    }

    @Override
    public void onClick(View view) {
        //must be play button
        //create a new intent object
        Intent i = new Intent(this, GameActivity.class);
        //start gameactivity
        startActivity(i);
        //now shit this one done
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }
}
