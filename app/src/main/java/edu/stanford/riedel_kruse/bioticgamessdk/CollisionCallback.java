package edu.stanford.riedel_kruse.bioticgamessdk;

/**
 * Created by dchiu on 1/27/15.
 */
public abstract class CollisionCallback {
    protected GameObject mObj1;
    protected GameObject mObj2;

    public CollisionCallback(GameObject obj1, GameObject obj2) {
        mObj1 = obj1;
        mObj2 = obj2;
    }

    public GameObject obj1() {
        return mObj1;
    }

    public GameObject obj2() {
        return mObj2;
    }

    public void process() {
        if (collisionHasOccurred()) {
            onCollision();
        }
    }

    public boolean collisionHasOccurred() {
        return CollisionUtil.intersects(mObj1, mObj2);
    }

    public abstract void onCollision();
}
