package com.gamecodeschool.escapewinter;

import android.content.Context;

import java.util.Random;

public class SpaceDust {



    private int x, y;
    private int speed;

    //detect dust leavijng scren
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;

    //constructor
    public SpaceDust(int screenX, int screenY) {

        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        //set a speed betwwen 0 and 9
        Random generator = new Random();
        speed = generator.nextInt(10);
        //set coordinates
        x = generator.nextInt(maxX);
        y = generator.nextInt(maxY);
    }

    public void update(int playerSpeed) {
        //speed up when the player does
        x -= playerSpeed;
        x -= speed;

        //respawn space dust
        if(x < 0) {
            x = maxX;
            Random generator = new Random();
            y = generator.nextInt(maxY);
            speed = generator.nextInt(15);
        }
    }

    //getters and setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
