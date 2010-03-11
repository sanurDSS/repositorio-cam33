package javavis.jip2d.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.Properties;
import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPBmpFloat;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;

import org.apache.log4j.Logger;

import javax.swing.*;


/**
	 * Class which has the information about the elements of the
	 * program, the cursor position, its value, band and numFrame where
	 * we are worked etc...
   */
public class InfoPanelBottom extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1428532347396392736L;
	
	private static Logger logger = Logger.getLogger(InfoPanelBottom.class);

	/** The canvas */
	Canvas2D canvas;

	/** Position label */
	JLabel pos;

	/** Actual value label */
	JLabel value;

	/** Frame size label */
	JLabel tamFrame;

	/** Deployed of the selected numFrame */
	JComboBox frameSel;

	/** Deployed of the selected band */
	JComboBox bandSel;

	/** Progress Bar */
	JProgressBar pBar;
	
	/** Properties for language  */
	Properties prop;

	/**
	* 	Class constructor. It creates the information panel at the bottom of
	* the main window
	* @param c Geometric canvas
	*/
	public InfoPanelBottom(Canvas2D c, Properties propi) {
		canvas = c;
		prop = propi;
		
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());
		
		tamFrame = new JLabel(prop.getProperty("FrameSize")+":     ");
		pos = new JLabel(prop.getProperty("Coords")+":         ");
		value = new JLabel(prop.getProperty("Value")+":          ");
		
		JPanel auxPanel1 = new JPanel();
		JPanel auxPanel2 = new JPanel();
		JPanel auxPanel21 = new JPanel();
		JPanel auxPanel22 = new JPanel();
		JPanel auxPanel23 = new JPanel();
		
		auxPanel1.setLayout(new BoxLayout(auxPanel1, BoxLayout.Y_AXIS));
		
		auxPanel1.add(tamFrame);
		auxPanel1.add(pos);
		auxPanel1.add(value);
		
		auxPanel21.setLayout(new BorderLayout());
		auxPanel22.setLayout(new BorderLayout());
		auxPanel23.setLayout(new BorderLayout());
		auxPanel2.setLayout(new BoxLayout(auxPanel2, BoxLayout.Y_AXIS));
		
		JLabel labFrame = new JLabel (prop.getProperty("Frame")+":  ");
		auxPanel21.add(labFrame,BorderLayout.WEST);
				
		frameSel = new JComboBox();
		frameSel.addItem("[ "+prop.getProperty("Nosequence")+" ]");
		frameSel.setPreferredSize(new Dimension(250,20));
		auxPanel21.add(frameSel,BorderLayout.EAST);
					
		JLabel labBanda = new JLabel (prop.getProperty("Band")+":   ");
		auxPanel22.add(labBanda,BorderLayout.WEST);
		
		bandSel = new JComboBox();
		bandSel.addItem("[ "+prop.getProperty("Nosequence")+" ]");
		bandSel.setPreferredSize(new Dimension(250,20));
		auxPanel22.add(bandSel,BorderLayout.EAST);
		
		auxPanel2.add(auxPanel21);
		auxPanel2.add(auxPanel22);
		
		JLabel auxpbar = new JLabel (prop.getProperty("Progress")+":");
		auxPanel23.add(auxpbar,BorderLayout.WEST);
//		 The progress bar has a minimum value 0 and a maximum of 100
		pBar = new JProgressBar(0, 100); 
		pBar.setPreferredSize(new Dimension(250,15));
		pBar.setStringPainted(true);
		auxPanel23.add(pBar,BorderLayout.EAST);
		auxPanel2.add(auxPanel23);
		
		add(auxPanel1,BorderLayout.WEST);
		add(auxPanel2,BorderLayout.EAST);
		
		bandSel.addActionListener(this);
		frameSel.addActionListener(this);

	}

	/**
	* Method which put information of values of the new sequence in the panel	
	* @param s New sequence
	*/
	public void assocSequence (JIPSequence s) {
		frameSel.removeAllItems();
		bandSel.removeAllItems();
		if (s == null) {
			frameSel.addItem("[ "+prop.getProperty("Nosequence")+" ]");
			bandSel.addItem("[ "+prop.getProperty("Nosequence")+" ]");
			return;
		}
		
		try {
			for (int i = 0; i < s.getNumFrames(); i++) {
				String tipo = s.getFrame(i).getType().toString();
				String nomF = s.getFrame(i).getName();
				if (nomF.length() > 10) 
					nomF = nomF.substring(0, 7) + "...";
				frameSel.addItem(i + ":" + tipo + ":" + nomF);
			}
			JIPImage aux = s.getFrame(0);
			updateBandsFrame(aux, 0);
		}catch (JIPException e) {logger.error(e);}
	}
	

	/**
	* Method which updates the value of the bands
	* @param img New image
	* @param n Number of bands
	*/
	public void updateBandsFrame(JIPImage img, int n) {
		bandSel.removeAllItems();
		if (img instanceof JIPBmpColor) {
			bandSel.addItem("COLOR-Bands RGB");
		}
		else if (img instanceof JIPImgGeometric) {
			bandSel.addItem("Geometric image");
		}
		else {
			for (int i = 0; i < ((JIPImgBitmap)img).getNumBands(); i++)
				bandSel.addItem(prop.getProperty("Band")+" " + i);
			tamFrame.setText(prop.getProperty("FrameSize")+": " + img.getWidth() + "x" + img.getHeight());
		}
	}
	
	/**
	* Method which updates the value of the progress bar
	* @param value Integer between 0 and 100
	*/
	public void setBar (int value) {
		if (value >=0 && value <=100)
			pBar.setValue(value);
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
		try {
			if (x < w && x >= 0 && y < h && y >= 0) {
				pos.setText(prop.getProperty("Coords")+": (" + x + "," + y + ")");
				if (img == null)
					value.setText(prop.getProperty("Value")+": <-1>");
				else if (img.getType() == ImageType.FLOAT) {
					value.setText(prop.getProperty("Value")+": <" + 
							Float.toString(((JIPBmpFloat)img).getPixelFloat(b, x, y))+ ">");
				} else if (img.getType() == ImageType.COLOR) {
					value.setText(prop.getProperty("Value")+": <" + (int)((JIPBmpColor)img).getPixelRed(x, y) + ","
							+ (int)((JIPBmpColor)img).getPixelGreen(x, y) + "," + (int)((JIPBmpColor)img).getPixelBlue(x, y) + ">");
				} else if (img instanceof JIPImgBitmap){
					value.setText(prop.getProperty("Value")+": <" + (int)((JIPImgBitmap)img).getPixel(b, x, y) + ">");
				} else { // Geometric type
					value.setText(prop.getProperty("Value")+":<Geom>");
				}
			} else {
				pos.setText(prop.getProperty("Coords")+": ( OUT )     ");
				value.setText(prop.getProperty("Value")+": ( OUT )");
			}
		}catch (JIPException e) {logger.error(e);}
	}


	/**
	* Method which selects a numFrame which number is passed as parameter
	* @param nframe Number of numFrame
	*/
	public void setFrame(int nFrame) {
		frameSel.setSelectedIndex(nFrame);
	}

	/**
	* 	It captures the events  produced in the bottom panel
	* @param e Event
	*/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==frameSel) {
			int sel=frameSel.getSelectedIndex();
			if (sel<0) sel=0;
			try {
				canvas.changeToFrame(sel);
			} catch (JIPException ex) {logger.error("This never must show "+ex);}
		}
		if (e.getSource()==bandSel) {
			int sel=bandSel.getSelectedIndex();
			if (sel<0) sel=0;
			try {
				canvas.changeBand(sel);
			}catch(JIPException ex) {logger.error("This never must show "+ex);}
		}
	}
}
