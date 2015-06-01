package com.example.kristianschjonning.motswe.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;


import com.example.kristianschjonning.motswe.Game;
import com.example.kristianschjonning.motswe.R;
import com.example.kristianschjonning.motswe.model.DatabaseHelper;
import com.example.kristianschjonning.motswe.model.Score;
import com.example.kristianschjonning.motswe.model.State;
import com.example.kristianschjonning.motswe.model.User;
import com.example.kristianschjonning.motswe.model.UserScore;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class SplashScreen extends OrmLiteBaseActivity<DatabaseHelper> {
    private final String LOG_TAG = getClass().getSimpleName();
    private final int splashScreenLoad = 1000;
    private DatabaseHelper databaseHelper = null;
    private ProgressDialog progressDialog;
    //creates a ViewSwitcher object, to switch between Views
    private ViewSwitcher viewSwitcher;
    private TextView tv_progress;
    private ProgressBar pb_progressBar;
    SQLiteDatabase db;
    DatabaseHelper helper;
    private Dao<Score,Integer> scoreDao;
    private Dao<UserScore,Integer> userScoreDao;
    private Dao<User,Integer> userDao;



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        helper = OpenHelperManager.getHelper(this,DatabaseHelper.class);
        db = helper.getWritableDatabase();
        new LoadViewTask().execute();
        getHelper();
        /*try  {
            User user = new User("denlillemand");
            Score score1 = new Score(47);
            Score score2 = new Score(57);
            Score score3 = new Score(69);
            Score score4 = new Score(75);
            scoreDao = getHelper().getScoreDao();
            userDao = getHelper().getUserDao();
            try {
                userDao.create(user);
            } catch(Exception e){
                Log.e(LOG_TAG,"user allready exist probably",e);
            }


            user = getHelper().findUserByUserName("denlillemand");
            scoreDao.create(score1);
            scoreDao.create(score2);
            scoreDao.create(score3);
            scoreDao.create(score4);
            userScoreDao = getHelper().getUserScoreDao();
            userScoreDao.create(new UserScore(user,score1));
            userScoreDao.create(new UserScore(user,score2));
            userScoreDao.create(new UserScore(user,score3));
            userScoreDao.create(new UserScore(user,score4));
            Log.d(LOG_TAG, "does db exist:" + helper.doesDatabaseExist());
            Log.d(LOG_TAG, "The current user id:" + user.getId());
            List<Score> scores = helper.lookupScoresForUser(user);
            Log.e(LOG_TAG,"trying to iterate over scores of list size:" + scores.size());
            for(Score score15 : scores) {
                Log.e(LOG_TAG,"this is the score:"+score15.getScore());
            }
        } catch(Exception e) {
            Log.e(LOG_TAG," we couldn't initialize the database",e);
        }*/

    }


    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void,Integer,Long>
    {
        @Override
        protected Long doInBackground(Void... params) {
            try
            {
                synchronized (this)
                {
                    int counter = 0;

                    while(counter <= 4)
                    {
                        this.wait(850);
                        counter++;
                        publishProgress(counter*25);
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return 0l;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewSwitcher = new ViewSwitcher(SplashScreen.this);
            viewSwitcher.addView(ViewSwitcher.inflate(SplashScreen.this, R.layout.splash_screen, null));
            tv_progress = (TextView) viewSwitcher.findViewById(R.id.tv_progress);
            pb_progressBar = (ProgressBar) viewSwitcher.findViewById(R.id.pb_progressbar);
            pb_progressBar.setMax(100);
            setContentView(viewSwitcher);
        }

        @Override
         protected void onPostExecute(Long l) {
        super.onPostExecute(l);
            /*viewSwitcher.addView(ViewSwitcher.inflate(SplashScreen.this, R.layout.activity_game, null));
            viewSwitcher.showNext();*/
        Intent i = new Intent(getApplicationContext(), GameMenu.class);
        startActivity(i);
    }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d(LOG_TAG, "onProgressUpdate:" + values[0]);
            if(values[0] <= 100)
            {
                tv_progress.setText("Progress: " + Integer.toString(values[0]) + "%");
                Log.d(LOG_TAG, "text:" + tv_progress.getText());
                pb_progressBar.setProgress(values[0]);
                Log.d(LOG_TAG, "progress:" + pb_progressBar.getProgress());
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        //Emulate the progressDialog.setCancelable(false) behavior
        //If the first view is being shown
        if(viewSwitcher.getDisplayedChild() == 0)
        {
            //Do nothing
            return;
        }
        else
        {
            //Finishes the current Activity
            super.onBackPressed();
        }
    }





}


