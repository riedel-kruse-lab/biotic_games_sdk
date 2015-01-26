package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.List;

/**
 * The Shape class models basic shapes like Rectangles and Circles.
 */
public abstract class Shape extends DisplayObject {
    /**
     * Default color to use for drawing if no color is set. This corresponds to black.
     */
    public static final Scalar DEFAULT_COLOR = new Scalar(0, 0, 0);

    /**
     * The color to draw this shape. Specified as an RGB value using an OpenCV Scalar object.
     */
    protected Scalar mColor;
    /**
     * The thickness with which to draw this shape. Negative values specify that the shape should
     * be filled.
     */
    protected int mThickness;

    /**
     * Constructor for the Shape class.
     * @param position the position of the Shape.
     * @param color the color of the Shape, specified as an RGB value.
     * @param thickness the thickness with which to draw this shape. Negative values specify that
     *                  the shape should be filled.
     * @param isPhysical whether or not this Shape should interact with other DisplayObjects
     *                   physically.
     */
    public Shape(Point position, Scalar color, int thickness, boolean isPhysical) {
        super(position, isPhysical);

        mColor = color;
        if (mColor == null) {
            mColor = DEFAULT_COLOR;
        }

        mThickness = thickness;
    }
}
