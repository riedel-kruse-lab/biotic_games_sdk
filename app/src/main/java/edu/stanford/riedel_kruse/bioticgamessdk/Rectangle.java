package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

/**
 * The Rectangle class describes a basic rectangular shape.
 */
public class Rectangle extends Shape {
    /**
     * An OpenCV rectangle object. Use this as the backing for a rectangle because OpenCV has
     * several convenient functions already implemented for checking whether or not a rectangle
     * contains points and the like.
     */
    private Rect mRect;

    /**
     * Constructor for the Rectangle class.
     * @param position the position of the top left corner of the rectangle.
     * @param width the width of the rectangle.
     * @param height the height of the rectangle.
     * @param color the color of the rectangle expressed as an RGB value
     * @param thickness the thickness of the rectangle. Negative values specify that the rectangle
     *                  should be filled.
     * @param isPhysical whether or not the rectangle should interact physically with other
     *                   DisplayObjects.
     */
    public Rectangle(Point position, int width, int height, Scalar color, int thickness,
                     boolean isPhysical) {
        super(position, color, thickness, isPhysical);

        mRect = new Rect(0, 0, width, height);
    }

    /**
     * Returns the top left corner of the rectangle.
     * @return the top left corner of the rectangle.
     */
    public Point topLeft() {
        return MathUtil.addPoints(mRect.tl(), mPosition);
    }

    /**
     * Returns the bottom right corner of the rectangle.
     * @return the bottom right corner of the rectangle.
     */
    public Point bottomRight() {
        return MathUtil.addPoints(mRect.br(), mPosition);
    }

    @Override
    public void draw(Mat frame, Point offset) {
        Core.rectangle(frame, MathUtil.addPoints(offset, topLeft()),
                MathUtil.addPoints(offset, bottomRight()), mColor, mThickness);
    }
}
