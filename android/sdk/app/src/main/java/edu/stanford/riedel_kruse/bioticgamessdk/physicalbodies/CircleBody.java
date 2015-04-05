package edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies;

import org.opencv.core.Point;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.PhysicalBody;

/**
 * Created by dchiu on 4/2/15.
 */
public class CircleBody extends PhysicalBody {
    protected int mRadius;

    public CircleBody(GameObject gameObject, int radius) {
        super(gameObject);
        mRadius = radius;
    }

    /**
     * Getter for the point at the center of the Circle.
     * @return a point which is at the center of the Circle.
     */
    public Point center() {
        // Circle really just uses the mPosition property for the center, but aliases it as the
        // center so that it is slightly more intuitive to clients of this class.
        return mGameObject.position();
    }

    public int radius() {
        return mRadius;
    }

    public void setRadius(int newRadius) {
        mRadius = newRadius;
    }

    @Override
    public boolean contains(Point point) {
        Point position = mGameObject.position();
        return Math.pow(point.x - position.x, 2) + Math.pow(point.y - position.y, 2) <=
                Math.pow(mRadius, 2);
    }
}
