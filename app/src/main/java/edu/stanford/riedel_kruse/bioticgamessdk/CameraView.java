package edu.stanford.riedel_kruse.bioticgamessdk;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;

/**
 * The CameraView class subclasses JavaCameraView and provides an easy way to edit the camera's
 * parameters.
 */
public class CameraView extends JavaCameraView {

    /**
     * The stored camera parameters to set. Needed in case the camera has not yet been initialized
     * when setCameraParameters() is called.
     */
    private Camera.Parameters mCameraParameters;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean connectCamera(int width, int height) {
        boolean cameraConnected = super.connectCamera(width, height);

        if (cameraConnected && mCameraParameters != null) {
            mCamera.setParameters(mCameraParameters);
        }

        return cameraConnected;
    }

    /**
     * Sets the parameters of the camera to allow control over things like camera zoom level.
     * @param parameters the Camera.Parameters object containing data about the camera's parameters.
     */
    public void setCameraParameters(Camera.Parameters parameters) {
        mCameraParameters = parameters;
        if (mCamera != null) {
            mCamera.setParameters(mCameraParameters);
        }
    }
}
