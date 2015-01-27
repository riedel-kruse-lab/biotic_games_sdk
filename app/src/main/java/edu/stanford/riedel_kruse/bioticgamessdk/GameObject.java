package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * The DisplayObject class is the parent class for all drawable objects.
 */
public abstract class GameObject {
    /**
     * Instance variable which keeps track of the position of the DisplayObject on the screen.
     */
    protected Point mPosition;
    /**
     * Instance variable flag which indicates whether or not this DisplayObject should interact
     * physically with other DisplayObjects (e.g. collisions).
     */
    protected boolean mIsPhysical;

    /**
     * Constructor for the DisplayObject class.
     * @param position the position of the DisplayObject.
     * @param isPhysical whether or not this DisplayObject should interact physically with other
     *                   DisplayObjects.
     */
    public GameObject(Point position, boolean isPhysical) {
        mPosition = position;
        mIsPhysical = isPhysical;
    }

    /**
     * Sets a new position for this DisplayObject.
     * @param newPosition the new position of the DisplayObject.
     */
    public void setPosition(Point newPosition) {
        mPosition = newPosition;
    }

    /**
     * Whether or not this DisplayObject should interact physically with other DisplayObjects.
     * @return
     */
    public boolean isPhysical() {
        return mIsPhysical;
    }

    /**
     * Draws this DisplayObject to the given frame. Since no origin point is specified, this draws
     * without translating the object at all.
     * @param frame the frame to draw the DisplayObject on.
     */
    public void draw(Mat frame) {
        draw(frame, new Point(0, 0));
    }

    /**
     * Draws this DisplayObject to the given frame.
     * @param frame the frame to draw the DisplayObject on.
     * @param offset the offset to draw at (translates all subsequent draw calls)
     */
    public abstract void draw(Mat frame, Point offset);

    public boolean intersects(GameObject obj) {
        return CollisionUtil.intersects(this, obj);
    }
}
