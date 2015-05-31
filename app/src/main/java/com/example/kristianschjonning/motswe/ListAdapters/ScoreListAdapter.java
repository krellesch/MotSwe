package com.example.kristianschjonning.motswe.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kristianschjonning.motswe.R;
import com.example.kristianschjonning.motswe.model.Score;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by herpderp on 31/05/2015.
 */
public class ScoreListAdapter extends ArrayAdapter<Score>{

    public ScoreListAdapter(Context context, List<Score> scoreList) {
        super(context, 0,scoreList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Score score = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_item, parent, false);
        }
        TextView tvScore = (TextView) convertView.findViewById(R.id.score_textview);
        tvScore.setText("Score:"+score.getScore());
        return convertView;
    }


}
