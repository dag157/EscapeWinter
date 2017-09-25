package com.gamecodeschool.escapewinter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable {

    //for the fx
    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int win = -1;

    private boolean gameEnded;

    private Context context;

    private int screenX;
    private int screenY;

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    volatile boolean playing;
    Thread gameThread = null;

    //game objects
    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    public EnemyShip enemy4;
    public EnemyShip enemy5;

    //make space stuff lol
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    //for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public TDView(Context context, int x, int y) {
        super(context);
        this.context = context;

        //this soundpool is deprecated but no worries
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            //create onjects of 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            //create our three fx in meomory ready for use
            descriptor = assetManager.openFd("start.ogg");
            start = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("win.ogg");
            win = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("bump.ogg");
            bump = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("destroyed.ogg");
            destroyed = soundPool.load(descriptor, 0);

        } catch(IOException e) {
            //print error
            Log.e("error", "failed to load sound files");
        }

        screenX = x;
        screenY = y;

        //intialize objects
        ourHolder = getHolder();
        paint = new Paint();

        //Initialize our player Ship
        //player = new PlayerShip(context, x, y);
        //enemies
        // enemy1 = new EnemyShip(context, x, y);
        //enemy2 = new EnemyShip(context, x, y);
        //enemy3 = new EnemyShip(context, x, y);

        //int numSpecs = 40;

        //for (int i = 0; i < numSpecs; i++) {
        //where will it spawn
        //  SpaceDust spec = new SpaceDust(x, y);
        // dustList.add(spec);
        //}

        //get a refereence file called hiscore
        //if id doesnt exist one is created
        prefs = context.getSharedPreferences("HiScores", context.MODE_PRIVATE);

        //initialize the ediotr readu
        editor = prefs.edit();

        //load fastest time from a entry in the file
        //labeled "fastest time"
        //if not available highscore =1000000
        fastestTime = prefs.getLong("fastestTime", 1000000);

        startGame();
    }

    private void startGame() {
        //play start sound
        //soundPool.play(start, 1, 1, 0, 0, 1);

        //initialize game objects
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        if(screenX > 1000) {
            enemy4 = new EnemyShip(context, screenX, screenY);
        }

        if(screenX > 1200) {
            enemy5 = new EnemyShip(context, screenX, screenY);
        }

        int numSpecs = 40;

        for (int i = 0; i < numSpecs; i++) {
            //where will it spawn
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }

        //reset time and distance
        distanceRemaining = 10000; //10kn
        timeTaken = 0;

        //get start time
        timeStarted = System.currentTimeMillis();

        gameEnded = false;

        // soundPool.play(start, 1, 1, 0, 0, 1);
    }

    @Override
    public void run() {


        while (playing) {

            update();
            draw();
            control();

        }
    }

    private void update() {

        //collisons detection
        //before move cuz we checkin last frames
        //positon which has been drawn
        boolean hitDetected = false;

        if(Rect.intersects(player.getHitbox(), enemy1.getHitbox())) {
            hitDetected = true;
            enemy1.setX(-300);
        }

        if(Rect.intersects(player.getHitbox(), enemy2.getHitbox())) {
            hitDetected = true;
            enemy2.setX(-300);
        }

        if(Rect.intersects(player.getHitbox(), enemy3.getHitbox())) {
            hitDetected = true;
            enemy3.setX(-300);
        }

        if(screenX > 1000) {
            if(Rect.intersects(player.getHitbox(), enemy4.getHitbox())) {
                hitDetected = true;
                enemy4.setX(-300);
            }
        }

        if(screenX > 1200) {
            if(Rect.intersects(player.getHitbox(), enemy5.getHitbox())) {
                hitDetected = true;
                enemy5.setX(-300);
            }
        }

        if(hitDetected) {
            //soundPool.play(destroyed, 1, 1, 0, 0, 1);
            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0) {
                soundPool.play(destroyed, 1, 1, 0, 0, 1);
                gameEnded = true;
            }
        }

        player.update();
        //update the enemies
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        if(screenX > 1000) {
            enemy4.update(player.getSpeed());
        }
        if(screenX > 1200) {
            enemy5.update(player.getSpeed());
        }

        for (SpaceDust sd : dustList) {
            sd.update(player.getSpeed());
        }

        if(!gameEnded) {
            //subtract distance to home planet
            distanceRemaining -= player.getSpeed();

            //how long weve been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        //compelted the game!
        if(distanceRemaining < 0) {
            //soundPool.play(win, 1, 1, 0, 0, 1);
            //checkv for new fastest time
            if (timeTaken < fastestTime) {
                //save high score
                editor.putLong("fastestTime", timeTaken);
                editor.commit();
                fastestTime = timeTaken;
            }

            //avoid ugly negative in HUD
            distanceRemaining = 0;

            //now end hthe game
            gameEnded = true;
        }
    }

    private void draw() {

        if (ourHolder.getSurface().isValid()) {

            //lock area of memory
            canvas = ourHolder.lockCanvas();

            //rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));


            //debugging
            //swit hto white pixels
            //paint.setColor(Color.argb(255, 255, 255, 255));

            //draw hit boxes
            /*
            canvas.drawRect(player.getHitbox().left, player.getHitbox().top, player.getHitbox().right, player.getHitbox().bottom, paint);

            canvas.drawRect(enemy1.getHitbox().left,
                    enemy1.getHitbox().top,
                    enemy1.getHitbox().right,
                    enemy1.getHitbox().bottom,
                    paint);

            canvas.drawRect(enemy2.getHitbox().left,
                    enemy2.getHitbox().top,
                    enemy2.getHitbox().right,
                    enemy2.getHitbox().bottom,
                    paint);

            canvas.drawRect(enemy3.getHitbox().left,
                    enemy3.getHitbox().top,
                    enemy3.getHitbox().right,
                    enemy3.getHitbox().bottom,
                    paint);
            */

            //white dust
            paint.setColor(Color.argb(255, 255, 255, 255));

            //draw dust
            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            // draw the player
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);
            //enemies
            canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
            canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
            canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);

            if (screenX > 1000) {
                canvas.drawBitmap(enemy4.getBitmap(), enemy4.getX(), enemy4.getY(), paint);
            }
            if (screenX > 1200) {
                canvas.drawBitmap(enemy5.getBitmap(), enemy5.getX(), enemy5.getY(), paint);
            }

            //draw the hud
            if (!gameEnded) {
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                //canvas.drawText("Fastest: " + fastestTime + "s", 10, 20, paint);
                canvas.drawText("Fastest: " + formatTime(fastestTime) + "s", 10, 20, paint);

                //canvas.drawText("Time: " + timeTaken + "s", screenX / 2, 20, paint);
                canvas.drawText("Time: " + formatTime(timeTaken) + "s", screenX / 2, 20, paint);
                canvas.drawText("Distance: " + distanceRemaining / 1000 + " KM", screenX / 3, screenY - 20, paint);

                canvas.drawText("Lives: " + player.getShieldStrength(), 10, screenY - 20, paint);

                canvas.drawText("Speed: " + player.getSpeed() * 60 + " MPS", (screenX / 3) * 2, screenY - 20, paint);
            } else {
                //this happens when game is ended
                //show pasue screen
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX / 2, 100, paint);
                paint.setTextSize(25);
                //canvas.drawText("Fastest: " + fastestTime + "s", screenX / 2, 160, paint);
                canvas.drawText("Fastest:"+ formatTime(fastestTime) + "s", screenX/2, 160, paint);

                //canvas.drawText("Time: " + timeTaken + "s", screenX / 2, 200, paint);
                canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 200, paint);

                canvas.drawText("Distance remaining: " + distanceRemaining / 1000 + " KM", screenX / 2, 240, paint);

                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX / 2, 350, paint);
            }
            //unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }

    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            //catch here
        }
    }

    //surfacer view allows us to handle on touch events
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        //there ae many different events
        //we care about 2 rn
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            //has the player lifted their finger up?
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                if (gameEnded) {
                    startGame();
                }
                break;
        }
        return true;
    }



    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            //catch
        }
    }

    //make a thread
    //execuiton moves to our r
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private String formatTime(long time) {
        long seconds = (time) / 1000;
        long thousandths = (time) - (seconds * 1000);
        String strThousandths = "" + thousandths;
        if (thousandths < 100) {
            strThousandths = "0" + thousandths;
        }
        if (thousandths < 10) {
            strThousandths = "0" + strThousandths;
        }
        String stringTime = "" + seconds + "." + strThousandths;
        return stringTime;
    }



}
