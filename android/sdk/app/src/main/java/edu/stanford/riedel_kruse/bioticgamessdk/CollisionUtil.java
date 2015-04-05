package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Point;

import java.util.List;

import edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies.RectangleBody;
import edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies.CircleBody;

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
    public static boolean collided(PhysicalBody obj1, PhysicalBody obj2) {
        // TODO: There is probably a cleaner way of doing this. This requires a combinatorial number
        // of cases. Yuck!
        if (obj1 instanceof RectangleBody) {
            RectangleBody rect1 = (RectangleBody) obj1;
            if (obj2 instanceof RectangleBody) {
                RectangleBody rect2 = (RectangleBody) obj2;
                return collided(rect1, rect2);
            }
            else if (obj2 instanceof CircleBody) {
                CircleBody circle2 = (CircleBody) obj2;
                return collided(rect1, circle2);
            }
        }
        else if (obj1 instanceof CircleBody) {
            CircleBody circle1 = (CircleBody) obj1;
            if (obj2 instanceof RectangleBody) {
                RectangleBody rect2 = (RectangleBody) obj2;
                return collided(rect2, circle1);
            }
            else if (obj2 instanceof CircleBody) {
                CircleBody circle2 = (CircleBody) obj2;
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
    public static boolean collided(RectangleBody rect1, RectangleBody rect2) {
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
    public static boolean collided(RectangleBody rect, CircleBody circle) {
        Point circleCenter = circle.center();
        int circleRadius = circle.radius();
        Point rectCenter = rect.center();
        int rectWidth = rect.width();
        int rectHeight = rect.height();

        Point circleDistance = new Point();
        circleDistance.x = Math.abs(circleCenter.x - rectCenter.x);
        circleDistance.y = Math.abs(circleCenter.y - rectCenter.y);

        if (circleDistance.x > rectWidth / 2.0 + circleRadius) {
            return false;
        }
        if (circleDistance.y > rectHeight / 2.0 + circleRadius) {
            return false;
        }

        if (circleDistance.x <= rectWidth / 2.0) {
            return true;
        }
        if (circleDistance.y <= rectHeight / 2.0) {
            return true;
        }

        double cornerDistanceSquared = Math.pow(circleDistance.x - rectWidth / 2.0, 2) +
                Math.pow(circleDistance.y - rectHeight / 2.0, 2);

        return cornerDistanceSquared <= Math.pow(circleRadius, 2);
    }

    /**
     * Checks if two Circles have collided.
     * @param circle1 the first Circle to check
     * @param circle2 the second Circle to check
     * @return true if the two Circles have collided, false otherwise
     */
    public static boolean collided(CircleBody circle1, CircleBody circle2) {
        Point center1 = circle1.center();
        Point center2 = circle2.center();
        int radius1 = circle1.radius();
        int radius2 = circle2.radius();

        double distance = MathUtil.distance(center1, center2);

        return distance <= radius1 + radius2;
    }
}
