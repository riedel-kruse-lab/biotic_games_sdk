package edu.stanford.riedel_kruse.euglenascientist;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;
import edu.stanford.riedel_kruse.bioticgamessdk.renderables.LineRenderable;

/**
 * Created by honestykim on 8/20/2015.
 */
public class ScaleBar extends GameObject {
    private int mScaleSize;
    private Scalar mColor;
    private Point mStart;
    private int mThickness;

    class ScaleBarRenderable extends Renderable {

        public ScaleBarRenderable(GameObject gameObject) {
            super(gameObject);

        }

        @Override
        public void draw(Mat frame) {
            Core.line(frame, mStart, new Point(mStart.x + mScaleSize * 2, mStart.y), mColor, mThickness);
            Core.putText(frame, "0.1 mm", new Point(mStart.x, mStart.y + 55), Core.FONT_HERSHEY_PLAIN, 4, new Scalar(0, 0, 0), 6);
//            TextObject scaleText = new TextObject(mScaleFactor*((mFieldWidth * 3/4)/mScaleFactor),
//                mFieldHeight - SoccerField.PADDING * 2, mResources.getString(R.string.scale),
//                Core.FONT_HERSHEY_PLAIN, 3, COLOR_LIGHT_BLUE, 4);
//        addGameObject(scaleText);
        }
    }
    public ScaleBar(Point start, int scale, Scalar color, int thickness) {
        super(start);
        mScaleSize = scale;
        mColor = color;
        mStart = start;
        mThickness = thickness;

        mRenderable = new ScaleBarRenderable(this);
    }

    public void setScaleSize(int scale, int width){
        mScaleSize = scale;
        mStart = new Point(scale*((width * 3/4)/scale), mStart.y);
    }
}