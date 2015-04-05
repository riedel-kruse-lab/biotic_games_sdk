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
public class LeftGoal extends GameObject {
    private int mWidth;
    private int mHeight;

    class LeftGoalRenderable extends Renderable {
        private Scalar mColor;

        public LeftGoalRenderable(GameObject gameObject, Scalar color) {
            super(gameObject);

            mColor = color;
        }

        @Override
        public void draw(Mat frame) {
            Point backBottomRight = new Point(mWidth, mHeight);
            backBottomRight = MathUtil.addPoints(mGameObject.position(), backBottomRight);
            Core.rectangle(frame, mGameObject.position(), backBottomRight, mColor, -1);

            Point topTopLeft = mGameObject.position();
            Point topBottomRight = MathUtil.addPoints(topTopLeft, new Point(mHeight / 8, mWidth));
            Core.rectangle(frame, topTopLeft, topBottomRight, mColor, -1);

            Point bottomTopLeft = new Point(0, mHeight - mWidth);
            bottomTopLeft = MathUtil.addPoints(bottomTopLeft, mGameObject.position());
            Point bottomBottomRight = MathUtil.addPoints(bottomTopLeft,
                    new Point(mHeight / 8, mWidth));
            Core.rectangle(frame, bottomTopLeft, bottomBottomRight, mColor, -1);
        }
    }

    public LeftGoal(int x, int y, int goalWidth, int goalHeight, Scalar color) {
        this(new Point(x, y), goalWidth, goalHeight, color);
    }

    public LeftGoal(Point position, int goalWidth, int goalHeight, Scalar color) {
        super(position);

        mWidth = goalWidth;
        mHeight = goalHeight;

        mRenderable = new LeftGoalRenderable(this, color);
        mPhysicalBody = new RectangleBody(this, goalWidth, goalHeight);
        /*Rectangle back = new Rectangle(new Point(0, 0), goalWidth, goalHeight, color, -1, true);
        Rectangle top = new Rectangle(new Point(0, 0), goalHeight / 8, goalWidth, color, -1, false);
        Point bottomPosition = new Point(0, goalHeight - goalWidth);
        Rectangle bottom = new Rectangle(bottomPosition, goalHeight / 8, goalWidth, color, -1,
                false);

        addChild(back);
        addChild(top);
        addChild(bottom);*/
    }
}
