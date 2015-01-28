package edu.stanford.riedel_kruse.bioticgamessdk;

import junit.framework.TestCase;

import org.opencv.core.Point;

/**
 * Contains test cases for the CollisionUtil class.
 */
public class CollisionUtilTest extends TestCase {
    public void testRectangleCollision() {
        // Case where the two rectangles overlap.
        Rectangle rect1 = new Rectangle(new Point(0, 0), 10, 15, null, -1, true);
        Rectangle rect2 = new Rectangle(new Point(8, 13), 5, 8, null, -1, true);

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
}
