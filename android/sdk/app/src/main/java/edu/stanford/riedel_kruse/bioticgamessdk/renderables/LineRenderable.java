package edu.stanford.riedel_kruse.bioticgamessdk.renderables;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;

/**
 * Created by dchiu on 4/4/15.
 */
public class LineRenderable extends SimpleRenderable {
    private Point mStart;
    private Point mEnd;

    public LineRenderable(GameObject object, double startX, double startY, double endX, double endY,
                          Scalar color, int thickness) {
        this(object, new Point(startX, startY), new Point(endX, endY), color, thickness);
    }

    public LineRenderable(GameObject gameObject, Point start, Point end, Scalar color,
                          int thickness) {
        super(gameObject, color, thickness);

        mStart = start;
        mEnd = end;
        mColor = color;
        mThickness = thickness;
    }

    @Override
    public void draw(Mat frame) {
        Core.line(frame, mStart, mEnd, mColor, mThickness);
    }
}
