package edu.stanford.riedel_kruse.paceuglena;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.Renderable;

/**
 * Created by honestykim on 6/21/2015.
 */
public class BoxedRegion extends GameObject {
    class BoxedRegionRenderable extends Renderable {

        public BoxedRegionRenderable(GameObject gameObject) {
            super(gameObject);
        }

        @Override
        public void draw(Mat frame) {

            Scalar color = new Scalar(255,0,0);

            Core.rectangle(frame, new Point(0, 400), new Point(600, 960), color,5);
            Core.putText(frame, "TAP IN BOX", new Point(50, 600), Core.FONT_HERSHEY_PLAIN, 5, color, 5);
        }
    }

    public BoxedRegion(Point position){
        super(position);
        mRenderable = new BoxedRegionRenderable(this);
    }
}
