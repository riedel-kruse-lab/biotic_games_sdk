package edu.stanford.riedel_kruse.euglenasoccer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;

/**
 * Created by honestykim on 4/21/2015.
 */
public class Pattern01 extends GameObject {

    private int mLength1 = 600;
    private int mLength2 = 300;
    private int mLength3 = 500;
    private int mThickness = 10;

    private Scalar mColor = new Scalar(100,100,100);


    class Pattern01Renderable extends Renderable{

        public Pattern01Renderable(GameObject gameObject) {
            super(gameObject);
        }

        @Override
        public void draw(Mat frame) {

            Point point1 = new Point(mPosition.x+mLength1,mPosition.y);
            Core.line(frame, mPosition, point1, mColor, 2);

            Point point2 = new Point(point1.x, point1.y - mLength2);
            Core.line(frame, point1, point2, mColor, 2);

            Point point3 = new Point(point2.x - mLength3, point2.y);
            Core.line(frame, point2, point3, mColor, 2);
        }
    }

    public Pattern01(Point position){
        super(position);
        mRenderable = new Pattern01Renderable(this);
    }

}
