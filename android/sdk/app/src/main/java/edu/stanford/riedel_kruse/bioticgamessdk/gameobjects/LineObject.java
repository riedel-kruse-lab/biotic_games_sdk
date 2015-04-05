package edu.stanford.riedel_kruse.bioticgamessdk.gameobjects;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.renderables.LineRenderable;

/**
 * Created by dchiu on 4/4/15.
 */
public class LineObject extends GameObject {
    public LineObject(double startX, double startY, double endX, double endY, Scalar color,
                      int thickness) {
        this(new Point(startX, startY), new Point(endX, endY), color, thickness);
    }

    public LineObject(Point start, Point end, Scalar color, int thickness) {
        super(start);

        mRenderable = new LineRenderable(this, start, end, color, thickness);
    }
}
