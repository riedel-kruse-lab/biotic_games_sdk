package edu.stanford.riedel_kruse.euglenasoccer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.MathUtil;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;
import edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies.RectangleBody;

/**
 * Created by honestykim on 6/15/2015.
 */
public class QuestionBox extends GameObject{
    private int mWidth;
    private int mHeight;
    private String mString;
    private final Scalar TEXT_COLOR = new Scalar(0,0,0);

    class QuestionBoxRenderable extends Renderable {
        private Scalar mColor;

        public QuestionBoxRenderable(GameObject gameObject, Scalar color) {
            super(gameObject);

            mColor = color;
        }

        @Override
        public void draw(Mat frame) {
            Core.rectangle(frame, mPosition, new Point(mPosition.x + mWidth, mPosition.y + mHeight), mColor, -1);
            Core.putText(frame, mString, new Point(mPosition.x + mWidth/10, mPosition.y + mHeight/10), Core.FONT_HERSHEY_SIMPLEX, 1.0, TEXT_COLOR, 3);
        }
    }

    public QuestionBox(int x, int y, int goalWidth, int goalHeight, String string, Scalar color) {
        this(new Point(x, y), goalWidth, goalHeight, string, color);
    }

    public QuestionBox(Point position, int goalWidth, int goalHeight, String string, Scalar color) {
        super(position);

        mWidth = goalWidth;
        mHeight = goalHeight;
        mString = string;

        mRenderable = new QuestionBoxRenderable(this, color);
        mPhysicalBody = new RectangleBody(this, goalWidth, goalHeight);
    }

    public void setText(String string){
        mString = string;
    }
}