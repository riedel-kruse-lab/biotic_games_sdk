package edu.stanford.riedel_kruse.bioticgamessdk.gameobjects;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies.RectangleBody;
import edu.stanford.riedel_kruse.bioticgamessdk.renderables.RectangleRenderable;

/**
 * Created by dchiu on 4/4/15.
 */
public class RectangleObject extends GameObject {
    public RectangleObject(Point position, int width, int height, Scalar color,
                           int thickness) {
        super(position);

        mRenderable = new RectangleRenderable(this, width, height, color, thickness);
        mPhysicalBody = new RectangleBody(this, width, height);
    }
}
