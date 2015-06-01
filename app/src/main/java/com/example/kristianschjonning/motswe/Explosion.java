package com.example.kristianschjonning.motswe;

/**
 * Created by kristianschjonning on 30/05/15.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import sound.Sound;

public class Explosion {
    private int x;
    private int y;
    private int width;
    private int height;
    private int row;
    private Animation animation = new Animation();
    private Bitmap spritesheet;
    private Context context;
    private SoundPool soundPool;


    // Sekunder 0,12
    public Explosion(Context con,Bitmap res, int x, int y, int w, int h, int numFrames)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.context = con;

        Bitmap[] image = new Bitmap[numFrames];

        spritesheet = res;

        for(int i = 0; i<image.length; i++)
        {
            if(i%5==0&&i>0)row++;
            image[i] = Bitmap.createBitmap(spritesheet, (i-(5*row))*width, row*height, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(10);
        Sound.playSound(context);
        //start();
    }

    public void draw(Canvas canvas)
    {
        if(!animation.isPlayedOnce())
        {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        }

    }
    public void update()
    {
        if(!animation.isPlayedOnce())
        {
            animation.update();
        }
    }
    public int getHeight(){return height;}
}
