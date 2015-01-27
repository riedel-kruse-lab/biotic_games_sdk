package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Point;

import java.util.List;

/**
 * Created by dchiu on 1/26/15.
 */
public class CollisionUtil {
    public static boolean intersects(GameObject obj1, GameObject obj2) {
        // TODO: Fix. I'm not proud of this.
        if (obj1 instanceof Rectangle) {
            Rectangle rect1 = (Rectangle) obj1;
            if (obj2 instanceof Rectangle) {
                Rectangle rect2 = (Rectangle) obj2;
                return intersects(rect1, rect2);
            }
            else if (obj2 instanceof CompositeShape) {
                CompositeShape shape2 = (CompositeShape) obj2;
                return intersects(shape2, rect1);
            }
            else if (obj2 instanceof Circle) {
                Circle circle2 = (Circle) obj2;
                return intersects(rect1, circle2);
            }
        }
        else if (obj1 instanceof CompositeShape) {
            CompositeShape shape1 = (CompositeShape) obj1;
            if (obj2 instanceof Rectangle) {
                Rectangle rect2 = (Rectangle) obj2;
                return intersects(shape1, rect2);
            }
            else if (obj2 instanceof CompositeShape) {
                CompositeShape shape2 = (CompositeShape) obj2;
                return intersects(shape1, shape2);
            }
            else if (obj2 instanceof Circle) {
                Circle circle2 = (Circle) obj2;
                return intersects(shape1, circle2);
            }
        }
        else if (obj1 instanceof Circle) {
            Circle circle1 = (Circle) obj1;
            if (obj2 instanceof Rectangle) {
                Rectangle rect2 = (Rectangle) obj2;
                return intersects(rect2, circle1);
            }
            else if (obj2 instanceof CompositeShape) {
                CompositeShape shape2 = (CompositeShape) obj2;
                return intersects(shape2, circle1);
            }
            else if (obj2 instanceof Circle) {
                Circle circle2 = (Circle) obj2;
                return intersects(circle1, circle2);
            }
        }

        return false;
    }

    public static boolean intersects(Rectangle rect1, Rectangle rect2) {
        // If either shape isn't physical, it's not possible for them to intersect.
        if (!rect1.isPhysical() || !rect2.isPhysical()) {
            return false;
        }

        Point tl1 = rect1.topLeft();
        Point tl2 = rect2.topLeft();
        Point br1 = rect1.bottomRight();
        Point br2 = rect2.bottomRight();

        // If one rectangle is on the left side of the other
        if (tl1.x > br2.x || tl2.x > br1.x) {
            return false;
        }

        // If one rectangle is above the other
        if (tl1.y < br2.y || tl2.y < br1.y) {
            return false;
        }

        return true;
    }

    public static boolean intersects(Rectangle rect, Circle circle) {
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

    public static boolean intersects(Circle circle1, Circle circle2) {
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

    public static boolean intersects(CompositeShape shape1, CompositeShape shape2) {
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

    public static boolean intersects(CompositeShape shape1, Shape shape2) {
        // If either shape isn't physical, it's not possible for them to intersect.
        if (!shape1.isPhysical() || !shape2.isPhysical()) {
            return false;
        }

        List<Shape> children = shape1.children();

        for (Shape child : children) {
            if (child.intersects(shape2)) {
                return true;
            }
        }

        return false;
    }
}
