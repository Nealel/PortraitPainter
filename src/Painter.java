import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import detection.Detector;

/**
 * A Painter objects runs the main stages of the GA. Initialises and breeds population of images.
 */
public class Painter {
	private JLabel imageDisplay;
	private ArrayList<Image> cands;
	private ArrayList<Double> scores;
	private Detector faceDet;
	private Random r;
	
	//Visual image
	public ImageIcon img;

	//fixed params
	private static int maxGen = 10000000; //maximum allowed number of generations
	private static int viewPeriod = 100; //how often display image updates
	private static int size = 100; //image size
	private int pop; //population size

	/**
	 * Constructor. Creates a painter.
	 */
	public Painter(){
		cands = new ArrayList<Image>();
		//faceDet = Detector.create("src/resources/haarcascade_frontalface_default.xml");
		faceDet = Detector.create(getDetector());
		scores = new ArrayList<Double>();
		r = new Random();
		initializeDisplay();
	}
	
	/**
	 * Gets trained Haar Cascade detector from resources.
	 * @return InputStream of the detector
	 */
	private InputStream getDetector(){		
		String path = "haarcascade_frontalface_default.xml";
		InputStream in = this.getClass().getResourceAsStream(path);
		return in;
	}

	/**
	 * Initializes and runs a single GA
	 * @param popSize population size
	 */
	public void run(int popSize){
		pop = popSize;
		initializePop();
		if (!Thread.currentThread().isInterrupted()){
			evolve();
		}
	}

	/**
	 * Runs main stages of the GA, repeatedly generates and replaces individuals
	 */
	public void evolve(){
		DecimalFormat d = new DecimalFormat("00.00");

		for (int j = 0; j < maxGen; j++){
			//create new i,age
			Image child = breed();
			
			//remove the weakest
			int kill = getLeastFit();
			cands.remove(kill);
			scores.remove(kill);
			
			//add new image
			scores.add(child.getFitness());
			cands.add(child);
			
			//update display label
			imageDisplay.setText("<html>#" + j + "<br>" + "BEST SCORE: " + d.format(scores.get(getMostFit())) + "</html>");

			//check for convergence
			if (scores.get(getMostFit()) == scores.get(getLeastFit())){ 
				Toolkit.getDefaultToolkit().beep(); //beep to alert user to finish
				break; //end GA
			}
			
			//check for interruption
			if(Thread.currentThread().isInterrupted()){
				break;
			}
			
			//periodically update display
			if (j%viewPeriod == 0){
				imageDisplay.setIcon(new ImageIcon(child.draw()));
			}
		}
		
		//final update for display
		imageDisplay.setText("<html>FINISHED<br>SCORE: " + d.format(scores.get(getMostFit())) + "</html>");
		imageDisplay.setIcon(new ImageIcon(cands.get(getMostFit()).draw()));
	}

	/**
	 * Initialises Population
	 */
	public void initializePop(){
		//reset population
		cands.clear();
		scores.clear();
		
		//initialise population
		while(cands.size() < pop && !Thread.currentThread().isInterrupted()){ //check for interruption
			Image cand = new Image();
			cand.setFitness(scoreImage(cand.draw()));
			if (cand.getFitness() > 0){ //check fitness isn't absolute 0 before adding it
				cands.add(cand);
				scores.add(cand.getFitness());
				imageDisplay.setText("<html>Initializing <br>" + cands.size() + "/" + pop +  "</html>");
			}
		}

	}

	/**
	 * Scores an image
	 */
	private double scoreImage(BufferedImage img){
		double score = 0f;
		score += faceDet.getFaces(img);
		return score;
	}

	/**
	 * Selects image randomly proportional to fitness
	 */
	public Image selectFit(){
		//rescale scores so that sum of fitness population = 1
		double sum = 0;
		for (Double score : scores){
			sum += score;
		}

		ArrayList<Double> probs = new ArrayList<Double>();

		for (Double score : scores){
			probs.add(score/sum);
		}
		
		//select target based on fitness
		int index = -1;
		double total = 0;	
		double target = r.nextDouble(); 
		for (int i = 0; i<scores.size(); i++){
			total += probs.get(i);
			if (total >= target){
				index = i;
				break;
			}
		}
		return cands.get(index);
	}

	/**
	 * creates a new image from current population
	 */
	private Image breed(){
		//select parents
		Image p1 = selectFit();
		Image p2 = selectFit();

		//check parents are not identical (to a limit)
		int it = 0;
		while (p1 == p2 && it < 100){
			p2 = selectFit();
			it++;
		}

		//create and score child
		Image child = new Image(p1, p2);
		child.setFitness(scoreImage(child.draw()));

		return child;
	}

	/**
	 * gets index of least fit member of population
	 */
	public int getLeastFit(){
		double minFit =  Double.POSITIVE_INFINITY;
		int index = 0;
		for (int i = 0; i < scores.size(); i++){ //iterate through scores
			double score = scores.get(i); 
			if (score <= minFit){ //if least fit member of population so far, remember it
				minFit = score;
				index = i;
			}
		}
		return index;
	}

	/**
	 * Gets index of fittest member of population, for display/saving purposes
	 */
	public int getMostFit(){
		double maxFit = 0.0;
		int index = 0;
		for (int i = 0; i < scores.size(); i++){  //iterate through scores
			double score = scores.get(i); 
			if (score >= maxFit){ //if most fit member of population so far, remember it
				maxFit = score;
				index = i;
			}
		}
		return index;
	}

	/**
	 * Writes fittest member to file.
	 * @param file The file to write to, a png
	 */
	public void writeFittest(File file){
		int index = getMostFit();
		Image best = cands.get(index);

		try{
			ImageIO.write(best.draw(), "png", file);
		}
		catch (IOException e){
			System.out.println("File not found");
		}
	}

	/**
	 * Creates label to display image and corresponding information
	 */
	private void initializeDisplay(){
		//Image Display
		BufferedImage blank = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
		imageDisplay = new JLabel(new ImageIcon(blank));
		imageDisplay.setText("<html>Ready<br>...</html>");
		imageDisplay.setHorizontalTextPosition(JLabel.CENTER);
		imageDisplay.setVerticalTextPosition(JLabel.BOTTOM);
	}


	/**
	 * @return Image display
	 */
	public JLabel getImagePanel(){
		return imageDisplay;
	}
}
