package edu.stanford.riedel_kruse.euglenasoccer;

import android.os.Bundle;
import android.widget.Toast;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.List;

import edu.stanford.riedel_kruse.bioticgamessdk.BioticGameActivity;
import edu.stanford.riedel_kruse.bioticgamessdk.Circle;
import edu.stanford.riedel_kruse.bioticgamessdk.CollisionCallback;
import edu.stanford.riedel_kruse.bioticgamessdk.ImageProcessing;
import edu.stanford.riedel_kruse.bioticgamessdk.MathUtil;

/**
 * Created by dchiu on 1/31/15.
 */
public class SoccerGameActivity extends BioticGameActivity {

    private Circle mBall;
    private int mFieldWidth;
    private int mFieldHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_soccer_game);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getCameraViewResourceId() {
        return R.id.camera_view;
    }

    @Override
    protected void initGame(final int width, final int height) {
        mFieldWidth = width;
        mFieldHeight = height;
        int goalHeight = height * 3 / 4;
        int goalWidth = 10;
        int goalY = (height - goalHeight) / 2;
        LeftGoal redGoal = new LeftGoal(new Point(0, goalY), goalWidth, goalHeight,
                new Scalar(255, 0, 0));
        addGameObject(redGoal);

        RightGoal blueGoal = new RightGoal(new Point(width - goalWidth, goalY), goalWidth,
                goalHeight, new Scalar(0, 0, 255));
        addGameObject(blueGoal);

        mBall = new Circle(new Point(width / 2, height / 2), 20, new Scalar(255, 0, 0), 1, true);
        addGameObject(mBall);

        addCollisionCallback(new CollisionCallback(mBall, blueGoal) {
            @Override
            public void onCollision() {
                mBall.position().x = width / 2;
                mBall.position().y = height / 2;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Goal!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void updateGame(Mat frame, long timeDelta) {
        /*Point closestEuglenaLocation = findClosestEuglenaToBall(frame);
        if (closestEuglenaLocation != null) {
            mBall.setPosition(closestEuglenaLocation);
        }*/
        //mBall.position().x += 10;
    }

    private Point findClosestEuglenaToBall(Mat frame) {
        // Get the model data about the ball.
        Point ballLocation = mBall.position();
        int ballRadius = mBall.radius();

        // Create a region of interest based on the location of the ball.
        Rect roi = new Rect();
        roi.x = Math.max((int) ballLocation.x - ballRadius, 0);
        roi.y = Math.max((int) ballLocation.y - ballRadius, 0);
        roi.width = Math.min(ballRadius * 2, mFieldWidth - roi.x);
        roi.height = Math.min(ballRadius * 2, mFieldHeight - roi.y);

        // Find all things that look like Euglena in the region of interest.
        List<Point> euglenaLocations = ImageProcessing.findEuglenaInRoi(frame, roi);

        // Find the location of the Euglena that is closest to the ball.
        return MathUtil.findClosestPoint(ballLocation, euglenaLocations);
    }
}
