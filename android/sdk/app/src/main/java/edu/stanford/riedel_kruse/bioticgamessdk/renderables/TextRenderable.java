package edu.stanford.riedel_kruse.bioticgamessdk.renderables;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;

/**
 * Created by dchiu on 4/4/15.
 */
public class TextRenderable extends SimpleRenderable {

    private String mText;
    private int mFontFace;
    private double mFontScale;
    private Scalar mColor;
    private int mThickness;

    public TextRenderable(GameObject gameObject, String text, int fontFace, double fontScale,
                          Scalar color, int thickness) {
        super(gameObject, color, thickness);

        mText = text;
        mFontFace = fontFace;
        mFontScale = fontScale;
        mColor = color;
        mThickness = thickness;
    }

    public String text() {
        return mText;
    }

    public void setText(String newText) {
        mText = newText;
    }

    @Override
    public void draw(Mat frame) {
        Core.putText(frame, mText, mGameObject.position(), mFontFace, mFontScale, mColor,
                mThickness);
    }
}
