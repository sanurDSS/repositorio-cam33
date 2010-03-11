package javavis.jip2d.gui;

import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.Properties;

import javavis.jip2d.base.JIPImage;

import javax.swing.*;


/**
	 * Class which has the information about the elements of the
	 * program, the cursor position, its value, band and numFrame where
	 * we are working etc...
   */
public class InfoPanelGeom extends JPanel {
	private static final long serialVersionUID = 5263348773283877531L;

	/**
	 * The canvas
	 */
	Canvas2D canvas;

	/** Number of segments */
	int nSegment = 0;
	
	/** Initial x */
	int xini;
	
	/** Initial y */
	int yini;

	/**
	 * Segments label
	 */
	JLabel segments;

	/**
	 * Points label
	 */
	JLabel points;

	/**
	 * Geometry label
	 */
	JLabel geom;

	/**
	 * Polygons label
	 */
	JLabel polygons;

	/**
	 * Start selection label
	 */
	JLabel iniSel;

	/**
	 * End selection label
	 */
	JLabel finSel;

	/**
	 * Segment length label
	 */
	JLabel longSel;

	/**
	 * Type of selection label
	 */
	JLabel tipoSel;

	/** Buttons for select or add geometrical objects */
	public JRadioButton bpoint;
	/** Buttons for select or add geometrical objects */
	public JRadioButton bsegment;
	/** Buttons for select or add geometrical objects */
	public JRadioButton bpoly;
	
	public JComboBox actionSel;

	/** String which shows the units */
	String unidad;
	
	/** Scale */
	float escala = 1;

	/** Number format */
	NumberFormat nf;
	
	Properties prop;
	
	/** Indicates if we are selecting or adding geometry */
	public enum GeomAction {SELECT, ADD}

	/**
	* 	Class constructor. It creates all the information panel on the left in 
	* the main program window.
	* @param c Geometric canvas
	*/
	public InfoPanelGeom(Canvas2D c, Properties propi) {
		canvas = c;
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		prop = propi;

		setBorder(BorderFactory.createEtchedBorder());

		unidad = prop.getProperty("Pixels");
		segments = new JLabel("# "+prop.getProperty("Segments")+":");
		points = new JLabel("# "+prop.getProperty("Points")+" :");
		polygons = new JLabel("# "+prop.getProperty("Polygons")+" :");
		geom = new JLabel(prop.getProperty("Geometry")+":");

		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		Spring yPad = Spring.constant(1);
		Spring xSpring = Spring.constant(5);
		Spring ySpring = yPad;
		Spring maxWidthSpring = Spring.constant(0);

		SpringLayout.Constraints cons;

		add(segments);
		cons = layout.getConstraints(segments);
		cons.setX(xSpring);
		ySpring = Spring.constant(10); 
		cons.setY(ySpring);
		ySpring = Spring.sum(yPad, cons.getConstraint("South"));
		maxWidthSpring = Spring.max(maxWidthSpring, cons.getConstraint("East"));

		add(points);
		cons = layout.getConstraints(points);
		cons.setX(xSpring);
		cons.setY(ySpring);
		ySpring = Spring.sum(yPad, cons.getConstraint("South"));
		maxWidthSpring = Spring.max(maxWidthSpring, cons.getConstraint("East"));

		add(polygons);
		cons = layout.getConstraints(polygons);
		cons.setX(xSpring);
		cons.setY(ySpring);
		ySpring = Spring.sum(yPad, cons.getConstraint("South"));
		maxWidthSpring = Spring.max(maxWidthSpring, cons.getConstraint("East"));

		add(geom);
		cons = layout.getConstraints(geom);
		cons.setX(xSpring);
		ySpring = Spring.sum(ySpring, Spring.constant(10)); 
		cons.setY(ySpring);
		ySpring = Spring.sum(yPad, cons.getConstraint("South"));
		maxWidthSpring = Spring.max(maxWidthSpring, cons.getConstraint("East"));

		JPanel auxPanelMode = new JPanel();
		auxPanelMode.setLayout(new BoxLayout(auxPanelMode, BoxLayout.Y_AXIS));
		auxPanelMode.setBorder(BorderFactory.createTitledBorder(" "+
				prop.getProperty("Mode")));

		ButtonGroup geoTipo = new ButtonGroup();
		
		bsegment = new JRadioButton(prop.getProperty("Segments"), true);
		bsegment.addActionListener(canvas);
		auxPanelMode.add(bsegment);
		geoTipo.add(bsegment);

		bpoint = new JRadioButton(prop.getProperty("Points"));
		bpoint.addActionListener(canvas);
		auxPanelMode.add(bpoint);
		geoTipo.add(bpoint);

		bpoly = new JRadioButton(prop.getProperty("Polygons"));
		bpoly.addActionListener(canvas);
		auxPanelMode.add(bpoly);
		geoTipo.add(bpoly);
		
		add(auxPanelMode);
		cons = layout.getConstraints(auxPanelMode);
		cons.setX(xSpring);
		ySpring = Spring.sum(ySpring, Spring.constant(10)); 
		cons.setY(ySpring);
		ySpring = Spring.sum(yPad, cons.getConstraint("South"));
		maxWidthSpring = Spring.max(maxWidthSpring, cons.getConstraint("East"));
		//End of RadioButton
		
		actionSel = new JComboBox();
		actionSel.addItem(prop.getProperty("Select"));
		actionSel.addItem(prop.getProperty("Add"));
		actionSel.setSelectedIndex(GeomAction.SELECT.ordinal());
		actionSel.addActionListener(canvas);
		actionSel.setMaximumSize(new Dimension(125, 50));
		
		JPanel auxPanelAction = new JPanel();
		auxPanelAction.setLayout(new BoxLayout(auxPanelAction, BoxLayout.Y_AXIS));
		auxPanelAction.setBorder(BorderFactory.createTitledBorder(prop.getProperty("Action")));
		auxPanelAction.setMaximumSize(new Dimension(125, 50));
		auxPanelAction.add(actionSel);
		
		add(auxPanelAction);
		cons = layout.getConstraints(auxPanelAction);
		cons.setX(xSpring);
		ySpring = Spring.sum(ySpring, Spring.constant(10)); 
		cons.setY(ySpring);
		ySpring = Spring.sum(yPad, cons.getConstraint("South"));
		maxWidthSpring = Spring.max(maxWidthSpring, cons.getConstraint("East"));

		
		JPanel auxPanelSelection = new JPanel();
		auxPanelSelection.setLayout(new BoxLayout(auxPanelSelection, BoxLayout.Y_AXIS));
		auxPanelSelection.setBorder(BorderFactory.createTitledBorder(prop.getProperty("Selection")));
		auxPanelSelection.setMaximumSize(new Dimension(0,0));

		tipoSel = new JLabel(" "); 
		auxPanelSelection.add(tipoSel);

		iniSel = new JLabel(" ");
		auxPanelSelection.add(iniSel);
		
		finSel = new JLabel(" ");
		auxPanelSelection.add(finSel);

		longSel = new JLabel(" ");
		auxPanelSelection.add(longSel);
		
		add(auxPanelSelection);
		cons = layout.getConstraints(auxPanelSelection);
		cons.setX(xSpring);
		ySpring = Spring.sum(ySpring, Spring.constant(10)); 
		cons.setY(ySpring);
		ySpring = Spring.sum(yPad, cons.getConstraint("South"));
		maxWidthSpring = Spring.max(maxWidthSpring, cons.getConstraint("East"));
		
		cons = layout.getConstraints(this);
		cons.setConstraint("East", maxWidthSpring);
		cons.setConstraint("South", ySpring);

		updateSel(-1, -1, -1, -1, "["+prop.getProperty("noselec")+"]");
	}

	/**
	* Method which changes the actual scale for another that it receive as parameter
	* @param esc New value of the scale
	*/
	public void changeScale(float esc) {
		escala = esc;
	}

	/**
	* Method which changes the actual units for another that it receive as parameter
	* @param esc New value of the units
	*/
	public void changeUnits(String uni) {
		if (uni.length() > 15)
			uni = uni.substring(0, 15);
		unidad = uni;
	}

	/**
	* Method which updates the state of the geometry which can be enable or
	* disable
	* @param geoEst Showing or not showing geometry (enable/disable)
	*/
	public void updateGeo(boolean geoEst) {
			geom.setText(prop.getProperty("Geometry")+(geoEst?": On":": Off"));
	}

	/**
	* Method which updates the information of the panel, it can be the width,
	* the height, number of segments, image, bands, etc...
	* @param w Width
	* @param h Height
	* @param x actual X 
	* @param y actual Y
	* @param numSeg Number of segments
	* @param numPoint Number of points
	* @param numPoly  Number of polygons
	* @param xini Initial X 
	* @param yini Initial Y
	* @param xfin Final X
	* @param yfin Final Y
	* @param img Image
	* @param b Number of bands
	*/
	public void updateInfo(int w, int h, int x, int y, int numSeg, int numPoint,
		int numPoly, int xini, int yini, int xfin, int yfin, JIPImage img, int b) {
		segments.setText("# "+prop.getProperty("Segments")+": " + numSeg);
		points.setText("# "+prop.getProperty("Points")+": " + numPoint);
		polygons.setText("# "+prop.getProperty("Polygons")+": " + numPoly);

	}

	/**
	* Method which updates the panel section which corresponds to the selection.
	* @param x0 Initial X 
	* @param y0 Initial Y 
	* @param x1 Final X
	* @param y1 Final Y
	* @param tipo Type
	*/
	public void updateSel(int x0, int y0, int x1, int y1, String tipo) {
		tipoSel.setText(prop.getProperty("Type")+":  " + tipo);
		iniSel.setText(prop.getProperty("Start")+":  (" + x0 + "," + y0 + ")");
		finSel.setText(prop.getProperty("End")+":  (" + x1 + "," + y1 + ")");
		longSel.setText(prop.getProperty("Length")+":  "+ nf.format(escala
				* Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2))) + 
				" " + unidad);
	}
}
