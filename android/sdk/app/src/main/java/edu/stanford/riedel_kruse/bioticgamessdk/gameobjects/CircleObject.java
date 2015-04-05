package edu.stanford.riedel_kruse.bioticgamessdk.gameobjects;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.PhysicalBody;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;
import edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies.CircleBody;
import edu.stanford.riedel_kruse.bioticgamessdk.renderables.CircleRenderable;

/**
 * Created by dchiu on 4/2/15.
 */
public class CircleObject extends GameObject {

    /**
     * Constructor for the GameObject class.
     * @param position   the position of the GameObject.
     */
    public CircleObject(Point position, int radius, Scalar color, int thickness) {
        super(position);

        mRenderable = new CircleRenderable(this, radius, color, thickness);
        mPhysicalBody = new CircleBody(this, radius);
    }
}
