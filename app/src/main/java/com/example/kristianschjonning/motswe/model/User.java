package com.example.kristianschjonning.motswe.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Matti
 */
public class User {
    public final static String ID_FIELD_NAME = "id";
    public final static String USERNAME_FIELD_NAME="username";
    public User(){
        //for ormlite
    }

    public User(String username){
        this.username= username;
    }


    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    int id;

    @DatabaseField(unique = true, columnName=USERNAME_FIELD_NAME)
    private String username;

    public String getUsername(){
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
}
