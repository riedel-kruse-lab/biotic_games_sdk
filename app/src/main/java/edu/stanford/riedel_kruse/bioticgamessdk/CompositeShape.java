package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.Arrays;
import java.util.List;

/**
 * The CompositeShape class describes shapes composed of other shapes.
 */
public class CompositeShape extends GameObject {
    /**
     * A list of the shapes that comprise this CompositeShape.
     */
    private List<Shape> mChildren;

    /**
     * Constructor of the CompositeShape class.
     * @param position the position of the CompositeShape.
     * @param children a list of the shapes that comprise this CompositeShape.
     */
    public CompositeShape(Point position, List<Shape> children) {
        super(position, false);

        mChildren = children;

        // For a CompositeShape, mIsPhysical is set to true so long as any child is physical.
        // Basically, this allows us to optimize and not do collision detection on the entire
        // CompositeShape if it contains no physical children, but if it does, then we handle
        // collision normally.
        mIsPhysical = listContainsPhysicalShape(mChildren);

        // Translate each of the children by the CompositeShape's position.
        // TODO: The way this is written, it would be confusing to get back a child added to a
        // CompositeShape because its coordinates will be relative to the global context even though
        // the original coordinates provided were relative to the parent.
        for (Shape child : mChildren) {
            Point childPosition = child.position();
            child.setPosition(new Point(childPosition.x + mPosition.x,
                    childPosition.y + mPosition.y));
        }
    }

    @Override
    public void setPosition(Point newPosition) {
        for (Shape child : mChildren) {
            Point childPosition = child.position();

            // Subtract out the current position from each child, add the new position.
            child.setPosition(new Point(childPosition.x - mPosition.x + newPosition.x,
                    childPosition.y - mPosition.y + newPosition.x));
        }

        super.setPosition(newPosition);
    }

    public CompositeShape(Point position, Shape... children) {
        this(position, Arrays.asList(children));
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

    /**
     * Getter for the list of child shapes that comprise this CompositeShape.
     * @return a List of the Shapes that comprise this CompositeShape.
     */
    public List<Shape> children() {
        return mChildren;
    }

    @Override
    public void draw(Mat frame, Point offset) {
        // Draw each child.
        for (Shape shape : mChildren) {
            shape.draw(frame, MathUtil.addPoints(offset, mPosition));
        }
    }
}
