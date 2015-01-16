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
            double distance = Math.sqrt(Math.pow(candidate.x - point.x, 2) +
                    Math.pow(candidate.y - point.y, 2));

            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = candidate;
            }
        }

        return closestPoint;
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
    public static Point computeAverageDirection(List<Point> points) {
        int numPoints = points.size();

        Point averageDirection = new Point();
        List<Point> directions = new ArrayList<Point>();

        // We need at least two points in order to compute a direction.
        if (numPoints <= 1) {
            return null;
        }

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
}
