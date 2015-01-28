package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Point;

import java.util.List;

/**
 * The CollisionUtil class contains static helper functions for deciding if two GameObjects have
 * collided.
 */
public class CollisionUtil {
    /**
     * Checks if two generic GameObjects have collided.
     * @param obj1 the first object to check collision for
     * @param obj2 the second object to check collision for
     * @return true if the two objects have collided, false otherwise
     */
    public static boolean collided(GameObject obj1, GameObject obj2) {
        // TODO: There is probably a cleaner way of doing this. This requires a combinatorial number
        // of cases. Yuck!
        if (obj1 instanceof Rectangle) {
            Rectangle rect1 = (Rectangle) obj1;
            if (obj2 instanceof Rectangle) {
                Rectangle rect2 = (Rectangle) obj2;
                return collided(rect1, rect2);
            }
            else if (obj2 instanceof CompositeShape) {
                CompositeShape shape2 = (CompositeShape) obj2;
                return collided(shape2, rect1);
            }
            else if (obj2 instanceof Circle) {
                Circle circle2 = (Circle) obj2;
                return collided(rect1, circle2);
            }
        }
        else if (obj1 instanceof CompositeShape) {
            CompositeShape shape1 = (CompositeShape) obj1;
            if (obj2 instanceof Rectangle) {
                Rectangle rect2 = (Rectangle) obj2;
                return collided(shape1, rect2);
            }
            else if (obj2 instanceof CompositeShape) {
                CompositeShape shape2 = (CompositeShape) obj2;
                return collided(shape1, shape2);
            }
            else if (obj2 instanceof Circle) {
                Circle circle2 = (Circle) obj2;
                return collided(shape1, circle2);
            }
        }
        else if (obj1 instanceof Circle) {
            Circle circle1 = (Circle) obj1;
            if (obj2 instanceof Rectangle) {
                Rectangle rect2 = (Rectangle) obj2;
                return collided(rect2, circle1);
            }
            else if (obj2 instanceof CompositeShape) {
                CompositeShape shape2 = (CompositeShape) obj2;
                return collided(shape2, circle1);
            }
            else if (obj2 instanceof Circle) {
                Circle circle2 = (Circle) obj2;
                return collided(circle1, circle2);
            }
        }

        return false;
    }

    /**
     * Checks if two Rectangles have collided. Based on code found online:
     * http://www.geeksforgeeks.org/find-two-rectangles-overlap/
     * @param rect1 the first Rectangle to check
     * @param rect2 the second Rectangle to check
     * @return true if the Rectangles have collided, false otherwise
     */
    public static boolean collided(Rectangle rect1, Rectangle rect2) {
        // If either shape isn't physical, it's not possible for them to intersect.
        if (!rect1.isPhysical() || !rect2.isPhysical()) {
            return false;
        }

        Point tl1 = rect1.topLeft();
        Point tl2 = rect2.topLeft();
        Point br1 = rect1.bottomRight();
        Point br2 = rect2.bottomRight();

        // Check if one rectangle is on the left side of the other
        if (tl1.x > br2.x || tl2.x > br1.x) {
            return false;
        }

        // Check if one rectangle is above the other
        return !(tl1.y > br2.y || tl2.y > br1.y);
    }

    /**
     * Checks if a Rectangle and a Circle have collided. Based on code provided by user e.James on
     * StackOverflow: http://stackoverflow.com/a/402010/599391
     * @param rect the Rectangle to check
     * @param circle the Circle to check
     * @return true if the Rectangle and the Circle have collided, false otherwise
     */
    public static boolean collided(Rectangle rect, Circle circle) {
        // If either shape isn't physical, it's not possible for them to intersect.
        if (!rect.isPhysical() || !circle.isPhysical()) {
            return false;
        }

        Point circleCenter = circle.center();
        int circleRadius = circle.radius();
        Point rectCenter = rect.center();
        int rectWidth = rect.width();
        int rectHeight = rect.height();

        Point circleDistance = new Point();
        circleDistance.x = Math.abs(circleCenter.x - rectCenter.x);
        circleDistance.y = Math.abs(circleCenter.y - rectCenter.y);

        if (circleDistance.x > rectWidth / 2 + circleRadius) {
            return false;
        }
        if (circleDistance.y > rectHeight / 2 + circleRadius) {
            return false;
        }

        if (circleDistance.x <= rectWidth / 2) {
            return true;
        }
        if (circleDistance.y <= rectHeight / 2) {
            return true;
        }

        double cornerDistanceSquared = Math.pow(circleDistance.x - rectWidth / 2, 2) +
                Math.pow(circleDistance.y - rectHeight / 2, 2);

        return cornerDistanceSquared <= Math.pow(circleRadius, 2);
    }

    /**
     * Checks if two Circles have collided.
     * @param circle1 the first Circle to check
     * @param circle2 the second Circle to check
     * @return true if the two Circles have collided, false otherwise
     */
    public static boolean collided(Circle circle1, Circle circle2) {
        // If either shape isn't physical, it's not possible for them to intersect.
        if (!circle1.isPhysical() || !circle2.isPhysical()) {
            return false;
        }

        Point center1 = circle1.center();
        Point center2 = circle2.center();
        int radius1 = circle1.radius();
        int radius2 = circle2.radius();

        double distance = MathUtil.distance(center1, center2);

        return distance <= radius1 + radius2;
    }

    /**
     * Checks if two CompositeShapes have collided.
     * @param shape1 the first CompositeShape to check
     * @param shape2 the second CompositeShape to check
     * @return true if any of the children of either CompositeShape has collided with any child
     *         in the other CompositeShape, false otherwise
     */
    public static boolean collided(CompositeShape shape1, CompositeShape shape2) {
        // If either shape isn't physical, it's not possible for them to intersect.
        if (!shape1.isPhysical() || ! shape2.isPhysical()) {
            return false;
        }

        List<Shape> children1 = shape1.children();
        List<Shape> children2 = shape2.children();

        for (Shape childShape1 : children1) {
            for (Shape childShape2 : children2) {
                if (childShape1.intersects(childShape2)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a CompositeShape has collided with a Shape object.
     * @param compositeShape the CompositeShape to check
     * @param shape the Shape to check
     * @return true if any of the CompositeShape's children have collided with the Shape, false
     *         otherwise
     */
    public static boolean collided(CompositeShape compositeShape, Shape shape) {
        // If either shape isn't physical, it's not possible for them to intersect.
        if (!compositeShape.isPhysical() || !shape.isPhysical()) {
            return false;
        }

        List<Shape> children = compositeShape.children();

        for (Shape child : children) {
            if (child.intersects(shape)) {
                return true;
            }
        }

        return false;
    }
}
