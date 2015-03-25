package edu.stanford.riedel_kruse.euglenasoccer;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.CompositeShape;
import edu.stanford.riedel_kruse.bioticgamessdk.Rectangle;

/**
 * Created by dchiu on 2/2/15.
 */
public class RightGoal extends CompositeShape {
    public RightGoal(Point position, int goalWidth, int goalHeight, Scalar color) {
        super(position);
        Rectangle back = new Rectangle(new Point(0, 0), goalWidth, goalHeight, color, -1, true);
        Rectangle top = new Rectangle(new Point(-goalHeight / 4 + goalWidth, 0), goalHeight / 4, goalWidth,
                color, -1, false);
        Rectangle bottom = new Rectangle(new Point(-goalHeight / 4 + goalWidth, goalHeight - goalWidth),
                goalHeight / 4, goalWidth, color, -1, false);

        addChild(back);
        addChild(top);
        addChild(bottom);
    }
}
