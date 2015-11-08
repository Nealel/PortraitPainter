
import java.util.Random;

/**
 * Defined a shape of undefined type, to be drawn on an Image.
 * Abstract class: use Circle, Square, or Triangle in stead of Shape directly.
 *
 */
public abstract class Shape {
	//this shape's parameters
	protected int[] points; //points of the polygon
	private float[] cols; //colours
	
	//size limits
	protected static int maxSize;
	protected static int minSize;
	
	//colour limits
	private static float[][] palette; 
	
	//other
	protected static int size; //image size
	protected static double mr; //mutation rate
	protected static Random r;
	protected static boolean preventThinning;
	
	/**
	 * Creates a randomized shape
	 * @param size image size
	 * @param mr mutation rate
	 */
	public Shape(){
		cols = new float[palette[1].length];
	}
	
	/**
	 * Sets the parameters for the shape class. Used by display to enact user preferences.
	 * @param size Image Size
	 * @param mr Mutation Rate
	 * @param palette Colour limits
	 * @param minSize Maximum shape size
	 * @param maxSize Minimum shape size
	 */
	public static void setParameters(int size, double mr, float[][] palette, int minSize, int maxSize, boolean preventThinning){
		Shape.size = size;
		Shape.mr = mr;
		Shape.r = new Random();
		Shape.palette = palette;
		Shape.minSize = minSize;
		Shape.maxSize = maxSize;
		Shape.preventThinning = preventThinning;
	}
	
	/*
	 * Randomly sets shape's colours according to the palette.
	 */
	protected void randomizeShape(){
		for (int i=0; i<cols.length; i++){
			float min = palette[0][i];
			float max = palette[1][i];
			cols[i] = (r.nextFloat()*(max-min)) + min;
		}
	}
	
	/**
	 * Copies this shape, with mutation
	 */
	public abstract Shape clone();
	
	/**
	 * Copies this shape, and performs mutation
	 * @return Shape the copy
	 */
	public Shape clone(Shape s){
		
		s.points = new int[points.length];
		for (int i=0; i<points.length; i++){
			s.points[i] = points[i];
		}
		for (int i=0; i<cols.length; i++){
			s.cols[i] = cols[i];
		}
		s.mutate();
		return s;
	}
	
	/**
	 * Mutates the shape
	 */
	protected void mutate(){
		for (int i=0; i<cols.length; i++){
			if (r.nextFloat()<mr){
				cols[i] = (int)Math.min(1, Math.max(0, cols[i] + r.nextGaussian()*0.03));
			}
		}
		//lower-than-mr chance to completely replace this shape with a new shape.
		if (r.nextFloat()<mr/20){
			randomizeShape();
		}
	}
	
	/**
	 * @return this shape's colours
	 */
	public float[] getColours(){
		return cols;
	}
	
	/**
	 * @return this shape's list of points
	 */
	public int[] getPoints(){
		return points;
	}
	
}
