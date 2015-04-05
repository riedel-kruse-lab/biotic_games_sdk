package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Mat;

/**
 * Abstract class which represents a renderable item. When attached to a GameObject, gives that
 * GameObject an appearance to the user.
 */
public abstract class Renderable {
    /**
     * The game object associated with this Renderable.
     */
    protected GameObject mGameObject;

    public Renderable(GameObject gameObject) {
        mGameObject = gameObject;
    }

    /**
     * Draws this Renderable to the given frame.
     * @param frame the frame to draw the Renderable on.
     */
    public abstract void draw(Mat frame);
}
