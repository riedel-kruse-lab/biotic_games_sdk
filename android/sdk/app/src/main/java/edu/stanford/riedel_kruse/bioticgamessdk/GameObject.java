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
     * Instance variable flag which indicates whether or not this GameObject should be drawn.
     */
    protected boolean mIsVisible;

    /**
     * Constructor for the GameObject class.
     * @param position the position of the GameObject.
     * @param isPhysical whether or not this GameObject should interact physically with other
     *                   GameObjects.
     */
    public GameObject(Point position, boolean isPhysical) {
        mPosition = position;
        mIsPhysical = isPhysical;
        mIsVisible = true;
    }

    /**
     * Sets a new position for this GameObject.
     * @param newPosition the new position of the GameObject.
     */
    public void setPosition(Point newPosition) {
        mPosition = newPosition;
    }

    /**
     * Gets a copy of the position of this GameObject.
     * @return a copy of the position of this GameObject.
     */
    public Point position() {
        return mPosition.clone();
    }

    /**
     * Gets the x-value of this GameObject's position.
     * @return the x-value of this GameObject's position.
     */
    public double x() {
        return mPosition.x;
    }

    /**
     * Gets the y-value of this GameObject's position.
     * @return the y-value of this GameObject's position.
     */
    public double y() {
        return mPosition.y;
    }

    /**
     * Sets the x-value of this GameObject's position.
     * @param newX the new x-value to set.
     */
    public void setX(double newX) {
        mPosition.x = newX;
    }

    /**
     * Sets the y-value of this GameObject's position.
     * @param newY the new y-value to set.
     */
    public void setY(double newY) {
        mPosition.y = newY;
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
     * Setter for the isPhysical property of this GameObject. GameObjects involved in collisions
     * that are made non-physical will no longer activate the collision callback.
     * @param isPhysical the new value for whether or not this GameObject is physical.
     */
    public void setPhysical(boolean isPhysical) {
        mIsPhysical = isPhysical;
    }

    /**
     * Getter for the visibility property of this GameObject. True means that this GameObject is
     * being drawn to the screen. False means that this GameObject is currently being hidden from
     * view.
     * @return true if this GameObject is being drawn; false otherwise.
     */
    public boolean isVisible() {
        return mIsVisible;
    }

    /**
     * Setter for the visibility property of this GameObject. True means that this GameObject is
     * being drawn to the screen. False means that this GameObject is currently being hidden from
     * view.
     * @param isVisible the new value for whether or not this GameObject is visible.
     */
    public void setVisible(boolean isVisible) {
        mIsVisible = isVisible;
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
