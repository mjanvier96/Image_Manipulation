/*
 *
 * assign4/Main.java
 * (c) 2008--2015
 *
 */

package assign4;

import java.awt.image.BufferedImage;
import java.awt.Point;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * The Main class implements the command-line interface for an
 * image-warping tool.
 *
 * @author Michael Janvier
 */
public class Main {

    /**
     * The main method expects three Strings in input, and produces a
     * warped image in output.
     *
     * @param args[0] A String with the filename for the image file to
     *                be warped
     * @param args[1] A String with the integer x-coordinate for the
     *                point controlling the warping
     * @param args[2] A String with the integer y-coordinate for the
     *                point controlling the warping
     *
     * @return        nothing really, but a warped image is stored
     *                in a file named "warped_<orig-file>", where
     *                <orig-file> is the String passed as args[0]
     *
     */
    public static void main(String[] args) throws IOException {

        /* A note about testing your code: 
	 * ==============================
	 *
	 * Download the test files 'checkers.jpg' and
	 * 'warped_checkers.jpg' from the URLs below, and make sure
	 * that executing your code with the following arguments
	 * produces a file that looks like 'warped_checkers.jpg':
	 *
	 *   java assign4.Main checkers.jpg 90 45
	 *
	 * URLs for the test images:
	 *
	 *   http://www.cs.stevens.edu/~nicolosi/classes/15fa-cs181/assign4/checkers.jpg
	 *   http://www.cs.stevens.edu/~nicolosi/classes/15fa-cs181/assign4/warped_checkers.jpg
	 */
	
 
	String fileName = args[0];
	Point controlPt = new Point(Integer.parseInt(args[1]),
				    Integer.parseInt(args[2]));
	// read the input image
	BufferedImage image = ImageIO.read(new File(fileName));
	// extract width/height and pixels
	int width = image.getWidth();
	int height = image.getHeight();
	int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
	
	System.out.println("# of pixels = " + pixels.length);
	System.out.println("width = " + width);
	System.out.println("height = " + height);
	System.out.println("last pixel at " + (width-1 + (height-1)*width));
	// set-up a warping image transform
	ImageTransform imageTransform = new ImageTransform(pixels, width, height, controlPt);
	// apply transformation
	int[] dstPixels = imageTransform.transform();
	// update image pixels
	System.out.println(pixels);
	image.setRGB(0, 0, width, height, dstPixels, 0, width);
	// save the warped image
	ImageIO.write(image, "jpg", new File("warped_" + fileName));
    }
}
