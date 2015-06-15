package edu.stanford.riedel_kruse.euglenasoccer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;
import edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies.CircleBody;

/**
 * Created by honestykim on 6/11/2015.
 */
public class PMan extends GameObject {
    private static Scalar mColor = new Scalar(250,238,0);
    private static int mRadius = 40;
    private static int mNumPointsCircle = 50;
    private static int mThickness = 4;
    private Point mDirection;
    private double mPercentCircle = 8/10;

    class PManRenderable extends Renderable {

        public PManRenderable(GameObject gameObject) {
            super(gameObject);
        }

        @Override
        public void draw(Mat frame) {
            Core.polylines(frame, getPointsFromCircle(), true, mColor, mThickness);
        }
    }

    public List<MatOfPoint> getPointsFromCircle(){
        List<MatOfPoint> list = new ArrayList<>();
        List<Point> pointList = new ArrayList<>();
        MatOfPoint matOfPoint = new MatOfPoint();

        double angleOffset = pointToRadian();

        for(int i = 3; i < mNumPointsCircle -3; i++){
            pointList.add(new Point(mPosition.x + mRadius*Math.cos(angleOffset + i*2*Math.PI/mNumPointsCircle), mPosition.y + mRadius*Math.sin(angleOffset + i*2*Math.PI/mNumPointsCircle)));
        }

        pointList.add(new Point(mPosition.x, mPosition.y));

        matOfPoint.fromList(pointList);

        list.add(matOfPoint);
        return list;
    }

    public PMan(Point position){
        super(position);
        mRenderable = new PManRenderable(this);
        mPhysicalBody = new CircleBody(this, mRadius);
    }

    public Point direction() {
        return mDirection.clone();
    }

    public void setDirection(Point direction) {
        mDirection = direction;
    }

    public double pointToRadian(){
        double angle = (float) Math.atan2(mDirection.y, mDirection.x);

        return angle;
    }
}
