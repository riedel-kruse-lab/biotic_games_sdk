package edu.stanford.riedel_kruse.euglenascientist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by dchiu on 12/7/14.
 */
public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startGame(View view)
    {
        Intent intent = new Intent(this, SoccerGameActivity.class);
        intent.setPackage("edu.stanford.riedel_kruse.euglenascientist");
        startActivity(intent);
    }

    public void startTutorial(View view)
    {
        Intent intent = new Intent(this, SoccerGameActivity.class);
        intent.putExtra(SoccerGameActivity.EXTRA_TUTORIAL_MODE, true);
        startActivity(intent);
    }

    public void infoButtonPressed(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Euglena:");
        builder.setMessage("Euglena are a single-celled, photosynthetic organism! " +
                        "Euglena can be controlled by light simuli. Can you tell if the Euglena seek or avoid the light?"
        );
        builder.setCancelable(false);
        builder.setPositiveButton("I feel smarter already!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    public void creditsButtonPressed(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Credits");
        builder.setMessage("Honesty Kim: Messing Things Up\nDaniel Chiu: Programming\n" +
                        "Seung Ah Lee: Optics\nAlice Chung: Euglena Biology\nSherwin Xia: Electronics\n" +
                        "Lukas Gerber: Sticker Microfluidics\nNate Cira: Microfluidics\n" +
                        "Ingmar Riedel-Kruse: Advisor"
        );
        builder.setCancelable(false);
        builder.setPositiveButton("Good job guys!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    public void onHighScoresClicked(View v) {
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }


}
