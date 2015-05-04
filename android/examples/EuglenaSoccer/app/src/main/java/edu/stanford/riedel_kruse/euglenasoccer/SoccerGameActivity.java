package edu.stanford.riedel_kruse.euglenasoccer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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

    public static final String EXTRA_TUTORIAL_MODE =
            "edu.stanford.riedel_kruse.euglenasoccer.TUTORIAL_MODE";
    public static final String EXTRA_TIME =
            "edu.stanford.riedel_kruse.euglenasoccer.TIME";

    private static final int MILLIS_PER_SEC = 1000;
    private static final int MILLIS_PER_DIRECTION = 30 * 1000;
    private static final int MILLIS_PER_MESSAGE = 5 * 1000;

    private static final Scalar COLOR_RED = new Scalar(255, 0, 0);
    private static final Scalar COLOR_YELLOW = new Scalar(255, 255, 0);
    private static final Scalar COLOR_LIGHT_BLUE = new Scalar(200, 200, 250);

    private static final int GAME_OVER_SCORE = 2;

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

    private Tutorial mTutorial;

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
    private int mMessageTime;

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

    private Pattern01 mPattern;

    private boolean endGame = false;
//    private static Scalar LOWER_HSV_THRESHOLD = new Scalar(50, 50, 0);
//    private static Scalar UPPER_HSV_THRESHOLD = new Scalar(96, 200, 255);
    private static Scalar LOWER_HSV_THRESHOLD = new Scalar(100, 100, 100);
    private static Scalar UPPER_HSV_THRESHOLD = new Scalar(200, 200, 200);

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

        connectToJoystick(this, "00:06:66:67:E8:99");

        Intent intent = getIntent();
        boolean tutorialMode = intent.getBooleanExtra(EXTRA_TUTORIAL_MODE, false);

        if (tutorialMode) {
            mTutorial = new Tutorial();
            findViewById(R.id.tutorial_layout).setVisibility(View.VISIBLE);
            updateTutorialViews();
        }
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
        //addGameObject(soccerField);

        //Add guide patterns
        mPattern = new Pattern01(new Point(mTouchX,mTouchY));
        addGameObject(mPattern);
        mPattern.setVisible(false);


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

        //below if for data collection
        mRightGoal.setVisible(false);
        mRightGoal.setVisible(false);

        // Initialize the number of switches in direction to 0 since none have happened yet.
        mNumDirectionSwitches = 0;

        // Add the soccer ball
        mBall = new SoccerBall(new Point(width / 2, height / 2));
        addGameObject(mBall);

        mPassing = false;
        mPassingTime = 0;

        mMessageTime = 0;

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

        if (mTutorial == null || mTutorial.shouldTrack()) {
            updateBallLocation(frame, timeDelta);
        }

        // Update the time based on how long it's been since the last frame.
        if (mTutorial == null || mTutorial.shouldKeepTime()) {
            setTime(mTimeMillis + timeDelta);
        }

        // Decide whether or not it's time to switch directions. Need to add 1 otherwise direction
        // will switch before the first time MILLIS_PER_DIRECTION milliseconds have passed.
        if (mTimeMillis > (mNumDirectionSwitches + 1) * MILLIS_PER_DIRECTION) {
            switchDirections();
        }

        if (mTutorial == null || mTutorial.shouldUpdateZoomView()) {
            updateZoomView(frame);
        }

        mMessageTime += timeDelta;
        if (mMessageTime > MILLIS_PER_MESSAGE) {
            displayMessage("");
        }

        if (mTutorial != null) {
            mBall.setVisible(mTutorial.shouldDrawBall());
            mBall.setDirectionVisible(mTutorial.shouldDrawDirection());
            if (mNumDirectionSwitches == 0) {
                mRightGoal.setVisible(mTutorial.shouldDrawGoals());
            }
        }

        if (mTutorial == null){
            trackData(mBall.x(),mBall.y(),pointToAngle(mBall.direction()),mBallSpeed,mLightDir,mTimeMillis);
        }

        if (endGame == true){
            endGame = false;
            try {
                createDataFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    @Override
    public void onJoystickDirectionStarted(JoystickThread.Direction direction) {
        if (direction == JoystickThread.Direction.LEFT) {
            mRightLightIndicator.setVisible(true);
            mLightDir += 1000;
        }
        else if (direction == JoystickThread.Direction.RIGHT) {
            mLeftLightIndicator.setVisible(true);
            mLightDir += 100;
        }
        else if (direction == JoystickThread.Direction.TOP) {
            mBottomLightIndicator.setVisible(true);
            mLightDir += 10;
        }
        else {
            mTopLightIndicator.setVisible(true);
            mLightDir += 1;
        }
    }

    @Override
    public void onJoystickDirectionFinished(JoystickThread.Direction direction) {
        if (direction == JoystickThread.Direction.LEFT) {
            mRightLightIndicator.setVisible(false);
            mLightDir -= 1000;
        }
        else if (direction == JoystickThread.Direction.RIGHT) {
            mLeftLightIndicator.setVisible(false);
            mLightDir -= 100;
        }
        else if (direction == JoystickThread.Direction.TOP) {
            mBottomLightIndicator.setVisible(false);
            mLightDir -= 10;
        }
        else {
            mTopLightIndicator.setVisible(false);
            mLightDir -= 1;
        }
    }

    @Override
    public void onJoystickDown() {

    }

    @Override
    public void onJoystickUp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                simulateButtonPress((Button) findViewById(R.id.action_button));
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouchX = (int)event.getX();
        mTouchY = (int)event.getY();

        if (mTutorial != null && !mTutorial.shouldDisplayActionButton()) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                simulateButtonPress((Button) findViewById(R.id.action_button));
        }

        return super.onTouchEvent(event);
    }

    private void simulateButtonPress(final Button button) {
        // Run the button's onClick functionality.
        button.performClick();

        // Make the button look pressed.
        button.setPressed(true);
        button.invalidate();

        // Reset the button to normal after a small delay.
        button.postDelayed(new Runnable() {
            public void run() {
                button.setPressed(false);
                button.invalidate();
            }
        }, 100);
    }

    private void updateBallLocation(Mat frame, long timeDelta) {
        Point newPosition;
        if (mPassing) {
            /*mPassingTime += timeDelta;

            if (mPassingTime > PASS_TIME) {
                stopPassing();
            }

            int distance = (int) (timeDelta * PASS_SPEED);
            newPosition = mBall.direction();
            newPosition.x *= distance;
            newPosition.y *= distance;

            newPosition = MathUtil.addPoints(newPosition, mBall.position());
            mBall.setPosition(newPosition);*/

            stopPassing();

            newPosition = assignROI();
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
        List<Point> euglenaLocations = ImageProcessing.findEuglenaInRoi(frame, roi, UPPER_HSV_THRESHOLD, LOWER_HSV_THRESHOLD);

        // Find the location of the Euglena that is closest to the ball.
        return MathUtil.findClosestPoint(ballLocation, euglenaLocations);
    }

    private void onGoalScored() {
        resetBall();

        // Increase the score
        setScore(mScore + 1);

        playSound(mSoundIdCrowdCheer);

        displayMessage(mResources.getString(R.string.goal));

        if (mScore >= GAME_OVER_SCORE) {

            // at the end of the game, save data collection
            try {
                createDataFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            finish();

            Intent intent = new Intent(this, HighScoreActivity.class);
            intent.putExtra(EXTRA_TIME, (int) mTimeMillis / MILLIS_PER_SEC);
            startActivity(intent);
        }
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
        //passOrBounceBall();

        mPassing = true;
        mPattern.setPosition(new Point(mTouchX, mTouchY));
        mPattern.setVisible(true);
    }

    public void onTutorialButtonPressed(View view) {
        mTutorial.advance();
        updateTutorialViews();

        if (mTutorial.finished()) {
            finish();
        }
    }

    public void updateTutorialViews() {
        TextView tutorialText = (TextView) findViewById(R.id.tutorial_text);
        tutorialText.setText(mTutorial.getCurrentStringResource());

        Button tutorialButton = (Button) findViewById(R.id.tutorial_button);
        tutorialButton.setText(mTutorial.getButtonTextResource());

        TextView scoreText = (TextView) findViewById(R.id.score);
        TextView timeText = (TextView) findViewById(R.id.time);

        Button actionButton = (Button) findViewById(R.id.action_button);

        if (mTutorial.shouldDisplayScores()) {
            scoreText.setVisibility(View.VISIBLE);
        }
        else {
            scoreText.setVisibility(View.GONE);
        }

        if (mTutorial.shouldDisplayTime()) {
            timeText.setVisibility(View.VISIBLE);
        }
        else {
            timeText.setVisibility(View.GONE);
        }

        if (mTutorial.shouldDisplayActionButton()) {
            actionButton.setVisibility(View.VISIBLE);
        }
        else {
            actionButton.setVisibility(View.GONE);
        }
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
        mMessageTime = 0;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView messageView = (TextView) findViewById(R.id.message);
                messageView.setText(message);
            }
        });
    }

    private void switchDirections() {
        /*if (mTutorial != null && !mTutorial.shouldDrawGoals()) {
            return;
        }

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

        mNumDirectionSwitches++;*/
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

    //temporarily place all the data logging stuff here!

    //variables

    List<String> mXPosList = new ArrayList<String>();
    List<String> mYPosList = new ArrayList<String>();
    List<String> mAngleList = new ArrayList<String>();
    List<String> mSpeedList = new ArrayList<String>();
    List<String> mLightDirList = new ArrayList<String>();
    List<String> mTimeList = new ArrayList<String>();
    double mLightDir = 0; //four digit double where the 1000's place is left, 100's is right, 10's is up, and 1's is down

    //Called every frame, and calls all the other functions necessary to log data
    public void trackData(double xPos, double yPos, double angle, double speed, double lightDir, Long time){
        updateX(xPos);
        updateY(yPos);
        updateAngle(angle);
        updateSpeed(speed);
        updateLightDir(lightDir);
        updateTimeList(time);
    }

    public void updateX(double xPos){
        mXPosList.add(Double.toString(xPos));
    }

    public void updateY(double yPos){
        mYPosList.add(Double.toString(yPos));
    }

    public void updateAngle(double angle){
        mAngleList.add(Double.toString(angle));
    }

    public void updateSpeed(double speed){
        mSpeedList.add(Double.toString(speed));
    }

    public void updateLightDir(double lightDir){
        mLightDirList.add(Double.toString(lightDir));
    }

    public void updateTimeList(Long currentTime) {mTimeList.add(Long.toString(currentTime));}

    public String convertListToString(List<String> input){
        String output = "";
        for (String s : input){
            output += s + ",";
        }

        return output;
    }

    public void createDataFile() throws IOException {
        String xPos = convertListToString(mXPosList);
        String yPos = convertListToString(mYPosList);
        String angle = convertListToString(mAngleList);
        String speed = convertListToString(mSpeedList);
        String lightDir = convertListToString(mLightDirList);
        String time = convertListToString(mTimeList);
/*        String csv = "data.csv";

        //File dir = getExternalFilesDir(null);
        File dir = new File("/DCIM/");
        CSVWriter writer = new CSVWriter(new FileWriter(dir + csv));

        //Create record
        String [] recordX = xPos.split(",");
        String [] recordY = yPos.split(",");
        String [] recordAng = angle.split(",");
        String [] recordSpd = speed.split(",");
        String [] recordLight = lightDir.split(",");
        //Write the record to file
        writer.writeNext(recordX);
        writer.writeNext(recordY);
        writer.writeNext(recordAng);
        writer.writeNext(recordSpd);
        writer.writeNext(recordLight);

        //close the writer
        writer.close();*/

        try
        {
            // Creates a trace file in the primary external storage space of the
            // current application.
            // If the file does not exists, it is created.
            File traceFile = new File(((Context)this).getExternalFilesDir(null), "Data.txt");
            if (!traceFile.exists())
                traceFile.createNewFile();
            // Adds a line to the trace file
            BufferedWriter writer = new BufferedWriter(new FileWriter(traceFile, true /*append*/));
            writer.write(xPos + "\n\n" + yPos + "\n\n" + angle + "\n\n" + speed + "\n\n" + lightDir + "\n\n" + time + "\n\nEND");
            writer.close();
            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile((Context)(this),
                    new String[] { traceFile.toString() },
                    null,
                    null);
        }
        catch (IOException e)
        {
            Log.e("com.BioticGame", "Unable to write to the Data.txt file.");
        }

        resetDataVectors();
    }

    public void resetDataVectors() {
        mXPosList.clear();
        mYPosList.clear();
        mAngleList.clear();
        mSpeedList.clear();
        mLightDirList.clear();
        mTimeList.clear();
    }

    public double pointToAngle(Point point){
        if(point.x == 0 && point.y == 0){
            return 10.0;
        }else if(point.x == 0){
            if(point.y > 0) {
                return Math.PI/2;
            }else{
                return 3*Math.PI/2;
            }
        }else if(point.y == 0){
            if(point.x > 0){
                return 0.0;
            }else {
                return Math.PI;
            }
        }else {
            double angle = Math.atan2(point.x, point.y);

            //Case Quadrant I:
            //Do nothing

            //Case Quadrant II/III:
            /*if(point.x < 0){
                angle += Math.PI;
            }else if(point.y <0){ //case Quadrant IV:
                angle = 2*Math.PI + angle;
            }*/

            return angle;
        }
    }

    //Below is all the interface mods for experiment setup
    //

    public int mTouchX = 0;
    public int mTouchY = 0;

    private Point assignROI(){
        return new Point(mTouchX,mTouchY);
    }

    public void onEndButtonPressed(View view){
//        try {
//            createDataFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        finish();

        endGame = true;
    }
}
