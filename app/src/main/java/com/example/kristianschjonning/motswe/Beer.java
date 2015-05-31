package com.example.kristianschjonning.motswe;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by kristianschjonning on 30/05/15.
 */
public class Beer extends GameObject {
    private int score;
    private int speed;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Beer(Bitmap res, int x, int y, int w, int h, int s, int numFrames)
    {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;
        //speed of the Beer
        speed = 3 + (int) (rand.nextDouble()*score/30);

        //cap missile speed
        if(speed>=40)speed = 40;

        Bitmap[] image = new Bitmap[numFrames];

        spritesheet = res;

        for(int i = 0; i<image.length; i++)
        {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i*height, width, height);
        }

        animation.setFrames(image);
        //if missile goes faster, it spins faster
        animation.setDelay(100-speed);
    }

    public void update()
    {
        x-=speed;
        animation.update();
    }
    public void draw(Canvas canvas)
    {
        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }catch (Exception e){}
    }

    @Override
    public int getWidth()
    {
        //offset slightly for more realistic collision detection, it should be able to get pass the tail
        return width-10;
    }
}
