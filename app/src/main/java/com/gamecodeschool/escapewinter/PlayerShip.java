package com.gamecodeschool.escapewinter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class PlayerShip {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 0;

    private int shieldStrength;
    private boolean boosting;

    private final int GRAVITY = -12;

    //stop ship from leaving screen
    private int maxY;
    private int minY;

    //limit bounds of the ships speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    //rect for hitbox
    private Rect hitBox;

    public PlayerShip(Context context, int screenX, int screenY) {
        boosting = false;
        x = 50;
        y = 50;

        shieldStrength = 2;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);

        maxY = screenY - bitmap.getHeight();
        minY = 0;

        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());

    }

    public void update() {

        //are we boosting
        if (boosting) {
            //speed yp
            speed += 2;
        } else {
            //slow down
            speed -=5;
        }

        //constrian if top speed
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        //never stop cpmpletely
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        //move ship up and down
        y -= speed + GRAVITY;

        //but dont let ship stray off screen
        if (y < minY) {
            y = minY;
        }

        if (y > maxY) {
            y = maxY;
        }

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public void setBoosting() {
        boosting = true;
    }

    public void stopBoosting() {
        boosting = false;
    }

    //getters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rect getHitbox() {
        return hitBox;
    }

    public int getShieldStrength() {
        return shieldStrength;
    }

    public void reduceShieldStrength() {
        shieldStrength--;
    }
}
