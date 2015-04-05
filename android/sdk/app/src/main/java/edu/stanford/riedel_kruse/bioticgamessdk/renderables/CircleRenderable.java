package edu.stanford.riedel_kruse.bioticgamessdk.renderables;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;

/**
 * Created by dchiu on 4/2/15.
 */
public class CircleRenderable extends SimpleRenderable {
    protected int mRadius;

    public CircleRenderable(GameObject gameObject, int radius, Scalar color, int thickness) {
        super(gameObject, color, thickness);

        mRadius = radius;
    }

    public int radius() {
        return mRadius;
    }

    public void setRadius(int newRadius) {
        mRadius = newRadius;
    }

    @Override
    public void draw(Mat frame) {
        Core.circle(frame, mGameObject.position(), mRadius, mColor, mThickness);
    }
}
