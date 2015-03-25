package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
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
     * Constructor for a CompositeShape with no children to start out with.
     * @param position the position of the CompositeShape.
     */
    public CompositeShape(Point position) {
        super(position, false);

        mChildren = new ArrayList<Shape>();
    }

    /**
     * Constructor for the CompositeShape class which takes an already constructed list of children.
     * @param position the position of the CompositeShape.
     * @param children a list of the shapes that comprise this CompositeShape.
     */
    public CompositeShape(Point position, List<Shape> children) {
        this(position);

        // Translate each of the children by the CompositeShape's position.
        // TODO: The way this is written, it would be confusing to get back a child added to a
        // CompositeShape because its coordinates will be relative to the global context even though
        // the original coordinates provided were relative to the parent.
        for (Shape child : children) {
            addChild(child);
        }
    }

    /**
     * Constructor for the CompositeShape class which takes a variable number of arguments for
     * children.
     * @param position the position of the CompositeShape.
     * @param children an indefinitely sized array of children.
     */
    public CompositeShape(Point position, Shape... children) {
        this(position, Arrays.asList(children));
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

    /**
     * Getter for the list of child shapes that comprise this CompositeShape.
     * @return a List of the Shapes that comprise this CompositeShape.
     */
    public List<Shape> children() {
        return mChildren;
    }

    /**
     * Adds a Shape to this CompositeShape.
     * @param child the child Shape to add to this CompositeShape.
     */
    public void addChild(Shape child) {
        Point childPosition = child.position();
        child.setPosition(new Point(childPosition.x + mPosition.x, childPosition.y + mPosition.y));
        mChildren.add(child);

        mIsPhysical = mIsPhysical || child.isPhysical();
    }

    @Override
    public void draw(Mat frame, Point offset) {
        // Draw each child.
        for (Shape shape : mChildren) {
            shape.draw(frame);
        }
    }
}
