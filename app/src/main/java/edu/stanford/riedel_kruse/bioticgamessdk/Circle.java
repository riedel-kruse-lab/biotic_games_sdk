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

    @Override
    public void draw(Mat frame, Point offset) {
        Core.circle(frame, MathUtil.addPoints(offset, mPosition), mRadius, mColor, mThickness);
    }
}
