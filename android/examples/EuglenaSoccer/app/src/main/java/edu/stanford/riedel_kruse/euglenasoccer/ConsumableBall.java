package edu.stanford.riedel_kruse.euglenasoccer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;
import edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies.CircleBody;

/**
 * Created by honestykim on 6/10/2015.
 */
public class ConsumableBall extends GameObject {

    private Scalar mColor = new Scalar(250,250,250);
    private int mRadius = 10;
    public boolean mIsEaten = false;

    class ConsumableBallRenderable extends Renderable {

        public ConsumableBallRenderable(GameObject gameObject) {
            super(gameObject);
        }

        @Override
        public void draw(Mat frame) {
            Point point1 = new Point(mPosition.x, mPosition.y);
            Core.circle(frame, point1, mRadius, mColor, -1);
        }
    }

    public ConsumableBall(Point position){
        super(position);
        mRenderable = new ConsumableBallRenderable(this);
        mPhysicalBody = new CircleBody(this, mRadius);
    }

    public void setIsEaten(boolean b){
        mIsEaten = b;
    }

    public boolean isEaten(){
        return mIsEaten;
    }
}
