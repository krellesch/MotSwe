package com.example.kristianschjonning.motswe;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.kristianschjonning.motswe.model.DatabaseHelper;
import com.example.kristianschjonning.motswe.model.Score;
import com.example.kristianschjonning.motswe.model.State;
import com.example.kristianschjonning.motswe.model.User;
import com.example.kristianschjonning.motswe.model.UserScore;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import sound.Sound;

/**
 * Matti, Kristian og Niclas
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public final String LOG_TAG = getClass().getSimpleName();
    public static final int MOVESPEED = -5;
    private long smokeStartTime;
    private long missileStartTime;
    private long beerStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private Game game;
    private ArrayList<Smokepuff> smoke;
    private ArrayList<Missile> missiles;
    private ArrayList<Beer> beers;
    private ArrayList<TopBorder> topborder;
    private ArrayList<BotBorder> botborder;
    private Random rand = new Random();
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean botDown = true;
    private boolean newGameCreated;
    private Handler handler = new Handler();

    //increase to slow down difficulty progression, decrease to speed up difficulty progression
    private int progressDenom = 20;

    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean dissapear;
    private boolean started;
    private int best;

    private AsyncTask<Void, Void, Void> taskA;
    private volatile int i;

    private DatabaseHelper databaseHelper = null;



    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }


    public GamePanel(Context context)
    {
        super(context);


        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);



        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        //release database.
         if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
        while(retry && counter<1000)
        {
            counter++;
            try{thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;

            }catch(InterruptedException e){e.printStackTrace();}

        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.stars));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.svensker), 65, 26, 1);
        smoke = new ArrayList<Smokepuff>();
        missiles = new ArrayList<Missile>();
        beers = new ArrayList<Beer>();
        topborder = new ArrayList<TopBorder>();
        botborder = new ArrayList<BotBorder>();
        smokeStartTime=  System.nanoTime();
        missileStartTime = System.nanoTime();
        beerStartTime = System.nanoTime();

        thread = new MainThread(getHolder(), this);
        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!player.getPlaying() && newGameCreated && reset)
            {
                player.setPlaying(true);
                player.setUp(true);
            }
            if(player.getPlaying())
            {

                if(!started)started = true;
                reset = false;
                player.setUp(true);
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    public void update()

    {
        if(player.getPlaying()) {

            if(botborder.isEmpty())
            {
                player.setPlaying(false);
                return;
            }
            if(topborder.isEmpty())
            {
                player.setPlaying(false);
                return;
            }

            bg.update();
            player.update();

            //calculate the threshold of height the border can have based on the score
            //max and min border heart are updated, and the border switched direction when either max or
            //min is met

            maxBorderHeight = 30+player.getScore()/progressDenom;
            //cap max border height so that borders can only take up a total of 1/2 the screen
            if(maxBorderHeight > HEIGHT/4)maxBorderHeight = HEIGHT/4;
            minBorderHeight = 5+player.getScore()/progressDenom;

            //check bottom border collision
            for(int i = 0; i<botborder.size(); i++)
            {
                if(collision(botborder.get(i), player))
                    player.setPlaying(false);
            }

            //check top border collision
            for(int i = 0; i <topborder.size(); i++)
            {
                if(collision(topborder.get(i),player))
                    player.setPlaying(false);
            }

            //update top border
            this.updateTopBorder();

            //udpate bottom border
            this.updateBottomBorder();

            //add missiles on timer
            long missileElapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missileElapsed >(2000 - player.getScore()/4)){


                //first missile always goes down the middle
                if(missiles.size()==0)
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile),WIDTH + 10, HEIGHT/2, 45, 15, player.getScore(), 13));
                }
                else
                {

                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, (int)(rand.nextDouble()*(HEIGHT - (maxBorderHeight * 2))+maxBorderHeight),45,15, player.getScore(),13));
                }

                //reset timer
                missileStartTime = System.nanoTime();
            }
            //loop through every missile and check collision and remove
            for(int i = 0; i<missiles.size();i++)
            {
                //update missile
                missiles.get(i).update();

                if(collision(missiles.get(i),player))
                {
                    missiles.remove(i);
                    player.setPlaying(false);
                    //closeActivity.run();
                    break;
                }
                //remove missile if it is way off the screen
                if(missiles.get(i).getX()<-100)
                {
                    missiles.remove(i);
                    break;
                }
            }



            //add missiles on timer
            long beerElapsed = (System.nanoTime()-beerStartTime)/1000000;
            if(missileElapsed >(2000 - player.getScore()/4)){


                //first missile always goes down the middle
                if(beers.size()==0)
                {
                    beers.add(new Beer(BitmapFactory.decodeResource(getResources(),R.drawable.beer
                            ),WIDTH + 10, HEIGHT/2, 10, 25, player.getScore(), 1));
                }
                else
                {

                    beers.add(new Beer(BitmapFactory.decodeResource(getResources(),R.drawable.beer),
                            WIDTH+10, (int)(rand.nextDouble()*(HEIGHT - (maxBorderHeight * 2))+maxBorderHeight),10,25, player.getScore(),1));
                }

                //reset timer
                beerStartTime = System.nanoTime();
            }
            //loop through every missile and check collision and remove
            for(int i = 0; i<beers.size();i++)
            {
                //update beer
                beers.get(i).update();

                if(collision(beers.get(i),player))
                {
                    beers.remove(i);
                    long startTime = System.currentTimeMillis(); //fetch starting time
                    player.setScore(player.getScore()+10);
                    Sound.playSound(getContext(),6);
                    break;
                }
                //remove beer if it is way off the screen
                if(beers.get(i).getX()<-100)
                {
                    beers.remove(i);
                    break;
                }
            }

            //add smoke puffs on timer
            long elapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if(elapsed > 120){
                smoke.add(new Smokepuff(player.getX(), player.getY()+10));
                smokeStartTime = System.nanoTime();
            }

            for(int i = 0; i<smoke.size();i++)
            {
                smoke.get(i).update();
                if(smoke.get(i).getX()<-10)
                {
                    smoke.remove(i);
                }
            }
        } else{
            player.resetDYA();
            if(!reset)
            {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                dissapear = true;
                explosion = new Explosion(getContext(),BitmapFactory.decodeResource(getResources(),R.drawable.explosion),player.getX(),
                        player.getY()-30, 100, 100, 25);
            }

            explosion.update();
            long resetElapsed = (System.nanoTime()-startReset)/1000000;

            if(resetElapsed > 2500 && !newGameCreated)
            {
                newGame();
            }


        }

    }

    public boolean collision(GameObject a, GameObject b) {
        if (Rect.intersects(a.getRectangle(), b.getRectangle()))
        {
            return true;
        }
        return false;
    }
    @Override
    public void draw(Canvas canvas)
    {
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);

        if(canvas!=null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            if(!dissapear) {
                player.draw(canvas);
            }
            //draw smokepuffs
            for(Smokepuff sp: smoke)
            {
                sp.draw(canvas);
            }
            //draw missiles
            for(Missile m: missiles)
            {
                m.draw(canvas);
            }
            //draw missiles
            for(Beer b: beers)
            {
                b.draw(canvas);
            }
            //draw topborder
            for(TopBorder tb: topborder)
            {
                tb.draw(canvas);
            }

            //draw botborder
            for(BotBorder bb: botborder)
            {
                bb.draw(canvas);
            }
            //draw explosion
            if(started)
            {
                explosion.draw(canvas);
            }
            drawText(canvas);
            canvas.restoreToCount(savedState);

        }
    }

    public void updateTopBorder()
    {
        //every 50 points, insert randomly placed top blocks that break the pattern
        if(player.getScore()%50 ==0)
        {
            topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
            ),topborder.get(topborder.size()-1).getX()+20,0,(int)((1*(maxBorderHeight
            ))+1)));
        }
        for(int i = 0; i<topborder.size(); i++)
        {
            topborder.get(i).update();
            if(topborder.get(i).getX()<-20)
            {
                topborder.remove(i);
                //remove element of arraylist, replace it by adding a new one

                //calculate topdown which determines the direction the border is moving (up or down)
                if(topborder.get(topborder.size()- 1).getHeight()>=maxBorderHeight)
                {
                    topDown = false;
                }
                if(topborder.get(topborder.size()- 1).getHeight()<=minBorderHeight)
                {
                    topDown = true;
                }
                //new border added will have larger height
                if(topDown)
                {
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,
                            0, topborder.get(topborder.size()- 1).getHeight()+1));
                }
                //new border added wil have smaller height
                else
                {
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,
                            0, topborder.get(topborder.size()- 1).getHeight()-1));
                }

            }
        }

    }

    public void updateBottomBorder()
    {
        //every 40 points, insert randomly placed bottom blocks that break pattern
        if(player.getScore()%40 == 0)
        {
            botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    botborder.get(botborder.size()-1).getX()+20,(int)((rand.nextDouble()
                    *maxBorderHeight)+(HEIGHT-maxBorderHeight))));
        }

        //update bottom border
        for(int i = 0; i<botborder.size(); i++)
        {
            botborder.get(i).update();

            //if border is moving off screen, remove it and add a corresponding new one
            if(botborder.get(i).getX()<-20) {
                botborder.remove(i);


                //determine if border will be moving up or down
                if (botborder.get(botborder.size() - 1).getY() <= HEIGHT-maxBorderHeight) {
                    botDown = true;
                }
                if (botborder.get(botborder.size() - 1).getY() >= HEIGHT - minBorderHeight) {
                    botDown = false;
                }

                if (botDown) {
                    botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), botborder.get(botborder.size() - 1).getX() + 20, botborder.get(botborder.size() - 1
                    ).getY() + 1));
                } else {
                    botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), botborder.get(botborder.size() - 1).getX() + 20, botborder.get(botborder.size() - 1
                    ).getY() - 1));
                }
            }
        }
    }
    public void newGame()
    {
        try{
            if(player.getScore() != 0){
            Score score = new Score(player.getScore());
            Log.e(LOG_TAG,"score:"+ score);
            getHelper().getScoreDao().create(score);
            User user = State.getInstance().getCurrentUser();
            Log.e(LOG_TAG,"User:"+ user);
            Log.e(LOG_TAG,"getHelper:"+ getHelper());
            UserScore userScore = new UserScore(user,score);
            Log.e(LOG_TAG,"UserScore:"+ userScore);
            getHelper().getUserScoreDao().create(userScore);
            }
        } catch(SQLException e){
            Log.e(LOG_TAG,"we couldn't update the database with the score.",e );
        }


        dissapear = false;

        botborder.clear();
        topborder.clear();

        missiles.clear();
        beers.clear();
        smoke.clear();
        //explosion.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        player.resetDYA();
        player.setY(HEIGHT/2);
        try{
            List<Score> scoreList = getHelper().lookupScoresForUser(State.getInstance().getCurrentUser());
            Collections.sort(scoreList);
            best = scoreList.get(0).getScore()*3;
        } catch(Exception e){
            best = 0;
            Log.e(LOG_TAG,"something went wrong when trying to set best, so we just set it to 0");
        }
        if(player.getScore()>best)
        {
            best = player.getScore()*3;
        }
        player.resetScore();

        //create initial borders

        //initial top border
        for(int i = 0; i*20<WIDTH+40;i++)
        {
            //first top border create
            if(i==0)
            {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, 10));
            }
            else
            {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, topborder.get(i-1).getHeight()+1));
            }
        }
        //initial bottom border
        for(int i = 0; i*20<WIDTH+40; i++)
        {
            //first border ever created
            if(i==0)
            {
                botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        ,i*20,HEIGHT - minBorderHeight));
            }
            //adding borders until the initial screen is filed
            else
            {
                botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i * 20, botborder.get(i - 1).getY() - 1));
            }
        }
        newGameCreated = true;
    }


    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("BEER : " + (player.getScore()*3), 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + best, WIDTH - 215, HEIGHT - 10, paint);


        if(!player.getPlaying()&&newGameCreated&&reset)
        {
            Paint paint1 = new Paint();
            paint1.setTextSize(40);
            paint1.setColor(Color.WHITE);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH / 2 - 50, HEIGHT / 2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-50, HEIGHT/2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-50, HEIGHT/2 + 40, paint1);
            canvas.drawText("Highscores:", WIDTH/2-50, HEIGHT/2 + 60, paint1);
            List<Score> highScoreList = null;
            try{
                highScoreList=getHelper().lookupScoresForUser(State.getInstance().getCurrentUser());
            } catch(Exception e){
                Log.d(LOG_TAG,"Maybe the current user is null",e);
            }
            int counter = 1;
            int height = HEIGHT/2 + 80;
            Collections.sort(highScoreList);
            if(highScoreList !=null){
                for(Score score :highScoreList){
                    if(counter > 3){
                        break;
                    }
                    canvas.drawText(counter + ": " + score.getScore()*3, WIDTH/2-50, height, paint1);
                    counter++;
                    height += 20;
                }
            }
        }
    }
}