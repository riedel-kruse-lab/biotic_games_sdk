package edu.stanford.riedel_kruse.bioticgamessdk;

import junit.framework.TestCase;

import org.opencv.core.Point;

import edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies.RectangleBody;

/**
 * Contains test cases for the CollisionUtil class.
 */
public class CollisionUtilTest extends TestCase {
    public void testRectangleRectangleCollision() {
        // Case where the two rectangles overlap.
        RectangleBody rect1 = new RectangleBody(new Point(0, 0), 10, 15, null, -1, true);
        RectangleBody rect2 = new RectangleBody(new Point(8, 13), 5, 8, null, -1, true);

        assertEquals(true, CollisionUtil.collided(rect1, rect2));

        // Case where the two rectangles do not overlap.
        rect2.setPosition(new Point(15, 20));

        assertEquals(false, CollisionUtil.collided(rect1, rect2));

        // Case where the two rectangles touch, but do not overlap.
        rect2.setPosition(new Point(9, 14));

        assertEquals(true, CollisionUtil.collided(rect1, rect2));

        // Case where one rectangle contains the other.
        rect2.setPosition(new Point(1, 1));

        assertEquals(true, CollisionUtil.collided(rect1, rect2));
    }

    public void testRectangleCircleCollision() {
        // Case where the rectangle and circle overlap.
        RectangleBody rect = new RectangleBody(new Point(0, 0), 5, 5, null, -1, true);
        CircleBody circle = new CircleBody(new Point(0, 0), 3, null, -1, true);

        assertEquals(true, CollisionUtil.collided(rect, circle));

        // Case where the rectangle and circle do not overlap.
        circle.setPosition(new Point(10, 10));

        assertEquals(false, CollisionUtil.collided(rect, circle));

        // Case where the rectangle contains the circle.
        circle.setPosition(new Point(2, 2));

        assertEquals(true, CollisionUtil.collided(rect, circle));

        // Case where the circle and rectangle touch, but do not overlap.
        circle.setPosition(new Point(8, 2.5));

        assertEquals(true, CollisionUtil.collided(rect, circle));
    }

    public void testCircleCircleCollision() {
        // Case where the circles do not overlap.
        CircleBody circle1 = new CircleBody(new Point(0, 0), 5, null, -1, true);
        CircleBody circle2 = new CircleBody(new Point(10, 10), 2, null, -1, true);

        assertEquals(false, CollisionUtil.collided(circle1, circle2));

        // Case where the two circles overlap.
        circle2.setPosition(new Point(5, 0));

        assertEquals(true, CollisionUtil.collided(circle1, circle2));

        // Case where one circle sits inside the other.
        circle2.setPosition(new Point(0, 0));

        assertEquals(true, CollisionUtil.collided(circle1, circle2));

        // Case where the circles touch, but do not overlap.
        circle2.setPosition(new Point(7, 0));

        assertEquals(true, CollisionUtil.collided(circle1, circle2));

        // Case where the circles are just off touching.
        circle2.setPosition(new Point(7.1, 0));

        assertEquals(false, CollisionUtil.collided(circle1, circle2));
    }

    public void testCompositeShapeCompositeShapeCollision() {
        RectangleBody stick = new RectangleBody(new Point(0, 0), 10, 2, null, -1, true);
        CircleBody ball = new CircleBody(new Point(10, 1), 4, null, -1, true);

        CompositeShape ballAndStick = new CompositeShape(new Point(0, 0), stick, ball);

        RectangleBody goalBack = new RectangleBody(new Point(0, 0), 2, 10, null, -1, true);
        RectangleBody goalTopArm = new RectangleBody(new Point(2, 0), 4, 2, null, -1, false);
        RectangleBody goalBottomArm = new RectangleBody(new Point(2, 8), 4, 2, null, -1, false);

        CompositeShape goal = new CompositeShape(new Point(100, 100), goalBack, goalTopArm,
                goalBottomArm);

        // Case where the two objects do not overlap at all.
        assertEquals(false, CollisionUtil.collided(ballAndStick, goal));
    }

    public void testCompositeShapeShapeCollision() {
        RectangleBody goalBack = new RectangleBody(new Point(0, 0), 2, 10, null, -1, true);
        RectangleBody goalTopArm = new RectangleBody(new Point(2, 0), 4, 2, null, -1, false);
        RectangleBody goalBottomArm = new RectangleBody(new Point(2, 8), 4, 2, null, -1, false);

        CompositeShape compositeShape = new CompositeShape(new Point(0, 0), goalBack, goalTopArm,
                goalBottomArm);

        // Case where the circle intersects the physical back of the goal.
        CircleBody circle = new CircleBody(new Point(2, 5), 2, null, -1, true);
        assertEquals(true, CollisionUtil.collided(compositeShape, circle));

        // Case where the circle intersects the non-physical arms of the goal.
        circle.setPosition(new Point(5, 0));
        assertEquals(false, CollisionUtil.collided(compositeShape, circle));

        // Case where the circle does not intersect the goal at all.
        circle.setPosition(new Point(10, 0));
        assertEquals(false, CollisionUtil.collided(compositeShape, circle));
    }
}
