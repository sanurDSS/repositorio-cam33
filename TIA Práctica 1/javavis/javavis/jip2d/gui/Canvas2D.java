package javavis.jip2d.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import javavis.base.Dialog;
import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.JIPToolkit;
import javavis.jip2d.base.*;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPGeomEdges;
import javavis.jip2d.base.geometrics.JIPGeomPoint;
import javavis.jip2d.base.geometrics.JIPGeomPoly;
import javavis.jip2d.base.geometrics.JIPGeomSegment;
import javavis.jip2d.base.geometrics.JIPImgGeometric;
import javavis.jip2d.gui.InfoPanelGeom.GeomAction;

import org.apache.log4j.Logger;

import javax.swing.*;


/**
 * Class managing all the visual data
   */
public class Canvas2D extends JPanel implements ActionListener, MouseListener, KeyListener, MouseMotionListener{
	private static final long serialVersionUID = 2538428251952768226L;
	
	private static Logger logger = Logger.getLogger(Canvas2D.class);

	/** Array containing the segments drawn in the canvas, but not converted in an JIP image yet */
	ArrayList<Integer> segments;

	/** Array containing the points drawn in the canvas, but not converted in an JIP image yet */
	ArrayList<Integer> points;

	/** Array containing the polygons drawn in the canvas, but not converted in an JIP image yet */
	ArrayList<ArrayList<Integer>> polygons;

	/** Array containing the edges drawn in the canvas, but not converted in an JIP image yet */
	ArrayList<ArrayList<Integer>> edges;

	/** Array which has the colors for geometric data. */
	ArrayList<Color> colors;

	/** Vector which has the historic of the scale factors */
	ArrayList<Integer> factoresEscalas;

	/** Vector which has the elements which is been wacthed */
	ArrayList<Integer> visualizing;
	
	/** Segment index */
	int indiceSeg = 0;
	
	/** Point index */
	int indicePunto = 0;
	
	/** Polygon index */
	int indicePoly = 0;
	
	/** edges index */
	int indiceEdge = 0;

	/** Active sequence in the program */
	JIPSequence secuencia;

	
	/** Width */
	int w = 400;
	
	/** Height */
	int h = 300;
	
	/** Scale factor */
	int factorEscala = 100;
	
	/** Flag which shows if the geometry is activate or not */
	boolean estadoGeo = true;
	
	/** Original width */
	int worg;
	
	/** Original height */
	int horg;
	
	/** Flag which shows if the mouse is been dragged */
	boolean arrastrando;
	
	/** Flag which shows if we have to emphasize the segments */
	boolean resaltaSegmentos;
	
	
	/** Flag which shows if we have to emphasize the points */
	boolean resaltaPuntos;
	
	/** Flag which shows if we have to emphasize the polygons */
	boolean resaltaPoligonos;
	
	/** Flag which shows if we have to emphasize the edges */
	boolean resaltaEdges;
	
	/** Flag which shows if the background Bitmap has been shown*/	
	boolean verBitmap;
	
	/** Flag which shows if the segments have been shown*/
	boolean verSegmentos;
	
	/** Flag which shows if the points have been shown*/
	boolean verPuntos;
	
	/** Flag which shows if the polygons have been shown*/
	boolean verPoligonos;
	
	/** Flag which shows if the edges have been shown*/
	boolean verEdges;
	
	/** x initial position */
	int xini;
	
	/** y initial position */
	int yini;
	
	/** x final position */
	int xfin;
	
	/** y final position */
	int yfin;
	
	/** Information panel */
	InfoPanelGeom infoGeom;
	
	InfoPanelBottom infoBottom;

	
	/** Maximum number of segments */
	int maxSeg = (1000 * 4);
	
	/** Component */
	Component zona;

	/** Background image */
	ImageIcon backGround;

	/** Background color */
	Color colorFondo;
	
	/** Background color of the scroll panel */
	Color colorFondoScroll;
	
	/** Lines color */
	Color colorLinea;
	
	/** Current color line */	
	Color currentColorLine;
	
	/** Color of the points */
	Color colorPunto;
	
	/** Color of the actual point */
	Color colorPuntoActual;
	
	/** Color of the polygons */
	Color colorPoligono;
	
	/** Color of the actual polygon */
	Color colorPoligonoActual;
	
	/** Color of the edge */
	Color colorEdges;

	/** Vector which has the actual polygon */
	ArrayList<Integer> currentPolygon;

	/** Constants indicating the mode */
	enum ModeGeom {SEG, POINT, POLY};

	/** Indicates if we are using points, segments or polygons */
	ModeGeom modo;

	/** Indicates if we are selected points, segments or polygons */
	ModeGeom tipoSel;
	
	/** Last position of X*/
	int ultimoX = -1;
	
	/** Last position of Y*/
	int ultimoY = -1;
	
	/** Shows the current frame of the sequence */
	int numFrame;
	
	/** Shows the current band of the sequence */
	int numBand;
	
	/** Shows if we are selecting o adding */
	GeomAction accion = GeomAction.SELECT;
	
	/** Auxiliar variable to select */
	int x0Sel = -1;
	
	/** Auxiliar variable to select */
	int y0Sel = -1;
	
	/** Auxiliar variable to select */
	int x1Sel = -1;
	
	/** Auxiliar variable to select */
	int y1Sel = -1;
	
	/** Auxiliar variable to select */
	int idxPolySel = -1;

	/** Auxiliar Image type Variable */
	JIPImage imgtmp;

	/** These variables allow to control the cursor with the keyboard */
	int posXRaton=0;
	int posYRaton=0;
	
	
	/**
	* Class constructor. The vectors and variables of the class are started
	* @param ww Width of the canvas
	* @param hh Height of the canvas
	*/
	public Canvas2D(int ww, int hh) {
		w = ww;
		h = hh;
		segments = new ArrayList<Integer>();
		points = new ArrayList<Integer>();
		polygons = new ArrayList<ArrayList<Integer>>();
		edges = new ArrayList<ArrayList<Integer>>();
		colors = new ArrayList<Color>();
		currentPolygon = new ArrayList<Integer>();
		imgtmp = null;
		   
		setFocusable(true);
	    addMouseListener(this);
	    addMouseMotionListener(this);
	    addKeyListener(this);
		// vector historico de factores de escala aplicados 
		factoresEscalas = new ArrayList<Integer>();
		visualizing = new ArrayList<Integer>();
		estadoGeo = true;

		zona = Box.createRigidArea(new Dimension(w, h));
		add(zona);
		MyMouseListener mml = new MyMouseListener();
		addMouseListener(mml);
		addMouseMotionListener(mml);
		colorLinea = new Color(255, 255, 255);
		currentColorLine = new Color(255, 255, 0);
		colorFondo = new Color(0, 0, 0);
		colorPunto = new Color(128, 128, 255);
		colorPoligono = new Color(255, 255, 255);
		colorPoligonoActual = new Color(128, 255, 128);
		colorFondoScroll = new Color(63, 63, 63);
		colorEdges = new Color(255, 63, 255);
		resaltaSegmentos = false;
		resaltaPuntos = true;
		resaltaPoligonos = true;
		resaltaEdges = true;
		verBitmap = verSegmentos = verPuntos = verPoligonos = verEdges = true;
		setBackground(colorFondoScroll);
		
		modo = ModeGeom.SEG;
	}

	/**
	 *   It associates the information panel with the class
	 * @param i Information panel Geometry
	 */
	public void assoccInfoGeom(InfoPanelGeom i) {
		infoGeom = i;
	}
	
	/**
	 *   It associates the information panel with the class
	 * @param i Information panel Botom
	 */
	public void assocInfoBottom(InfoPanelBottom i) {
		infoBottom = i;
	}

	/**
	 * It creates a new bitmap to create a new sequence, either it paints or 
	 * adds new images
	 * @param ww Width
	 * @param hh Height
	 * @param bg Background image
	 */
	public void newBitmap(int ww, int hh, ImageIcon bg) {
		factorEscala = 100;
		factoresEscalas.clear();
		estadoGeo = true;
		w = ww;
		h = hh;
		remove(zona);
		zona = Box.createRigidArea(new Dimension(w, h));
		add(zona);
		backGround = bg;
		if (secuencia == null)
			infoBottom.assocSequence(null);
		x0Sel = y0Sel = x1Sel = y1Sel = -1;
		getParent().repaint();

	}
	
	/**
	 * It adds a new segment into the segments vector.
	 * @param x0 initial X coordinate of the segment
	 * @param y0 initial Y coordinate of the segment
	 * @param x1 final X coordinate of the segment
	 * @param y1 final Y coordinate of the segment
	 */
	public void addSegment(int x0, int y0, int x1, int y1) {
		if (!(x0 >= 00 && x0 < w && y0 >= 0 && y0 < h && x1 >= 00
			&& x1 < w && y1 >= 0 && y1 < h && (x0 != x1 || y0 != y1)))
			return;
		for (int i = 0; i < segments.size(); i += 4) {
			int x00 = segments.get(i);
			int y00 = segments.get(i + 1);
			int x11 = segments.get(i + 2);
			int y11 = segments.get(i + 3);
			if (x0 == x00 && y0 == y00 && x1 == x11 && y1 == y11)
				return;
		}
		segments.add(x0);
		segments.add(y0);
		segments.add(x1);
		segments.add(y1);
		colors.add(null);
	}

	/**
	* It adds a new point into the points vector
	* @param x X coordinate of the point
	* @param y Y coordinate of the point   
	*/
	public void addPoint(int x, int y) {
		for (int i = 0; i < points.size(); i += 2) {
			int x00 = points.get(i);
			int y00 = points.get(i + 1);
			if (x == x00 && y == y00)
				return;
		}
		if (x >= 0 && x < w && y >= 0 && y < h) {
			points.add(x);
			points.add(y);
			colors.add(null);
		}
	}

	/**
	* It adds a new point into the actual polygon
	* @param x X coordintae of the new point in the polygon
	* @param y Y coordintae of the new point in the polygon  
	*/
	public void addPolyPoint(int x, int y) {
		for (int i = 0; i < currentPolygon.size(); i += 2) {
			int x00 = currentPolygon.get(i);
			int y00 = currentPolygon.get(i + 1);
			if (x == x00 && y == y00)
				return;
		}
		if (x >= 0 && x < w && y >= 0 && y < h) {
			currentPolygon.add(x);
			currentPolygon.add(y);
			ultimoX = x;
			ultimoY = y;
		}
	}

	/**
	* It adds the current polygon into the global vector of polygons.
	* This polygon is already closed.  
	*/
	public void addPoligon() {
		polygons.add(new ArrayList<Integer>(currentPolygon));
		colors.add(null);
		currentPolygon.clear();
		ultimoX = ultimoY = -1;
		getParent().repaint();
	}

	/**
	*   This method is called when we don not have to display a background bitmap
	*/
	public void noBitmap() {
		backGround = null;
	}

	/**
	*   It sets the background bitmap from the bitmap path
	* @param ruta bitmap path
	*/
	public void putBitmap(String ruta) throws JIPException {
		ImageIcon aux = new ImageIcon(ruta);
		if (aux.getImageLoadStatus() == MediaTracker.ERRORED)
			return;
		backGround = aux;
		Image imagen = backGround.getImage();
		secuencia = new JIPSequence(JIPToolkit.getColorImage(imagen));
		newBitmap(imagen.getWidth(backGround.getImageObserver()),
			imagen.getHeight(backGround.getImageObserver()),
			backGround);
		infoBottom.assocSequence(secuencia);
		numFrame = numBand = 0;
		if (secuencia != null)
			imgtmp = secuencia.getFrame(numFrame);
		else
			imgtmp = null;
	}

	/**
	  *   It changes the background color in the scroll panel
	  * @param nuevo New background color
	  */
	public void backgroundColorScroll(Color nuevo) {
		colorFondoScroll = nuevo;
		setBackground(colorFondoScroll);
	}

	/**
	*   It changes the line color
	* @param nuevo New line color   
	*/
	public void lineColor(Color nuevo) {
		colorLinea = nuevo;
	}

	/**
	 *   It changes the color of the current line
	 * @param nuevo New color  
	 */
	public void currentLineColor(Color nuevo) {
		currentColorLine = nuevo;
	}

	/**
	 *   It changes the background of the referenced frame to insert geometric data
	 * when we do  File->New
	 * @param nuevo New background color of the referenced frame
	 */
	public void backgroundColor(Color nuevo) {
		colorFondo = nuevo;
	}

	/**
	* 	It changes the color of  points
	* @param nuevo New color of points
	*/
	public void pointColor(Color nuevo) {
		colorPunto = nuevo;
	}
	
	/**
	 * 	 It changes the color of actual point
	 * @param nuevo New color of actual point
	 */

	public void currentPointColor(Color nuevo) {
		colorPuntoActual = nuevo;
	}

	/**
	*   It changes the color of poligons
	* @param nuevo New color of polygons
	*/

	public void polygonColor(Color nuevo) {
		colorPoligono = nuevo;
	}

	/**
	 * 	 It changes the color of actual polygon
	 * @param nuevo New color of actual polygon
	 */
	public void currentPolygonColor(Color nuevo) {
		colorPoligonoActual = nuevo;
	}

	/**
	* 	It changes the color of edges
	* @param nuevo New color of edges
	*/
	public void edgeColor(Color nuevo) {
		colorEdges = nuevo;
	}

	/**
	* 	It changes the class variable to emphasize the segments
	* @param b Boolean which shows if the segments have been emphasized
	*/
	public void enhanceSegments(boolean b) {
		resaltaSegmentos = b;
	}

	/**
	 * 	It changes the class variable to emphasize the points.
	 * @param b Boolean which shows if the points have been emphasized
	 */

	public void enhancePoints(boolean b) {
		resaltaPuntos = b;
	}

	/**
	* 	It changes the class variable to emphasize the polygons.
	* @param b Boolean which shows if the polygons have been emphasized
	*/
	public void enhancePolygon(boolean b) {
		resaltaPoligonos = b;
	}
	
	/**
	* 	It changes the class variable to emphasize the edges.
	* @param b Boolean which shows if the edges have been emphasized	
	*/
	public void enhanceEdges(boolean b) {
		resaltaEdges = b;
	}
	
	/**
	* 	It changes the class variable to display the bitmap.
	* @param b Boolean which shows if the polygons have been dispalyed	
	*/
	public void bitmapVisible(boolean b) {
		verBitmap = b;
	}
	
	/**
	*<FONT COLOR="BLUE">
	* 	It changes the class variable to display the segments.
	* @param b Boolean which shows if the segments have been dispalyed
	*/
	public void segmentsVisible(boolean b) {
		verSegmentos = b;
	}
	
	/**
	* 	It changes the class variable to display the points.
	* @param b Boolean which shows if the points have been dispalyed
	*/
	public void pointsVisible(boolean b) {
		verPuntos = b;
	}
	
	/**
	* 	It changes the class variable to display the polygons.
	* @param b Boolean which shows if the polygons have been dispalyed
	*/
	public void polygonsVisible(boolean b) {
		verPoligonos = b;
	}
	
	/**
	* 	It changes the class variable to display the edges.
	* @param b Boolean which shows if the edges have been dispalyed
	*/
	public void edgesVisible(boolean b) {
		verEdges = b;
	}

	/**
	* 	It returns the current selected frame 
	* @return Number of current numFrame
	*/
	public int getFrameNum() {
		return numFrame;
	}

	/**
	* 	It returns the current band 
	* @return Number of current band
	*/
	public int getBandNum() {
		return numBand;
	}

	/**
	*   It associates a new name with the current sequence
	* @param s New name of the sequence
	*/
	public void setSequenceName(String s) {
		if (secuencia != null) {
			secuencia.setName(s);
			infoBottom.assocSequence(secuencia);
		}
	}
	
	/**
	* 	It returns the name of the actual sequence
	* @return Name of the actual sequence
	*/
	public String getSequenceName() {
		if (secuencia != null)
			return secuencia.getName();
		else
			return null;
	}
	
	/**
	*   It associates a new name with the current frame
	* @param s New name for the current frame
	*/
	public void setFrameName(String s) throws JIPException {
		if (secuencia == null)
			return;
		secuencia.getFrame(numFrame).setName(s);
		infoBottom.assocSequence(secuencia);
	}

	/**
	* 	It associates a new sequence with the class
	* @param s new sequence to associate
	*/
	public void setSequence(JIPSequence s) throws JIPException {
		secuencia = s;
		if (s == null) {
			imgtmp = null;
			return;
		}
		imgtmp = secuencia.getFrame(numFrame);

		JIPImage img = secuencia.getFrame(0);
		ImageType t = img.getType();

		if (t == ImageType.BYTE || t == ImageType.BIT || t == ImageType.COLOR 
				|| t == ImageType.FLOAT) {
			Image img2 = JIPToolkit.getAWTImage(img);
			newBitmap(img.getWidth(), img.getHeight(), new ImageIcon(img2));
		} else if (t == ImageType.POINT || t == ImageType.POLY || 
				t == ImageType.SEGMENT || t == ImageType.EDGES) 
			newBitmap(img.getWidth(), img.getHeight(), null);
		numFrame = numBand = 0;
		getParent().repaint();
	}

	/**
	* 	Necessary to repaint the new image 
	*/
	public void reassignedSeq() throws JIPException {
		JIPImage img = secuencia.getFrame(numFrame);
		ImageType t = img.getType();

		if (t == ImageType.BYTE || t == ImageType.BIT || t == ImageType.COLOR 
				|| t == ImageType.FLOAT) {
			Image img2 = JIPToolkit.getAWTImage(img);
			newBitmap(img.getWidth(), img.getHeight(), new ImageIcon(img2));
		} else if (t == ImageType.POINT || t == ImageType.POLY || 
				t == ImageType.SEGMENT || t == ImageType.EDGES) 
			newBitmap(img.getWidth(), img.getHeight(), null);
	}
	
	/**
	* 	It returns the actual sequence
	* @return Actual sequence
	*/
	public JIPSequence getSequence() {
		factorEscala = 100;
		return secuencia;
	}

	/**
	* 	It appends a frame to the sequence
	* @param img Image to add
	*/
	public void addFrame(JIPImage img) throws JIPException {
		if (secuencia != null)
			secuencia.addFrame(img);
		else{
			setSequence(new JIPSequence(img));
			setSequenceName(img.getName());
		}
		infoBottom.assocSequence(secuencia);
	}

	/**
	* 	It appends a sequence to the current one
	* @param s Sequence to append
	*/
	public void addFrames(JIPSequence s) throws JIPException {
		if (secuencia == null || s == null)
			return;
		secuencia.appendSequence(s);
		infoBottom.assocSequence(secuencia);
	}

	/**
	 * 	It duplicates the current frame. 
	 */
	public void duplicateFrame() throws JIPException {
		JIPImage aux=secuencia.getFrame(numFrame).clone();
		secuencia.addFrame(aux);	
		infoBottom.assocSequence(secuencia);
	}
	
	/**
	*  	It changes the current frame
	* @param img New image
	*/
	public void changeFrame(JIPImage img) throws JIPException {
		secuencia.setFrame(img, numFrame);
		infoBottom.assocSequence(secuencia);
	}

	/**
	 * 	 It removes the current frame (if it is not the last one)
	 */
	public void removeFrame() throws JIPException {
		secuencia.removeFrame(numFrame);
		backGround = null;
		outView();
		infoBottom.assocSequence(secuencia);
		if (secuencia != null)
			changeToFrame(0);
	}

	/**
	*   It removes the current band (if it is not the last one)
	*/
	public void removeBand() throws JIPException {
		JIPImage aux = secuencia.getFrame(numFrame);
		if (aux instanceof JIPImgGeometric)
			throw new JIPException("Canvas2D.removeBand: do not valid for geometric types");
		((JIPImgBitmap)aux).removeBand(numBand);
		secuencia.setFrame(aux, numFrame);
		infoBottom.assocSequence(secuencia);
	}

	/**
	* 	Returns the length of the last segment
	* @return Length of last segment
	*/
	public float getLengthLastSegment() {
		if (segments.size() == 0)
			return -1f;
		int tam = segments.size();
		int x0 = segments.get(tam - 4);
		int y0 = segments.get(tam - 3);
		int x1 = segments.get(tam - 2);
		int y1 = segments.get(tam - 1);
		return (float) Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
	}

	/**
	* 	Returns the length of the selected segment
	* @return Length of selected segment
	*/
	public float getLengthSelectedSegment() {
		if (segments.size() == 0 || x1Sel == -1 || tipoSel != ModeGeom.SEG)
			return -1f;
		return (float) Math.sqrt(
			Math.pow(x0Sel - x1Sel, 2) + Math.pow(y0Sel - y1Sel, 2));
	}

	/**
	*   Returns the distance between the last two points
	* @return Distance between the last two points
	*/
	public float getDistanceLastPoints() {
		if (points.size() < 4)
			return -1f;
		int tam = points.size();
		int x0 = points.get(tam - 4);
		int y0 = points.get(tam - 3);
		int x1 = points.get(tam - 2);
		int y1 = points.get(tam - 1);
		return (float) Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
	}
	
	/**
	 * Set the image icon 
	 */
	public void setBackGround(ImageIcon ic) {
		backGround = ic;
	}

	/**
	* 	It returns an array with the segment data
	* @return Array with segment data
	*/
	public Object[][] getSegmentData() {
		int x0, y0, x1, y1;
		float longitud;
		Object datos[][] = new Object[segments.size() / 4][6];
		for (int i = 0; i < segments.size(); i += 4) {
			x0 = segments.get(i);
			y0 = segments.get(i + 1);
			x1 = segments.get(i + 2);
			y1 = segments.get(i + 3);
			longitud = (float) Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
			datos[i / 4][0] = i / 4;
			datos[i / 4][1] = segments.get(i);
			datos[i / 4][2] = segments.get(i + 1);
			datos[i / 4][3] = segments.get(i + 2);
			datos[i / 4][4] = segments.get(i + 3);
			datos[i / 4][5] = longitud;
		}
		return (datos);
	}

	/**
	* 	It loads in the class a sequence from a file 
	* @param fich Name of the file which has the sequence
	*/
	public void loadSequence(String fich) throws JIPException {
		secuencia = JIPToolkit.getSeqFromFile(fich);
		if (secuencia != null)
			imgtmp = secuencia.getFrame(0);
		else
			imgtmp = null;
		if (secuencia == null)
			return;
		segments.clear();
		points.clear();
		polygons.clear();
		edges.clear();
		indiceSeg = indicePunto = indicePoly = indiceEdge = 0;
		backGround = null;
		infoBottom.assocSequence(secuencia);
		numFrame = numBand = 0;
		getParent().repaint();
	}

	/**
	* It returns the scale factor according to total of scale factor applied
	* @return Scale factor
	*/
	public double sequenceZoom() {
		double sol = 1.0;

		for (int i = 0; i < factoresEscalas.size(); i++)
			sol = sol * factoresEscalas.get(i) / 100.0;
		return (sol);
	}

	/**
	* 	It changes the current frame to the one indicated as parameter
	* @param n Number of numFrame that we will load
	*/
	public void changeToFrame(int n) throws JIPException {
		if (secuencia == null || n<0 || n > secuencia.getNumFrames()) {
			imgtmp = null;
			return;
		}
		numFrame = n;
		imgtmp = secuencia.getFrame(numFrame);
		ImageType t = imgtmp.getType();
		numBand = 0;

		if (imgtmp instanceof JIPImgBitmap) {
			Image img2 = JIPToolkit.getAWTImage(imgtmp);

			if (!estadoGeo) { // estoy en modo zoom
				Image zoom = img2.getScaledInstance((int)(imgtmp.getWidth() * sequenceZoom()),
							(int)(imgtmp.getHeight() * sequenceZoom()), 0);

				backGround = new ImageIcon(zoom);
				w = zoom.getWidth(backGround.getImageObserver());
				h = zoom.getHeight(backGround.getImageObserver());

				worg = secuencia.getFrame(numFrame).getWidth();
				horg = secuencia.getFrame(numFrame).getHeight();

				remove(zona);
				zona = Box.createRigidArea(new Dimension(w, h));
				add(zona);
			} else
				newBitmap(imgtmp.getWidth(), imgtmp.getHeight(), new ImageIcon(img2));

			segments.clear();
			points.clear();
			polygons.clear();
			edges.clear();

			indiceSeg = indicePunto = indicePoly = indiceEdge = 0;
			visualizing.clear();
			n = ((JIPImgBitmap)imgtmp).getNumBands();
			if (t == ImageType.COLOR)
				n = -1;
		} else {
			colors = ((JIPImgGeometric)imgtmp).getColors();

			for (int i = 0; i < visualizing.size(); i++)
				if (n == visualizing.get(i))
					return;
			visualizing.add(n);
			switch (t) {
				case SEGMENT :
					segments.addAll(((JIPImgGeometric)imgtmp).getData());
					indiceSeg += ((JIPImgGeometric)imgtmp).getLength();
					break;
				case POINT :
					points.addAll(((JIPImgGeometric)imgtmp).getData());
					indicePunto += ((JIPImgGeometric)imgtmp).getLength();
					break;
				case POLY :
					indicePoly += ((JIPImgGeometric)imgtmp).getLength();
					polygons.addAll(((JIPImgGeometric)imgtmp).getData());
					break;
				case EDGES :
					indiceEdge += ((JIPImgGeometric)imgtmp).getLength();
					edges.addAll(((JIPImgGeometric)imgtmp).getData());
					break;
			}

			if (backGround == null)
				newBitmap(imgtmp.getWidth(), imgtmp.getHeight(), null);
			n=-1;
		}

		infoBottom.updateBandsFrame(imgtmp, n);
		getParent().repaint();
	}
	
	/**
	* 	It removes all geometric elements
	*/
	public void outView() {
		segments.clear();
		points.clear();
		polygons.clear();
		edges.clear();
		indiceSeg = indicePunto = indicePoly = indiceEdge = 0;
		visualizing.clear();
	}

	/** 
	 * Changes the current band
	* @param n Number of band to show
	*/
	public void changeBand(int n) throws JIPException {
		if (secuencia == null || n == numBand)
			return;
		JIPImage img = secuencia.getFrame(numFrame);
		if (img instanceof JIPImgGeometric) 
			throw new JIPException("Canvas2D.changeBand: do not valid for geometric data");
		if (n > ((JIPImgBitmap)img).getNumBands())
			return;

		Image img2 = JIPToolkit.getAWTImage(img, n);

		if (img2 != null) {
			if (!estadoGeo) { // estoy en modo zoom
				Image zoom  = img2.getScaledInstance((int)(img.getWidth()*sequenceZoom()),
						(int)(img.getHeight()*sequenceZoom()), 0);

				backGround = new ImageIcon(zoom);
				w = zoom.getWidth(backGround.getImageObserver());
				h = zoom.getHeight(backGround.getImageObserver());

				worg = secuencia.getFrame(numFrame).getWidth();
				horg = secuencia.getFrame(numFrame).getHeight();

				remove(zona);
				zona = Box.createRigidArea(new Dimension(w, h));
				add(zona);
			} else
				newBitmap(img.getWidth(), img.getHeight(), new ImageIcon(img2));
		}
		numBand = n;
		getParent().repaint();
	}

	/**
	*   It removes the last segment
	*/
	public void deleteLastSegment() {
		if (segments.size() >= 4 && segments.size() > indiceSeg) {
			for (int r = 0; r < 4; r++)
				segments.remove(segments.size() - 1);
			if (colors.size() >= segments.size()/4)
				colors.remove(segments.size()/4-1);
		}
		getParent().repaint();
	}
	
	/**
	*   It removes the last point
	*/
	public void deleteLastPoint() {
		if (points.size() >= 2 && points.size() > indicePunto) {
			for (int r = 0; r < 2; r++)
				points.remove(points.size() - 1);
			if (colors.size() >= points.size()/2)
				colors.remove(points.size()/2-1);
		}
		getParent().repaint();
	}

	/**
	*   It removes the last polygon
	*/
	public void deleteLastPolygon() {
		if (polygons.size() >= 1 && polygons.size() > indicePoly)  {
			polygons.remove(polygons.size() - 1);
			if (colors.size() >= polygons.size())
				colors.remove(polygons.size()-1);
		}
		getParent().repaint();
	}

	/**
	*   It removes the actual selection
	*/
	public void deleteSelection() {
		if (tipoSel == ModeGeom.POINT) {
			for (int i = 0; i < points.size(); i += 2) {
				if (x0Sel == points.get(i)
					&& y0Sel == points.get(i + 1)) {
					if (i < indicePunto) {
						new Dialog(this).information("Data from a sequence can not be deleted", "ERROR");
						return;
					}
					points.remove(i + 1);
					points.remove(i);
					if (colors.size() > i/2)
						colors.remove(i/2);
					x0Sel = x1Sel = y0Sel = y1Sel = -1;
					tipoSel=null;
					getParent().repaint();
				}
			}
		}
		if (tipoSel == ModeGeom.SEG) {
			for (int i = 0; i < segments.size(); i += 4) {
				if (x0Sel == segments.get(i)
					&& y0Sel == segments.get(i + 1)
					&& x1Sel == segments.get(i + 2)
					&& y1Sel == segments.get(i + 3)) {
					if (i < indiceSeg) {
						new Dialog(this).information("Data from a sequence can not be deleted", "ERROR");
						return;
					}
					segments.remove(i + 3);
					segments.remove(i + 2);
					segments.remove(i + 1);
					segments.remove(i);
					if (colors.size() > i/4)
						colors.remove(i/4);
					x0Sel = x1Sel = y0Sel = y1Sel = -1;
					tipoSel = null;
					getParent().repaint();
				}
			}
		}
	}

	/**
	* Returns the new zoom value according to the scale factor
	* @param vorg
	* @return New value of the point
	*/
	public int getZoomValue(int vorg) {
		double dsol, dmul, dvorg = (double) vorg;
		dsol = dvorg; // por defecto

		for (int fa = 0; fa < factoresEscalas.size(); fa++) {
			factorEscala = factoresEscalas.get(fa);
			dmul = (factorEscala / 100.0);
			dsol = ((dvorg * dmul) + (dmul - 1));
			dvorg = dsol;
		}
		return ((int) dsol);
	}

	/**
	* @param porcent
	*/
	public void zoomWindow(int porcent) throws JIPException {
		if (secuencia == null || porcent == 100)
			return;

		factorEscala = porcent;
		factoresEscalas.add(factorEscala);
		estadoGeo = false; 

		Image zoom;
		if (secuencia.getFrame(numFrame).getType() == ImageType.COLOR)
			zoom = JIPToolkit.getAWTImage(secuencia.getFrame(numFrame)).getScaledInstance(
					(w * porcent) / 100, (h * porcent) / 100, 0);
		else
			zoom = JIPToolkit.getAWTImage(secuencia.getFrame(numFrame),
					numBand).getScaledInstance((w * porcent) / 100, (h * porcent) / 100, 0);

		backGround = new ImageIcon(zoom);
		w = zoom.getWidth(backGround.getImageObserver());
		h = zoom.getHeight(backGround.getImageObserver());

		worg = secuencia.getFrame(numFrame).getWidth();
		horg = secuencia.getFrame(numFrame).getHeight();

		remove(zona);
		zona = Box.createRigidArea(new Dimension(w, h));
		add(zona);
		infoGeom.updateGeo(estadoGeo);
		getParent().repaint();
	}

	/**
	* 	It exports geometric data of the temporal vector to ascii file
	*/
	public void exportAscii() {
		JFileChooser abreFich = new JFileChooser();
		FileWriter fOut;
		try {
			abreFich.setCurrentDirectory(new File("."));
			abreFich.setSelectedFile(new File("Ascii.txt"));
			int returnVal = abreFich.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File fich = abreFich.getSelectedFile();
				fOut = new FileWriter(fich);
				if (!points.isEmpty()) {
					fOut.write("POINTS " + points.size() / 2 + "\n");
					for (int x = 0; x < points.size(); x += 2) {
						fOut.write(points.get(x).toString());
						fOut.write(" ");
						fOut.write(points.get(x + 1).toString());
						fOut.write("\n");
					}
				}

				if (!segments.isEmpty()) {
					fOut.write("SEGMENTS " + segments.size() / 4 + "\n");
					for (int x = 0; x < segments.size(); x += 4) {
						fOut.write(segments.get(x).toString());
						fOut.write(" ");
						fOut.write(segments.get(x + 1).toString());
						fOut.write(" ");
						fOut.write(segments.get(x + 2).toString());
						fOut.write(" ");
						fOut.write(segments.get(x + 3).toString());
						fOut.write("\n");
					}
				}
				if (!polygons.isEmpty()) {
					fOut.write("POLYGONS " + polygons.size() + "\n");
					for (ArrayList<Integer> aux : polygons) {
						fOut.write(aux.size() + " : ");
						for (int y = 0; y < aux.size(); y++) {
							fOut.write(aux.get(y).toString());
							fOut.write(" ");
						}
						fOut.write("\n");
					}
				}
				fOut.close();
			}
		} catch (Exception err) {logger.error(err);}
	}

	/**
	* 	It import geometric data of the ascii file to the temporal vectors	
	*/
	public void importAscii() throws JIPException {
		boolean mensaje = false;
		int c1, c2, c3, c4, cantidad, numpoints;
		try {
			JFileChooser abreFich = new JFileChooser();
			abreFich.setCurrentDirectory(new File("."));
			int returnVal = abreFich.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File fich = abreFich.getSelectedFile();
				FileInputStream fentrada = new FileInputStream(fich);
				Reader r = new BufferedReader(new InputStreamReader(fentrada));
				StreamTokenizer st = new StreamTokenizer(r);
				ArrayList<Integer> P = new ArrayList<Integer>();

				String titulo = "";
				st.nextToken();
				if (st.ttype != StreamTokenizer.TT_EOF) {
					titulo = st.sval;
					if (titulo.compareTo("POINTS") == 0) {
						emptyPoints();
						st.nextToken();
						cantidad = (int) st.nval;
						for (int kp = 0; kp < cantidad * 2; kp += 2) {
							st.nextToken();
							c1 = (int) st.nval;
							st.nextToken();
							c2 = (int) st.nval;
							addPoint(c1, c2);
							if (c1 < 0 || c1 >= w || c2 < 0 || c2 >= h)
								mensaje = true;
						}
						st.nextToken();
					}
					if (st.ttype != StreamTokenizer.TT_EOF) {
						titulo = st.sval;
						if (titulo.compareTo("SEGMENTS") == 0) {
							emptySegments();
							st.nextToken();
							cantidad = (int) st.nval;
							for (int kp = 0; kp < cantidad * 4; kp += 4) {
								st.nextToken();
								c1 = (int) st.nval;
								st.nextToken();
								c2 = (int) st.nval;
								st.nextToken();
								c3 = (int) st.nval;
								st.nextToken();
								c4 = (int) st.nval;
								addSegment(c1, c2, c3, c4);
								if (c1 < 0 || c1 >= w || c3 < 0 || c3 >= w
									|| c2 < 0 || c2 >= h || c2 < 0 || c2 >= h) {
									mensaje = true;
								}
							}
							st.nextToken();
						}
						if (st.ttype != StreamTokenizer.TT_EOF) {
							titulo = st.sval;
							if (titulo.compareTo("POLYGONS") == 0) {
								emptyPolygons();
								st.nextToken();
								cantidad = (int) st.nval;
								for (int kp = 0; kp < cantidad; kp++) {
									st.nextToken();
									numpoints = (int) st.nval;
									st.nextToken(); // LOS :
									boolean graba = true;
									for (int kl = 0; kl < numpoints; kl += 2) {
										st.nextToken();
										c1 = (int) st.nval;
										st.nextToken();
										c2 = (int) st.nval;
										if (c1 < 0 || c1 >= w || c2 < 0 || c2 >= h) {
											graba = false;
											mensaje = true;
										}
										P.add(c1);
										P.add(c2);
									}
									if (graba) 
										polygons.add(new ArrayList<Integer>(P));
									P.clear();
								}
							}
						} 
					} 
				} 
				if (mensaje == true)
					new Dialog(this).information("Some points outside of the image have been omitted","ATTENTION");
			}
		} 
		catch (FileNotFoundException e) {logger.error(e);} 
		catch (IOException e) {logger.error(e);}

		infoBottom.assocSequence(secuencia);
		getParent().repaint();

	}

	/**
	* 	Converts the geometrical data into a sequence.
	* Every geometric type are in a different frame.
	*/
	public void saveGeometry() {
		JIPImgGeometric img;
		ArrayList<Integer> P = new ArrayList<Integer>(points);
		ArrayList<Integer> S = new ArrayList<Integer>(segments);
		ArrayList<ArrayList<Integer>> PL = new ArrayList<ArrayList<Integer>>(polygons);
		ArrayList<ArrayList<Integer>> E = new ArrayList<ArrayList<Integer>>(edges);

		try {
			if (!P.isEmpty()) {
				img = new JIPGeomPoint(w, h);
				img.setData(P);
				if (secuencia != null)
					secuencia.addFrame(img);
				else
					secuencia = new JIPSequence(img);
			}
			if (!S.isEmpty()) {
				img = new JIPGeomSegment(w, h);
				img.setData(S);
				if (secuencia != null)
					secuencia.addFrame(img);
				else
					secuencia = new JIPSequence(img);
			}
			if (!PL.isEmpty()) {
				img = new JIPGeomPoly(w, h);
				img.setData(PL);
				if (secuencia != null)
					secuencia.addFrame(img);
				else 
					secuencia = new JIPSequence(img);
			}
			if (!E.isEmpty()) {
				img = new JIPGeomEdges(w, h);
				img.setData(E);
				if (secuencia != null)
					secuencia.addFrame(img);
				else 
					secuencia = new JIPSequence(img);
			}
			numFrame = 0;
			numBand = 0;
	
			if (secuencia != null)
				imgtmp = secuencia.getFrame(numFrame);
			else
				imgtmp = null;
		}catch (JIPException e){logger.error(e);}
		points.clear();
		segments.clear();
		edges.clear();
		polygons.clear(); 
		infoBottom.assocSequence(secuencia);
		indiceSeg = indicePunto = indicePoly = indiceEdge = 0;

		getParent().repaint();
	}

	/**
	* 	It adds an empty frame with the type indicated as parameter 
	* @param tipo Geometric type 
	*/
	public void addEmptyFrame(ImageType tipo) throws JIPException {
		JIPImgGeometric img;
		switch (tipo) {
			case POINT: img = new JIPGeomPoint(w,h);
						img.setData(new ArrayList<Integer>());
						break;
			case SEGMENT: img = new JIPGeomSegment(w,h);
						img.setData(new ArrayList<Integer>());
						break;
			case POLY: img = new JIPGeomPoly(w,h);
						img.setData(new ArrayList<ArrayList<Integer>>());
						break;
			case EDGES: img = new JIPGeomEdges(w,h);
						img.setData(new ArrayList<ArrayList<Integer>>());
						break;
			default: img=null;
		}
		if (secuencia != null)
			secuencia.addFrame(img);
		else
			secuencia = new JIPSequence(img);
		infoBottom.assocSequence(secuencia);
	}

	/**
	* 	Empties the point array
	*/
	public void emptyPoints() throws JIPException {
		points.clear();
		indicePunto = 0;
		infoBottom.assocSequence(secuencia);
		getParent().repaint();
		for (int i = 0; i < visualizing.size(); i++) {
			if (secuencia.getFrame(visualizing.get(i))
				.getType() == ImageType.POINT) {
				visualizing.remove(i);
				i--;
			}
		}
	}

	/**
	* 	 Empties the segment array
	*/
	public void emptySegments() throws JIPException {
		segments.clear();
		indiceSeg = 0;
		infoBottom.assocSequence(secuencia);
		getParent().repaint();
		for (int i = 0; i < visualizing.size(); i++) {
			if (secuencia.getFrame(visualizing.get(i))
				.getType() == ImageType.SEGMENT) {
				visualizing.remove(i);
				i--;
			}
		}
	}

	/**
	  * Empties the polygons array
	  */
	public void emptyPolygons() throws JIPException {
		polygons.clear();
		indicePoly = 0;
		infoBottom.assocSequence(secuencia);
		getParent().repaint();
		for (int i = 0; i < visualizing.size(); i++) {
			if (secuencia.getFrame(visualizing.get(i))
				.getType() == ImageType.POLY) {
				visualizing.remove(i);
				i--;
			}
		}
	}

	/**
	* Empties all the geometric data
	*/
	public void emptyAll() {
		points.clear();
		segments.clear();
		polygons.clear();
		edges.clear();
		indiceSeg = 0;
		indicePunto = 0;
		indicePoly = 0;
		accion = GeomAction.SELECT;
		infoBottom.assocSequence(secuencia);
		visualizing.clear();
		getParent().repaint();
	}

	/**
	* 	It captures the events which are produced in the information panel
	* @param e Event
	*/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==infoGeom.bsegment)
			modo = ModeGeom.SEG;
		if (e.getSource()==infoGeom.bpoint) {
			modo = ModeGeom.POINT;
			arrastrando = false;
		}
		if (e.getSource()==infoGeom.bpoly) {
			modo = ModeGeom.POLY;
			arrastrando = false;
		}

		if (e.getSource()==infoGeom.actionSel) {
			if (infoGeom.actionSel.getSelectedIndex()==0)
				accion = GeomAction.SELECT;
			else  accion = GeomAction.ADD;
			if (accion == GeomAction.ADD) {
				x0Sel = y0Sel = x1Sel = y1Sel = idxPolySel = -1;
				infoGeom.updateSel(-1, -1, -1, -1, "[no selection]");
				infoGeom.updateGeo(estadoGeo);
				return;
			}
		}
	}

	/**
	* 	MouseListener class
	*/
	class MyMouseListener extends MouseAdapter implements MouseMotionListener {
		/**
		* 	Method which is called when the mouse is pressed
		* @param e Event
		*/
		public void mousePressed(MouseEvent e) {
			if (estadoGeo) {
				if (accion == GeomAction.SELECT) {
					if (modo == ModeGeom.POINT) {
						if (points.size() == 0)
							return;
						xini = e.getX();
						yini = e.getY();
						float dist = 99999, distAux;
						int min = -1, x0 = -1, y0 = -1;
						for (int i = 0; i < points.size(); i += 2) {
							x0 = points.get(i);
							y0 = points.get(i + 1);
							distAux = (float) Math.sqrt(Math.pow(x0 - xini, 2)
										+ Math.pow(y0 - yini, 2));
							if (distAux < dist) {
								dist = distAux;
								min = i;
							}
						}
						x0Sel = points.get(min);
						y0Sel = points.get(min + 1);
						tipoSel = ModeGeom.POINT;
						infoGeom.updateSel(x0Sel, y0Sel, x0Sel, y0Sel, "Point");
						infoGeom.updateGeo(estadoGeo);
						getParent().repaint();
						return;
					}
					if (modo == ModeGeom.SEG) {
						if (segments.size() == 0)
							return;
						xini = e.getX();
						yini = e.getY();
						float dist = 99999;
						float distAux;
						int min = -1, x0 = -1, y0 = -1, x1 = -1, y1 = -1;
						for (int i = 0; i < segments.size(); i += 4) {
							x0 = segments.get(i);
							y0 = segments.get(i + 1);
							x1 = segments.get(i + 2);
							y1 = segments.get(i + 3);
							distAux = (float) Math.sqrt(Math.pow(x0 - xini, 2)
										+ Math.pow(y0 - yini, 2));
							if (distAux < dist) {
								dist = distAux;
								min = i;
							}
							distAux = (float) Math.sqrt(Math.pow(x1 - xini, 2)
										+ Math.pow(y1 - yini, 2));
							if (distAux < dist) {
								dist = distAux;
								min = i;
							}
						}
						x0Sel = segments.get(min);
						y0Sel = segments.get(min + 1);
						x1Sel = segments.get(min + 2);
						y1Sel = segments.get(min + 3);
						infoGeom.updateSel(x0Sel, y0Sel, x1Sel, y1Sel, "Segment");
						infoGeom.updateGeo(estadoGeo);
						tipoSel = ModeGeom.SEG;
						getParent().repaint();
						return;
					}
				}
				else {
					if (modo == ModeGeom.SEG || (modo == ModeGeom.POLY && ultimoX == -1)) {
						xini = e.getX();
						yini = e.getY();
						xfin = xini;
						yfin = yini;
						if (modo == ModeGeom.POLY) {
							ultimoX = xini;
							ultimoY = yini;
						}
					}
					if (modo == ModeGeom.POLY && ultimoX != -1) {
						xini = ultimoX;
						yini = ultimoY;
						xfin = e.getX();
						yfin = e.getY();
						if (xfin > w - 1)
							xfin = w - 1;
						if (yfin > h - 1)
							yfin = h - 1;
						if (xfin < 0)
							xfin = 0;
						if (yfin < 0)
							yfin = 0;
					}
					if (modo == ModeGeom.SEG || modo == ModeGeom.POLY) {
						if (xini >= 0 && xini < w && yini >= 0 && yini < h)
							arrastrando = true;
					}
					if (modo == ModeGeom.POINT) {
						xini = e.getX();
						yini = e.getY();
						addPoint(xini, yini);
						getParent().repaint();
					}
				}
			}
		}

		/**
		* @param e Event
		*/
		public void mouseReleased(MouseEvent e) {
			if (estadoGeo) {
				if (accion == GeomAction.SELECT)
					return; 
				if (modo == ModeGeom.SEG) {
					arrastrando = false;
					addSegment(xini, yini, xfin, yfin);
					infoGeom.updateInfo(w, h, e.getX(), e.getY(), segments.size() / 4,
						points.size() / 2, polygons.size(), xini, yini, xfin,
						yfin, imgtmp, numBand);
					infoBottom.updateInfo(w, h, e.getX(), e.getY(), segments.size() / 4,
							points.size() / 2, polygons.size(), xini, yini, xfin,
							yfin, imgtmp, numBand);
					infoGeom.updateGeo(estadoGeo);
					getParent().repaint();
				}
				if (modo == ModeGeom.POLY) {
					if (xini == xfin && yini == yfin) {
						if (currentPolygon.size() > (2 * 2))
							addPoligon();
					} else {
						addPolyPoint(xini, yini);
						addPolyPoint(xfin, yfin);
					}
					getParent().repaint();
				}
			}
		}

		/**
		* @param e Event
		*/
		public void mouseMoved(MouseEvent e) {
			if (modo != ModeGeom.POINT && accion == GeomAction.ADD && estadoGeo) {
				infoGeom.updateInfo(w, h, e.getX(), e.getY(), segments.size() / 4,
					points.size() / 2, polygons.size(), xini, yini, xfin, yfin,
					imgtmp, numBand);
				infoBottom.updateInfo(w, h, e.getX(), e.getY(), segments.size() / 4,
						points.size() / 2, polygons.size(), xini, yini, xfin, yfin,
						imgtmp, numBand);
				infoGeom.updateGeo(estadoGeo);
			} else {
				int mx, my;
				mx = e.getX();
				my = e.getY();
				double mul = w / (double)worg;
				factorEscala = (int)(100*mul);
				if (!estadoGeo && mul > 1) {
					// geometria desactivada -> estamos haciendo zoom
					int xs, ys;
					ys = (int) (my / mul);
					xs = (int) (mx / mul);
					infoGeom.updateInfo((int) (w / mul), (int) (h / mul), xs,
						ys, segments.size() / 4, points.size() / 2,
						polygons.size(), xini, yini, xini, yini,
						imgtmp, numBand);
					infoBottom.updateInfo((int) (w / mul), (int) (h / mul), xs,
							ys, segments.size() / 4, points.size() / 2,
							polygons.size(), xini, yini, xini, yini,
							imgtmp, numBand);
					infoGeom.updateGeo(estadoGeo);
				} else {
					double dmul = factorEscala / 100.0;
					if (dmul < 1) {
						int xs, ys;
						ys = (int) (my / dmul);
						xs = (int) (mx / dmul);
						infoGeom.updateInfo((int) (w / dmul), (int) (h / dmul),
							xs, ys, segments.size() / 4, points.size() / 2,
							polygons.size(), xini, yini, xini, yini,
							imgtmp, numBand);
						infoBottom.updateInfo((int) (w / dmul), (int) (h / dmul),
								xs, ys, segments.size() / 4, points.size() / 2,
								polygons.size(), xini, yini, xini, yini,
								imgtmp, numBand);
						infoGeom.updateGeo(estadoGeo);
					} else {
						infoGeom.updateInfo(w, h, mx, my, segments.size() / 4,
							points.size() / 2, polygons.size(), xini,
							yini, xini, yini, imgtmp, numBand);
						infoBottom.updateInfo(w, h, mx, my, segments.size() / 4,
								points.size() / 2, polygons.size(), xini,
								yini, xini, yini, imgtmp, numBand);
						infoGeom.updateGeo(estadoGeo);
					}
				}
			}
		}
		
		/**
		* @param e Event
		*/
		public void mouseDragged(MouseEvent e) {
			if (modo != ModeGeom.POINT && accion == GeomAction.ADD && estadoGeo) {
				infoGeom.updateInfo(w, h, e.getX(), e.getY(), segments.size() / 4,
					points.size() / 2, polygons.size(), xini, yini, xfin,
					yfin, imgtmp, numBand);
				infoBottom.updateInfo(w, h, e.getX(), e.getY(), segments.size() / 4,
						points.size() / 2, polygons.size(), xini, yini, xfin,
						yfin, imgtmp, numBand);
				infoGeom.updateGeo(estadoGeo);
			} else {
				int mx, my;
				mx = e.getX();
				my = e.getY();
				factorEscala = (int) (100*w / (double) worg);
				// factor de escala en funcion de las resoluciones
				double mul = (factorEscala / 100.0);
				if (!estadoGeo && mul > 1) {
					// geometria desactivada -> estamos haciendo zoom
					int xs, ys;
					ys = (int) ((my / mul) + 1);
					xs = (int) ((mx / mul) + 1);
					infoGeom.updateInfo((int) (w / mul), (int) (h / mul), xs,
						ys, segments.size() / 4, points.size() / 2,
						polygons.size(), xini, yini, xini, yini,
						imgtmp, numBand);
					infoBottom.updateInfo((int) (w / mul), (int) (h / mul), xs,
							ys, segments.size() / 4, points.size() / 2,
							polygons.size(), xini, yini, xini, yini,
							imgtmp, numBand);
					infoGeom.updateGeo(estadoGeo);
				} else {
					double dmul = factorEscala / 100.0;
					if (dmul < 1) {
						int xs, ys;
						ys = (int) (my / dmul);
						xs = (int) (mx / dmul);
						infoGeom.updateInfo((int) (w / dmul), (int) (h / dmul),
							xs, ys, segments.size() / 4, points.size() / 2,
							polygons.size(), xini, yini, xini, yini,
							imgtmp, numBand);
						infoBottom.updateInfo((int) (w / dmul), (int) (h / dmul),
								xs, ys, segments.size() / 4, points.size() / 2,
								polygons.size(), xini, yini, xini, yini,
								imgtmp, numBand);
						infoGeom.updateGeo(estadoGeo);
					} else {
						infoGeom.updateInfo(w, h, mx, my, segments.size() / 4,
							points.size() / 2, polygons.size(), xini, yini,
							xini, yini, imgtmp, numBand);
						infoBottom.updateInfo(w, h, mx, my, segments.size() / 4,
								points.size() / 2, polygons.size(), xini, yini,
								xini, yini, imgtmp, numBand);
						infoGeom.updateGeo(estadoGeo);
					}
				}
			}
			if (estadoGeo) {
				if (accion == GeomAction.SELECT) return; // Seleccionar
				if (modo == ModeGeom.SEG || modo == ModeGeom.POLY) {
					if (arrastrando) {
						xfin = e.getX();
						yfin = e.getY();
						if (xfin > w - 1) xfin = w - 1;
						if (yfin > h - 1) yfin = h - 1;
						if (xfin < 0) xfin = 0;
						if (yfin < 0) yfin = 0;
						getParent().repaint();
					}
				}
			}
		}
	}

	/**
	 * 	 Method which repaints the background bitmap and the geometric elements	 
	 * @param g Graphics
	 */
	public void paint(Graphics g) {
		paintComponent(g);
		g.setColor(colorFondo);
		g.fillRect(0, 0, w, h);
		if (backGround != null && verBitmap)
			backGround.paintIcon(this, g, 0, 0);

		ArrayList<Integer> poli=null;
		if (verPoligonos)
			for (int i = 0; i < polygons.size(); i++) {
			poli = polygons.get(i);
			if (i < colors.size() && colors.get(i) != null) 
				g.setColor(colors.get(i));
			else
				g.setColor(colorPoligono);
			int xpoint[] = new int[poli.size() / 2];
			int ypoint[] = new int[poli.size() / 2];
			for (int j = 0; j < poli.size(); j += 2) {
				xpoint[j / 2] = getZoomValue(poli.get(j));
				ypoint[j / 2] = getZoomValue(poli.get(j + 1));
			}

			g.drawPolygon(xpoint, ypoint, poli.size() / 2);
			if (resaltaPoligonos) {
				for (int j = 0; j < poli.size() / 2; j++)
					xpoint[j]++;
				g.drawPolygon(xpoint, ypoint, poli.size() / 2);
				for (int j = 0; j < poli.size() / 2; j++) {
					ypoint[j]++;
					xpoint[j]--;
				}
				g.drawPolygon(xpoint, ypoint, poli.size() / 2);
			}
		}
		if (verEdges) {
			ArrayList<Integer> edg = null;
			for (int i = 0; i < edges.size(); i++) {
				edg = (ArrayList<Integer>) edges.get(i);
				if (i < colors.size() && colors.get(i) != null) 
					g.setColor(colors.get(i));
				else
					g.setColor(colorEdges);
				int xpoint[] = new int[edg.size() / 2];
				int ypoint[] = new int[edg.size() / 2];
				for (int j = 0; j < edg.size(); j += 2) {
					xpoint[j / 2] = getZoomValue(edg.get(j));
					ypoint[j / 2] = getZoomValue(edg.get(j + 1));
				}
				for (int j = 0; j < edg.size() / 2; j++) {
					if (resaltaEdges)
						g.fillOval(xpoint[j] - 2, ypoint[j] - 2, 4, 4);
					else
						g.fillOval(xpoint[j], ypoint[j], 1, 1);
				}
			}
		}

		for (int i = 0; i < segments.size() && verSegmentos; i += 4) {
			if (i/4 < colors.size() && colors.get(i/4) != null) 
				g.setColor(colors.get(i/4));
			else
				g.setColor(colorLinea);
			int x0 = getZoomValue(segments.get(i));
			int y0 = getZoomValue(segments.get(i + 1));
			int x1 = getZoomValue(segments.get(i + 2));
			int y1 = getZoomValue(segments.get(i + 3));
			g.drawLine(x0, y0, x1, y1);
			if (resaltaSegmentos) {
				g.drawLine(x0 + 1, y0, x1 + 1, y1);
				g.drawLine(x0, y0 + 1, x1, y1 + 1);
			}
		}
		g.setColor(currentColorLine);
		if (arrastrando && accion == GeomAction.ADD) {
			if (ultimoX != -1 || modo == ModeGeom.SEG)
				g.drawLine(xini, yini, xfin, yfin);
			if ((resaltaSegmentos && modo == ModeGeom.SEG)
				|| (resaltaPoligonos && modo == ModeGeom.POLY && ultimoX != -1)) {
				g.drawLine(xini + 1, yini, xfin + 1, yfin);
				g.drawLine(xini, yini + 1, xfin, yfin + 1);
			}
		}
		for (int i = 0; i < points.size() && verPuntos; i += 2) {
			if (i/2 < colors.size() && colors.get(i/2) != null) 
				g.setColor(colors.get(i/2));
			else
				g.setColor(colorPunto);
			int xp = getZoomValue(points.get(i));
			int yp = getZoomValue(points.get(i + 1));
			if (resaltaPuntos)
				g.fillOval(xp - 2, yp - 2, 4, 4);
			else
				g.fillOval(xp, yp, 1, 1);
		}
		g.setColor(colorPoligonoActual);
		for (int i = 0; i < currentPolygon.size() - 2; i += 2) {
			int x0 = getZoomValue(currentPolygon.get(i));
			int y0 = getZoomValue(currentPolygon.get(i + 1));
			int x1 = getZoomValue(currentPolygon.get(i + 2));
			int y1 = getZoomValue(currentPolygon.get(i + 3));
			g.drawLine(x0, y0, x1, y1);
			if (resaltaPoligonos) {
				g.drawLine(x0 + 1, y0, x1 + 1, y1);
				g.drawLine(x0, y0 + 1, x1, y1 + 1);
			}
		}
		if (estadoGeo && accion == GeomAction.SELECT && modo == ModeGeom.SEG && verSegmentos) {
			if (x0Sel != -1) {
				g.setColor(currentColorLine);
				g.drawLine(x0Sel, y0Sel, x1Sel, y1Sel);
				g.drawLine(x0Sel + 1, y0Sel, x1Sel + 1, y1Sel);
				g.drawLine(x0Sel, y0Sel + 1, x1Sel, y1Sel + 1);
			}
		}
		if (estadoGeo && accion == GeomAction.SELECT && modo == ModeGeom.POINT && verPuntos) {
			if (x0Sel != -1) {
				g.setColor(colorPuntoActual);
				g.drawOval(x0Sel - 5, y0Sel - 5, 10, 10);
			}
		}
	}
	
	public void mousePressed(MouseEvent evt) {   
		requestFocus();
	}   
	public void mouseEntered(MouseEvent evt) {}
	public void mouseExited(MouseEvent evt) {}
	public void mouseReleased(MouseEvent evt) {}
	public void mouseClicked(MouseEvent evt) {}
	//	 Event controllers
	public void keyPressed(KeyEvent evt) {}
	public void keyReleased(KeyEvent evt) {}
	public void keyTyped(KeyEvent evt) {  
		int inc_x=0, inc_y=0;
		char tecla = evt.getKeyChar();
		if (tecla == 'a' || tecla == 'A') {
			inc_x=-1; inc_y=0;
		}
		if (tecla == 's' || tecla == 'S') {
			inc_x=1; inc_y=0;
		}
		if (tecla == 'q' || tecla == 'Q') {
			inc_x=0; inc_y=-1;
		}
		if (tecla == 'z' || tecla == 'Z') {
			inc_x=0; inc_y=1;
		}
		try {
			Point p=getLocationOnScreen();
			Robot rob = new Robot();
			rob.mouseMove(p.x+posXRaton+inc_x, p.y+posYRaton+inc_y);
		}catch (Exception e) {logger.error(e);}
	}
	
	public void mouseMoved(MouseEvent evt) {
		posXRaton=evt.getX();
		posYRaton=evt.getY();
	}
	public void mouseDragged(MouseEvent evt) {}

}
