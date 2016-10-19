/*
 *
 * assign4/ImageTransform.java
 * (c) 2008--15
 *
 */

package assign4;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * The ImageTransform class represents a warping image transform.
 *
 * @author Michael Janvier
 */

public class ImageTransform {
    /**
     * pixels of the source image (in row-major order)
     */
    int[] srcPixels;

    /**
     * width of the source image
     */
    int width;
    /**
     * height of the source image
     */
    int height;

    /**
     * control point for the warping
     */
    Point controlPt;

    /**
     * Vanilla constructor for the ImageTransform class 
     */
    public ImageTransform(int[] srcPixels, int width, int height, 
			      Point controlPt) {
	this.srcPixels = srcPixels;     // scan-line is width-many pixels
	this.width = width;
	this.height = height;
   	this.controlPt = controlPt;
    }

    /**
     * The transform method implements the high-level warping logic
     *
     * @return int[] containing the pixels of the warped image.  
     *         Notice that the scan-line for the warped image is the
     *         same as for the original image, that is,
     *         this.width-many pixels.
     */
    public int[] transform() {
	/*
	 * Recall from the assignment handout how the warping works:
	 * The image is split into 4 quadrants (rectangles with the
	 * same dimension). The control point defines 4
	 * quadrilaterals. (Each quadrilateral has one vertex
	 * corresponding to the control points. The remaining vertices
	 * correspond to point on the border of the image, and are the
	 * same as the vertices for the corresponding quadrant.)  For
	 * each quadrant, we invoke warpRegion which maps the
	 * quadrilateral into the rectangle.
	 */

	// create the destination pixel array
	int[] newPixels = new int[srcPixels.length];
	
	Rectangle q1 = new Rectangle(width/2, 0, width/2, height/2);
	Rectangle q2 = new Rectangle(0, 0, width/2, height/2);
	Rectangle q3 = new Rectangle(0, height/2, width/2, height/2);
	Rectangle q4 = new Rectangle(width/2, height/2, width/2, height/2);

	

	// Quadrants are in accordance to unit circle
	// Quadrant 1

	warpRegion(newPixels, q1, new Point(width/2, 0), new Point(width, 0), controlPt, new Point(width, height/2));


	
	// Quadrant 2

	warpRegion(newPixels, q2, new Point(0, 0), new Point(width/2, 0), new Point(0, height/2), controlPt);


	// Quadrant 3

        warpRegion(newPixels, q3, new Point(0, height/2), controlPt, new Point(0, height), new Point(width/2, height));



	// Quadrant 4

	warpRegion(newPixels, q4, controlPt, new Point(width, height/2), new Point(width/2, height), new Point(width, height));

	

	// Return the result of warping transformation

	return newPixels;
    }

    /**
     * The warpRegion method performs the actual image warping
     * transformation.
     * 
     * @param dstPixels   int[] where the warped image is being stored 
     * @param r           Rectangle defining the (logical) quadrant
     *                    within the destination pixels that is to be
     *                    computed by this invocation of the method
     * @param nw          North-west (upper-left) corner of the
     *                    quadrilater in the original image that is
     *                    being warped
     * @param ne          North-east (upper-right) corner of the
     *                    quadrilater in the original image that is
     *                    being warped
     * @param sw          South-west (lower-left) corner of the
     *                    quadrilater in the original image that is
     *                    being warped
     * @param se          South-east (lower-right) corner of the
     *                    quadrilater in the original image that is
     *                    being warped
     */
    protected void warpRegion(int[] dstPixels, Rectangle r, 
			      Point nw, Point ne, Point sw, Point se) {
	System.out.println("Warping quadrilateral at ");
	System.out.println(nw);
	System.out.println(ne);
	System.out.println(sw);
	System.out.println(se);
	System.out.println("into rectangle");
	System.out.println(r);

	int offset = (int)(r.getX() + r.getY()*width);
	System.out.println(offset);
	int startX = (int)r.getX();
	int startY = (int)r.getY();

        for(int y = 0; y < r.getHeight(); y++){
	    for(int x = 0; x < r.getWidth(); x++){
		double alpha = (x/(r.getWidth()));
		double beta = (y/(r.getHeight()));
		
		int primePx = (int)(nw.getX() + (ne.getX() - nw.getX())*alpha + (sw.getX() - nw.getX())*beta + (nw.getX() - ne.getX() + se.getX() - sw.getX())*alpha*beta);
		int primePy = (int)(nw.getY() + (ne.getY() - nw.getY())*alpha + (sw.getY() - nw.getY())*beta + (nw.getY() - ne.getY() + se.getY() - sw.getY())*alpha*beta);
		//System.out.println(primePx + "  " + primePy);
		dstPixels[offset + y*width + x] = srcPixels[offset + (primePy-startY)*width + primePx-startX];
	    }
	}
    }

    /**
     * Return the pixel at (x,y)
     */
    public int getPixel(int x, int y) {
	return srcPixels[x + y*width];
    }

    /** 
     * Better version of the getPixel method with color interpolation 
     * (extra-credit 1).
     */
    public int getPixel(double x, double y) {
	/* 
	 * The idea of color interpolation is the following.  Say that
	 * you want to access the fractionary pixel at coordinates
	 * (3.7,4.4).  The default implementation (no color
	 * interpolation, see below) just truncates things and get the
	 * pixel at (3,4).  This is unsatisfactory, because we are
	 * loosing the information encoded in the fractional part: in
	 * particular, the pixel we wanted had more overlap (42%) with
	 * (4,4) than with (3,4) (18%).
         *
         * Thresholdizing the "fractionary" pixel so that it agrees
         * with the "actual" pixel that maximizes overlap is a better
         * option than the default implementation, but it still is not
         * very "smooth".  Instead, color interpolation averages the
         * four real pixels that have overlap with the fractionary
         * pixel (in the example, these are (3,4), (4,4), (3, 5), and
         * (4,5)), weighting them by their relative overlap ratio
         * (respectively 18%, 42%, 12%, and 28%).
         *
         * A complication with interpolating pixels arises because of
         * the way colors are encode as Java int's.  In the RGB model,
         * the color of each pixel is described by three 8-bit
         * unsigned numbers, respectively for the red, green, and blue
         * components of the color (hence the name).  These three
         * 8-bit unsigned are packed together into a Java int as
         * follows: the "blue" byte is stored in the least-significant
         * 8 bits of the int; the "green" byte is stored in the 8 bits
         * immediately to the left; and the "red" byte is stored in
         * the 8 bits further to the left.  (The 8 most-significant
         * bits of the int do not matter under the RGB color model.)
         * 
         * So in order to do color interpolation, you will have to do
         * bitwise manipulations to get to the right values; scale
         * them by the corresponding weights, yielding a
         * floating-point value; convert back to whole numbers; and
         * pack the three components back into the int.  You might
         * want to define a helper class (or at least some helper
         * static methods) for carrying out this "color arithmetic".
	 * 
	 */

	//Default implementation is no interpolation.

	
	return getPixel ((int) x, (int) y);
    }
}
