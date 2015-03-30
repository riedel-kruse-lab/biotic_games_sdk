package edu.stanford.riedel_kruse.euglenasoccer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.ArrayList;

import edu.stanford.riedel_kruse.bioticgamessdk.Circle;
import edu.stanford.riedel_kruse.bioticgamessdk.GameObject;
import edu.stanford.riedel_kruse.bioticgamessdk.MathUtil;
import edu.stanford.riedel_kruse.bioticgamessdk.Shape;

/**
 * Created by dchiu on 3/28/15.
 */
public class SoccerBall extends Circle {
    public static final int RADIUS = 30;
    private static final Scalar COLOR = new Scalar(255, 255, 255);
    private static final Scalar OUTLINE_COLOR = new Scalar(0, 0, 0);
    private static final int OUTLINE_THICKNESS = 5;
    private static final Scalar PENTAGONS_COLOR = new Scalar(150, 150, 150);

    private static final int TRACKING_CIRCLE_RADIUS = RADIUS * 2;
    private static final Scalar TRACKING_CIRCLE_COLOR = new Scalar(255, 0, 0);
    private static final int TRACKING_CIRCLE_THICKNESS = 3;

    public SoccerBall(Point position) {
        super(position, TRACKING_CIRCLE_RADIUS, TRACKING_CIRCLE_COLOR, TRACKING_CIRCLE_THICKNESS,
                false);
    }

    @Override
    public void draw(Mat frame, Point offset) {
        Point drawPosition = MathUtil.addPoints(offset, mPosition);
        Core.circle(frame, drawPosition, TRACKING_CIRCLE_RADIUS, TRACKING_CIRCLE_COLOR,
                TRACKING_CIRCLE_THICKNESS);

        // Draws the black outline of the ball.
        Core.circle(frame, drawPosition, RADIUS, OUTLINE_COLOR,
                OUTLINE_THICKNESS);

        // Draws a filled white circle, which is the background of the ball.
        Core.circle(frame, drawPosition, RADIUS, COLOR, -1);

        //Draw pentagons
        ArrayList<MatOfPoint> pentagons = new ArrayList<MatOfPoint>();
        pentagons.add(0, new MatOfPoint(new Point(drawPosition.x, drawPosition.y - 12),
                new Point(drawPosition.x - 11, drawPosition.y - 4),
                new Point(drawPosition.x - 6, drawPosition.y + 9),
                new Point(drawPosition.x + 6, drawPosition.y + 9),
                new Point(drawPosition.x + 11, drawPosition.y - 4)));
        pentagons.add(1, new MatOfPoint(new Point(drawPosition.x - 10, drawPosition.y - 20),
                new Point(drawPosition.x - 20, drawPosition.y - 12),
                new Point(drawPosition.x - 25, drawPosition.y - 12),
                new Point(drawPosition.x - 18, drawPosition.y - 19),
                new Point(drawPosition.x - 10, drawPosition.y - 25)));
        pentagons.add(2, new MatOfPoint(new Point(drawPosition.x + 10, drawPosition.y - 20),
                new Point(drawPosition.x + 20, drawPosition.y - 12),
                new Point(drawPosition.x + 25, drawPosition.y - 12),
                new Point(drawPosition.x + 18, drawPosition.y - 19),
                new Point(drawPosition.x + 10, drawPosition.y - 25)));
        pentagons.add(3, new MatOfPoint(new Point(drawPosition.x - 23, drawPosition.y + 3),
                new Point(drawPosition.x - 16, drawPosition.y + 16),
                new Point(drawPosition.x - 19, drawPosition.y + 18),
                new Point(drawPosition.x - 26, drawPosition.y + 11),
                new Point(drawPosition.x - 27, drawPosition.y + 3)));
        pentagons.add(4, new MatOfPoint(new Point(drawPosition.x + 23, drawPosition.y + 3),
                new Point(drawPosition.x + 16, drawPosition.y + 16),
                new Point(drawPosition.x + 19, drawPosition.y + 18),
                new Point(drawPosition.x + 26, drawPosition.y + 11),
                new Point(drawPosition.x + 27, drawPosition.y + 3)));
        pentagons.add(5, new MatOfPoint(new Point(drawPosition.x - 6, drawPosition.y + 22),
                new Point(drawPosition.x + 6, drawPosition.y + 22),
                new Point(drawPosition.x + 8, drawPosition.y + 27),
                new Point(drawPosition.x - 8, drawPosition.y + 27)));
        Core.fillPoly(frame, pentagons, PENTAGONS_COLOR);
    }
}