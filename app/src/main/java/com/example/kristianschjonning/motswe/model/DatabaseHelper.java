package com.example.kristianschjonning.motswe.model;

import android.content.Context;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Matti
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private final String LOG_TAG = getClass().getSimpleName();
    // name of the database file for your application -- change to something appropriate for your app
    public static final String DATABASE_NAME = "motoswer.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 7;
    Dao<Score, Integer> scoreDao = null;
    Dao<User,Integer> userDao = null;
    Dao<UserScore, Integer> userScoreDao = null;
    private Context context;
    private State state = State.getInstance();
    private PreparedQuery<Score> scoresForUserQuery = null;




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d("DatabaseHelper", "Context:" + context);
    }


    /**
     * Im thinking that the SplashScreen will create this object while it's loading.
     * The splash screen will run in the entire duration of this method. because we can't run our
     * application w/o a database.
     * @param db
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
    {
            try
            {
                Log.d(LOG_TAG, " we're trying to make the db");
                TableUtils.createTable(connectionSource, Score.class);
                TableUtils.createTable(connectionSource, User.class);
                TableUtils.createTable(connectionSource, UserScore.class);
                Log.d(LOG_TAG, " seems like we succeeded");
                state.setDatabasePathExist(true);
            }catch(Exception e)
            {
                Log.e(LOG_TAG,"OnCreate database",e);
                state.setDatabasePathExist(false);
            }
    }






    public Dao<Score, Integer> getScoreDao() throws SQLException
    {
        if (scoreDao == null)
        {
            scoreDao = getDao(Score.class);
        }
        return scoreDao;
    }

    public Dao<UserScore, Integer> getUserScoreDao() throws SQLException
    {
        if (userScoreDao == null)
        {
            userScoreDao = getDao(UserScore.class);
        }
        return userScoreDao;
    }

    public Dao<User, Integer> getUserDao() throws SQLException
    {
        if (userDao == null)
        {
            userDao = getDao(User.class);
        }
        return userDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close()
    {
        super.close();
        scoreDao = null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,int newVersion)
    {
            try {
                Log.i(LOG_TAG, "onUpgrade");
                TableUtils.dropTable(connectionSource, Score.class,true);
                TableUtils.dropTable(connectionSource, User.class,true);
                TableUtils.dropTable(connectionSource, UserScore.class,true);
            } catch(Exception e){
                Log.e(LOG_TAG,"couldn't create the tables",e);
            }
            onCreate(db, connectionSource);
    }


    public boolean doesDatabaseExist()
    {
        File databasePath = new File(context.getDatabasePath(DatabaseHelper.DATABASE_NAME).toString());
        Log.d(LOG_TAG, "this is the path we're checking for db: " + context.getDatabasePath(DatabaseHelper.DATABASE_NAME).toString());
        return databasePath.exists();
    }

    public List<Score> lookupScoresForUser(User user) throws SQLException {
        if (scoresForUserQuery == null) {
            scoresForUserQuery = makeScoresForUserQuery();
        }
        scoresForUserQuery.setArgumentHolderValue(0, user);
        return scoreDao.query(scoresForUserQuery);
    }

    private PreparedQuery<Score> makeScoresForUserQuery() throws SQLException {
        QueryBuilder<UserScore, Integer> userScoreQb = getUserScoreDao().queryBuilder();
        //selecter ScoreID Column
        userScoreQb.selectColumns(UserScore.SCORE_ID_FIELD_NAME);
        //laver et nyt SelectAry objekt.
        SelectArg userSelectArg = new SelectArg();
        //saetter where clausen til at den skal selecte fra user id kolonnen hvor userSelectArg = User_ID_FIELD_NAME kolonnen.
        userScoreQb.where().eq(UserScore.USER_ID_FIELD_NAME, userSelectArg);
        //henter vores ScoreDao, fordi det jo er det table vi skal selecte fra.
        QueryBuilder<Score, Integer> scoreQb = getScoreDao().queryBuilder();
        /* Så her giver vi vores scoreQb den instruktion at den skal hente
         * alle scores, hvor Score.ID_FIELD_NAME, matcher med det userId som er indikeret i userScoreQB. */
        scoreQb.where().in(Score.ID_FIELD_NAME, userScoreQb);

        return scoreQb.prepare();
    }

    public User findUserByUserName(String username) throws SQLException {
        QueryBuilder<User, Integer> queryBuilder = getUserDao().queryBuilder();
        queryBuilder.where().eq(User.USERNAME_FIELD_NAME, username);
        PreparedQuery<User> preparedQuery = queryBuilder.prepare();
        return getUserDao().queryForFirst(preparedQuery);
    }

}