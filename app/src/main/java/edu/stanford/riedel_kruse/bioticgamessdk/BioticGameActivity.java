package edu.stanford.riedel_kruse.bioticgamessdk;

import android.app.Activity;
import android.os.Bundle;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * The BioticGameActivity class serves as the base class from which all Biotic Games are created.
 * It makes all of the appropriate calls to OpenCV in order to setup a game that uses the camera to
 * track Euglena.
 */
public abstract class BioticGameActivity extends Activity implements
        CameraBridgeViewBase.CvCameraViewListener2
{
    /**
     * The live camera feed view.
     */
    // TODO: Should provide a subclass of JavaCameraView which allows passing of camera parameters
    // in order to do things like change the camera zoom level, etc.
    private JavaCameraView mCameraView;
    /**
     * Stores the timestamp for the previous frame. Used to compute time deltas between frames.
     */
    private long mLastTimestamp;
    /**
     * Flag to determine whether or not onCameraFrame has been called before. True means this is the
     * first time onCameraFrame has been called. False means it has been called before.
     */
    private boolean mFirstFrame;

    private List<GameObject> mGameObjects;

    private List<CollisionCallback> mCollisionCallbacks;

    /**
     * Custom OpenCV loader callback called once OpenCV has been loaded. This is the right place to
     * do initialization of OpenCV objects.
     */
    private BaseLoaderCallback mOpenCVLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    // Turn on the camera view so it starts receiving frames.
                    mCameraView.enableView();
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    /**
     * Initialization function to setup all public and private variables. This is the right place to
     * do all first-time initialization.
     */
    private void init() {
        // Call abstract method getCameraViewResourceId() to set the camera view. Subclasses of this
        // class must override getCameraViewResourceId() in order to specify where the main camera
        // view for the game is.
        mCameraView = (JavaCameraView) findViewById(getCameraViewResourceId());

        // Receive camera view listener callbacks for this camera view like onCameraFrame.
        mCameraView.setCvCameraViewListener(this);

        // Set mFirstFrame to true so that the first time we hit onCameraFrame it is true.
        mFirstFrame = true;

        mGameObjects = new ArrayList<GameObject>();
        mCollisionCallbacks = new ArrayList<CollisionCallback>();
    }

    /**
     * Turns of the camera view. Important to call this when the activity gets paused or destroyed
     * so that we don't keep hardware resources busy that other apps might want to use.
     */
    private void disableCameraView() {
        if (mCameraView != null) {
            // Stop receiving camera view frames.
            mCameraView.disableView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Run first-time initializations.
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disableCameraView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableCameraView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mOpenCVLoaderCallback);
    }

    /**
     * Retrieves the Android resource ID for the camera view to use as the main camera feed.
     * Subclasses should define a camera view in their activity layouts, then return the resource
     * ID for that camera view in this function. The BioticGameActivity class will take care of the
     * rest of the setup for the camera view.
     * @return the Android resource ID for the camera view to use as the main camera feed.
     */
    protected abstract int getCameraViewResourceId();

    @Override
    public void onCameraViewStarted(int width, int height)
    {

    }

    @Override
    public void onCameraViewStopped()
    {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame frame)
    {
        long timeDelta = 0;
        long currentTimestamp = System.currentTimeMillis();

        if (mFirstFrame) {
            mFirstFrame = false;
        }
        else {
            timeDelta = currentTimestamp - mLastTimestamp;
        }
        mLastTimestamp = currentTimestamp;

        Mat rgbaFrame = frame.rgba();
        updateGame(rgbaFrame, timeDelta);
        processCollisions();
        drawGame(rgbaFrame);

        return rgbaFrame;
    }

    protected void addGameObject(GameObject obj) {
        mGameObjects.add(obj);
    }

    protected void addCollisionCallback(CollisionCallback callback) {
        mCollisionCallbacks.add(callback);
    }

    /**
     * Updates the game model and runs game logic. This is the appropriate place to update the state
     * of your game based on the locations of things in the included frame.
     * @param frame an RGBA image matrix which contains the current frame from the camera
     * @param timeDelta the amount of time elapsed between the current call of updateGame and the
     *                  last one. If this is the first call to updateGame, timeDelta will be 0.
     */
    protected abstract void updateGame(Mat frame, long timeDelta);

    private void processCollisions() {
        for (CollisionCallback callback : mCollisionCallbacks) {
            callback.process();
        }
    }

    /**
     * Draws the game onto the provided frame and returns it.
     * @param frame an RGBA image matrix which contains the current frame from the camera
     */
    protected void drawGame(Mat frame) {
        for (GameObject obj : mGameObjects) {
            obj.draw(frame);
        }
    }
}