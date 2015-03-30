package edu.stanford.riedel_kruse.euglenasoccer;

import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.List;

import edu.stanford.riedel_kruse.bioticgamessdk.BioticGameActivity;
import edu.stanford.riedel_kruse.bioticgamessdk.CameraView;
import edu.stanford.riedel_kruse.bioticgamessdk.Circle;
import edu.stanford.riedel_kruse.bioticgamessdk.CollisionCallback;
import edu.stanford.riedel_kruse.bioticgamessdk.ImageProcessing;
import edu.stanford.riedel_kruse.bioticgamessdk.MathUtil;

/**
 * Created by dchiu on 1/31/15.
 */
public class SoccerGameActivity extends BioticGameActivity {

    private static final Scalar COLOR_RED = new Scalar(255, 0, 0);

    private SoccerBall mBall;
    private int mFieldWidth;
    private int mFieldHeight;
    private int mScore;
    private int mTime;

    private Resources mResources;

    private TextView mScoreTextView;
    private TextView mTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_soccer_game);
        super.onCreate(savedInstanceState);

        mResources = getResources();

        mScoreTextView = (TextView) findViewById(R.id.score);
        mTimeTextView = (TextView) findViewById(R.id.time);
    }

    @Override
    protected int getCameraViewResourceId() {
        return R.id.camera_view;
    }

    @Override
    protected void initGame(final int width, final int height) {
        CameraView cameraView = getCameraView();

        Camera.Parameters params = cameraView.getCameraParameters();
        params.setZoom(params.getMaxZoom() / 2);
        cameraView.setCameraParameters(params);

        mFieldWidth = width;
        mFieldHeight = height;

        // Add the soccer field lines
        SoccerField soccerField = new SoccerField();
        addGameObject(soccerField);

        // Set up the goals
        int goalHeight = height * 4 / 7;
        int goalWidth = 10;
        int goalY = (height - goalHeight) / 2;
        int goalOffset = SoccerField.PADDING + SoccerField.LINE_THICKNESS;
        LeftGoal leftGoal = new LeftGoal(new Point(goalOffset, goalY), goalWidth, goalHeight,
                COLOR_RED);
        RightGoal rightGoal = new RightGoal(new Point(width - goalWidth - goalOffset, goalY),
                goalWidth, goalHeight, COLOR_RED);
        addGameObject(leftGoal);
        addGameObject(rightGoal);

        // Add the soccer ball
        mBall = new SoccerBall(new Point(width / 2, height / 2));
        addGameObject(mBall);

        setScore(0);
        setTime(0);

        // TODO: Consider refactoring SDK so that these two callbacks can be combined into one.
        addCollisionCallback(new CollisionCallback(mBall, leftGoal) {
            @Override
            public void onCollision() {
                onGoalScored();
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, rightGoal) {
            @Override
            public void onCollision() {
                onGoalScored();
            }
        });
    }

    @Override
    protected void updateGame(Mat frame, long timeDelta) {
        Point closestEuglenaLocation = findClosestEuglenaToBall(frame);
        if (closestEuglenaLocation != null) {
            mBall.setPosition(closestEuglenaLocation);
        }
    }

    private Point findClosestEuglenaToBall(Mat frame) {
        // Get the model data about the ball.
        Point ballLocation = mBall.position();

        // Create a region of interest based on the location of the ball.
        Rect roi = new Rect();
        roi.x = Math.max((int) ballLocation.x - SoccerBall.RADIUS, 0);
        roi.y = Math.max((int) ballLocation.y - SoccerBall.RADIUS, 0);
        roi.width = Math.min(SoccerBall.RADIUS * 2, mFieldWidth - roi.x);
        roi.height = Math.min(SoccerBall.RADIUS * 2, mFieldHeight - roi.y);

        // Find all things that look like Euglena in the region of interest.
        List<Point> euglenaLocations = ImageProcessing.findEuglenaInRoi(frame, roi);

        // Find the location of the Euglena that is closest to the ball.
        return MathUtil.findClosestPoint(ballLocation, euglenaLocations);
    }

    private void onGoalScored() {
        mBall.position().x = mFieldWidth / 2;
        mBall.position().y = mFieldHeight / 2;

        // Increase the score
        setScore(mScore + 1);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Goal!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setScore(int newScore) {
        mScore = newScore;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScoreTextView.setText(String.format(mResources.getString(R.string.score), mScore));
            }
        });
    }

    private void setTime(int newTime) {
        mTime = newTime;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimeTextView.setText(String.format(mResources.getString(R.string.time), mTime));
            }
        });
    }

    public void onActionButtonPressed(View view) {

    }
}
