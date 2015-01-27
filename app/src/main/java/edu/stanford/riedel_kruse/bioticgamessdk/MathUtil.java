package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains utility functions for mathematical operations that are common to Biotic
 * Games.
 */
public class MathUtil
{
    private static final double VELOCITY_SCALE = 10;

    /**
     * Adds the x- and y-values of two rectangles together and returns a new point.
     * @param p1 the first point
     * @param p2 the second point
     * @return a new point whose x- and y-values are the sum of the given two points' x- and y-
     *         values.
     */
    public static Point addPoints(Point p1, Point p2) {
        return new Point(p1.x + p2.x, p1.y + p2.y);
    }

    /**
     * Given a point and a list of candidate points, finds the candidate point that is closest to
     * the original point.
     * @param point the point to find the closest point to
     * @param candidatePoints a list of points where each point is a candidate to consider for
     *                        closest point
     * @return the point closest to the original point or null if no such point is found
     */
    public static Point findClosestPoint(Point point, List<Point> candidatePoints) {
        Point closestPoint = null;
        double minDistance = Double.MAX_VALUE;

        for (Point candidate : candidatePoints) {
            double distance = distance(candidate, point);

            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = candidate;
            }
        }

        return closestPoint;
    }

    public static double distance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
    }

    /**
     * Normalizes the given vector by directly modifying the properties of the Point.
     * @param vector the vector to normalize
     */
    public static void normalizeVector(Point vector) {
        double magnitude = MathUtil.computeVectorMagnitude(vector);

        vector.x /= magnitude;
        vector.y /= magnitude;
    }

    /**
     * Computes the magnitude of a vector.
     * @param vector the vector to compute the magnitude of
     * @return the magnitude of the vector
     */
    public static double computeVectorMagnitude(Point vector) {
        return Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2));
    }

    /**
     * Computes the average direction from a list of points representing past locations that an
     * object has been at.
     * @param points a list of points that the object has been at
     * @return a Point object which represents a unit vector of the average direction of the given
     * points or null if there were not enough points provided to compute a direction
     */
    // TODO: If we have an average velocity function, do we need an average direction function?
    public static Point computeAverageDirection(List<Point> points) {
        int numPoints = points.size();

        // We need at least two points in order to compute a direction.
        if (numPoints <= 1) {
            return null;
        }

        Point averageDirection = new Point();
        List<Point> directions = new ArrayList<Point>();

        // Compute the directions pairwise between the points.
        for (int i = 0; i < numPoints - 1; i++) {
            Point previous = points.get(i);
            Point next = points.get(i + 1);
            Point direction = new Point(next.x - previous.x, next.y - previous.y);

            MathUtil.normalizeVector(direction);

            directions.add(direction);
        }

        int numDirections = directions.size();

        // Sum up all of the direction vectors
        for (Point direction: directions) {
            averageDirection.x += direction.x;
            averageDirection.y += direction.y;
        }

        // Divide to compute an average.
        averageDirection.x /= numDirections;
        averageDirection.y /= numDirections;

        MathUtil.normalizeVector(averageDirection);

        return averageDirection;
    }

    /**
     * Computes the average velocity from a list of points representing past locations that an
     * object has been at.
     * @param points a list of points that the object has been at
     * @return a Point object which represents the average velocity of the given points
     */
    // TODO: This function assumes an equal amount of time passes between points, which is not
    // necessarily true.
    public static double computeAverageSpeed(List<Point> points) {
        int numPoints = points.size();

        // If we have fewer than 2 points, then the velocity is 0 and the speed is 0.
        if (numPoints <= 1) {
            return 0;
        }

        Point averageVelocity = new Point();

        for (int i = 0; i < numPoints - 1; i++) {
            Point previous = points.get(i);
            Point next = points.get(i + 1);
            Point direction = new Point(next.x - previous.x, next.y - previous.y);

            averageVelocity.x += direction.x;
            averageVelocity.y += direction.y;
        }

        averageVelocity.x /= numPoints;
        averageVelocity.y /= numPoints;

        // Convert the velocity vector into a scalar to get speed.
        // TODO: Honesty, why do we need to multiply by VELOCITY_SCALE here?
        return VELOCITY_SCALE * MathUtil.computeVectorMagnitude(averageVelocity);
    }
}
