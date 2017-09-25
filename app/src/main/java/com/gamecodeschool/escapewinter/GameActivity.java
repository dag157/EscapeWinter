package com.gamecodeschool.escapewinter;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;

public class GameActivity extends Activity {

    private TDView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get a display onject to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        //load the reoslution into a point object
        Point size = new Point();
        display.getSize(size);

        //create an instance of our tdview
        //also passing in this si the context our our app
        gameView = new TDView(this, size.x, size.y);
        //make the game view
        setContentView(gameView);
    }

    //activitu and thread paused
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }
}
