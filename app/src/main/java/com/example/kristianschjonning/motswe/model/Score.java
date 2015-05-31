package com.example.kristianschjonning.motswe.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;

/**
 * Created by herpderp on 30/05/2015.
 */
public class Score {

    public final static String ID_FIELD_NAME = "id";

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

}
