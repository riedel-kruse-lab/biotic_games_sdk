package edu.stanford.riedel_kruse.bioticgamessdk.renderables;

import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;

/**
 * Created by dchiu on 4/2/15.
 */
public abstract class SimpleRenderable extends Renderable {
    protected Scalar mColor;
    protected int mThickness;

    public SimpleRenderable(GameObject gameObject, Scalar color, int thickness) {
        super(gameObject);
    }

    public Scalar color() {
        return mColor;
    }

    public void setColor(Scalar newColor) {
        mColor = newColor;
    }

    public int thickness() {
        return mThickness;
    }

    public void setThickness(int newThickness) {
        mThickness = newThickness;
    }
}
