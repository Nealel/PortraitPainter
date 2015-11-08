/**
 * defines a circle shape, to be drawn on an Image.
 *
 */
public class Circle extends Shape {

	/**
	 * Creates a randomly parameterised Circle object
	 */
	public Circle(){
		super();
		points = new int[4];
		randomizeShape();
	}

	
	/**
	 * Randomly sets all points and colours in the image to random allowed valued. 
	 */
	protected void randomizeShape(){
		super.randomizeShape(); //set colours

		//x,y
		for (int i=0; i<2; i++){
			points[i] = r.nextInt(size)-(maxSize-minSize)/4;
		}

		//width, height
		for (int i=2; i<4; i++){
			points[i] = r.nextInt(maxSize/2-minSize/2)+minSize;
		}
	}
	
	/**
	 * Copies this circle, with mutation
	 */
	public Shape clone(){
		Shape s = new Circle();
		s = super.clone(s);
		return s;
	}

	/**
	 * Mutates the points and colours in this shape.
	 */
	protected void mutate(){
		super.mutate(); //colour, shape reset

		//x,y
		for (int i=0; i<2; i++){
			if (r.nextFloat() < mr){
				points[i] = Math.min(size, Math.max(0, (int)(points[i] + r.nextGaussian()*3)));
			}
		}

		//width, height
		for (int i=2; i<4; i++){
			if (r.nextFloat() < mr){
				points[i] = Math.min(maxSize/2, Math.max(minSize/2, (int)(points[i] + r.nextGaussian()*3)));
			}
		}

	}
}
