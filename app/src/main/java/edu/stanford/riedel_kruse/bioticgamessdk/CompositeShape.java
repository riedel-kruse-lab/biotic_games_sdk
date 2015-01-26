package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.List;

/**
 * The CompositeShape class describes shapes composed of other shapes.
 */
public class CompositeShape extends DisplayObject {
    /**
     * A list of the shapes that comprise this CompositeShape.
     */
    private List<Shape> mShapes;

    /**
     * Constructor of the CompositeShape class.
     * @param position the position of the CompositeShape.
     * @param shapes a list of the shapes that comprise this CompositeShape.
     */
    public CompositeShape(Point position, List<Shape> shapes) {
        super(position, false);

        mShapes = shapes;
        mIsPhysical = listContainsPhysicalShape(shapes);
    }

    /**
     * Determines whether or not there are shapes in the given list that are physical.
     * @param shapes a list of shapes to check.
     * @return true if at least one shape in the list is physical, false otherwise.
     */
    private boolean listContainsPhysicalShape(List<Shape> shapes) {
        for (Shape shape : shapes) {
            if (shape.isPhysical()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Mat frame, Point offset) {
        for (Shape shape : mShapes) {
            shape.draw(frame, MathUtil.addPoints(offset, mPosition));
        }
    }
}
