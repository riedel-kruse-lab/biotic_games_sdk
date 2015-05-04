package edu.stanford.riedel_kruse.bioticgamessdk;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains utility functions for image processing tasks common to Biotic Games.
 */
public class ImageProcessing
{
//These are the default value
    private static Scalar LOWER_HSV_THRESHOLD = new Scalar(50, 50, 0);
    private static Scalar UPPER_HSV_THRESHOLD = new Scalar(96, 200, 255);

//    private static Scalar LOWER_HSV_THRESHOLD = new Scalar(50,50,50);
//    private static Scalar UPPER_HSV_THRESHOLD = new Scalar(100,100,255);

    private static Size STRUCTURING_ELEMENT_SIZE = new Size(5, 5);
    private static Mat MORPHOLOGICAL_STRUCTURING_ELEMENT = Imgproc
            .getStructuringElement(Imgproc.MORPH_ELLIPSE, STRUCTURING_ELEMENT_SIZE);

    /**
     * Given an image, returns a list of points where each point is believed to be the location of a
     * Euglena.
     * @param image the image to search for Euglena in
     * @return a list of points where each point is believed to be the location of a Euglena
     */
    // TODO: Is image passed by value or by reference? This will possibly create a bug if modifying
    // the Mat in the argument modifies the original data as well.
    public static List<Point> findEuglena(Mat image) {
        // Convert image representation into hue-saturation-value format.
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

        // Threshold the hue-saturation-values to the range that is typically green. This helps us
        // to filter the image for just things that look like they might be Euglena.
        Core.inRange(image, LOWER_HSV_THRESHOLD, UPPER_HSV_THRESHOLD, image);

        // Morphological closing: closes small holes inside of objects in the image, reducing noise.
        Imgproc.dilate(image, image, MORPHOLOGICAL_STRUCTURING_ELEMENT);
        Imgproc.erode(image, image,MORPHOLOGICAL_STRUCTURING_ELEMENT);

        // Morphological opening: helps remove small objects, reducing noise.
        Imgproc.erode(image, image, MORPHOLOGICAL_STRUCTURING_ELEMENT);
        Imgproc.dilate(image, image, MORPHOLOGICAL_STRUCTURING_ELEMENT);

        // Detect the contours in the image.
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE);

        // Compute the centroid of each contour. These centroids represent where we thing the
        // Euglena are.
        List<Point> centroids = new ArrayList<Point>();
        for (MatOfPoint contour : contours) {
            Moments p = Imgproc.moments(contour, false);
            Point centroid = new Point(p.get_m10() / p.get_m00(), p.get_m01() / p.get_m00());
            centroids.add(centroid);
        }

        return centroids;
    }

    /**
     * Overloaded. Given an image, returns a list of points where each point is believed to be the location of a
     * Euglena. Given two Scalars, sets the upper and lower HSV bounds
     * @param image the image to search for Euglena in
     * @param upper the upper HSV bound
     * @param lower the lower HSV bound
     * @return a list of points where each point is believed to be the location of a Euglena
     */
    public static List<Point> findEuglena(Mat image, Scalar upper, Scalar lower) {
        LOWER_HSV_THRESHOLD = lower;
        UPPER_HSV_THRESHOLD = upper;

        // Convert image representation into hue-saturation-value format.
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

        // Threshold the hue-saturation-values to the range that is typically green. This helps us
        // to filter the image for just things that look like they might be Euglena.
        Core.inRange(image, LOWER_HSV_THRESHOLD, UPPER_HSV_THRESHOLD, image);

        // Morphological closing: closes small holes inside of objects in the image, reducing noise.
        Imgproc.dilate(image, image, MORPHOLOGICAL_STRUCTURING_ELEMENT);
        Imgproc.erode(image, image,MORPHOLOGICAL_STRUCTURING_ELEMENT);

        // Morphological opening: helps remove small objects, reducing noise.
        Imgproc.erode(image, image, MORPHOLOGICAL_STRUCTURING_ELEMENT);
        Imgproc.dilate(image, image, MORPHOLOGICAL_STRUCTURING_ELEMENT);

        // Detect the contours in the image.
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE);

        // Compute the centroid of each contour. These centroids represent where we thing the
        // Euglena are.
        List<Point> centroids = new ArrayList<Point>();
        for (MatOfPoint contour : contours) {
            Moments p = Imgproc.moments(contour, false);
            Point centroid = new Point(p.get_m10() / p.get_m00(), p.get_m01() / p.get_m00());
            centroids.add(centroid);
        }

        return centroids;
    }

    /**
     * Finds Euglena within just the given region of interest of the given image.
     * @param image the image to find Euglena in
     * @param roi a rectangle whose properties represent the region of interest
     * @return a list of points where each point is believed to be the location of a Euglena in the
     * region of interest
     */
    public static List<Point> findEuglenaInRoi(Mat image, Rect roi) {
        return findEuglenaInRoi(image, roi.x, roi.y, roi.width, roi.height);
    }

    /**
     * Overloaded. Finds Euglena within just the given region of interest of the given image.
     * @param image the image to find Euglena in
     * @param roi a rectangle whose properties represent the region of interest
     * @param upper the upper HSV bound
     * @param lower the lower HSV bound
     * @return a list of points where each point is believed to be the location of a Euglena in the
     * region of interest
     */
    public static List<Point> findEuglenaInRoi(Mat image, Rect roi, Scalar upper, Scalar lower) {
        return findEuglenaInRoi(image, roi.x, roi.y, roi.width, roi.height, upper, lower);
    }

    /**
     * Finds Euglena within just the given region of interest of the given image.
     * @param image the image to find Euglena in
     * @param x the x coordinate for the top-left corner of the region of interest
     * @param y the y coordinate for the top-left corner of the region of interest
     * @param width the width of the region of interest
     * @param height the height of the region of interest
     * @return a list of points where each point is believed to be the location of a Euglena in the
     * region of interest
     */
    public static List<Point> findEuglenaInRoi(Mat image, int x, int y, int width, int height) {
        List<Point> points = findEuglena(image.submat(y, y + height, x,
                x + width));

        // TODO: Could optimize by translating coordinates in the same pass as centroids are being
        // computed. Not a serious concern at the moment, though.
        // Translate all coordinates from ROI-local coordinates to whole image coordinates.
        for (Point point : points) {
            point.x = point.x + x;
            point.y = point.y + y;
        }

        return points;
    }

    /**
     * Overloaded. Finds Euglena within just the given region of interest of the given image.
     * @param image the image to find Euglena in
     * @param x the x coordinate for the top-left corner of the region of interest
     * @param y the y coordinate for the top-left corner of the region of interest
     * @param width the width of the region of interest
     * @param height the height of the region of interest
     * @param upper the upper HSV bound
     * @param lower the lower HSV bound
     * @return a list of points where each point is believed to be the location of a Euglena in the
     * region of interest
     */
    public static List<Point> findEuglenaInRoi(Mat image, int x, int y, int width, int height, Scalar upper, Scalar lower) {
        List<Point> points = findEuglena(image.submat(y, y + height, x,
                x + width), upper, lower);

        // TODO: Could optimize by translating coordinates in the same pass as centroids are being
        // computed. Not a serious concern at the moment, though.
        // Translate all coordinates from ROI-local coordinates to whole image coordinates.
        for (Point point : points) {
            point.x = point.x + x;
            point.y = point.y + y;
        }

        return points;
    }
}