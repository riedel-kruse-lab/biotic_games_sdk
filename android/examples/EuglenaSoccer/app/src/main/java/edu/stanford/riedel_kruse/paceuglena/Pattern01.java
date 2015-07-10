package edu.stanford.riedel_kruse.paceuglena;

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
    private int mThickness = 2;
    private int mThickness2 = 10;
    private int mBoxThickness = 20;
    private int mSpacing = 70;

    private Scalar mColor = new Scalar(100,100,100);
    private Scalar mColor2 = new Scalar(0,0,128);

    class Pattern01Renderable extends Renderable{

        public Pattern01Renderable(GameObject gameObject) {
            super(gameObject);
        }

        @Override
        public void draw(Mat frame) {

            Point point1 = new Point(mPosition.x+mLength1,mPosition.y);
            Core.line(frame, mPosition, point1, mColor, mThickness);

            Point point2 = new Point(point1.x, point1.y - mLength2);
            Core.line(frame, point1, point2, mColor, mThickness);

            Point point3 = new Point(point2.x - mLength3, point2.y);
            Core.line(frame, point2, point3, mColor, mThickness);

            Core.line(frame, new Point(mPosition.x, mPosition.y + mSpacing), new Point(mPosition.x + mLength1 + mSpacing, mPosition.y + mSpacing), mColor2, mThickness2);
            Core.line(frame, new Point(mPosition.x + mLength1 + mSpacing, mPosition.y + mSpacing), new Point(mPosition.x + mLength1 + mSpacing, mPosition.y - mLength2 - mSpacing), mColor2, mThickness2);
            Core.line(frame, new Point(mPosition.x + mLength1 + mSpacing, mPosition.y - mLength2 - mSpacing), new Point(mPosition.x, mPosition.y - mLength2 - mSpacing), mColor2, mThickness2);

            Core.line(frame, new Point(mPosition.x, mPosition.y + mSpacing + mBoxThickness), new Point(mPosition.x + mLength1 + mSpacing + mBoxThickness, mPosition.y + mSpacing + mBoxThickness), mColor2, mThickness2);
            Core.line(frame, new Point(mPosition.x + mLength1 + mSpacing + mBoxThickness, mPosition.y + mSpacing + mBoxThickness), new Point(mPosition.x + mLength1 + mSpacing + mBoxThickness, mPosition.y - mLength2 - mSpacing - mBoxThickness), mColor2, mThickness2);
            Core.line(frame, new Point(mPosition.x + mLength1 + mSpacing + mBoxThickness, mPosition.y - mLength2 - mSpacing - mBoxThickness), new Point(mPosition.x, mPosition.y - mLength2 - mSpacing - mBoxThickness), mColor2, mThickness2);

            Core.line(frame, new Point(mPosition.x, mPosition.y + mSpacing), new Point(mPosition.x, mPosition.y + mSpacing + mBoxThickness), mColor2, mThickness2);
            Core.line(frame, new Point(mPosition.x, mPosition.y - mLength2 - mSpacing), new Point(mPosition.x, mPosition.y - mLength2 - mSpacing - mBoxThickness), mColor2, mThickness2);

            Core.line(frame, new Point(mPosition.x, mPosition.y - mSpacing), new Point(mPosition.x + mLength1 - mSpacing, mPosition.y - mSpacing), mColor2, mThickness2);
            Core.line(frame, new Point(mPosition.x + mLength1 - mSpacing, mPosition.y - mSpacing), new Point(mPosition.x+ mLength1 - mSpacing, mPosition.y + mSpacing - mLength2), mColor2, mThickness2);
            Core.line(frame, new Point(mPosition.x+ mLength1 - mSpacing, mPosition.y + mSpacing - mLength2), new Point(mPosition.x, mPosition.y + mSpacing - mLength2), mColor2, mThickness2);
            Core.line(frame, new Point(mPosition.x, mPosition.y + mSpacing - mLength2), new Point(mPosition.x, mPosition.y - mSpacing), mColor2, mThickness2);
        }
    }

    public Pattern01(Point position){
        super(position);
        mRenderable = new Pattern01Renderable(this);
    }

}
