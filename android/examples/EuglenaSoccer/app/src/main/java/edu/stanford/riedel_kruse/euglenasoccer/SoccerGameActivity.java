package edu.stanford.riedel_kruse.euglenasoccer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.riedel_kruse.bioticgamessdk.BioticGameActivity;
import edu.stanford.riedel_kruse.bioticgamessdk.CameraView;
import edu.stanford.riedel_kruse.bioticgamessdk.CollisionCallback;
import edu.stanford.riedel_kruse.bioticgamessdk.ImageProcessing;
import edu.stanford.riedel_kruse.bioticgamessdk.JoystickListener;
import edu.stanford.riedel_kruse.bioticgamessdk.JoystickThread;
import edu.stanford.riedel_kruse.bioticgamessdk.MathUtil;
import edu.stanford.riedel_kruse.bioticgamessdk.gameobjects.LineObject;
import edu.stanford.riedel_kruse.bioticgamessdk.gameobjects.RectangleObject;
import edu.stanford.riedel_kruse.bioticgamessdk.gameobjects.TextObject;

/**
 * Created by dchiu on 1/31/15.
 */
public class SoccerGameActivity extends BioticGameActivity implements JoystickListener {

    private static final int MILLIS_PER_SEC = 1000;
    private static final int MILLIS_PER_DIRECTION = 30 * 1000;

    private static final Scalar COLOR_RED = new Scalar(255, 0, 0);
    private static final Scalar COLOR_YELLOW = new Scalar(255, 255, 0);
    private static final Scalar COLOR_LIGHT_BLUE = new Scalar(200, 200, 250);

    private static final int PASS_TIME = 400;
    /**
     * How fast the ball moves when passed in pixels/ms.
     */
    private static final double PASS_SPEED = 1;

    private static final int NUM_RECENT_POSITIONS = 10;

    private static final int SOUND_POOL_MAX_STREAMS = 1;
    private static final int SOUND_POOL_SRC_QUALITY = 0;
    private static final float SOUND_POOL_LEFT_VOLUME = 1.0f;
    private static final float SOUND_POOL_RIGHT_VOLUME = SOUND_POOL_LEFT_VOLUME;
    private static final int SOUND_POOL_PRIORITY = 1;
    private static final int SOUND_POOL_LOOP = 0;
    private static final float SOUND_POOL_FLOAT_RATE = 1.0f;

    private enum Direction {
        LEFT,
        RIGHT
    }

    private SoccerBall mBall;
    private LeftGoal mLeftGoal;
    private RightGoal mRightGoal;

    private int mFieldWidth;
    private int mFieldHeight;
    private int mScore;
    private long mTimeMillis;
    private int mNumDirectionSwitches;

    private Direction mCurrentDirection;

    private boolean mPassing;
    private int mPassingTime;

    private Resources mResources;

    private TextView mScoreTextView;
    private TextView mTimeTextView;

    private List<Point> mRecentBallPositions;
    private double mBallSpeed;

    private SoundPool mSoundPool;
    private int mSoundIdWallBounce;
    private int mSoundIdPassBall;
    private int mSoundIdOutOfBounds;
    private int mSoundIdCrowdCheer;
    private int mSoundIdBounceBall;

    private RectangleObject mLeftLightIndicator;
    private RectangleObject mRightLightIndicator;
    private RectangleObject mTopLightIndicator;
    private RectangleObject mBottomLightIndicator;

    private TextObject mSpeedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_soccer_game);
        super.onCreate(savedInstanceState);

        mResources = getResources();

        mScoreTextView = (TextView) findViewById(R.id.score);
        mTimeTextView = (TextView) findViewById(R.id.time);

        mSoundPool = new SoundPool(SOUND_POOL_MAX_STREAMS, AudioManager.STREAM_MUSIC,
                SOUND_POOL_SRC_QUALITY);
        mSoundIdWallBounce = mSoundPool.load(this, R.raw.wall_bounce, SOUND_POOL_PRIORITY);
        mSoundIdPassBall = mSoundPool.load(this, R.raw.pass_ball, SOUND_POOL_PRIORITY);
        mSoundIdOutOfBounds = mSoundPool.load(this, R.raw.out_of_bounds, SOUND_POOL_PRIORITY);
        mSoundIdCrowdCheer = mSoundPool.load(this, R.raw.crowd_cheer, SOUND_POOL_PRIORITY);
        mSoundIdBounceBall = mSoundPool.load(this, R.raw.bounce_ball, SOUND_POOL_PRIORITY);

        startBluetooth(this);
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

        // Store image width and height for future use
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
        mLeftGoal = new LeftGoal(new Point(goalOffset, goalY), goalWidth, goalHeight,
                COLOR_RED);
        mRightGoal = new RightGoal(new Point(width - goalWidth - goalOffset, goalY),
                goalWidth, goalHeight, COLOR_RED);
        addGameObject(mLeftGoal);
        addGameObject(mRightGoal);

        LineObject scaleLine = new LineObject(mFieldWidth - 300,
                mFieldHeight - SoccerField.PADDING * 2 - 50, mFieldWidth - 150,
                mFieldHeight - SoccerField.PADDING * 2 - 50, COLOR_LIGHT_BLUE, 3);
        addGameObject(scaleLine);

        TextObject scaleText = new TextObject(mFieldWidth - 312.5,
                mFieldHeight - SoccerField.PADDING * 2, mResources.getString(R.string.scale),
                Core.FONT_HERSHEY_PLAIN, 3, COLOR_LIGHT_BLUE, 4);
        addGameObject(scaleText);

        mSpeedText = new TextObject(150, mFieldHeight - SoccerField.PADDING * 2,
                String.format(mResources.getString(R.string.speed), mBallSpeed),
                Core.FONT_HERSHEY_PLAIN, 3, COLOR_LIGHT_BLUE, 4);
        addGameObject(mSpeedText);
        setBallSpeed(0);

        // Initialize light indicators which show the direction that lights are turned on
        int lightIndicatorSize = SoccerField.PADDING - SoccerField.LINE_THICKNESS;
        mLeftLightIndicator = new RectangleObject(new Point(0, lightIndicatorSize),
                lightIndicatorSize, mFieldHeight - 2 * lightIndicatorSize, COLOR_YELLOW, -1);
        mLeftLightIndicator.setVisible(false);
        addGameObject(mLeftLightIndicator);

        mRightLightIndicator = new RectangleObject(
                new Point(mFieldWidth - lightIndicatorSize, lightIndicatorSize),
                lightIndicatorSize, mFieldHeight - 2 * lightIndicatorSize, COLOR_YELLOW, -1);
        mRightLightIndicator.setVisible(false);
        addGameObject(mRightLightIndicator);

        mTopLightIndicator = new RectangleObject(new Point(lightIndicatorSize, 0),
                mFieldWidth - 2 * lightIndicatorSize, lightIndicatorSize, COLOR_YELLOW, -1);
        mTopLightIndicator.setVisible(false);
        addGameObject(mTopLightIndicator);

        mBottomLightIndicator = new RectangleObject(
                new Point(lightIndicatorSize, mFieldHeight - lightIndicatorSize),
                mFieldWidth - 2 * lightIndicatorSize, lightIndicatorSize, COLOR_YELLOW, -1);
        mBottomLightIndicator.setVisible(false);
        addGameObject(mBottomLightIndicator);

        // Set the current direction to right and hide the left goal.
        mCurrentDirection = Direction.RIGHT;
        mLeftGoal.setVisible(false);
        mLeftGoal.setPhysical(false);

        // Initialize the number of switches in direction to 0 since none have happened yet.
        mNumDirectionSwitches = 0;

        // Add the soccer ball
        mBall = new SoccerBall(new Point(width / 2, height / 2));
        addGameObject(mBall);

        mPassing = false;
        mPassingTime = 0;

        // Initialize score and time
        setScore(0);
        setTime(0);

        // Initialize recent ball positions array
        mRecentBallPositions = new ArrayList<Point>();

        // TODO: Consider refactoring SDK so that these two callbacks can be combined into one.
        addCollisionCallback(new CollisionCallback(mBall, mLeftGoal) {
            @Override
            public void onCollision() {
                onGoalScored();
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mRightGoal) {
            @Override
            public void onCollision() {
                onGoalScored();
            }
        });
    }

    @Override
    protected void updateGame(Mat frame, long timeDelta) {

        updateBallLocation(frame, timeDelta);

        // Update the time based on how long it's been since the last frame.
        setTime(mTimeMillis + timeDelta);

        // Decide whether or not it's time to switch directions. Need to add 1 otherwise direction
        // will switch before the first time MILLIS_PER_DIRECTION milliseconds have passed.
        if (mTimeMillis > (mNumDirectionSwitches + 1) * MILLIS_PER_DIRECTION) {
            switchDirections();
        }

        updateZoomView(frame);
    }

    @Override
    public void onJoystickDirectionStarted(JoystickThread.Direction direction) {
        if (direction == JoystickThread.Direction.LEFT) {
            mRightLightIndicator.setVisible(true);
        }
        else if (direction == JoystickThread.Direction.RIGHT) {
            mLeftLightIndicator.setVisible(true);
        }
        else if (direction == JoystickThread.Direction.TOP) {
            mBottomLightIndicator.setVisible(true);
        }
        else {
            mTopLightIndicator.setVisible(true);
        }
    }

    @Override
    public void onJoystickDirectionFinished(JoystickThread.Direction direction) {
        if (direction == JoystickThread.Direction.LEFT) {
            mRightLightIndicator.setVisible(false);
        }
        else if (direction == JoystickThread.Direction.RIGHT) {
            mLeftLightIndicator.setVisible(false);
        }
        else if (direction == JoystickThread.Direction.TOP) {
            mBottomLightIndicator.setVisible(false);
        }
        else {
            mTopLightIndicator.setVisible(false);
        }
    }

    @Override
    public void onJoystickDown() {

    }

    @Override
    public void onJoystickUp() {
        passOrBounceBall();
    }

    private void updateBallLocation(Mat frame, long timeDelta) {
        Point newPosition;
        if (mPassing) {
            mPassingTime += timeDelta;

            if (mPassingTime > PASS_TIME) {
                stopPassing();
            }

            int distance = (int) (timeDelta * PASS_SPEED);
            newPosition = mBall.direction();
            newPosition.x *= distance;
            newPosition.y *= distance;

            newPosition = MathUtil.addPoints(newPosition, mBall.position());
            mBall.setPosition(newPosition);
        }
        else {
            newPosition = findClosestEuglenaToBall(frame);
            if (newPosition == null) {
                newPosition = mBall.position();
            }
            mBall.setPosition(newPosition);
            mRecentBallPositions.add(newPosition);
            if (mRecentBallPositions.size() > NUM_RECENT_POSITIONS) {
                mRecentBallPositions.remove(0);
            }
            mBall.setDirection(MathUtil.computeAverageDirection(mRecentBallPositions));
            setBallSpeed(MathUtil.computeAverageSpeed(mRecentBallPositions));
        }

        if (newPosition == null) {
            return;
        }

        if (newPosition.x < 0 || newPosition.x > mFieldWidth || newPosition.y < 0
                || newPosition.y > mFieldHeight) {
            if (!mPassing) {
                onOutOfBounds();
                playSound(mSoundIdOutOfBounds);
            }
            else {
                Point newDirection = mBall.direction();
                if (newPosition.x < 0) {
                    newDirection.x *= -1;
                    newPosition.x = 0;
                }
                else if (newPosition.x > mFieldWidth) {
                    newDirection.x *= -1;
                    newPosition.x = mFieldWidth;
                }

                if (newPosition.y < 0) {
                    newDirection.y *= -1;
                    newPosition.y = 0;
                }
                else if (newPosition.y > mFieldHeight) {
                    newDirection.y *= -1;
                    newPosition.y = mFieldHeight;
                }

                mBall.setPosition(newPosition);
                mBall.setDirection(newDirection);

                playSound(mSoundIdWallBounce);
            }
        }
    }

    private Rect roiForBall() {
        // Get the model data about the ball.
        Point ballLocation = mBall.position();

        // Create a region of interest based on the location of the ball.
        Rect roi = new Rect();
        roi.x = Math.max((int) ballLocation.x - SoccerBall.RADIUS, 0);
        roi.y = Math.max((int) ballLocation.y - SoccerBall.RADIUS, 0);
        roi.width = Math.min(SoccerBall.RADIUS * 2, mFieldWidth - roi.x);
        roi.height = Math.min(SoccerBall.RADIUS * 2, mFieldHeight - roi.y);

        return roi;
    }

    private Point findClosestEuglenaToBall(Mat frame) {
        // Get the model data about the ball.
        Point ballLocation = mBall.position();

        // Create a region of interest based on the location of the ball.
        Rect roi = roiForBall();

        // Find all things that look like Euglena in the region of interest.
        List<Point> euglenaLocations = ImageProcessing.findEuglenaInRoi(frame, roi);

        // Find the location of the Euglena that is closest to the ball.
        return MathUtil.findClosestPoint(ballLocation, euglenaLocations);
    }

    private void onGoalScored() {
        resetBall();

        // Increase the score
        setScore(mScore + 1);

        playSound(mSoundIdCrowdCheer);

        displayMessage(mResources.getString(R.string.goal));
    }

    private void onOutOfBounds() {
        resetBall();

        displayMessage(mResources.getString(R.string.out_of_bounds));
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

    private void setTime(long newTime) {
        mTimeMillis = newTime;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimeTextView.setText(String.format(mResources.getString(R.string.time),
                        mTimeMillis / MILLIS_PER_SEC));
            }
        });
    }

    private void setBallSpeed(double newSpeed) {
        mBallSpeed = newSpeed;
        mSpeedText.setText(String.format(mResources.getString(R.string.speed), mBallSpeed));
    }

    public void onActionButtonPressed(View view) {
        passOrBounceBall();
    }

    private void passOrBounceBall() {
        mPassing = true;

        // If the ball is not moving, then instead of passing in the direction of the ball, we
        // "bounce" the ball by choosing a random direction for the ball to move in.
        // Logically, a bounce is considered the same as a pass, but in a random starting direction.
        if (mBallSpeed == 0) {
            Point newDirection = new Point(Math.random() - 0.5, Math.random() - 0.5);
            MathUtil.normalizeVector(newDirection);
            mBall.setDirection(newDirection);
            playSound(mSoundIdBounceBall);
        }
        else {
            playSound(mSoundIdPassBall);
        }
    }

    private void stopPassing() {
        mPassing = false;
        mPassingTime = 0;
        mBall.setDirection(new Point(0, 0));
        mRecentBallPositions.clear();
        setBallSpeed(0);
    }

    private void resetBall() {
        mBall.setX(mFieldWidth / 2);
        mBall.setY(mFieldHeight / 2);

        stopPassing();
    }

    private void displayMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void switchDirections() {
        mLeftGoal.setPhysical(!mLeftGoal.isPhysical());
        mLeftGoal.setVisible(!mLeftGoal.isVisible());

        mRightGoal.setPhysical(!mRightGoal.isPhysical());
        mRightGoal.setVisible(!mRightGoal.isVisible());

        if (mCurrentDirection == Direction.RIGHT) {
            mCurrentDirection = Direction.LEFT;
        }
        else {
            mCurrentDirection = Direction.RIGHT;
        }

        mNumDirectionSwitches++;
    }

    private void playSound(int soundId) {
        mSoundPool.play(soundId, SOUND_POOL_LEFT_VOLUME, SOUND_POOL_RIGHT_VOLUME,
                SOUND_POOL_PRIORITY, SOUND_POOL_LOOP, SOUND_POOL_FLOAT_RATE);
    }

    private void updateZoomView(Mat frame) {
        Rect roi = roiForBall();
        Mat zoomMat = new Mat(frame, roi);
        final Bitmap zoomBitmap = Bitmap.createBitmap(zoomMat.cols(), zoomMat.rows(),
                Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(zoomMat, zoomBitmap);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView zoomView = (ImageView) findViewById(R.id.zoom_view);
                zoomView.setImageBitmap(zoomBitmap);
            }
        });
    }
}