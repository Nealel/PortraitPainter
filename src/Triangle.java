/**
 * defines a Triangle shape, to be drawn on an Image.
 *
 */
public class Triangle extends Shape {

	private int[] polarities;

	/**
	 * Creates a randomly parameterised Triangle object
	 */
	public Triangle(){
		super();
		points = new int[6];
		polarities = new int[]{1,1,1,1};

		randomizeShape();
	}

	/**
	 * Randomly sets all points and colours in the shape to random allowed valued. 
	 */
	protected void randomizeShape(){
		super.randomizeShape();

		//primary corner
		points[0] = r.nextInt(size);
		points[1] = r.nextInt(size);

		//distances from primary corner to supporting corners
		for (int i=2; i<points.length; i++){
			points[i] = r.nextInt(maxSize-minSize)+minSize;
		}


		//polarities
		if (preventThinning){
			polarities[r.nextInt(3)] = -1;
		}
		else{
			for (int i=0; i<polarities.length; i++){
				if (r.nextBoolean()){
					polarities[i] = 1;
				}
				else{
					polarities[i] = -1;
				}
			}
		}
	}

	/**
	 * Copies this triangle, with mutation
	 */
	public Shape clone(){
		Shape s = new Triangle();
		s = super.clone(s);
		return s;
	}

	/**
	 * Mutates the points and colours in this shape.
	 */
	protected void mutate(){
		super.mutate();

		for (int i=0; i<2; i++){
			points[i] = Math.min((size-(maxSize-minSize)/2), Math.max(0, (int)(points[i] + r.nextGaussian()*3)));
		}

		for (int i=2; i<points.length; i++){
			if (r.nextFloat() < mr){
				points[i] = Math.min(maxSize, Math.max(minSize, (int)(points[i] + r.nextGaussian()*3)));
			}
		}

	}

	/**
	 * @return array of points
	 */
	public int[] getPoints(){
		int[] pts = new int[6];
		pts[0] = points[0];
		pts[1] = points[1];

		//convert relative points to absolute points
		for (int i=2; i<points.length; i++){
			if (i%2==0){
				pts[i] = points[0] + points[i]*polarities[i-2];
			}
			else{
				pts[i] = points[1] + points[i]*polarities[i-2];
			}
		}
		return pts;
	}
}
