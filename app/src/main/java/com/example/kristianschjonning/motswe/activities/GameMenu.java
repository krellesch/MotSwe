package com.example.kristianschjonning.motswe.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.kristianschjonning.motswe.Game;
import com.example.kristianschjonning.motswe.ListAdapters.ScoreListAdapter;
import com.example.kristianschjonning.motswe.R;
import com.example.kristianschjonning.motswe.model.DatabaseHelper;
import com.example.kristianschjonning.motswe.model.Score;
import com.example.kristianschjonning.motswe.model.State;
import com.example.kristianschjonning.motswe.model.User;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameMenu extends OrmLiteBaseActivity<DatabaseHelper> implements View.OnClickListener {
    private final String LOG_TAG = getClass().getSimpleName();
    private TextView startView;
    private TextView settingsView;
    private TextView aboutView;
    private TextView exitView;
    private ListView scoreView;
    private Button loginButton;
    private EditText userNameEditText;
    private ScoreListAdapter scoreListAdapter;


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start:
                Intent i = new Intent(getApplicationContext(), Game.class);
                startActivity(i);
                break;
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), ToggleButtonActivity.class);
                startActivity(intent);
                break;
            case R.id.about:
                break;
            case R.id.exit:
                break;
            case R.id.loginButton:
                //toString is not the object reference in this case.
                Log.e(LOG_TAG,"something is up:" +userNameEditText );
                Log.e(LOG_TAG,"something is up:" +userNameEditText.getText() );
                Log.e(LOG_TAG,"something is up:" +userNameEditText.getText().toString() );
                setUser(""+userNameEditText.getText());
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_menu);
        startView = (TextView) findViewById(R.id.start);
        settingsView = (TextView) findViewById(R.id.settings);
        aboutView = (TextView) findViewById(R.id.about);
        exitView = (TextView) findViewById(R.id.exit);
        scoreView = (ListView) findViewById(R.id.scoreView);
        loginButton = (Button) findViewById(R.id.loginButton);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
    }

    public void setUser(String username) {
        User user = new User(username);
        try {
            getHelper().getUserDao().create(user);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "we couldnt create the user, hopefully its allready created.", e);
        }
        try {
            user = getHelper().findUserByUserName(username);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "okay, so we probably couldnt create the user,and we couldnt retrieve it? whats up?" + e);
        }

        State.getInstance().setCurrentUser(user);

        try {
            scoreListAdapter = new ScoreListAdapter(getApplicationContext(), getHelper().lookupScoresForUser(user));
        } catch (SQLException e) {
            scoreListAdapter = new ScoreListAdapter(getApplicationContext(), new ArrayList<Score>());
            Log.e(LOG_TAG, "database did exist, and user wasnt null, but it still wasnt possible to retrieve the users scorelist. " +
                    "we just returned an empty list to the adapter.", e);
        }
        scoreView.setAdapter(scoreListAdapter);
        scoreListAdapter.sort(new Comparator<Score>() {
            @Override
            public int compare(Score leftScore, Score rightScore) {
                return leftScore.getScore() == rightScore.getScore() ? 0 : (rightScore.getScore() < leftScore.getScore() ? -1 : 1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //release database.
        super.onBackPressed();
        if (getHelper() != null) {
            OpenHelperManager.releaseHelper();
        }
    }
}
