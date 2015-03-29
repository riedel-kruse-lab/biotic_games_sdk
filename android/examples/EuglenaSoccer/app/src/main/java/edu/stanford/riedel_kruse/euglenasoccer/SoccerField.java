package edu.stanford.riedel_kruse.euglenasoccer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.stanford.riedel_kruse.bioticgamessdk.Shape;

/**
 * Created by dchiu on 3/29/15.
 */
public class SoccerField extends Shape {
    public static final int PADDING = 15;
    public static final int LINE_THICKNESS = 3;

    private static final int GOAL_BOX_WIDTH = 200;
    private static final int GOAL_BOX_HEIGHT = 250;
    private static final Scalar COLOR = new Scalar(235, 235, 235);
    private static final int CIRCLE_RADIUS = 105;

    private Point mFieldTopLeft;
    private Point mFieldBottomRight;
    private Point mFieldTopCenter;
    private Point mFieldBottomCenter;
    private Point mLeftGoalBoxTopLeft;
    private Point mLeftGoalBoxBottomRight;
    private Point mRightGoalBoxTopLeft;
    private Point mRightGoalBoxBottomRight;

    private boolean mFirstDraw;

    public SoccerField() {
        super(new Point(0, 0), COLOR, LINE_THICKNESS, false);

        mFirstDraw = true;
    }

    @Override
    public boolean contains(Point point) {
        return false;
    }

    @Override
    public void draw(Mat frame, Point point) {
        int width = frame.cols();
        int height = frame.rows();

        if (mFirstDraw) {
            mFieldTopLeft = new Point(PADDING, height - PADDING);
            mFieldBottomRight = new Point(width - PADDING, PADDING);

            mFieldTopCenter = new Point(width / 2, height - PADDING);
            mFieldBottomCenter = new Point(width / 2, PADDING);

            mLeftGoalBoxTopLeft = new Point(PADDING, height / 2 +
                    GOAL_BOX_HEIGHT);
            mLeftGoalBoxBottomRight = new Point(PADDING + GOAL_BOX_WIDTH,
                    height / 2 - GOAL_BOX_HEIGHT);

            mRightGoalBoxTopLeft = new Point(width - GOAL_BOX_WIDTH, height / 2 + GOAL_BOX_HEIGHT);
            mRightGoalBoxBottomRight = new Point(width - PADDING, height / 2 - GOAL_BOX_HEIGHT);
            mFirstDraw = false;
        }

        // Draw the out of bounds lines
        Core.rectangle(frame, mFieldTopLeft, mFieldBottomRight, COLOR, LINE_THICKNESS);

        // Draw the center line
        Core.line(frame, mFieldTopCenter, mFieldBottomCenter, COLOR, LINE_THICKNESS);

        // Draw the circle in the center of the field
        Core.circle(frame, new Point(width / 2, height / 2), CIRCLE_RADIUS, COLOR, LINE_THICKNESS);

        // Draw the left goal box
        Core.rectangle(frame, mLeftGoalBoxTopLeft, mLeftGoalBoxBottomRight, COLOR, LINE_THICKNESS);
        Core.rectangle(frame, mRightGoalBoxBottomRight, mRightGoalBoxTopLeft, COLOR, LINE_THICKNESS);
    }
}
