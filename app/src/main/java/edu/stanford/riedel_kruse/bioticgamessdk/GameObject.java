package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * The GameObject class is the parent class for all game objects. A GameObject describes how an
 * entity in a game should be drawn and how it should be modeled physically.
 */
public abstract class GameObject {
    /**
     * Instance variable which keeps track of the position of the GameObject on the screen.
     */
    protected Point mPosition;
    /**
     * Instance variable flag which indicates whether or not this GameObject should interact
     * physically with other GameObjects (e.g. collisions).
     */
    protected boolean mIsPhysical;

    /**
     * Constructor for the GameObject class.
     * @param position the position of the GameObject.
     * @param isPhysical whether or not this GameObject should interact physically with other
     *                   GameObjects.
     */
    public GameObject(Point position, boolean isPhysical) {
        mPosition = position;
        mIsPhysical = isPhysical;
    }

    /**
     * Sets a new position for this GameObject.
     * @param newPosition the new position of the GameObject.
     */
    public void setPosition(Point newPosition) {
        mPosition = newPosition;
    }

    /**
     * Whether or not this GameObject should interact physically with other GameObjects.
     * @return true if this GameObject should interact physically with other GameObjects, false
     *         otherwise.
     */
    public boolean isPhysical() {
        return mIsPhysical;
    }

    /**
     * Draws this GameObject to the given frame. Since no origin point is specified, this draws
     * without translating the object at all.
     * @param frame the frame to draw the GameObject on.
     */
    public void draw(Mat frame) {
        draw(frame, new Point(0, 0));
    }

    /**
     * Draws this GameObject to the given frame.
     * @param frame the frame to draw the GameObject on.
     * @param offset the offset to draw at (translates all subsequent draw calls)
     */
    public abstract void draw(Mat frame, Point offset);

    public boolean intersects(GameObject obj) {
        return CollisionUtil.collided(this, obj);
    }
}
