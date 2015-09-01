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
 * Created by dchiu on 2/2/15.
 */
public class RightGoal extends GameObject {

    private int mWidth;
    private int mHeight;

    class RightGoalRenderable extends Renderable {
        private Scalar mColor;

        public RightGoalRenderable(GameObject gameObject, Scalar color) {
            super(gameObject);

            mColor = color;
        }

        @Override
        public void draw(Mat frame) {
            Point backBottomRight = new Point(mWidth, mHeight);
            backBottomRight = MathUtil.addPoints(mGameObject.position(), backBottomRight);
            Core.rectangle(frame, mGameObject.position(), backBottomRight, mColor, -1);

            Point topTopLeft = new Point(-mHeight / 8 + mWidth, 0);
            topTopLeft = MathUtil.addPoints(topTopLeft, mGameObject.position());
            Point topBottomRight = MathUtil.addPoints(topTopLeft, new Point(mHeight / 8, mWidth));
            Core.rectangle(frame, topTopLeft, topBottomRight, mColor, -1);

            Point bottomTopLeft = new Point(-mHeight / 8 + mWidth, mHeight - mWidth);
            bottomTopLeft = MathUtil.addPoints(bottomTopLeft, mGameObject.position());
            Point bottomBottomRight = MathUtil.addPoints(bottomTopLeft,
                    new Point(mHeight / 8, mWidth));
            Core.rectangle(frame, bottomTopLeft, bottomBottomRight, mColor, -1);

            Core.line(frame, new Point(frame.width()/2,0), new Point(frame.width()/2,frame.height()), mColor, 1);
        }
    }

    public RightGoal(int x, int y, int goalWidth, int goalHeight, Scalar color) {
        this(new Point(x, y), goalWidth, goalHeight, color);
    }

    public RightGoal(Point position, int goalWidth, int goalHeight, Scalar color) {
        super(position);

        mRenderable = new RightGoalRenderable(this, color);
        mPhysicalBody = new RectangleBody(this, goalWidth, goalHeight);

        mWidth = goalWidth;
        mHeight = goalHeight;
    }
}
