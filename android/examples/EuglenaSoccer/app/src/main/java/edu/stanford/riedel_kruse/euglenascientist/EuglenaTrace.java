package edu.stanford.riedel_kruse.euglenascientist;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.MathUtil;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;
import edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies.RectangleBody;

/**
 * Created by honestykim on 7/15/2015.
 */
public class EuglenaTrace extends GameObject {

    private List<Double> mPointList = new ArrayList<Double>();

    class EuglenaTraceRenderable extends Renderable {
        private Scalar mColor;

        public EuglenaTraceRenderable(GameObject gameObject, Scalar color) {
            super(gameObject);

            mColor = color;
        }

        @Override
        public void draw(Mat frame) {
        }
    }

    public EuglenaTrace(int x, int y, Scalar color) {
        this(new Point(x, y), color);
    }

    public EuglenaTrace(Point position, Scalar color) {
        super(position);

        mRenderable = new EuglenaTraceRenderable(this, color);
        mPhysicalBody = new RectangleBody(this,10, 10);
    }

    public void AddPoint(double x, double y){

    }
}