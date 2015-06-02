package com.example.kristianschjonning.motswe.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Matti
 */
public class UserScore {
    public final static String USER_ID_FIELD_NAME = "user_id";
    public final static String SCORE_ID_FIELD_NAME = "score_id";

    /**
     * This id is generated by the database and set on the object when it is passed to the create method. An id is
     * needed in case we need to update or delete this object in the future.
     */
    @DatabaseField(generatedId = true)
    int id;

    // This is a foreign object which just stores the id from the User object in this table.
    @DatabaseField(foreign = true, columnName = USER_ID_FIELD_NAME)
    User user;

    // This is a foreign object which just stores the id from the Score object in this table.
    @DatabaseField(foreign = true, columnName = SCORE_ID_FIELD_NAME)
    Score score;

    UserScore() {
        // for ormlite
    }

    public UserScore(User user, Score score) {
        this.user = user;
        this.score = score;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }


}
