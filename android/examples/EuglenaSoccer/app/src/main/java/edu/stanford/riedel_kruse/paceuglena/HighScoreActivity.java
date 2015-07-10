package edu.stanford.riedel_kruse.paceuglena;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by dchiu on 3/3/15.
 */
public class HighScoreActivity extends Activity {

    public static final int NUM_SCORES = 5;

    private ArrayList<String> initials;
    private ArrayList<Integer> times;

    private int mNewHighScoreIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        initials = new ArrayList<String>();
        times = new ArrayList<Integer>();

        String initialsString = prefs.getString("leaderBoardInitials", "");
        StringTokenizer initialsTokenizer = new StringTokenizer(initialsString, ",");

        String timesString = prefs.getString("leaderBoardTimes", "");
        StringTokenizer timesTokenizer = new StringTokenizer(timesString, ",");

        LinearLayout leaderBoardLayout = (LinearLayout) findViewById(R.id.leader_board);

        while (timesTokenizer.hasMoreTokens() && initialsTokenizer.hasMoreTokens()) {
            RelativeLayout leaderBoardEntry = new RelativeLayout(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(400,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            leaderBoardEntry.setLayoutParams(layoutParams);

            String time = timesTokenizer.nextToken();
            String initial = initialsTokenizer.nextToken();
            times.add(Integer.parseInt(time));
            initials.add(initial);

            TextView leaderBoardTime = new TextView(this);
            leaderBoardTime.setText(time);
            RelativeLayout.LayoutParams timeLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            timeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            leaderBoardTime.setLayoutParams(timeLayoutParams);

            TextView leaderBoardInitials = new TextView(this);
            leaderBoardInitials.setText(initial);
            RelativeLayout.LayoutParams initialsLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            initialsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            leaderBoardInitials.setLayoutParams(initialsLayoutParams);

            leaderBoardEntry.addView(leaderBoardInitials);
            leaderBoardEntry.addView(leaderBoardTime);

            leaderBoardLayout.addView(leaderBoardEntry);
        }

        if (times.size() == 0) {
            TextView highScoresLabel = (TextView) findViewById(R.id.high_scores_label);
            highScoresLabel.setVisibility(View.GONE);

            leaderBoardLayout.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        int time = intent.getIntExtra(SoccerGameActivity.EXTRA_TIME, -1);

        mNewHighScoreIndex = -1;
        if (time != -1) {

            for (int i = 0; i < times.size(); i++) {
                if (time < times.get(i)) {
                    times.add(i, time);
                    mNewHighScoreIndex = i;
                    break;
                }
            }

            if (times.size() < NUM_SCORES && mNewHighScoreIndex == -1) {
                times.add(time);
                mNewHighScoreIndex = times.size() - 1;
            }

            String resultsString = "";

            if (mNewHighScoreIndex != -1) {
                resultsString += "New high score! ";
                EditText initialsEditText = (EditText) findViewById(R.id.initials);
                initialsEditText.setVisibility(View.VISIBLE);
            }

            resultsString += "You took " + time + " seconds to score 5 goals!";

            TextView resultsTextView = (TextView) findViewById(R.id.results);
            resultsTextView.setText(resultsString);
        }
        else {
            TextView gameOverLabel = (TextView) findViewById(R.id.game_over_label);
            gameOverLabel.setVisibility(View.GONE);
            TextView resultsTextView = (TextView) findViewById(R.id.results);
            resultsTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mNewHighScoreIndex != -1) {
            EditText initialsEditText = (EditText) findViewById(R.id.initials);
            String initial = initialsEditText.getText().toString();
            initials.add(mNewHighScoreIndex, initial);
        }

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        StringBuilder timeBuilder = new StringBuilder();
        StringBuilder initialsBuilder = new StringBuilder();
        for (int i = 0; i < NUM_SCORES && i < times.size(); i++) {
            timeBuilder.append(times.get(i)).append(",");
            initialsBuilder.append(initials.get(i)).append(",");
        }

        prefs.edit().putString("leaderBoardTimes", timeBuilder.toString()).commit();
        prefs.edit().putString("leaderBoardInitials", initialsBuilder.toString()).commit();

        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onDoneClicked(View v) {
        onBackPressed();
    }
}
