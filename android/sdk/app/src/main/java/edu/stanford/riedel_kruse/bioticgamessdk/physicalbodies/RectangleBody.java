package edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.MathUtil;
import edu.stanford.riedel_kruse.bioticgamessdk.PhysicalBody;

/**
 * The RectangleBody class describes a basic rectangular shape.
 */
public class RectangleBody extends PhysicalBody {
    /**
     * An OpenCV rectangle object. Use this as the backing for a rectangle because OpenCV has
     * several convenient functions already implemented for checking whether or not a rectangle
     * contains points and the like.
     */
    private Rect mRect;

    /**
     * Constructor for the RectangleBody class.
     * @param gameObject the GameObject associated with this RectangleBody
     * @param width the width of the rectangle.
     * @param height the height of the rectangle.
     */
    public RectangleBody(GameObject gameObject, int width, int height) {
        super(gameObject);

        mRect = new Rect(0, 0, width, height);
    }

    /**
     * Getter for the width property of the Rectangle.
     * @return the width of the rectangle
     */
    public int width() {
        return mRect.width;
    }

    /**
     * Getter for the height property of the Rectangle.
     * @return the height of the rectangle
     */
    public int height() {
        return mRect.height;
    }

    /**
     * Retrieves the center point of the Rectangle.
     * @return a point which is at the center of the Rectangle.
     */
    public Point center() {
        Point center = topLeft();
        center.x += width() / 2.0;
        center.y += height() / 2.0;

        return center;
    }

    /**
     * Returns the top left corner of the rectangle.
     * @return the top left corner of the rectangle.
     */
    public Point topLeft() {
        return MathUtil.addPoints(mRect.tl(), mGameObject.position());
    }

    /**
     * Returns the bottom right corner of the rectangle.
     * @return the bottom right corner of the rectangle.
     */
    public Point bottomRight() {
        return MathUtil.addPoints(mRect.br(), mGameObject.position());
    }

    @Override
    public boolean contains(Point point) {
        Rect rect = new Rect((int) point.x, (int) point.y, width(), height());
        return rect.contains(point);
    }
}
