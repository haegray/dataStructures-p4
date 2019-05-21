
/**
 * @author Helena Gray
 * @version 11-29-2018
 * 
 * This class represents a detector for "blobs" that are collections of pixels of a certain color.
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.AbstractCollection;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import javax.swing.JPanel;

public class Detector extends JPanel {
	/**
	 * @param c1
	 *            a color
	 * @param c2
	 *            another color, may be the same as c1
	 * @return the distance between two colors as an integer value
	 * 
	 *         This method determines the distance between two colors as a value
	 *         between 0 and 100.
	 */
	public static int getDifference(Color c1, Color c2) {
		if (c1.equals(c2)) {
			return 0;
		} else {
			int distanceRed = (int) Math.pow((c1.getRed() - c2.getRed()), 2);
			int distanceBlue = (int) Math.pow((c1.getBlue() - c2.getBlue()), 2);
			int distanceGreen = (int) Math.pow((c1.getGreen() - c2.getGreen()), 2);
			int distance = (int) Math
					.floor(((distanceRed + distanceBlue + distanceGreen) / (3 * Math.pow(255, 2))) * 100);
			return distance;
		}
	}

	/**
	 * @param image
	 *            is a BufferedImage object of the picture we will use for blob
	 *            detection
	 * @param c
	 *            the color of the blob(s) we want to detect
	 * @param okDist
	 *            indicates the acceptable "distance" between the pixel and the
	 *            color c (inclusive).
	 * 
	 *            This method colors the pixels white (if the pixel is not color
	 *            we want) or black (if it's the color we want). okDist
	 */
	public static void thresh(BufferedImage image, Color c, int okDist) {
		int width = image.getWidth();
		int height = image.getHeight();
		for (int i = 0; i < width; i++) {
			for (int k = 0; k < height; k++) {
				int pixelId = getId(image, i, k);
				Pixel p = getPixel(image, pixelId);
				Color c2 = getColor(image, p);

				int distance = getDifference(c2, c);
				if (distance > okDist) {
					image.setRGB(i, k, Color.WHITE.getRGB());
				} else {
					image.setRGB(i, k, Color.BLACK.getRGB());
				}
			}
		}
	}

	/**
	 * @param image
	 *            is a BufferedImage object of the picture we will use for blob
	 *            detection
	 * @param ds
	 *            disjoint sets object to hold blobs
	 * @param pixelId
	 *            a pixel whose neighboring sets will be found
	 * @return a pair object representing the blobs above and to the left of the
	 *         pixel's set
	 * 
	 *         Given an image, a disjoint set, and a pixel (defined by its id),
	 *         this method returns a pair which contains (a) the blob above and
	 *         (b) the blob to the left (each represented by their _root_ ids)
	 *         If there is no above/left neighbor, then the appropriate part of
	 *         the pair is null
	 */
	public static Pair<Integer, Integer> getNeighborSets(BufferedImage image, DisjointSets<Pixel> ds, int pixelId) {
		int baseRoot = ds.find(pixelId);
		Integer rootAbove = null;
		if (baseRoot >= image.getWidth()) {
			rootAbove = baseRoot - image.getWidth();
			rootAbove = ds.find(rootAbove);
		}
		Integer rootLeft = baseRoot - 1;
		if (baseRoot % image.getWidth() != 0) {
			rootLeft = ds.find(rootLeft);
		} else {
			rootLeft = null;
		}

		Pair<Integer, Integer> returnedPair = new Pair(rootAbove, rootLeft);
		return returnedPair;
	}

	/**
	 * This method detects blobs of the desired color in an image.
	 */
	public void detect() {
		// threshold the image
		thresh(img, blobColor, okDist);

		ArrayList<Pixel> pixels = new ArrayList();
		int width = img.getWidth();
		int height = img.getHeight();

		// make your DS data structure
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				int pixelId = getId(img, k, i);
				Pixel p = getPixel(img, pixelId);
				pixels.add(p);
			}
		}

		ds = new DisjointSets(pixels);
		// for all pixel ids
		// walk through the image and perform
		// finds and unions where appropriate
		for (int i = 0; i < img.getWidth() * img.getHeight(); i++) {
			int rootOriginal = ds.find(i);
			Pair pair = getNeighborSets(img, ds, rootOriginal);
			Color colorOriginal = getColor(img, pixels.get(i));

			if (pair.a != null) {
				Color colorAbove = getColor(img, pixels.get((int) pair.a));
				if (colorOriginal.equals(colorAbove)) {
					ds.union(rootOriginal, (int) pair.a);
					rootOriginal = ds.find(i);
				}
			}

			if (pair.b != null) {
				Color colorLeft = getColor(img, pixels.get((int) pair.b));
				if (colorOriginal.equals(colorLeft) && rootOriginal != (int) pair.b) {
					ds.union(rootOriginal, (int) pair.b);
				}
			}
		}

		// After this, the instance variable this.ds should contain your color
		// blobs
		// (and non-color areas) for this.img
	}

	/**
	 * @param outputFileName
	 *            this is the name for the output image with k blobs colored in
	 *            a gradient of the desired color
	 * @param outputECFileName
	 *            this is the name for the output image where the largest blob
	 *            is enclosed in a box This method recolors all in the k largest
	 *            blobs and saves output.
	 * @param k
	 *            the number of blobs we are interested in
	 */
	public void outputResults(String outputFileName, String outputECFileName, int k) {
		if (k < 1) {
			throw new IllegalArgumentException(new String("! Error: k should be greater than 0, current k=" + k));
		}
		// get all the roots from the DS
		TreeSet roots = new TreeSet();
		for (int i = 0; i < ds.sizeDisjoint(); i++) {
			roots.add(ds.find(i));
		}
		ArrayList<Set> setsRoots = new ArrayList<Set>();

		// using the roots, collect all sets of pixels and sort them by size
		for (Object i : roots) {
			Integer root = (Integer) i;
			Set s = ds.get(root);
			Pixel p = getPixel(img, (int) root);
			Color c2 = getColor(img, p);
			if (c2.equals(Color.BLACK)) {
				setsRoots.add(s);
			}
		}

		Collections.sort(setsRoots, new SortBySize());

		if (setsRoots.size() < k) {
			k = setsRoots.size();
		}
		System.out.println(k + "/" + setsRoots.size());
		Integer rectwidth = null;
		Integer rectheight = null;
		Integer top = null;
		Integer bottom = null;
		Integer left = null;
		Integer right = null;

		// recolor the k-largest blobs from black to a color from getSeqColor()
		// and output all blobs to console
		for (int i = 0; i < k; i++) {
			Color c = getSeqColor(i, k);
			Set<Pixel> s = setsRoots.get(i);
			for (Pixel p : s) {
				if (i == 0) {
					if (left == null || p.a < left) {
						left = p.a;
					}
					if (right == null || p.a > right) {
						right = p.a;
					}
					if (top == null || p.b < top) {
						top = p.b;
					}
					if (bottom == null || p.b > bottom) {
						bottom = p.b;
					}
				}
				img.setRGB(p.a, p.b, c.getRGB());
			}
			System.out.println("Blob " + (i + 1) + ": " + s.size() + " pixels");
		}

		if (right != null && left != null && bottom != null && top != null) {
			rectwidth = (right - left) + 3;
			rectheight = (bottom - top) + 3;

		}

		Graphics g = img.getGraphics();
		paint(g);

		// save output image -- provided
		try {
			File ouptut = new File(outputFileName);
			ImageIO.write(this.img, "png", ouptut);
			System.err.println("- Saved result to " + outputFileName);
		} catch (Exception e) {
			System.err.println("! Error: Failed to save image to " + outputFileName);
		}

		reloadImage();
		Graphics2D g2 = img.createGraphics();

		if (rectwidth != null && rectheight != null) {
			Rectangle2D shape = new Rectangle2D.Double(left - 1, top - 1, rectwidth, rectheight);
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.BLACK);
			g2.draw(shape);
		}

		try {
			File ouptut = new File(outputECFileName);
			ImageIO.write(this.img, "png", ouptut);
			System.err.println("- Saved result to " + outputECFileName);
		} catch (Exception e) {
			System.err.println("! Error: Failed to save image to " + outputECFileName);
		}

	}

	/**
	 * Comparator class to sort disjoint sets by size
	 */
	private static class SortBySize implements Comparator<Set> {

		/**
		 * @param a
		 *            one set to compare
		 * @param b
		 *            the other set to compare
		 * @return return the difference in size
		 */
		public int compare(Set a, Set b) {
			return b.size() - a.size();
		}
	}

	/**
	 * @param args
	 *            command line arguments Main method
	 */
	public static void main(String[] args) {

		File imageFile = new File("../input/04_Circles.png");
		BufferedImage img = null;

		try {
			img = ImageIO.read(imageFile);
		} catch (IOException e) {
			System.err.println("! Error: Failed to read " + imageFile + ", error msg: " + e);
			return;
		}

		Pixel p = getPixel(img, 110); // 100x100 pixel image, pixel id 110
		System.out.println(p.a); // x = 10
		System.out.println(p.b); // y = 1
		System.out.println(getId(img, p)); // gets the id back (110)
		System.out.println(getId(img, p.a, p.b)); // gets the id back (110)

	}

	// Data
	public BufferedImage img; // this is the 2D array of RGB pixels
	private Color blobColor; // the color of the blob we are detecting
	private String imgFileName; // input image file name
	private DisjointSets<Pixel> ds; // the disjoint set
	private int okDist; // the distance between blobColor and the pixel which
	// "still counts" as the color

	/**
	 * @param imgfile
	 *            the name of the image file
	 * @param blobColor
	 *            the color of the blob to be detected
	 * @param okDist
	 *            the thresh hold from the color that is acceptable for a color
	 *            considered the "same color" as the blobColor Constructor -
	 *            reads image from file
	 */
	public Detector(String imgfile, Color blobColor, int okDist) {
		this.imgFileName = imgfile;
		this.blobColor = blobColor;
		this.okDist = okDist;

		reloadImage();
	}

	/**
	 * Constructor - reads image from file
	 */
	public void reloadImage() {
		File imageFile = new File(this.imgFileName);

		try {
			this.img = ImageIO.read(imageFile);
		} catch (IOException e) {
			System.err.println("! Error: Failed to read " + this.imgFileName + ", error msg: " + e);
			return;
		}
	}

	/**
	 * @param g
	 *            JPanel function
	 */
	public void paint(Graphics g) {
		g.drawImage(this.img, 0, 0, this);
	}

	/**
	 * Convenient helper class representing a pair of things
	 */
	private static class Pair<A, B> {
		A a;
		B b;

		/**
		 * @param a
		 *            the first thing
		 * @param b
		 *            the second thing Constructor for pair class
		 */
		public Pair(A a, B b) {
			this.a = a;
			this.b = b;
		}
	}

	/**
	 * A pixel is a set of locations a (aka. x, distance from the left) and b
	 * (aka. y, distance from the top)
	 */
	private static class Pixel extends Pair<Integer, Integer> {
		public Pixel(int x, int y) {
			super(x, y);
		}
	}

	/**
	 * @param image
	 *            the image containing the pixel
	 * @param p
	 *            the pixel to be converted
	 * @return the id of the pixel
	 */

	private static int getId(BufferedImage image, Pixel p) {
		return getId(image, p.a, p.b);
	}

	/**
	 * @param image
	 *            the image containing the pixel
	 * @param id
	 *            the id of the pixel
	 * @return the pixel corresponding to the id
	 */
	private static Pixel getPixel(BufferedImage image, int id) {
		int y = id / image.getWidth();
		int x = id - (image.getWidth() * y);

		if (y < 0 || y >= image.getHeight() || x < 0 || x >= image.getWidth())
			throw new ArrayIndexOutOfBoundsException();

		return new Pixel(x, y);
	}

	/**
	 * @param image
	 *            the image containing the location
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return the id of the pixel at the location in the image
	 */
	private static int getId(BufferedImage image, int x, int y) {
		return (image.getWidth() * y) + x;
	}

	/**
	 * @param image
	 *            the image containing the pixel
	 * @param p
	 *            the pixel whose color we want to know
	 * @return color of the pixel
	 */
	private static Color getColor(BufferedImage image, Pixel p) {
		return new Color(image.getRGB(p.a, p.b));
	}

	/**
	 * @param i
	 *            the ith largest blob
	 * @param max
	 *            the number of blobs we want to recolor
	 * @return the color that the ith largest blob should be changed to
	 */
	private Color getSeqColor(int i, int max) {
		if (i < 0)
			i = 0;
		if (i >= max)
			i = max - 1;

		int r = (int) (((max - i + 1) / (double) (max + 1)) * blobColor.getRed());
		int g = (int) (((max - i + 1) / (double) (max + 1)) * blobColor.getGreen());
		int b = (int) (((max - i + 1) / (double) (max + 1)) * blobColor.getBlue());

		if (r == 0 && g == 0 && b == 0) {
			r = g = b = 10;
		} else if (r == 255 && g == 255 && b == 255) {
			r = g = b = 245;
		}

		return new Color(r, g, b);
	}
}