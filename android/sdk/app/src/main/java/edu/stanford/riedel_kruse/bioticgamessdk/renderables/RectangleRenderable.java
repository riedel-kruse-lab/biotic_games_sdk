package edu.stanford.riedel_kruse.bioticgamessdk.renderables;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.MathUtil;

/**
 * Created by dchiu on 4/3/15.
 */
public class RectangleRenderable extends SimpleRenderable {

    private Rect mRect;

    public RectangleRenderable(GameObject gameObject, int width, int height, Scalar color,
                               int thickness) {
        super(gameObject, color, thickness);

        mRect = new Rect(0, 0, width, height);
    }

    public Point topLeft() {
        return MathUtil.addPoints(mGameObject.position(), mRect.tl());
    }

    public Point bottomRight() {
        return MathUtil.addPoints(mGameObject.position(), mRect.br());
    }

    @Override
    public void draw(Mat frame) {
        Core.rectangle(frame, topLeft(), bottomRight(), mColor, mThickness);
    }
}
