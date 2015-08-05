package edu.stanford.riedel_kruse.euglenascientist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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

    private static final int NUM_RECENT_POSITIONS = 30;

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
    private int mSoundIdWakka;
    private int mSoundStartSong;
    private int mSoundEndSong;
    private int mJumpSound;
    private int mPlayingSoundFX = 0;
    private int mPlayingSountrack = 0;

    private RectangleObject mLeftLightIndicator;
    private RectangleObject mRightLightIndicator;
    private RectangleObject mTopLightIndicator;
    private RectangleObject mBottomLightIndicator;

    private TextObject mSpeedText;

    private Pattern01 mPattern;
    private ConsumableBall mConsumableBall1;
    private ConsumableBall mConsumableBall2;
    private ConsumableBall mConsumableBall3;
    private ConsumableBall mConsumableBall4;
    private ConsumableBall mConsumableBall5;
    private ConsumableBall mConsumableBall6;
    private ConsumableBall mConsumableBall7;
    private ConsumableBall mConsumableBall8;
    private ConsumableBall mConsumableBall9;
    private ConsumableBall mConsumableBall10;
    private ConsumableBall mConsumableBall11;
    private ConsumableBall mConsumableBall12;
    private ConsumableBall mConsumableBall13;
    private ConsumableBall mConsumableBall14;

    private PMan mPMan;

    private int mConsumableBallOffset = 100;

    private int mBallCountScore = 0;

    private BoxedRegion mBoxedRegion;

    private int frameCount;
    private boolean endGame = false;
    private boolean velocityCalculate = false;
    private boolean velocityCalculateTapped = false;
    private boolean mTraceEuglena = false;
    private boolean mTraceEuglenaTapped = false;
    private boolean plotGraph = false;
    private boolean isTapped = false;
    private boolean mStartMeasuring = false;
    private boolean dataCollectionFinished = false;
    private boolean mFollowLine = false;
    private Scalar LOWER_HSV_THRESHOLD = new Scalar(30, 30, 0);
    private Scalar UPPER_HSV_THRESHOLD = new Scalar(96, 200, 255);

    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat df1 = new DecimalFormat("0.0");
    private Long mIntervalTimer = 1234567890L;

    private int mFollowLineIndex = 0;
    private int mTraceExpStartIndex = 0;
    private int mTraceExpMidIndex = 0;
    private int mTraceExpEndIndex = 0;

    private String mInputText = "";

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
        mSoundIdWakka = mSoundPool.load(this, R.raw.wakka, SOUND_POOL_PRIORITY);
        mSoundStartSong = mSoundPool.load(this, R.raw.start_song, SOUND_POOL_PRIORITY);
        mSoundEndSong = mSoundPool.load(this, R.raw.end_song, SOUND_POOL_PRIORITY);
        mJumpSound = mSoundPool.load(this, R.raw.jump_sound, SOUND_POOL_PRIORITY);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

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
        params.setZoom((int) (params.getMaxZoom() / 3.3));
//        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
//        params.setAutoWhiteBalanceLock(true);
        cameraView.setCameraParameters(params);

        // Store image width and height for future use
        mFieldWidth = width;
        mFieldHeight = height;

        // Add the soccer field lines
        SoccerField soccerField = new SoccerField();
        //addGameObject(soccerField);

        //Add boxed tapping region
        mBoxedRegion = new BoxedRegion(new Point(0, 0));
        addGameObject(mBoxedRegion);
        mBoxedRegion.setVisible(false);

        //Add guide patterns
        mPattern = new Pattern01(new Point(mTouchX, mTouchY));
        addGameObject(mPattern);
        mPattern.setVisible(false);

        //Add consumable balls
        mConsumableBall1 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall1);
        mConsumableBall1.setVisible(false);

        mConsumableBall2 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall2);
        mConsumableBall2.setVisible(false);

        mConsumableBall3 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall3);
        mConsumableBall3.setVisible(false);

        mConsumableBall4 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall4);
        mConsumableBall4.setVisible(false);

        mConsumableBall5 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall5);
        mConsumableBall5.setVisible(false);

        mConsumableBall6 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall6);
        mConsumableBall6.setVisible(false);

        mConsumableBall7 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall7);
        mConsumableBall7.setVisible(false);

        mConsumableBall8 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall8);
        mConsumableBall8.setVisible(false);

        mConsumableBall9 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall9);
        mConsumableBall9.setVisible(false);

        mConsumableBall10 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall10);
        mConsumableBall10.setVisible(false);

        mConsumableBall11 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall11);
        mConsumableBall11.setVisible(false);

        mConsumableBall12 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall12);
        mConsumableBall12.setVisible(false);

        mConsumableBall13 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall13);
        mConsumableBall13.setVisible(false);

        mConsumableBall14 = new ConsumableBall(new Point(mTouchX, mTouchY));
        addGameObject(mConsumableBall14);
        mConsumableBall14.setVisible(false);

        //add pman
        mPMan = new PMan(new Point(mTouchX, mTouchY));
        addGameObject(mPMan);
        mPMan.setVisible(false);

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

        // Start counting frames
        frameCount = 0;

        // Initialize recent ball positions array
        mRecentBallPositions = new ArrayList<Point>();

        // TODO: Consider refactoring SDK so that these two callbacks can be combined into one.
        addCollisionCallback(new CollisionCallback(mBall, mLeftGoal) {
            @Override
            public void onCollision() {
                //onGoalScored();
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mRightGoal) {
            @Override
            public void onCollision() {
                //onGoalScored();
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall1) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall1);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall2) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall2);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall3) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall3);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall4) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall4);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall5) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall5);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall6) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall6);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall7) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall7);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall8) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall8);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall9) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall9);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall10) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall10);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall11) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall11);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall12) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall12);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall13) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall13);
            }
        });

        addCollisionCallback(new CollisionCallback(mBall, mConsumableBall14) {
            @Override
            public void onCollision() {
                onBallEaten(mConsumableBall14);
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

        if (mTutorial == null) {
            trackData(mBall.x(), mBall.y(), pointToAngle(mBall.direction()), mBallSpeed, mLightDir, mTimeMillis, isTapped);
        }

        if (endGame == true) {
            endGame = false;
            try {
                createDataFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            finish();
        }

        if (velocityCalculate == true) {
            //only enter this function the first time
            if (velocityCalculateTapped) {
                isTapped = false;
                velocityCalculate = false;
                velocityCalculateTapped = false;
                //reset all velocity measurement stuff
                avgVelocityMessage();
            }


            //calculate avg speed for next two seconds on tap...

            if (isTapped) {
                mIntervalTimer = mTimeMillis + INTERVAL_FOR_COMPUTING_AVG_SPD_2;
                mStartMeasuring = true;
            }

            if ((mTimeMillis >= mIntervalTimer) && mStartMeasuring) {
                //This function needs to back-calculate the average velocity over the past 2 seconds
                //and keep track of the number of different counts. After 5 samples, starts the
                //next condition. After that, set dataCollectionFinished = true
                avgSpeedInterval2();
                mStartMeasuring = false;
            }

            if (dataCollectionFinished) {
                dataCollectionFinished = false;
                velocityCalculate = false;
                avgVelocityFinishedMessage();
            }

//            try {
//                createDataFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        if (mTraceEuglena == true) {
            //only enter this function the first time
            if (mTraceEuglenaTapped) {
                isTapped = false;
                mTraceEuglena = false;
                mTraceEuglenaTapped = false;
                mIntervalTimer = 1234567890L;
                mTraceExpMidIndex = 0;
                mTraceExpEndIndex = 0;
                mTraceExpStartIndex = 0;
                traceExpMessage();
            }

            //calculate avg speed for next two seconds on tap...

            if (isTapped) {
                mIntervalTimer = mTimeMillis + TIME_BEFORE_LIGHT_STIMULUS;
                mTraceExpStartIndex = mTimeList.size();
                mStartMeasuring = true;
            }

            if ((mTimeMillis >= mIntervalTimer) && mStartMeasuring) {
                playSound(mJumpSound);
                mTraceExpMidIndex = mTimeList.size();
                mStartMeasuring = false;
            }

            if (mTimeMillis >= mIntervalTimer + TIME_AFTER_LIGHT_STIMULUS) {
                dataCollectionFinished = true;
                mTraceExpEndIndex = mTimeList.size();

                Double humanReactionOffsetTime = Double.parseDouble(mTimeList.get(mTraceExpMidIndex)) + HUMAN_REACTION_TIME;
                int tempMidIndex = 0;
                for (int i = mTraceExpMidIndex; humanReactionOffsetTime > Double.parseDouble(mTimeList.get(i)); i++) {
                    tempMidIndex = i;
                }
                mTraceExpMidIndex = tempMidIndex;
            }

            if (dataCollectionFinished) {
                dataCollectionFinished = false;
                mTraceEuglena = false;
                List<Double> listX = new ArrayList<>(convertStringListToDouble(mXPosList, mTraceExpStartIndex, mTraceExpEndIndex));
                List<Double> listY = new ArrayList<>(convertStringListToDouble(mYPosList, mTraceExpStartIndex, mTraceExpEndIndex));
                traceFinishedMessage(listX, listY);
            }

//            try {
//                createDataFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        if (plotGraph == true) {
            plotGraph = false;
            createGraph();
        }

        if (mFollowLine && areAllBallsEaten()) {
            mPattern.setVisible(false);
            clearFollowLineMode();
        }

        //every turn needs to reset the isTapped to keep it false as default
        isTapped = false;

        frameCount++;
    }

    @Override
    public void onJoystickDirectionStarted(JoystickThread.Direction direction) {
        if (direction == JoystickThread.Direction.LEFT) {
            mRightLightIndicator.setVisible(true);
            mLightDir += 1000;
        } else if (direction == JoystickThread.Direction.RIGHT) {
            mLeftLightIndicator.setVisible(true);
            mLightDir += 100;
        } else if (direction == JoystickThread.Direction.TOP) {
            mBottomLightIndicator.setVisible(true);
            mLightDir += 10;
        } else {
            mTopLightIndicator.setVisible(true);
            mLightDir += 1;
        }
    }

    @Override
    public void onJoystickDirectionFinished(JoystickThread.Direction direction) {
        if (direction == JoystickThread.Direction.LEFT) {
            mRightLightIndicator.setVisible(false);
            mLightDir -= 1000;
        } else if (direction == JoystickThread.Direction.RIGHT) {
            mLeftLightIndicator.setVisible(false);
            mLightDir -= 100;
        } else if (direction == JoystickThread.Direction.TOP) {
            mBottomLightIndicator.setVisible(false);
            mLightDir -= 10;
        } else {
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
        isTapped = true;
        mFollowLineIndex = mTimeList.size() - 1;

        // - 180 needed in getY to balance offset... offset possibly because different views are calling???
        mTouchX = (int) event.getX();
        mTouchY = (int) event.getY() - 180;

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
        } else {
            newPosition = findClosestEuglenaToBall(frame);
            if (newPosition == null) {
                newPosition = mBall.position();
            }
            mBall.setPosition(newPosition);
            mPMan.setPosition(newPosition);
            mRecentBallPositions.add(newPosition);
            if (mRecentBallPositions.size() > NUM_RECENT_POSITIONS) {
                mRecentBallPositions.remove(0);
            }
            mBall.setDirection(MathUtil.computeAverageDirection(mRecentBallPositions));
            setBallSpeed(MathUtil.computeAverageSpeed(mRecentBallPositions));
            mPMan.setDirection(mBall.direction());
        }

        if (newPosition == null) {
            return;
        }

        if (newPosition.x < 0 || newPosition.x > mFieldWidth || newPosition.y < 0
                || newPosition.y > mFieldHeight) {
            if (!mPassing) {
                onOutOfBounds();
                playSound(mSoundIdOutOfBounds);
            } else {
                Point newDirection = mBall.direction();
                if (newPosition.x < 0) {
                    newDirection.x *= -1;
                    newPosition.x = 0;
                } else if (newPosition.x > mFieldWidth) {
                    newDirection.x *= -1;
                    newPosition.x = mFieldWidth;
                }

                if (newPosition.y < 0) {
                    newDirection.y *= -1;
                    newPosition.y = 0;
                } else if (newPosition.y > mFieldHeight) {
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
        mBallSpeed = (mBallSpeed + newSpeed)/2;
        mSpeedText.setText(String.format(mResources.getString(R.string.speed), mBallSpeed));
    }

    public void onActionButtonPressed(View view) {
        //passOrBounceBall();

        mPassing = true;

        if (mFollowLine) {
            mPattern.setPosition(new Point(mTouchX, mTouchY));
            mPattern.setVisible(true);

            mConsumableBall1.setPosition(new Point(mTouchX + mConsumableBallOffset, mTouchY));
            mConsumableBall1.setVisible(true);
            mConsumableBall1.setPhysical(true);
            mConsumableBall1.setIsEaten(false);

            mConsumableBall2.setPosition(new Point(mTouchX + 2 * mConsumableBallOffset, mTouchY));
            mConsumableBall2.setVisible(true);
            mConsumableBall2.setPhysical(true);
            mConsumableBall2.setIsEaten(false);

            mConsumableBall3.setPosition(new Point(mTouchX + 3 * mConsumableBallOffset, mTouchY));
            mConsumableBall3.setVisible(true);
            mConsumableBall3.setPhysical(true);
            mConsumableBall3.setIsEaten(false);

            mConsumableBall4.setPosition(new Point(mTouchX + 4 * mConsumableBallOffset, mTouchY));
            mConsumableBall4.setVisible(true);
            mConsumableBall4.setPhysical(true);
            mConsumableBall4.setIsEaten(false);

            mConsumableBall5.setPosition(new Point(mTouchX + 5 * mConsumableBallOffset, mTouchY));
            mConsumableBall5.setVisible(true);
            mConsumableBall5.setPhysical(true);
            mConsumableBall5.setIsEaten(false);
            mConsumableBall5.setIsEaten(false);

            mConsumableBall6.setPosition(new Point(mTouchX + 6 * mConsumableBallOffset, mTouchY));
            mConsumableBall6.setVisible(true);
            mConsumableBall6.setPhysical(true);
            mConsumableBall6.setIsEaten(false);

            mConsumableBall7.setPosition(new Point(mTouchX + 6 * mConsumableBallOffset, mTouchY - mConsumableBallOffset));
            mConsumableBall7.setVisible(true);
            mConsumableBall7.setPhysical(true);
            mConsumableBall7.setIsEaten(false);

            mConsumableBall8.setPosition(new Point(mTouchX + 6 * mConsumableBallOffset, mTouchY - 2 * mConsumableBallOffset));
            mConsumableBall8.setVisible(true);
            mConsumableBall8.setPhysical(true);
            mConsumableBall8.setIsEaten(false);

            mConsumableBall9.setPosition(new Point(mTouchX + 6 * mConsumableBallOffset, mTouchY - 3 * mConsumableBallOffset));
            mConsumableBall9.setVisible(true);
            mConsumableBall9.setPhysical(true);
            mConsumableBall9.setIsEaten(false);

            mConsumableBall10.setPosition(new Point(mTouchX + 5 * mConsumableBallOffset, mTouchY - 3 * mConsumableBallOffset));
            mConsumableBall10.setVisible(true);
            mConsumableBall10.setPhysical(true);
            mConsumableBall10.setIsEaten(false);

            mConsumableBall11.setPosition(new Point(mTouchX + 4 * mConsumableBallOffset, mTouchY - 3 * mConsumableBallOffset));
            mConsumableBall11.setVisible(true);
            mConsumableBall11.setPhysical(true);
            mConsumableBall11.setIsEaten(false);

            mConsumableBall12.setPosition(new Point(mTouchX + 3 * mConsumableBallOffset, mTouchY - 3 * mConsumableBallOffset));
            mConsumableBall12.setVisible(true);
            mConsumableBall12.setPhysical(true);
            mConsumableBall12.setIsEaten(false);

            mConsumableBall13.setPosition(new Point(mTouchX + 2 * mConsumableBallOffset, mTouchY - 3 * mConsumableBallOffset));
            mConsumableBall13.setVisible(true);
            mConsumableBall13.setPhysical(true);
            mConsumableBall13.setIsEaten(false);

            mConsumableBall14.setPosition(new Point(mTouchX + mConsumableBallOffset, mTouchY - 3 * mConsumableBallOffset));
            mConsumableBall14.setVisible(true);
            mConsumableBall14.setPhysical(true);
            mConsumableBall14.setIsEaten(false);

            mBallCountScore = 0;

            mPMan.setPosition(new Point(mTouchX, mTouchY));
            mPMan.setVisible(true);
            mBoxedRegion.setVisible(false);

            loopSong(mSoundIdWakka);
        }
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
        } else {
            scoreText.setVisibility(View.GONE);
        }

        if (mTutorial.shouldDisplayTime()) {
            timeText.setVisibility(View.VISIBLE);
        } else {
            timeText.setVisibility(View.GONE);
        }

        if (mTutorial.shouldDisplayActionButton()) {
            actionButton.setVisibility(View.VISIBLE);
        } else {
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
        } else {
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

    private void loopSound(int soundId) {
        mPlayingSoundFX = mSoundPool.play(soundId, SOUND_POOL_LEFT_VOLUME, SOUND_POOL_RIGHT_VOLUME,
                SOUND_POOL_PRIORITY, -1, SOUND_POOL_FLOAT_RATE);
    }

    private void loopSong(int soundId) {
        mPlayingSountrack = mSoundPool.play(soundId, SOUND_POOL_LEFT_VOLUME, SOUND_POOL_RIGHT_VOLUME,
                SOUND_POOL_PRIORITY, -1, SOUND_POOL_FLOAT_RATE);
    }

    private void stopSound() {
        mSoundPool.stop(mPlayingSoundFX);
    }

    private void stopSong() {
        mSoundPool.stop(mPlayingSountrack);
    }

    private void updateZoomView(Mat frame) {
        Rect roi = roiForBall();
        Mat zoomMat = new Mat(frame, roi);

//        Imgproc.cvtColor(zoomMat, zoomMat, Imgproc.COLOR_BGR2HSV);
//        Core.inRange(zoomMat, LOWER_HSV_THRESHOLD, UPPER_HSV_THRESHOLD, zoomMat);

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
    List<String> mIsTapped = new ArrayList<String>();
    double mLightDir = 0; //four digit double where the 1000's place is left, 100's is right, 10's is up, and 1's is down

    //These are arraylists that contain the averaged velocity traces for each run
    List<Double> mAverageVelocity1 = new ArrayList<Double>();
    List<Double> mAverageVelocity2 = new ArrayList<Double>();

    //Called every frame, and calls all the other functions necessary to log data
    public void trackData(double xPos, double yPos, double angle, double speed, double lightDir, Long time, boolean tapped) {
        updateX(xPos);
        updateY(yPos);
        updateAngle(angle);
        updateSpeed(speed);
        updateLightDir(lightDir);
        updateTimeList(time);
        updateIsTapped(tapped);
    }

    public void updateX(double xPos) {
        mXPosList.add(Double.toString(xPos));
    }

    public void updateY(double yPos) {
        mYPosList.add(Double.toString(yPos));
    }

    public void updateAngle(double angle) {
        mAngleList.add(Double.toString(angle));
    }

    public void updateSpeed(double speed) {
        mSpeedList.add(Double.toString(speed));
    }

    public void updateLightDir(double lightDir) {
        mLightDirList.add(Double.toString(lightDir));
    }

    public void updateTimeList(Long currentTime) {
        mTimeList.add(Long.toString(currentTime));
    }

    public void updateIsTapped(boolean tapped) {
        mIsTapped.add(Boolean.toString(tapped));
    }

    public String convertListToString(List<String> input) {
        String output = "";
        for (String s : input) {
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
        String tapped = convertListToString(mIsTapped);
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

        try {
            // Creates a trace file in the primary external storage space of the
            // current application.
            // If the file does not exists, it is created.
            File traceFile = new File(((Context) this).getExternalFilesDir(null), "Data.txt");
            if (!traceFile.exists())
                traceFile.createNewFile();
            // Adds a line to the trace file
            BufferedWriter writer = new BufferedWriter(new FileWriter(traceFile, true /*append*/));
            writer.write(xPos + "\n\n" + yPos + "\n\n" + angle + "\n\n" + speed + "\n\n" + lightDir + "\n\n" + time + "\n\n" + tapped + "\n\nEND");
            writer.close();
            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile((Context) (this),
                    new String[]{traceFile.toString()},
                    null,
                    null);
        } catch (IOException e) {
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
        mIsTapped.clear();
    }

    public double pointToAngle(Point point) {
        if (point.x == 0 && point.y == 0) {
            return 10.0;
        } else if (point.x == 0) {
            if (point.y > 0) {
                return Math.PI / 2;
            } else {
                return 3 * Math.PI / 2;
            }
        } else if (point.y == 0) {
            if (point.x > 0) {
                return 0.0;
            } else {
                return Math.PI;
            }
        } else {
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

    private Point assignROI() {
        return new Point(mTouchX, mTouchY);
    }

    public void onEndButtonPressed(View view) {
//        try {
//            createDataFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        finish();


        endGame = true;
    }

    public void onMeasureVelocityPressed(View view) {
        velocityCalculate = true;
        velocityCalculateTapped = true;
    }

    private LineChart mChart;

    public void createGraph() {


        mChart = (LineChart) findViewById(R.id.chart);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChart.setVisibility(View.VISIBLE);
            }
        });
        mChart.setDescription("");

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < mTimeList.size(); i++) {
            xVals.add(mTimeList.get(i));
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mSpeedList.size(); i++) {
            yVals.add(new Entry(Float.parseFloat(mSpeedList.get(i)), i));
        }

        LineDataSet set = new LineDataSet(yVals, " ");

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set);
        //mChart.setBackgroundColor(Color.parseColor("#00ff00"));
        //XAxis xAxis = mChart.getXAxis();

//        set.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
//        set.setColor(ColorTemplate.COLORFUL_COLORS[1]);

        LineData data = new LineData(xVals, dataSets);

        mChart.setData(data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChart.invalidate();
            }
        });

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                GraphView graph = (GraphView) findViewById(R.id.graph);
//                LineGraphSeries series = new LineGraphSeries(new DataPoint[] {
//                });
//                for (int i = 0; i < mXPosList.size(); i++) {
//                    series.appendData(new DataPoint(Double.parseDouble(mTimeList.get(i)), Double.parseDouble(mSpeedList.get(i))), true, 10000);
//                }
//                graph.addSeries(series);
//            }
//        });
    }

    public void onPlotPressed(View view) {
        plotGraph = true;
    }

    private static int INTERVAL_FOR_COMPUTING_AVG_SPD = 60000;
    private static int NUMBER_OF_INTERVALS = 4;
    private static int LOWER_SPEED_THRESHOLD = 5;

    public void avgSpeedInterval() {
        double time = Double.parseDouble(mTimeList.get(0));

        double maxTime = time + INTERVAL_FOR_COMPUTING_AVG_SPD * NUMBER_OF_INTERVALS;
        if (maxTime > Double.parseDouble(mTimeList.get(mTimeList.size() - 1))) {
            maxTime = Double.parseDouble(mTimeList.get(mTimeList.size() - 1));
        }

        double startTime = time;
        int interval = 0;
        int index = 0;

        ArrayList<Double> averageList = new ArrayList<Double>();
        ArrayList<Integer> averageCount = new ArrayList<Integer>();

        for (int i = 0; i < NUMBER_OF_INTERVALS; i++) {
            averageList.add(0.);
            averageCount.add(0);
        }

        while (time < maxTime) {
            time = Double.parseDouble(mTimeList.get(index));

            if (time > (startTime + (interval + 1) * INTERVAL_FOR_COMPUTING_AVG_SPD)) {
                interval++;
            }

            if (Double.parseDouble(mSpeedList.get(index)) > LOWER_SPEED_THRESHOLD) {
                averageList.set(interval, averageList.get(interval) + Double.parseDouble(mSpeedList.get(index)));
                averageCount.set(interval, averageCount.get(interval) + 1);
            }

            index++;
        }

        for (int i = 0; i < NUMBER_OF_INTERVALS; i++) {
            if (averageCount.get(i) > 0) {
                averageList.set(i, averageList.get(i) / averageCount.get(i));
            }
        }

        final ArrayList<Double> averageListCopy = averageList;

        final DecimalFormat df = new DecimalFormat("#.00");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SoccerGameActivity.this)
                        .setTitle("Average Speeds")
                        .setMessage("Average 1: " + df.format(averageListCopy.get(0)) + " um/s\n Average 2: " + df.format(averageListCopy.get(1)) + " um/s\n Average 3: " + df.format(averageListCopy.get(2)) + " um/s\n Average 4: " + df.format(averageListCopy.get(3)) + " um/s")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    private static int NUMBER_OF_MEASUREMENTS = 5;
    private static int INTERVAL_FOR_COMPUTING_AVG_SPD_2 = 3000;

    public void avgSpeedInterval2() {

        //Traverse the velocity list backwards adding up the values until you reach 2 seconds before the current time
        //Divide this average by the number of values counted
        //Dialog box showing the data and asking if you want to keep it
        //If yes -> run function that puts this into the list of averages
        //If no -> do nothing
        //

        int numValues = 1;
        double sumValues = 0;

        double time = Double.parseDouble(mTimeList.get(frameCount - 1));
        double minTime = time - INTERVAL_FOR_COMPUTING_AVG_SPD_2;

        int index = frameCount - 1;

        while (Double.parseDouble(mTimeList.get(index)) > minTime) {
            sumValues = sumValues + Double.parseDouble(mSpeedList.get(index));
            numValues++;
            index--;
        }

        final double average = sumValues / numValues;
        final int currentCount = mAverageVelocity1.size() + mAverageVelocity2.size() + 1;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SoccerGameActivity.this)
                        .setTitle("Do you want to keep this measurement?")
                        .setMessage("Measurement " + currentCount + "\n" + "Velocity: " + df.format(average) + " um/s")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                addNewAverage(average);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    private void addNewAverage(double average) {
        if (mAverageVelocity1.size() < NUMBER_OF_MEASUREMENTS) {
            mAverageVelocity1.add(average);
            if (mAverageVelocity1.size() == NUMBER_OF_MEASUREMENTS) {
                swapConditionsMessage();
            }
        } else if (mAverageVelocity2.size() < NUMBER_OF_MEASUREMENTS) {
            mAverageVelocity2.add(average);
        }

        if (mAverageVelocity2.size() == NUMBER_OF_MEASUREMENTS) {
            dataCollectionFinished = true;
        }
    }

    public void avgVelocityMessage() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SoccerGameActivity.this)
                        .setTitle("Average Speed Experiment")
                        .setMessage("In this experiment, you will calculate the average speeds of Euglena under different conditions. " +
                                "To start a measurement, tap on a Euglena. This Euglena will be tracked for " + df1.format(INTERVAL_FOR_COMPUTING_AVG_SPD_2 / 1000.0) + " seconds. " +
                                "After each measurement, you will be prompted to keep or discard the measurement. " +
                                "You will repeat this " + NUMBER_OF_MEASUREMENTS + " times per condition. " +
                                "After " + NUMBER_OF_MEASUREMENTS + " measurements, move on to the next condition.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                velocityCalculate = true;
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    //Takes an ArrayList of doubles, and returns the mean, maximum, minimum, standard deviation, and standard error of mean
    public List<Double> calculateStatistics(List<Double> dataList) {
        int n = dataList.size();
        double sum = 0;
        double sumDevSquared = 0;
        double min = 100000;
        double max = 0;

        for (Double i : dataList) {
            if (i > max) {
                max = i;
            }
            if (i < min) {
                min = i;
            }
            sum = sum + i;
        }

        double mean = sum / n;

        for (Double i : dataList) {
            sumDevSquared += Math.pow((mean - i), 2);
        }

        double standDev = Math.sqrt(sumDevSquared / (n - 1));
        double standErrMean = standDev / (Math.sqrt(n));

        List<Double> stats = new ArrayList<Double>();

        stats.add(mean);
        stats.add(max);
        stats.add(min);
        stats.add(standDev);
        stats.add(standErrMean);

        return stats;
    }

    public void avgVelocityFinishedMessage() {

        //convert to Final in case we need to call from uithread
        final List<Double> averageVelocity1Copy = mAverageVelocity1;
        final List<Double> averageVelocity2Copy = mAverageVelocity2;
        final List<Double> stats1 = calculateStatistics(mAverageVelocity1);
        final List<Double> stats2 = calculateStatistics(mAverageVelocity2);


        //Use these lines if you want to sort the velocities in increasing order
//        Collections.sort(averageVelocity1Copy);
//        Collections.sort(averageVelocity2Copy);


        //These lines find the faster condition
        int fasterCondition = 0;
        double fasterSpeed = 0.;
        double slowerSpeed = 0.;
        double fasterSEM = 0.;
        double slowerSEM = 0.;


        if (stats1.get(0) > stats2.get(0)) {
            fasterCondition = 1;
            fasterSpeed = stats1.get(0);
            fasterSEM = stats1.get(4);
            slowerSpeed = stats2.get(0);
            slowerSEM = stats2.get(4);
        } else {
            fasterCondition = 2;
            fasterSpeed = stats2.get(0);
            fasterSEM = stats2.get(4);
            slowerSpeed = stats1.get(0);
            slowerSEM = stats1.get(4);
        }

        final int finFasterCondition = fasterCondition;
        final double finFasterSpeed = fasterSpeed;
        final double finFasterSEM = fasterSEM;
        final double finSlowerSpeed = slowerSpeed;
        final double finSlowerSEM = slowerSEM;


        //If the final chart type is ComboChart, use these
        final CombinedData comboData = new CombinedData(new String[]{
                "1", "2", "3", "4", "5"
        });

        final CombinedData comboData2 = new CombinedData(new String[]{
                "1", "2", "3", "4", "5"
        });


        //Filling in data for the first bar chart
        ArrayList <BarDataSet> barData = new ArrayList<BarDataSet>();

        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        barEntries.add(new BarEntry(averageVelocity1Copy.get(0).floatValue(), 0));
        barEntries.add(new BarEntry(averageVelocity1Copy.get(1).floatValue(), 1));
        barEntries.add(new BarEntry(averageVelocity1Copy.get(2).floatValue(), 2));
        barEntries.add(new BarEntry(averageVelocity1Copy.get(3).floatValue(), 3));
        barEntries.add(new BarEntry(averageVelocity1Copy.get(4).floatValue(), 4));

        BarDataSet barSet = new BarDataSet(barEntries, "Condition 1");
        barSet.setColor(Color.rgb(60, 220, 78));
        barSet.setValueTextColor(Color.rgb(60, 220, 78));
        barSet.setValueTextSize(10f);
        barSet.setDrawValues(false);

        barData.add(barSet);
        barSet.setAxisDependency(YAxis.AxisDependency.LEFT);


        //Filling in data for the second bar chart
        BarData barData2 = new BarData();

        ArrayList<BarEntry> barEntries2 = new ArrayList<BarEntry>();
        barEntries2.add(new BarEntry(averageVelocity2Copy.get(0).floatValue(), 0));
        barEntries2.add(new BarEntry(averageVelocity2Copy.get(1).floatValue(), 1));
        barEntries2.add(new BarEntry(averageVelocity2Copy.get(2).floatValue(), 2));
        barEntries2.add(new BarEntry(averageVelocity2Copy.get(3).floatValue(), 3));
        barEntries2.add(new BarEntry(averageVelocity2Copy.get(4).floatValue(), 4));
        BarDataSet barSet2 = new BarDataSet(barEntries2, "Condition 2");
        barSet2.setColor(Color.rgb(220, 90, 78));
        barSet2.setValueTextColor(Color.rgb(220, 90, 78));
        barSet2.setValueTextSize(10f);
        barSet2.setDrawValues(false);
//        barData2.addDataSet(barSet2);
        barSet2.setAxisDependency(YAxis.AxisDependency.LEFT);

        barData.add(barSet2);

        //Create final bardata type to put into the uithread
        final BarData bData = new BarData(new String[]{
                "1", "2", "3", "4", "5"
        }, barSet);

        final BarData bData2 = new BarData(new String[]{
                "1", "2", "3", "4", "5"
        }, barSet2);

        //These lines add a pseudo error bar...
//        comboData.setData(barData);
//        comboData2.setData(barData2);

//        CandleData candData = new CandleData();

//        ArrayList<CandleEntry> candEntries = new ArrayList<CandleEntry>();
//        //stats.get(4) returns SEM. Use stats.get(3) to get standard deviation
//        candEntries.add(new CandleEntry(0, (float) (stats1.get(0) + stats1.get(4)), (float) (stats1.get(0) - stats1.get(4)), stats1.get(0).floatValue(), stats1.get(0).floatValue()));
//        candEntries.add(new CandleEntry(1, (float) (stats2.get(0) + stats2.get(4)), (float) (stats2.get(0) - stats2.get(4)), stats2.get(0).floatValue(), stats2.get(0).floatValue()));
//        CandleDataSet candSet = new CandleDataSet(candEntries, "SEM");
//        candSet.setColor(Color.rgb(80, 80, 80));
//        candSet.setBodySpace(1f);
//        candSet.setValueTextSize(10f);
//        candSet.setDrawValues(false);
//        candData.addDataSet(candSet);

//        comboData.setData(barData);
//        comboData.setData(candData);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(SoccerGameActivity.this)
                        .setView(getLayoutInflater().inflate(R.layout.velocity_experiment_message, null))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mAverageVelocity1.clear();
                                mAverageVelocity2.clear();
                            }
                        })
                        .show();


                //Set up the first bar chart

                BarChart comboChart = (BarChart) dialog.findViewById(R.id.chart_view_left);
                comboChart.setDescription("");
                comboChart.setBackgroundColor(Color.WHITE);
                comboChart.setDrawGridBackground(false);
                comboChart.setDrawBarShadow(false);

                //This line determines which order to draw the components of the graphs
//                comboChart.setDrawOrder(new CombinedChart.DrawOrder[]{
//                        CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
//                });

                YAxis yAxis = comboChart.getAxisLeft();
                yAxis.setDrawGridLines(true);
                yAxis.setAxisMaxValue(100);
                yAxis.setLabelCount(5, true);
                YAxis yAxisR = comboChart.getAxisRight();
                yAxisR.setEnabled(false);

                XAxis xAxis = comboChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setSpaceBetweenLabels(1);

                comboChart.setData(bData);
                Legend l = comboChart.getLegend();
                l.setEnabled(false);


                //Set up the second bar chart

                BarChart comboChart2 = (BarChart) dialog.findViewById(R.id.chart_view_right);
                comboChart2.setDescription("");
                comboChart2.setBackgroundColor(Color.WHITE);
                comboChart2.setDrawGridBackground(false);
                comboChart2.setDrawBarShadow(false);

                //This line determines which order to draw the components of the graphs
//                comboChart2.setDrawOrder(new CombinedChart.DrawOrder[]{
//                        CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
//                });

                YAxis yAxis2 = comboChart2.getAxisLeft();
                yAxis2.setDrawGridLines(true);
                yAxis2.setAxisMaxValue(100);
                yAxis2.setLabelCount(5, true);
                YAxis yAxis2R = comboChart2.getAxisRight();
                yAxis2R.setEnabled(false);

                XAxis xAxis2 = comboChart2.getXAxis();
                xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis2.setSpaceBetweenLabels(1);

                comboChart2.setData(bData2);
                Legend l2 = comboChart2.getLegend();
                l2.setEnabled(false);

                //Fill in data table

                TextView cond1Mean = (TextView) dialog.findViewById(R.id.textView11);
                TextView cond1Max = (TextView) dialog.findViewById(R.id.textView12);
                TextView cond1Min = (TextView) dialog.findViewById(R.id.textView13);
                TextView cond1Stdev = (TextView) dialog.findViewById(R.id.textView14);

                TextView cond2Mean = (TextView) dialog.findViewById(R.id.textView21);
                TextView cond2Max = (TextView) dialog.findViewById(R.id.textView22);
                TextView cond2Min = (TextView) dialog.findViewById(R.id.textView23);
                TextView cond2Stdev = (TextView) dialog.findViewById(R.id.textView24);

                cond1Mean.setText(" " + df1.format(stats1.get(0)) + " ");
                cond1Max.setText(" " + df1.format(stats1.get(1)) + " ");
                cond1Min.setText(" " + df1.format(stats1.get(2)) + " ");
                cond1Stdev.setText(" " + df1.format(stats1.get(3)) + " ");

                cond2Mean.setText(" " + df1.format(stats2.get(0)) + " ");
                cond2Max.setText(" " + df1.format(stats2.get(1)) + " ");
                cond2Min.setText(" " + df1.format(stats2.get(2)) + " ");
                cond2Stdev.setText(" " + df1.format(stats2.get(3)) + " ");
            }
        });
    }

    public void swapConditionsMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SoccerGameActivity.this)
                        .setTitle("Change conditions!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });
    }

    public void startFollowLine(View view) {
        if (mFollowLine) {
            clearFollowLineMode();
        } else {
            playSound(mSoundStartSong);
            mFollowLine = true;
            mBoxedRegion.setVisible(true);
            mBall.setVisible(false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button button = (Button) findViewById(R.id.line_follow_button);
                    button.setText("End");
                }
            });

        }
    }

    public void finishFollowLine() {

        int firstIndex = mFollowLineIndex;
        int lastIndex = mTimeList.size() - 1;

        int xPos = mTouchX;
        int yPos = mTouchY;

        List<String> subListPosX = new ArrayList<String>(mXPosList.subList(firstIndex, lastIndex));
        List<String> subListPosY = new ArrayList<String>(mYPosList.subList(firstIndex, lastIndex));

        List<Double> subListPosXDoub = new ArrayList<Double>();
        List<Double> subListPosYDoub = new ArrayList<Double>();
        for (String s : subListPosX) {
            subListPosXDoub.add(Double.parseDouble(s));
        }
        for (String s1 : subListPosY) {
            subListPosYDoub.add(Double.parseDouble(s1));
        }

        double score = calcFollowLineScore(xPos, yPos, subListPosXDoub, subListPosYDoub, firstIndex, lastIndex);
        followLineMessage(xPos, yPos, subListPosXDoub, subListPosYDoub, score);
    }

    public double calcFollowLineScore(int xPos, int yPos, List<Double> listPosX, List<Double> listPosY, int firstIndex, int lastIndex) {

//        //these values are taken from Pattern01.java. Change here if changed in Pattern01.java
//        int mLength1 = 600;
//        int mLength2 = 300;
//        int mLength3 = 500;
//
//        double dev = 0.;
//
//        int scoreMultiplier = 100000000;
//
//        for(int i = 0; i < listPosX.size(); i++)
//        {
//            //bin data into seperate conditions...
//
//            if(listPosY.get(i) > (yPos + mLength2/2))
//            {
//                if(listPosX.get(i) < (xPos + mLength1 - mLength2/2)){
//                    //condition 1
//                    dev += Math.pow(yPos - listPosY.get(i), 2);
//                }else {
//                    //condition 2
//                    dev += Math.pow(xPos + mLength1 - listPosX.get(i), 2);
//                }
//            }else
//            {
//                if(listPosX.get(i) < (xPos + mLength1 - mLength2/2)){
//                    //condition 3
//                    dev += Math.pow(yPos - mLength2 - listPosY.get(i), 2);
//                }else{
//                    //condition 2
//                    dev += Math.pow(xPos + mLength1 - listPosX.get(i), 2);
//                }
//            }
//        }
//        dev = dev/listPosX.size();
//
//        return scoreMultiplier/dev;

        return (Double.parseDouble(mTimeList.get(lastIndex)) - Double.parseDouble(mTimeList.get(firstIndex))) / 1000.;
    }

    public void followLineMessage(int xPos, int yPos, List<Double> listPosX, List<Double> listPosY, double score) {
        //show message here

        final int xPosFin = xPos;
        final int yPosFin = yPos;
        final List<Double> listPosXFin = new ArrayList<Double>(listPosX);
        final List<Double> listPosYFin = new ArrayList<Double>(listPosY);
        final double scoreFin = score;

        runOnUiThread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(SoccerGameActivity.this)
                        .setView(getLayoutInflater().inflate(R.layout.follow_the_line_message, null))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

                TextView textView = (TextView) dialog.findViewById(R.id.line_score_view);
                textView.setText("Time: " + df.format(scoreFin) + " seconds\nBalls eaten: " + mBallCountScore);
                mBallCountScore = 0;

                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#CD5C5C"));
                Paint paint2 = new Paint();
                paint2.setColor(Color.BLACK);
                Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bg);
                for (int i = 0; i < listPosXFin.size(); i++) {
                    canvas.drawCircle(listPosXFin.get(i).floatValue() / 3.3f, listPosYFin.get(i).floatValue() / 1.5f, 2, paint);
                }
                canvas.drawLine(xPosFin / 3.3f, yPosFin / 1.5f, (xPosFin + 600) / 3.3f, yPosFin / 1.5f, paint2);
                canvas.drawLine((xPosFin + 600) / 3.3f, yPosFin / 1.5f, (xPosFin + 600) / 3.3f, (yPosFin - 300) / 1.5f, paint2);
                canvas.drawLine((xPosFin + 600) / 3.3f, (yPosFin - 300) / 1.5f, (xPosFin + 100) / 3.3f, (yPosFin - 300) / 1.5f, paint2);
                View ll = (View) dialog.findViewById(R.id.line_results_view);
                ll.setBackground(new BitmapDrawable(getResources(), bg));
            }
        });
    }

    public void onBallEaten(ConsumableBall ball) {
        ball.setVisible(false);
        ball.setPhysical(false);
        ball.setIsEaten(true);
    }

    public void resetAllConsumableBalls() {
        mConsumableBall1.setVisible(false);
        mConsumableBall1.setPhysical(false);
        mConsumableBall1.setIsEaten(false);
        mConsumableBall2.setVisible(false);
        mConsumableBall2.setPhysical(false);
        mConsumableBall2.setIsEaten(false);
        mConsumableBall3.setVisible(false);
        mConsumableBall3.setPhysical(false);
        mConsumableBall3.setIsEaten(false);
        mConsumableBall4.setVisible(false);
        mConsumableBall4.setPhysical(false);
        mConsumableBall4.setIsEaten(false);
        mConsumableBall5.setVisible(false);
        mConsumableBall5.setPhysical(false);
        mConsumableBall5.setIsEaten(false);
        mConsumableBall6.setVisible(false);
        mConsumableBall6.setPhysical(false);
        mConsumableBall6.setIsEaten(false);
        mConsumableBall7.setVisible(false);
        mConsumableBall7.setPhysical(false);
        mConsumableBall7.setIsEaten(false);
        mConsumableBall8.setVisible(false);
        mConsumableBall8.setPhysical(false);
        mConsumableBall8.setIsEaten(false);
        mConsumableBall9.setVisible(false);
        mConsumableBall9.setPhysical(false);
        mConsumableBall9.setIsEaten(false);
        mConsumableBall10.setVisible(false);
        mConsumableBall10.setPhysical(false);
        mConsumableBall10.setIsEaten(false);
        mConsumableBall11.setVisible(false);
        mConsumableBall11.setPhysical(false);
        mConsumableBall11.setIsEaten(false);
        mConsumableBall12.setVisible(false);
        mConsumableBall12.setPhysical(false);
        mConsumableBall12.setIsEaten(false);
        mConsumableBall13.setVisible(false);
        mConsumableBall13.setPhysical(false);
        mConsumableBall13.setIsEaten(false);
        mConsumableBall14.setVisible(false);
        mConsumableBall14.setPhysical(false);
        mConsumableBall14.setIsEaten(false);
    }

    public boolean areAllBallsEaten() {
        return (mConsumableBall1.isEaten() && mConsumableBall2.isEaten() && mConsumableBall3.isEaten() && mConsumableBall4.isEaten() && mConsumableBall5.isEaten() &&
                mConsumableBall6.isEaten() && mConsumableBall7.isEaten() && mConsumableBall8.isEaten() && mConsumableBall9.isEaten() && mConsumableBall10.isEaten() &&
                mConsumableBall11.isEaten() && mConsumableBall12.isEaten() && mConsumableBall13.isEaten() && mConsumableBall14.isEaten());
    }

    public void clearFollowLineMode() {
        mBallCountScore = getNumBallsConsumed();
        resetAllConsumableBalls();
        mBall.setVisible(true);
        mPattern.setVisible(false);
        mPMan.setVisible(false);
        mFollowLine = false;
        stopSong();
        playSound(mSoundEndSong);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button button = (Button) findViewById(R.id.line_follow_button);
                button.setText("PacEuglena");
            }
        });
        finishFollowLine();
    }

    public void onEditHSVPressed(View view) {

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SoccerGameActivity.this)
                        .setView(input)
                        .setTitle("Change HSV")
                        .setMessage("Default high: 96, 200, 255\nDefault low: 50, 50, 0")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mInputText = input.getText().toString();
                                String[] ar = mInputText.split(",");
                                UPPER_HSV_THRESHOLD = new Scalar(Double.parseDouble(ar[0]), Double.parseDouble(ar[0]), Double.parseDouble(ar[0]));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    public void onEditHSVPressed2(View view) {

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SoccerGameActivity.this)
                        .setView(input)
                        .setTitle("Change HSV")
                        .setMessage("Default high: 96, 200, 255\nDefault low: 50, 50, 0")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mInputText = input.getText().toString();
                                String[] ar = mInputText.split(",");
                                LOWER_HSV_THRESHOLD = new Scalar(Double.parseDouble(ar[0]), Double.parseDouble(ar[0]), Double.parseDouble(ar[0]));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    public int getNumBallsConsumed() {
        int count = 0;

        if (mConsumableBall1.isEaten()) {
            count++;
        }
        if (mConsumableBall2.isEaten()) {
            count++;
        }
        if (mConsumableBall3.isEaten()) {
            count++;
        }
        if (mConsumableBall4.isEaten()) {
            count++;
        }
        if (mConsumableBall5.isEaten()) {
            count++;
        }
        if (mConsumableBall6.isEaten()) {
            count++;
        }
        if (mConsumableBall7.isEaten()) {
            count++;
        }
        if (mConsumableBall8.isEaten()) {
            count++;
        }
        if (mConsumableBall9.isEaten()) {
            count++;
        }
        if (mConsumableBall10.isEaten()) {
            count++;
        }
        if (mConsumableBall11.isEaten()) {
            count++;
        }
        if (mConsumableBall12.isEaten()) {
            count++;
        }
        if (mConsumableBall13.isEaten()) {
            count++;
        }
        if (mConsumableBall14.isEaten()) {
            count++;
        }

        return count;
    }

    /*
    Below is code for the trace generating experiment
     */

    final private int TIME_BEFORE_LIGHT_STIMULUS = 1750;
    final private int TIME_AFTER_LIGHT_STIMULUS = 4000;
    final private int HUMAN_REACTION_TIME = 250;

    public void onTraceGeneratorPressed(View view) {
        //Display message explaining the steps
        //First, need to direct light and select Euglena
        //Second, need to change light direction after noise...
        //After set amount of time, the trace is automatically saved and the activity ends
        mTraceEuglena = true;
        mTraceEuglenaTapped = true;

    }

    public void traceFinishedMessage(List<Double> listX, List<Double> listY) {

        final List<Double> listPosXFin = new ArrayList<>(listX);
        final List<Double> listPosYFin = new ArrayList<>(listY);

        runOnUiThread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(SoccerGameActivity.this)
                        .setView(getLayoutInflater().inflate(R.layout.follow_the_line_message, null))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

                TextView textView = (TextView) dialog.findViewById(R.id.line_score_view);
                textView.setVisibility(View.GONE);
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                Paint paint2 = new Paint();
                paint2.setColor(Color.LTGRAY);
                Paint paint3 = new Paint();
                paint3.setColor(Color.GREEN);
                Paint paint4 = new Paint();
                paint4.setColor(Color.RED);
                Paint paint5 = new Paint();
                paint5.setColor(Color.YELLOW);
                Paint paintBlue = new Paint();
                paintBlue.setColor(Color.BLUE);
                Bitmap bg = Bitmap.createBitmap(640, 360, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bg);

                canvas.drawRect(0, 0, 640, 360, paint2);

                for (int i = 0; i < mTraceExpMidIndex - mTraceExpStartIndex; i++) {
                    canvas.drawCircle(listPosXFin.get(i).floatValue() / 2f, listPosYFin.get(i).floatValue() / 2f, 2, paint5);
                }

                for (int i = mTraceExpMidIndex - mTraceExpStartIndex; i < mTraceExpEndIndex - mTraceExpStartIndex; i++) {
                    canvas.drawCircle(listPosXFin.get(i).floatValue() / 2f, listPosYFin.get(i).floatValue() / 2f, 2, paintBlue);
                }

//                canvas.drawCircle(listPosXFin.get(0).floatValue()/2f, listPosYFin.get(0).floatValue()/2f, 5, paint3);
//                canvas.drawCircle(listPosXFin.get((mTraceExpEndIndex-mTraceExpStartIndex)/2).floatValue()/2f, listPosYFin.get((mTraceExpEndIndex-mTraceExpStartIndex)/2).floatValue()/2f, 5, paint5);
//                canvas.drawCircle(listPosXFin.get(listPosXFin.size() - 1).floatValue() / 2f, listPosYFin.get(listPosXFin.size() - 1).floatValue() / 2f, 5, paint4);

                canvas.drawLine(450, 300, 530, 300, paint);
                canvas.drawText("100um", 470, 320, paint);

//                canvas.drawText("Start", 590, 20, paint3);
//                canvas.drawText("Turn", 590, 40, paint5);
//                canvas.drawText("Stop", 590, 60, paint4);

                View view = (View) dialog.findViewById(R.id.line_results_view);
                view.setBackground(new BitmapDrawable(getResources(), bg));


                //Code to save file image...

//                view.setDrawingCacheEnabled(true);
//                Bitmap b = view.getDrawingCache();
//
//                saveTraceImage(b);
            }
        });
    }

//    public void saveTraceImage(Bitmap b){
//        File traceFile = new File(((Context) this).getExternalFilesDir(null), "trace.jpg");
//
//        try {
//            b.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream("/data/trace.jpg"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    public void traceExpMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(SoccerGameActivity.this)
                        .setTitle("Euglena Trace Experiment")
                        .setMessage("1) Apply constant light stimulus and tap to select a responsive organism.\n\n2) Change the direction of the light stimulus when you hear the beep.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mTraceEuglena = true;
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    public List<Double> convertStringListToDouble(List<String> list, int startIndex, int endIndex) {
        List<Double> returnList = new ArrayList<>();

        for (int i = startIndex; i < endIndex; i++) {
            returnList.add(Double.parseDouble(list.get(i)));
        }

        return returnList;
    }

    public void createTraceDataFile(int indexStart, int indexEnd, String title) throws IOException {
        List<String> xList = new ArrayList<>();
        List<String> yList = new ArrayList<>();

        xList = mXPosList.subList(indexStart, indexEnd);
        yList = mYPosList.subList(indexStart, indexEnd);

        String xPos = convertListToString(xList);
        String yPos = convertListToString(yList);

        try {
            // Creates a trace file in the primary external storage space of the
            // current application.
            // If the file does not exists, it is created.
            File traceFile = new File(((Context) this).getExternalFilesDir(null), title + ".txt");
            if (!traceFile.exists())
                traceFile.createNewFile();
            // Adds a line to the trace file
            BufferedWriter writer = new BufferedWriter(new FileWriter(traceFile, true /*append*/));
            writer.write(xPos + "\n\n" + yPos + "\n\nEND");
            writer.close();
            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile((Context) (this),
                    new String[]{traceFile.toString()},
                    null,
                    null);
        } catch (IOException e) {
            Log.e("com.BioticGame", "Unable to write to the Data.txt file.");
        }
    }

    /*
    End code for trace generating experiment
     */

    /*
    Code for taking screenshot
     */

//    public static Bitmap screenshot(Activity activity){
//        View view = activity.getWindow().getDecorView();
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//
//        Bitmap bitmap = view.getDrawingCache();
//        Rect rect = new Rect();
////        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//
//    }
}