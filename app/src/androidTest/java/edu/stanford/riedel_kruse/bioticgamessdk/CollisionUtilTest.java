package edu.stanford.riedel_kruse.bioticgamessdk;

import junit.framework.TestCase;

import org.opencv.core.Point;

/**
 * Contains test cases for the CollisionUtil class.
 */
public class CollisionUtilTest extends TestCase {
    public void testRectangleCollision() {
        Rectangle rect1 = new Rectangle(new Point(0, 0), 10, 15, null, -1, true);
        Rectangle rect2 = new Rectangle(new Point(9, 14), 5, 8, null, -1, true);

        assertEquals(true, CollisionUtil.collided(rect1, rect2));
    }
}
