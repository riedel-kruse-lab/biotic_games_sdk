package edu.stanford.riedel_kruse.euglenascientist;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.MathUtil;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;
import edu.stanford.riedel_kruse.bioticgamessdk.physicalbodies.RectangleBody;

/**
 * Created by honestykim on 8/18/2015.
 */
public class GridOverlay extends GameObject {
    private int mWidth;
    private int mHeight;

    class GridOverlayRenderable extends Renderable {
        private Scalar mColor;

        public GridOverlayRenderable(GameObject gameObject) {
            super(gameObject);

            //mColor = color;
        }

        @Override
        public void draw(Mat frame) {

            int size = 100;
            int lineThickness = 2;
            int frameWidth = frame.cols();
            int frameHeight = frame.rows();
            int frameWidthCount = 0;
            int frameHeightCount = 0;

            while(frameWidthCount < frameWidth){
                Point startPoint = new Point(frameWidthCount,0);
                Point endPoint = new Point(frameWidthCount,frameHeight);
                Core.line(frame, startPoint, endPoint, new Scalar(50,50,50), lineThickness);

                frameWidthCount += size;
            }

            while(frameHeightCount < frameHeight){
                Point startPoint = new Point(0,frameHeightCount);
                Point endPoint = new Point(frameWidth,frameHeightCount);
                Core.line(frame, startPoint, endPoint, new Scalar(50,50,50), lineThickness);

                frameHeightCount += size;
            }
        }
    }
    public GridOverlay() {
        super(new Point(0,0));

        mRenderable = new GridOverlayRenderable(this);
    }
}