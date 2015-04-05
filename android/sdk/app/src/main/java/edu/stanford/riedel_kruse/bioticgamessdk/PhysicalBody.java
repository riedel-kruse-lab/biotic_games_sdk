package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Point;

/**
 * Abstract class which represents the physical component of a GameObject. That is, the part of a
 * GameObject which is considered in interactions with other objects.
 */
public abstract class PhysicalBody {
    protected GameObject mGameObject;

    public PhysicalBody(GameObject gameObject) {
        mGameObject = gameObject;
    }

    public boolean intersects(PhysicalBody body) {
        return CollisionUtil.collided(this, body);
    }

    public abstract boolean contains(Point point);
}
