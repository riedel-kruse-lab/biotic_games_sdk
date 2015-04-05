package edu.stanford.riedel_kruse.bioticgamessdk.gameobjects;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.renderables.TextRenderable;

/**
 * Created by dchiu on 4/4/15.
 */
public class TextObject extends GameObject {
    public TextObject(double x, double y, String text, int fontFace, double fontScale, Scalar color,
                      int thickness) {
        this(new Point(x, y), text, fontFace, fontScale, color, thickness);
    }

    public TextObject(Point position, String text, int fontFace, double fontScale, Scalar color,
                      int thickness) {
        super(position);

        mRenderable = new TextRenderable(this, text, fontFace, fontScale, color, thickness);
    }

    public void setText(String newText) {
        ((TextRenderable) mRenderable).setText(newText);
    }
}
