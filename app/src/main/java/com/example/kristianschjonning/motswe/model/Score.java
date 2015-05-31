package com.example.kristianschjonning.motswe.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;

/**
 * Created by herpderp on 30/05/2015.
 */
public class Score<T> implements Comparable<Score>{

    public final static String ID_FIELD_NAME = "id";
    public final static String SCORE = "Score:";

    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int id;

    @DatabaseField
    private int score;


    public Score(){
        //for ormlite
    }

    public Score(int score){
        this.score = score;
    }



    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    @Override
    public int compareTo(Score another) {
        return  this.getScore() == another.getScore() ? 0 : (another.getScore() < this.getScore() ? -1 : 1);
    }
}
