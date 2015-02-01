package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

/**
 * The Circle class describes a basic circular shape.
 */
public class Circle extends Shape {
    /**
     * Instance variable for the radius of the circle.
     */
    private int mRadius;

    /**
     * Constructor for the Circle class.
     * @param center the location of the center of the circle.
     * @param radius the radius of the circle.
     * @param color the color of the circle specified as an RGB value.
     * @param thickness the thickness of the circle. Negative values specify that the circle should
     *                  be filled.
     * @param isPhysical whether or not this Circle should interact with other DisplayObjects
     *                   physically
     */
    public Circle(Point center, int radius, Scalar color, int thickness, boolean isPhysical) {
        super(center, color, thickness, isPhysical);

        mRadius = radius;
    }

    /**
     * Getter for the radius of the Circle.
     * @return the radius of the Circle.
     */
    public int radius() {
        return mRadius;
    }

    /**
     * Getter for the point at the center of the Circle.
     * @return a point which is at the center of the Circle.
     */
    public Point center() {
        // Circle really just uses the mPosition property for the center, but aliases it as the
        // center so that it is slightly more intuitive to clients of this class.
        return mPosition;
    }

    @Override
    public void draw(Mat frame, Point offset) {
        Core.circle(frame, MathUtil.addPoints(offset, mPosition), mRadius, mColor, mThickness);
    }

    @Override
    public boolean contains(Point point) {
        // Check if the distance to the point is less than or equal to the radius of the circle.
        return Math.pow(point.x - mPosition.x, 2) + Math.pow(point.y - mPosition.y, 2) <=
                Math.pow(mRadius, 2);
    }
}
