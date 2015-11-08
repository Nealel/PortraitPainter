import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Represents a single image, defined by a list of shapes.
 */
public class Image {
	//static Image parameters
	private static double cr; //crossover rate
	private static int size; //image size
	private static int nShapes; //number of shapes in image
	private static float opacity; //opacity of rectangles in the image
	private static float bg; //background colour
	private static double circs; //percentage of shapes which are circles
	private static double tris; //and triangles
	private static Random r; 
	
	//parameters which define this image in particular
	private double fitness; //image fitness
	private ArrayList<Shape> shapes; //stores shapes
	
	/**
	 * Constructs a new, randomised image.
	 */
	public Image(){
		shapes = new ArrayList<Shape>();

		//fill shape list with nShapes randomised shapes, according to given shape distribution.
		for (int i=0; i<nShapes; i++){
			double d = r.nextDouble();
			
			if ( d < circs){ //chance of a circle
				shapes.add(new Circle());
			}
			
			else if (d < tris + circs){ // chance of a triangle
				shapes.add(new Triangle());
			}
			
			else{ // chance of a square
				shapes.add(new Square());
			}
		}
	}

	/**
	 * Construct child Image from parents. Performs mutation and cross-over functions.
	 * @param p1 Parent 1
	 * @param p2 Parent 2
	 */
	public Image(Image p1, Image p2){
		shapes = new ArrayList<Shape>();

		//for each shape, crossover% chance to inherit from parent 1, else parent 2.
		for (int i = 0; i<nShapes ; i++){
			if (r.nextDouble() > cr){
				shapes.add(p1.shapes.get(i).clone());
			}
			else{
				shapes.add(p2.shapes.get(i).clone());
			}
		}
	}
	
	/**
	 * Sets parameters for all images.
	 * @param size Image size
	 * @param nShapes Number of shapes in the image
	 * @param cr Crossover rate when breeding
	 * @param opacity Opacity of shapes
	 * @param bg Background colour of image
	 * @param circs Proportion of circles
	 * @param tris Proportion of triangles
	 */
	public static void setParameters(int size, int nShapes, double cr, double opacity, double bg, double circs, double tris){
		Image.size = size;
		Image.cr = cr;
		Image.opacity = (float)opacity;
		Image.bg = (float)bg;
		Image.nShapes = nShapes;
		Image.r = new Random();
		Image.circs = circs;
		Image.tris = tris;
	}

	/**
	 * draws visual image
	 * @return BufferedImage the visual image that this Image represents
	 */
	public BufferedImage draw(){
		//set up canvas
		BufferedImage canvas = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = canvas.createGraphics();
		g.setColor(new Color(bg,bg,bg));
		g.fillRect(0, 0, 100, 100);

		//draw each shape
		for (int i=0; i<nShapes; i++){
			Shape s  = shapes.get(i);
			
			//set colour
			float[] cols = s.getColours();
			
			if (cols.length == 1){
				g.setColor(new Color(cols[0], cols[0], cols[0],opacity));
			}
			else{
				g.setColor(new Color(cols[0], cols[1], cols[2],opacity));
			}
			
			//Draw as circle
			if (s instanceof Circle){
				int[] p = s.getPoints();
				g.fillOval(p[0],p[1],p[2],p[3]);
			}
			
			//Draw as triangle
			else if (s instanceof Triangle){ 
				int[] p = s.getPoints();
				int[]x = new int[]{p[0],p[2],p[4]};
				int[]y = new int[]{p[1],p[3],p[5]};
				g.fillPolygon(x, y, 3);
			}
			
			//Draw as square
			else if (s instanceof Square){
				int[] p = s.getPoints();
				g.fillRect(p[0],p[1],p[2],p[3]);
			}
		}
		
		return canvas;
	}
	
	/**
	 * @return this image's fitness
	 */
	public double getFitness(){
		return fitness;
	}
	
	/**
	 * sets this image's fitness
	 * @param score fitness
	 */
	public void setFitness(double score){
		fitness = score;
	}
}
