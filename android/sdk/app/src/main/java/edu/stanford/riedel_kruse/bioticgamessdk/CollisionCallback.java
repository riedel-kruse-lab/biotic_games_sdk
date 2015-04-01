package edu.stanford.riedel_kruse.bioticgamessdk;

/**
 * A CollisionCallback abstracts the logic for determining if two GameObjects have collided.
 */
public abstract class CollisionCallback {
    protected GameObject mObj1;
    protected GameObject mObj2;

    /**
     * Public constructor for the CollisionCallback.
     * @param obj1 the first object to consider in a collision
     * @param obj2 the second object to consider in a collision
     */
    public CollisionCallback(GameObject obj1, GameObject obj2) {
        mObj1 = obj1;
        mObj2 = obj2;
    }

    /**
     * Getter for GameObject #1
     * @return GameObject #1
     */
    public GameObject obj1() {
        return mObj1;
    }

    /**
     * Getter for GameObject #2.
     * @return GameObject #2
     */
    public GameObject obj2() {
        return mObj2;
    }

    /**
     * Processes this CollisionCallback by checking if its GameObjects have collided and calling
     * onCollision() if they have.
     */
    public void process() {
        if (collisionHasOccurred()) {
            onCollision();
        }
    }

    /**
     * Determines whether or not the GameObjects for this CollisionCallback have collided.
     * @return true if the GameObjects for this CollisionCallback have collided; false otherwise.
     */
    public boolean collisionHasOccurred() {
        return mObj1.isPhysical() && mObj2.isPhysical() && CollisionUtil.collided(mObj1, mObj2);
    }

    /**
     * onCollision() gets called when the GameObjects for this CollisionCallback collide. Subclasses
     * should override this function with custom code to run when a collision occurs.
     */
    public abstract void onCollision();
}
