package edu.stanford.riedel_kruse.bioticgamessdk;

import android.app.Activity;
import android.os.Bundle;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Run first-time initializations.
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disableCameraView();
    }

    @Override
    public void onPause() {
        super.onPause();
        disableCameraView();
    }

    @Override
    public void onResume() {
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
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        return processCameraFrame(inputFrame.rgba());
    }

    /**
     * Processes a frame from the camera. This is the function where it is appropriate to do image
     * processing tasks like finding all Euglena on the screen or altering the appearance of the
     * image to be displayed on the main camera view.
     * @param inputFrame an RGBA image matrix which contains the current frame from the camera
     * @return the processed frame which will be displayed on the main camera view
     */
    protected abstract Mat processCameraFrame(Mat inputFrame);
}