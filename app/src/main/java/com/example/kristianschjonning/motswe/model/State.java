package com.example.kristianschjonning.motswe.model;


import java.util.HashMap;
import java.util.List;

/**
 * Matti
 */

public class State
{
    private static State instance = null;
    private final String LOG_TAG = getClass().getSimpleName();
    private User currentUser;
    private boolean databaseHasBeenDownloaded = false;
    private boolean databasePathExist = false;
    private HashMap<String,Score> scoreMap = new HashMap<String,Score>();

    private HashMap<String,Score> getScoreMap(){
        return scoreMap;
    }

    public void computeScoreMap(List<Score> scoreList){
        String username = currentUser.getUsername();
        for(Score score : scoreList){
            scoreMap.put(username,score);
        }
    }


    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    public User getCurrentUser(){
        return currentUser;
    }

    private State()
    {
        //handle reflection
    }

    public static State getInstance()
    {
        if(instance == null)
        {
            instance = new State();
        }
        return instance;
    }

    public boolean getDatabaseHasBeenDownloaded()
    {
        return databaseHasBeenDownloaded;
    }

    public void setDatabaseHasBeenDownloaded(boolean databaseHasBeenDownloaded)
    {
        this.databaseHasBeenDownloaded = databaseHasBeenDownloaded;
    }

    public boolean getDatabasePathExist()
    {
        return databasePathExist;
    }

    public void setDatabasePathExist(boolean databasePathExist)
    {
        this.databasePathExist = databasePathExist;
    }

}