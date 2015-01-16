package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Point;

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
}
