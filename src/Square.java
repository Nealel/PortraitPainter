/**
 * defines a square shape, to be drawn on an Image.
 *
 */
public class Square extends Shape {

	/**
	 * Creates a randomly parameterised Square object
	 */
	public Square(){
		super();
		points = new int[4];
		randomizeShape();
	}

	/**
	 * Randomly sets all points and colours in the shape to random allowed valued. 
	 */
	protected void randomizeShape(){
		super.randomizeShape();

		//x,y
		for (int i=0; i<2; i++){
				points[i] = r.nextInt(size)-(maxSize-minSize)/2; //
		}

		//width,height
		for (int i=2; i<4; i++){
				points[i] = r.nextInt(maxSize-minSize)+minSize;
		}
	}

	/**
	 * Copies this square, with mutation
	 */
	public Shape clone(){
		Shape s = new Square();
		s = super.clone(s);
		return s;
	}


	/**
	 * Mutates the points and colours in this shape.
	 */
	protected void mutate(){
		super.mutate();

		//x,y
		for (int i=0; i<2; i++){
			if (r.nextFloat() < mr){
			points[i] = Math.min((size-(maxSize-minSize)/2), Math.max(0, (int)(points[i] + r.nextGaussian()*3))); //
			}
		}

		//width,height
		for (int i=2; i<4; i++){
			if (r.nextFloat() < mr){
				points[i] = Math.min(maxSize, Math.max(minSize, (int)(points[i] + r.nextGaussian()*3)));
			}
		}

	}

}
