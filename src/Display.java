

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import detection.Detector;

/**
 * Contains the user interface, which initialises and runs the rest of the program.
 *
 */
public class Display implements ActionListener{
	private static final int SIZE = 100; //Image size

	private Painter painter;

	private Thread t;
	boolean running = false;

	//parameters-containers in the interface
	private JSpinner mrs, pss, crs; //GA params
	private JSpinner nss, sos, mins, maxs, cirs, tris, bgs, dss; //image params
	private JCheckBox gsb, ptb; //checkbox option
	private JSpinner[][] cols = new JSpinner[2][3]; //colour limits


	/**
	 * Main method. Creates display, which creates everything else needed.
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Display display = new Display();
	}

	/**
	 * Creates a display, including a Painter. Shows user interface.
	 */
	public Display(){
		painter = new Painter();
		initializeFrame();
	}

	/**
	 * Creates and fills the display frame of the interface.
	 */
	private void initializeFrame(){
		//General settings
		ToolTipManager.sharedInstance().setInitialDelay(10);
		ToolTipManager.sharedInstance().setDismissDelay(100000);
		
		//main frame
		JFrame frame = new JFrame();
		Container container = frame.getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		//Image panel
		JPanel imagePanel = new JPanel();
		JLabel imageLabel = painter.getImagePanel();
		setPanelLayout(imagePanel, "Image");
		container.add(imagePanel);
		imagePanel.add(imageLabel);


		//Settings panels
		container.add(initializeGASettings());
		container.add(initializeImageSettings());
		container.add(initializeColourSettings());

		//Run/stop button
		JPanel runpanel = new JPanel();
		container.add(runpanel);
		JButton run = new JButton("Run");
		runpanel.add(run);

		run.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (!running){
					if (checkParams()){
						run.setText("Stop");
						t = new Thread(new Runnable() {
							public void run() {
								setGAParameters();
								painter.run((int)pss.getValue());
								run.setText("Run");
								running = false;
							}
						});
						t.start();
						running=true;
					}
				}
				else{
					run.setText("Run");
					t.interrupt();
					running=false;
				}
			}});


		//Menu bar
		frame.setJMenuBar(makeMenuBar());

		//Finalise, display
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * sets the parameters of Images and Shapes in the painter, according to user preferences.
	 */
	private void setGAParameters(){
		Detector.baseScale = (float)(double)dss.getValue();
		
		Image.setParameters(SIZE,
				(int)nss.getValue(),
				(double)crs.getValue(),
				(double)sos.getValue(),
				(double)bgs.getValue(),
				(double)cirs.getValue()/100,
				(double)tris.getValue()/100
				);

		//get palette
		float[][] palette;
		if (gsb.isSelected()){
			palette = new float[2][1];
			palette[0][0] = 0.0f;
			palette[1][0] = 1.0f;
		}
		else{
			palette = new float[2][3];
			for (int r = 0; r<2; r++){
				for (int h=0;h<3;h++){
					palette[r][h] = ((Double) cols[r][h].getValue()).floatValue();;
				}
			}
		}

		//set shape params
		Shape.setParameters(SIZE,
				(double)mrs.getValue()/((int)nss.getValue()*8),
				palette,
				(int)mins.getValue(),
				(int)maxs.getValue(),
				ptb.isSelected()
				);
	}

	/**
	 * Creates the menu bar for the interface
	 * @return the menu bar
	 */
	private JMenuBar makeMenuBar(){
		JMenuBar menu = new JMenuBar();

		JMenu file = new JMenu("File");
		menu.add(file);

		//Save button
		JMenuItem save = new JMenuItem("Save Fittest Image");
		file.add(save);
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { 
				// parent component of the dialog
				JFrame parentFrame = new JFrame();

				JFileChooser fileChooser = new JFileChooser();

				fileChooser.setDialogTitle("Save");


				fileChooser.showSaveDialog(parentFrame);

				File file = fileChooser.getSelectedFile();
				if(!file.getAbsolutePath().endsWith(".png") ) {
					file = new File(file.getAbsolutePath() + ".png");
				}
				painter.writeFittest(file);
			}});


		return menu;
	}

	/**
	 * creates the GA settings panel
	 * @return GA settings panel
	 */
	private JPanel initializeGASettings(){
		
		//Panel itself
		JPanel gaSettings = new JPanel();
		setPanelLayout(gaSettings, "GA Settings");
		SpringLayout gaLayout = new SpringLayout();
		gaSettings.setLayout(gaLayout);

		//Labels for the panel
		JLabel mrl, psl, crl, dsl;
		JLabel mrq, psq, crq, dsq;

		//Mutation Rate
		mrl = new JLabel("Mutation Rate");
		gaSettings.add(mrl);
		mrs = new JSpinner((new SpinnerNumberModel(1, //initial value
				0, //min
				100, //max
				0.1)));//step
		gaSettings.add(mrs);
		mrq = new JLabel("[?]");
		mrq.setToolTipText("<HTML>Mutation Rate describes the number of random shape mutations that occur with each new generated image."
						+ "<BR>Higher mutation rates are good for exploration, while lower mutation rates are better at exploiting local"
						+ "<BR>maxima. The best mutation rate will depend on other system settings.");
		gaSettings.add(mrq);

		//Population Size
		psl = new JLabel("Population Size");
		gaSettings.add(psl);
		pss = new JSpinner((new SpinnerNumberModel(500, //initial value
				1, //min
				10000, //max
				1)));//step
		gaSettings.add(pss);
		psq = new JLabel("[?]");
		psq.setToolTipText("<HTML>Population Size controls the number of images that exist in the system at any one time."
						+ "<BR>Smaller population sizes are quicker, but often achieve less optimal results."
						+ "<BR>Smaller populations are recommended for images with fewer shapes in them."
						+ "</HTML>");
		gaSettings.add(psq);

		//Crossover
		crl = new JLabel("Crossover Rate");
		gaSettings.add(crl);
		crs = new JSpinner((new SpinnerNumberModel(0.5, //initial value
				0, //min
				1, //max
				0.1)));//step
		gaSettings.add(crs);
		crq = new JLabel("[?]");
		crq.setToolTipText("<HTML>Cross-over rate controls the percentage of genes which are taken from the first parent."
						+ "<BR>This tool may provide an interesting experiment for those interested in genetic algorithms."
						+ "</HTML>");
		gaSettings.add(crq);

		//Detector Scale
		dsl = new JLabel("Detector Scale");
		gaSettings.add(dsl);
		dss = new JSpinner((new SpinnerNumberModel(3.5, //initial value
				1, //min
				4.1, //max
				0.1)));//step
		gaSettings.add(dss);
		dsq = new JLabel("[?]");
		dsq.setToolTipText("<HTML>Detector scale controls the smallest possible face the system with recognize."
						+ "<BR>Reccomended values range from 2.5 to 3.5 (60 to 84 pixels)."
						+ "<BR>Larger scales are less robust, while faces at smaller scales can become indistinct."
						+ "</HTML>");
		gaSettings.add(dsq);

		//Lay out labels and spinners
		SpringUtilities.makeCompactGrid(gaSettings, //parent
				4, 3, //row, col
				3, 3,  //initX, initY
				10, 3); //xPad, yPad
		return gaSettings;
	}


	/**
	 * Creates image panel, using image taken from painter.
	 * @return Image Panel
	 */
	private JPanel initializeImageSettings(){
		//panel itself
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		setPanelLayout(panel, "Image Settings");

		//text in the panel
		JLabel nsl, sol, minl, maxl, cirl, tril, ptl, bgl;
		JLabel nsq, soq, minq, maxq, cirq, triq, ptq, bgq;

		//number of shapes
		nsl = new JLabel("Number of Shapes");
		panel.add(nsl);
		nss = new JSpinner((new SpinnerNumberModel(200, //initial value
				2, //min
				9999, //max
				2)));//step
		panel.add(nss);
		nsq = new JLabel("[?]");
		nsq.setToolTipText("<HTML>The number of shapes in each image.</HTML>");
		panel.add(nsq);

		//opacity of shapes
		sol = new JLabel("Shape opacity");
		panel.add(sol);
		sos = new JSpinner((new SpinnerNumberModel(0.2, //initial value
				0, //min
				2, //max
				0.1)));//step
		panel.add(sos);
		soq = new JLabel("[?]");
		soq.setToolTipText("<HTML>The opacity of shapes in the image, from 0 (totally transparent) to 1 (totally opaque).</HTML>");
		panel.add(soq);
		
		//minimum shape size
		minl = new JLabel("Min Shape Size");
		panel.add(minl);
		mins = new JSpinner((new SpinnerNumberModel(1, //initial value
				1, //min
				150, //max
				1)));//step
		panel.add(mins);
		minq = new JLabel("[?]");
		minq.setToolTipText("<HTML>The minimum permitted size of shapes in the image, in pixels.</HTML>");
		panel.add(minq);

		//maximum shape size
		maxl = new JLabel("Max Shape Size");
		panel.add(maxl);
		maxs = new JSpinner((new SpinnerNumberModel(100, //initial value
				1, //min
				150, //max
				1)));//step
		panel.add(maxs);
		maxq = new JLabel("[?]");
		maxq.setToolTipText("<HTML>The maximum permitted size of shapes in the image, in pixels.</HTML>");
		panel.add(maxq);

		//amount of circles
		cirl = new JLabel("% Circles");
		panel.add(cirl);
		cirs = new JSpinner((new SpinnerNumberModel(40, //initial value
				0, //min
				100, //max
				0.1)));//step
		panel.add(cirs);
		cirq = new JLabel("[?]");
		cirq.setToolTipText("<HTML>The percentage of shapes in image which are circles."
						+ "<BR>Note: The remainding percentage, after circles and triangles"
						+ "<BR>are accounted for will be squares."
								+ "</HTML>");
		panel.add(cirq);

		//amount of triangles
		tril = new JLabel("% Triangles");
		panel.add(tril);
		tris = new JSpinner((new SpinnerNumberModel(30, //initial value
				0, //min
				100, //max
				0.1)));//step
		panel.add(tris);
		triq = new JLabel("[?]");
		triq.setToolTipText("<HTML>The percentage of shapes in image which are triangles."
				+ "<BR>Note: The remainding percentage, after circles and triangles"
				+ "<BR>are accounted for will be squares."
						+ "</HTML>");
		panel.add(triq);

		//amount of triangles
		ptl = new JLabel("No Thin Triangles");
		panel.add(ptl);
		ptb = new JCheckBox();//step
		panel.add(ptb);
		ptq = new JLabel("[?]");
		ptq.setToolTipText("<HTML>If ticked, this prevents thin, shard-like triangles from being generated."
								+ "<BR>This may be very useful when working with a narrow size range."
								+ "</HTML>");
		panel.add(ptq);

		//background shade
		bgl = new JLabel("Background tone");
		panel.add(bgl);
		bgs = new JSpinner((new SpinnerNumberModel(0.0, //initial value
				0, //min
				1, //max
				0.1)));//step
		panel.add(bgs);
		bgq = new JLabel("[?]");
		bgq.setToolTipText("<HTML>The background shade of the image, from 0 (black) to 1 (white)."
								+ "Note: the background is always rendered as a shade of grey."
								+ "</HTML>");
		panel.add(bgq);
		

		//lay out components
		SpringUtilities.makeCompactGrid(panel, //parent
				8, 3,
				3, 3,  //initX, initY
				10, 3); //xPad, yPad


		return panel;
	}

	/**
	 * creates colour settings panel, including RGB component options and greyscale option
	 * @return Colour Panel
	 */
	private JPanel initializeColourSettings(){
		//panel itself
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		setPanelLayout(panel, "Colour Palette");
		


		JPanel palette = new JPanel();
		
		

		//palette
		panel.add(palette);

		SpringLayout layout = new SpringLayout();
		palette.setLayout(layout);

		//headers
		palette.add(new JLabel());
		palette.add(new JLabel("Min   "));
		JLabel maxl = new JLabel("Max  [?]");
		maxl.setToolTipText("<HTML>Controls the maximum and minimum intensities of RGB colour components for shapes."
				+ "<BR>Used to render images in particular colours, e.g. increase blue minimum and decrease"
				+ "<BR>red maximum to create a blue/teal image.."
				+ "</HTML>");
		palette.add(maxl);

		String[] labels = {"Red", "Green", "Blue"};

		int i = 0;
		for (String label : labels){ //for each colour component
			//label
			JLabel l = new JLabel(label);
			palette.add(l);

			//minimum value
			JSpinner min = new JSpinner((new SpinnerNumberModel(0, //initial value
					0, //min
					1, //max
					0.1)));//step
			palette.add(min);

			//maximum value
			JSpinner max = new JSpinner((new SpinnerNumberModel(1, //initial value
					0, //min
					1, //max
					0.1)));//step
			palette.add(max);

			//add spinners to settings trackers.
			cols[0][i] = min;
			cols[1][i] = max;
			i++;
		}
		
		//layout RGB min/max grid
		SpringUtilities.makeCompactGrid(palette, //parent
				4, 3,
				3, 3,  //initX, initY
				10, 3); //xPad, yPad

		//Greyscale checkbox
		JPanel settings = new JPanel();
		panel.add(settings);
		settings.setLayout(new SpringLayout());
		JLabel gsl;

		gsl = new JLabel("Greyscale");
		settings.add(gsl);
		gsb = new JCheckBox();
		gsb.setSelected(false);
		settings.add(gsb);
		gsb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for (JSpinner[] spinners : cols){
					for (JSpinner spinner : spinners){
						spinner.setEnabled(!gsb.isSelected()); //disable RGB selection when RGB is selected
					}
				}
			}
		});
		JLabel gsq = new JLabel("[?]");
		gsq.setToolTipText("<HTML>Renders all shapes in greyscale."
				+ "</HTML>");
		settings.add(gsq);

		//layout checkbox
		SpringUtilities.makeCompactGrid(settings, //parent
				1, 3,
				3, 3,  //initX, initY
				10, 3); //xPad, yPad


		return panel;
	}

	/**
	 * Checks whether all settings are allowable, and if not, alerts user to the problem.
	 * @return true if all allowable are acceptable
	 */
	private boolean checkParams(){
		boolean pass = true;
		String message = "<HTML>The system cannot run because of the following problems.";

		if (!gsb.isSelected()){ //check RGB mins are lower than maxs
			for (int h=0;h<3;h++){
				if ((double)cols[0][h].getValue() > (double)cols[1][h].getValue()){
					pass=false;
					message += "<br>Colour minimums must not exceed colour maximums.";
					break;
				}
			}
		}

		//check min shape size is lower than maximum shape size
		if ((Integer)mins.getValue() >= (Integer)maxs.getValue()){
			pass=false;
			message += "<br>Shape size minimums be smaller than maximums.";
		}
		message += "</HTML>";

		//if settings are not acceptable, display message.
		if(!pass){
			JOptionPane.showMessageDialog(new JFrame(), message);
		}

		return pass;
	}

	/**
	 * sets panel's layout, with black titled boarder
	 * @param panel The panel to layout
	 * @param title The title of the panel
	 */
	private void setPanelLayout(JPanel panel, String title) {
		TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), title);
		b.setTitleJustification(TitledBorder.CENTER);
		panel.setBorder(b);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
	}

}
